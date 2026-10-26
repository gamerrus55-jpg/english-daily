@file:OptIn(ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/** Слово, на которое нажали, и предложение, где оно встретилось. */
object WordTap {
    var word by mutableStateOf<String?>(null)
        private set
    var sentence by mutableStateOf("")
        private set

    fun select(w: String, inSentence: String) {
        val clean = Dictionary.clean(w)
        if (clean.isEmpty()) return
        word = clean
        sentence = inSentence
    }

    fun close() {
        word = null
    }
}

/** Перевод слова в контексте через ИИ — запоминаем, чтобы не спрашивать снова. */
object ContextTranslate {
    private val cache = mutableMapOf<String, Pair<String, String>>()

    private val system = """
You translate an English word for a Russian learner, exactly in the meaning it has in the given sentence.
Answer in EXACTLY this format:
RU: <short Russian translation, 1–4 words>
NOTE: <if it's slang, an idiom, a phrasal verb or has a special meaning here — one short sentence in Russian; otherwise write ->
""".trimIndent()

    fun cached(word: String, sentence: String) = cache["$word|$sentence"]

    suspend fun translate(store: ProgressStore, word: String, sentence: String): Result<Pair<String, String>> {
        cached(word, sentence)?.let { return Result.success(it) }
        return Ai.ask(store, system, listOf(Ai.Msg("user", "Word: $word\nSentence: $sentence")), maxTokens = 120).map { text ->
            val names = listOf("RU", "NOTE")
            val ru = Ai.field(text, "RU", names).ifBlank { text.lines().firstOrNull().orEmpty() }.trim()
            val note = Ai.field(text, "NOTE", names).trim().let { if (it == "-" || it == "—") "" else it }
            (ru to note).also { cache["$word|$sentence"] = it }
        }
    }
}

/**
 * Текст, в котором каждое слово можно нажать: появится перевод, транскрипция
 * и кнопка «В повторение».
 */
@Composable
fun TapText(
    text: String,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    val selected = WordTap.word
    FlowRow(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        text.split(" ").filter { it.isNotEmpty() }.forEach { w ->
            val isSel = selected != null && Dictionary.clean(w) == selected && WordTap.sentence == text
            Text(
                "$w ",
                style = style,
                color = if (isSel) Grads.Recall.b else color,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { WordTap.select(w, text) }
            )
        }
    }
}

/** Карточка выбранного слова — показывается поверх любого экрана, внизу. */
@Composable
fun BoxScope.WordSheet(speaker: Speaker, store: ProgressStore) {
    val w = WordTap.word ?: return
    val sentence = WordTap.sentence
    val scope = rememberCoroutineScope()
    @Suppress("UNUSED_VARIABLE") val mv = MyWords.version
    val local = remember(w) { Dictionary.lookup(w, emptyMap()) }
    var ai by remember(w, sentence) { mutableStateOf(ContextTranslate.cached(w, sentence)) }
    var loading by remember(w, sentence) { mutableStateOf(false) }
    var error by remember(w, sentence) { mutableStateOf<String?>(null) }

    LaunchedEffect(w) { speaker.say(w) }

    // Нет в словаре — сразу спрашиваем ИИ (если он подключён)
    LaunchedEffect(w, sentence) {
        if (local == null && ai == null && store.aiReady) {
            loading = true
            ContextTranslate.translate(store, w, sentence)
                .onSuccess { ai = it }
                .onFailure { error = it.message }
            loading = false
        }
    }

    val ru = ai?.first ?: local
    val shape = RoundedCornerShape(24.dp)
    Column(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, Grads.Recall.a, shape)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(w, style = MaterialTheme.typography.headlineSmall, color = Grads.Recall.b)
                PronLine(w)
            }
            CircleButton("✕") { WordTap.close() }
        }
        Spacer(Modifier.height(6.dp))
        when {
            ru != null -> Text(ru, style = MaterialTheme.typography.titleLarge)
            loading -> Hint("🤖 Перевожу в контексте…")
            !store.aiReady -> Hint("Нет в словаре. Подключи бесплатный ИИ в ⚙️ — он переведёт любое слово с учётом предложения.")
            else -> Hint(error ?: "Не удалось перевести.")
        }
        ai?.second?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(4.dp))
            Text("💡 $it", style = MaterialTheme.typography.bodyMedium)
        }
        if (local != null && ai == null && store.aiReady) {
            Spacer(Modifier.height(4.dp))
            Text(
                if (loading) "🤖 Перевожу…" else "🤖 Перевести именно в этом предложении",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !loading) {
                    loading = true
                    scope.launch {
                        ContextTranslate.translate(store, w, sentence)
                            .onSuccess { ai = it }
                            .onFailure { error = it.message }
                        loading = false
                    }
                }
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("🔊", { speaker.say(w) })
            Pill("🐢", { speaker.say(w, slow = true) })
            val inMine = MyWords.contains(w)
            Pill(
                if (inMine) "⭐ В повторении" else "＋ В повторение",
                {
                    if (!inMine && MyWords.add(w, ru ?: "")) {
                        // Предложение, где встретилось слово, — как пример употребления
                        MyWords.items.lastOrNull()?.let { item ->
                            Usages.register(item, Usage(sentence, "", "💬 где встретилось"))
                        }
                    }
                },
                selected = inMine,
                grad = Grads.Recall
            )
        }
    }
}
