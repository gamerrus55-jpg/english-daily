package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/** Примеры от ИИ: запоминаем, чтобы не спрашивать дважды. */
object AiUsages {
    private val cache = mutableMapOf<String, List<Usage>>()

    private val system = """
You help a Russian speaker learn English vocabulary in context.
For the given English word or phrase, write 3 short, natural example sentences that people really say,
each showing a DIFFERENT context where the word is used (for example: everyday conversation, work, shopping, health, legal or official language — only contexts where the word is actually used).
If the word has different meanings in different contexts, show those different meanings.
Answer with EXACTLY 3 lines in this format and nothing else:
CONTEXT IN RUSSIAN (1–2 words) | English sentence | Russian translation
""".trimIndent()

    fun cached(word: String): List<Usage>? = cache[word.lowercase()]

    suspend fun fetch(store: ProgressStore, word: String): Result<List<Usage>> {
        cached(word)?.let { return Result.success(it) }
        return Ai.ask(store, system, listOf(Ai.Msg("user", word)), maxTokens = 400).mapCatching { text ->
            val list = text.lines()
                .map { it.trim().trimStart('-', '•', '*', ' ').replace("**", "") }
                .mapNotNull { line ->
                    val parts = line.split("|").map { it.trim() }
                    if (parts.size >= 3 && parts[1].isNotEmpty()) Usage(parts[1], parts[2], "🤖 " + parts[0].lowercase()) else null
                }
            if (list.isEmpty()) throw Exception("ИИ ответил непонятно, попробуй ещё раз.")
            cache[word.lowercase()] = list
            list
        }
    }
}

/**
 * Блок «Как употребляется»: примеры фраз с пометкой, где так говорят.
 * Для отдельных слов показываем всегда, для фраз — только кнопку ИИ.
 */
@Composable
fun UsageBlock(item: Item, speaker: Speaker, store: ProgressStore, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val local = remember(item.id) { Usages.forItem(item) }
    var ai by remember(item.id) { mutableStateOf(AiUsages.cached(item.en)) }
    var loading by remember(item.id) { mutableStateOf(false) }
    var error by remember(item.id) { mutableStateOf<String?>(null) }
    val isWord = item.en.split(" ").size <= 3

    if (!isWord && local.isEmpty() && ai == null) return

    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Text("Как употребляется", style = MaterialTheme.typography.titleMedium)
        val all = local + (ai ?: emptyList())
        if (all.isEmpty() && !loading) {
            Hint("Готовых примеров нет — попроси ИИ.")
        }
        all.forEach { us ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { speaker.say(us.en) }
                    .padding(10.dp)
            ) {
                Text(us.tag, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(us.en, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text("🔊")
                }
                Hint(us.ru)
            }
        }
        if (ai == null) {
            Spacer(Modifier.height(10.dp))
            Pill(if (loading) "Думаю…" else "🤖 Ещё примеры в разных ситуациях", {
                if (loading) return@Pill
                if (!store.aiReady) {
                    error = "Добавь бесплатный ключ GigaChat в ⚙️ настройках."
                    return@Pill
                }
                loading = true
                error = null
                scope.launch {
                    AiUsages.fetch(store, item.en)
                        .onSuccess { ai = it }
                        .onFailure { error = it.message }
                    loading = false
                }
            })
        }
        error?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, color = Bad, style = MaterialTheme.typography.bodySmall)
        }
    }
}
