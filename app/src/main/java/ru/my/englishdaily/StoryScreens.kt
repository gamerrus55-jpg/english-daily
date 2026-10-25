@file:OptIn(ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private val StoryGrad get() = Grads.Recall

// ---------- Список историй ----------

@Composable
fun StoryListScreen(store: ProgressStore, onOpen: (Story) -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopBar("📚 Истории", onExit)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Как заниматься", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        "1) Послушай историю целиком.\n" +
                            "2) Прочитай: нажми на незнакомое слово — увидишь перевод и сможешь добавить его в «Мои слова».\n" +
                            "3) Ответь на вопросы голосом — коротко, как в жизни: «Yes, he is», «At seven»."
                    )
                }
            }
            Stories.all.groupBy { it.level }.forEach { (level, list) ->
                item {
                    Text(
                        Stories.levelNames[level] ?: "Уровень $level",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                list.forEach { story ->
                    item(key = story.id) {
                        val best = store.storyBest(story.id)
                        SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(story) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconTile(story.emoji, StoryGrad, 52.dp)
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(story.title, style = MaterialTheme.typography.titleMedium)
                                    Hint(
                                        "${story.sentences.size} предложений · ${story.questions.size} вопросов" +
                                            if (best >= 0) " · лучший результат $best%" else ""
                                    )
                                }
                                Text(
                                    if (best >= 80) "✅" else "›",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ---------- История ----------

@Composable
fun StoryScreen(story: Story, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    Column(Modifier.fillMaxSize()) {
        TopBar("${story.emoji} ${story.title}", onExit)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("📖 Читать и слушать", { tab = 0; speaker.stop() }, selected = tab == 0, grad = StoryGrad)
            Pill("❓ Вопросы", { tab = 1; speaker.stop() }, selected = tab == 1, grad = StoryGrad)
        }
        Spacer(Modifier.height(12.dp))
        if (tab == 0) {
            StoryReader(story, speaker, onQuestions = { tab = 1 })
        } else {
            StoryQuestions(story, speaker, store, onExit)
        }
    }
}

@Composable
private fun ColumnScope.StoryReader(story: Story, speaker: Speaker, onQuestions: () -> Unit) {
    var showRu by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(-1) }
    var playing by remember { mutableStateOf(false) }
    var playToken by remember { mutableIntStateOf(0) }
    var word by remember { mutableStateOf<String?>(null) }
    var added by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    @Suppress("UNUSED_VARIABLE") val v = MyWords.version

    LaunchedEffect(playToken) {
        if (!playing) return@LaunchedEffect
        delay(200)
        for (i in story.sentences.indices) {
            current = i
            listState.animateScrollToItem(i)
            speaker.speakEn(story.sentences[i].en)
            delay(500)
        }
        current = -1
        playing = false
    }

    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Pill(if (playing) "⏹ Стоп" else "▶ Слушать историю", {
            playing = !playing
            if (!playing) {
                current = -1
                speaker.stop()
            }
            playToken++
        }, selected = playing, grad = StoryGrad)
        Pill("Перевод", { showRu = !showRu }, selected = showRu, grad = StoryGrad)
        Pill("❓ К вопросам", onQuestions)
    }
    Spacer(Modifier.height(10.dp))

    LazyColumn(Modifier.weight(1f), state = listState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(story.sentences) { i, sentence ->
            val active = current == i
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    .padding(14.dp)
            ) {
                FlowRow(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    sentence.en.split(" ").forEach { w ->
                        val selected = word != null && Dictionary.clean(w) == Dictionary.clean(word!!)
                        Text(
                            "$w ",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (selected) StoryGrad.b else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    word = w
                                    added = null
                                    speaker.say(Dictionary.clean(w))
                                }
                        )
                    }
                }
                if (showRu) {
                    Spacer(Modifier.height(4.dp))
                    Hint(sentence.ru)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("🔊", { speaker.say(sentence.en) })
                    Pill("🐢", { speaker.say(sentence.en, slow = true) })
                    val inMine = MyWords.contains(sentence.en)
                    Pill(if (inMine) "⭐ В моих" else "＋ Фразу", {
                        if (!inMine) MyWords.add(sentence.en, sentence.ru)
                    })
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }

    // Карточка выбранного слова
    val w = word
    if (w != null) {
        val clean = Dictionary.clean(w)
        val ru = Dictionary.lookup(w, story.glossary)
        Spacer(Modifier.height(8.dp))
        SoftCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(clean, style = MaterialTheme.typography.headlineSmall, color = StoryGrad.b)
                    Text(ru ?: "нет в словаре — посмотри перевод фразы", style = MaterialTheme.typography.bodyLarge)
                }
                CircleButton("✕", { word = null })
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("🔊 Слушать", { speaker.say(clean) })
                val inMine = MyWords.contains(clean)
                Pill(
                    when {
                        added == clean || inMine -> "⭐ В моих словах"
                        else -> "＋ В мои слова"
                    },
                    {
                        if (!inMine && MyWords.add(clean, ru ?: "")) added = clean
                    },
                    selected = inMine,
                    grad = StoryGrad
                )
            }
        }
    }
}

@Composable
private fun StoryQuestions(story: Story, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var verdict by remember(index) { mutableStateOf<Boolean?>(null) }
    var heardText by remember(index) { mutableStateOf("") }
    var showText by remember { mutableStateOf(true) }
    var saved by remember { mutableStateOf(false) }
    val recognizer = rememberRecognizer()
    val grad = StoryGrad

    DisposableEffect(Unit) { onDispose { recognizer.cancel() } }

    if (index >= story.questions.size) {
        val percent = correct * 100 / story.questions.size
        LaunchedEffect(Unit) {
            if (!saved) {
                saved = true
                store.saveStoryResult(story.id, percent)
            }
        }
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))
            ProgressRing(percent / 100f, 160.dp, 14.dp, MaterialTheme.colorScheme.surfaceVariant, grad.a) {
                Text("$percent%", style = MaterialTheme.typography.headlineLarge)
            }
            Spacer(Modifier.height(20.dp))
            BigText(if (percent >= 80) "История понята! 🎉" else "Хорошая попытка 💪")
            Hint("Ответов засчитано: $correct из ${story.questions.size} · +30 опыта", center = true)
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("Ещё раз", {
                    index = 0
                    correct = 0
                    saved = false
                }, Modifier.weight(1f))
                GradientButton("Готово", onExit, Modifier.weight(1f), grad = grad)
            }
        }
        return
    }

    val question = story.questions[index]
    LaunchedEffect(index) {
        delay(350)
        speaker.speakEn(question.q)
    }

    fun judge(alternatives: List<String>) {
        val heard = alternatives.flatMap { canonWords(it) }.toSet()
        val keyHit = question.keys.any { k -> canonWords(k).all { it in heard } }
        val ok = keyHit || scoreSpeech(question.answer, alternatives).score >= 0.7f
        heardText = alternatives.firstOrNull().orEmpty()
        verdict = ok
        if (ok) correct++
        speaker.say(question.answer)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PillProgress(index.toFloat() / story.questions.size, Modifier.fillMaxWidth(), grad)
        Spacer(Modifier.height(14.dp))
        SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
            Hint("Вопрос ${index + 1} из ${story.questions.size} · ответь голосом", center = true)
            Spacer(Modifier.height(10.dp))
            BigText(if (showText) question.q else "🔊 · · ·")
            Spacer(Modifier.height(4.dp))
            Hint(question.ru, center = true)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("🔊 Вопрос", { speaker.say(question.q) })
                Pill("🐢", { speaker.say(question.q, slow = true) })
                Pill(if (showText) "Скрыть текст" else "Показать текст", { showText = !showText })
            }
        }
        Spacer(Modifier.height(12.dp))

        val v = verdict
        if (v != null) {
            ResultBanner(v, if (v) "Засчитано! 🎉" else "Не совсем") {
                if (heardText.isNotBlank()) Hint("Ты сказал: «$heardText»")
                Spacer(Modifier.height(6.dp))
                Text("Образец: ${question.answer}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                SpeakRow(question.answer, speaker)
            }
            Spacer(Modifier.height(16.dp))
            GradientButton("Дальше", { index++ }, Modifier.fillMaxWidth(), grad = grad)
        } else {
            MicButton(recognizer, speaker, grad) { heard -> judge(heard) }
            Spacer(Modifier.height(8.dp))
            SoftButton("Не знаю — покажи ответ", {
                verdict = false
                speaker.say(question.answer)
            }, Modifier.fillMaxWidth(), height = 48.dp)
        }
    }
}

/** Цвет для слов в истории, когда нет фона. */
@Suppress("unused")
private val Transparent = Color.Transparent
