@file:OptIn(ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SessionScreen(s: Screen.Session, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val queue = remember(s) { mutableStateListOf<Item>().apply { addAll(s.items) } }
    val retried = remember(s) { mutableSetOf<String>() }
    var index by remember(s) { mutableIntStateOf(0) }
    var correct by remember(s) { mutableIntStateOf(0) }
    var answered by remember(s) { mutableIntStateOf(0) }
    val recognizer = rememberRecognizer()

    DisposableEffect(Unit) {
        onDispose {
            speaker.stop()
            recognizer.cancel()
        }
    }

    fun onResult(item: Item, ok: Boolean) {
        store.answer(item.id, ok)
        answered++
        if (ok) {
            correct++
        } else if (retried.add(item.id)) {
            // Ошибся — карточка вернётся в конце этого же занятия
            queue.add(item)
        }
    }

    Column(Modifier.fillMaxSize()) {
        SessionBar(
            onExit = onExit,
            progress = if (queue.isEmpty()) 1f else index.toFloat() / queue.size,
            counter = if (index < queue.size) "${index + 1}/${queue.size}" else null,
            grad = s.mode.grad
        )

        if (s.items.isEmpty()) {
            Spacer(Modifier.weight(1f))
            BigText("Здесь пока пусто")
            Spacer(Modifier.height(8.dp))
            Hint("Добавь слова в «⭐ Мои слова»: в разговорнике или в историях — нажми на слово и «＋ В мои слова».", center = true)
            Spacer(Modifier.weight(1f))
            GradientButton("На главную", onExit, Modifier.fillMaxWidth(), grad = s.mode.grad)
        } else if (index >= queue.size) {
            FinishView(s.mode, correct, answered, onExit)
        } else {
            val item = queue[index]
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(s.mode.emoji, s.mode.grad, 34.dp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(s.mode.title, style = MaterialTheme.typography.titleMedium)
                    Hint(if (item.group.isNotEmpty()) item.group else s.deck.title)
                }
            }
            Spacer(Modifier.height(18.dp))
            key(index) {
                val next: () -> Unit = { index++ }
                val answer: (Boolean) -> Unit = { ok -> onResult(item, ok) }
                when (s.mode) {
                    Mode.RECALL -> RecallCard(item, speaker, s.mode.grad) { ok -> answer(ok); next() }
                    Mode.BUILD -> BuildCard(item, speaker, s.mode.grad, answer, next)
                    Mode.LISTEN -> ListenCard(item, s.deck.items, speaker, s.mode.grad, answer, next)
                    Mode.DICTATION -> DictationCard(item, speaker, s.mode.grad, answer, next)
                    Mode.SPEAK -> SpeakCard(item, speaker, recognizer, s.mode.grad, repeat = false, answer, next)
                    Mode.REPEAT -> SpeakCard(item, speaker, recognizer, s.mode.grad, repeat = true, answer, next)
                    Mode.AUDIO -> Text("Аудиоурок открывается отдельно")
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.FinishView(mode: Mode, correct: Int, answered: Int, onExit: () -> Unit) {
    val ratio = if (answered == 0) 1f else correct.toFloat() / answered
    Spacer(Modifier.weight(1f))
    Box(Modifier.align(Alignment.CenterHorizontally)) {
        ProgressRing(ratio, 160.dp, 14.dp, MaterialTheme.colorScheme.surfaceVariant, mode.grad.a) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(ratio * 100).toInt()}%", style = MaterialTheme.typography.headlineLarge)
                Hint("верно")
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    BigText(
        when {
            ratio >= 0.9f -> "Отлично! 🎉"
            ratio >= 0.6f -> "Хорошая работа! 💪"
            else -> "Ошибки — это нормально 🙂"
        }
    )
    Spacer(Modifier.height(6.dp))
    Hint("Правильно: $correct из $answered. Ошибки вернутся на повторение.", center = true)
    Spacer(Modifier.weight(1f))
    GradientButton("На главную", onExit, Modifier.fillMaxWidth(), grad = mode.grad)
}

/** Большая карточка с фразой. */
@Composable
private fun PromptCard(label: String, text: String, sub: String? = null) {
    SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
        Hint(label, center = true)
        Spacer(Modifier.height(10.dp))
        BigText(text)
        if (sub != null) {
            Spacer(Modifier.height(8.dp))
            Hint(sub, center = true)
        }
    }
}

// ---------- ⚡ Вспомни быстро ----------

@Composable
private fun RecallCard(item: Item, speaker: Speaker, grad: Grad, onDone: (Boolean) -> Unit) {
    val totalMs = ((3000L + 600L * item.en.split(" ").size) * CurrentLevel.timerFactor).toLong()
    var revealed by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    var left by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (!revealed) {
            val elapsed = System.currentTimeMillis() - start
            left = (1f - elapsed.toFloat() / totalMs).coerceAtLeast(0f)
            if (elapsed >= totalMs) {
                revealed = true
                break
            }
            delay(40)
        }
    }
    LaunchedEffect(revealed) {
        if (revealed) speaker.say(item.en)
    }

    fun finish(ok: Boolean) {
        if (done) return
        done = true
        onDone(ok)
    }

    Column(Modifier.fillMaxSize()) {
        PromptCard("Как сказать по-английски?", item.ru)
        Spacer(Modifier.height(20.dp))

        if (!revealed) {
            PillProgress(left, Modifier.fillMaxWidth(), if (left < 0.3f) Grads.Speak else grad, height = 10.dp)
            Spacer(Modifier.height(10.dp))
            Hint("Скажи вслух, потом проверь себя", center = true)
            Spacer(Modifier.weight(1f))
            GradientButton("Показать ответ", { revealed = true }, Modifier.fillMaxWidth(), grad = grad)
        } else {
            BigText(item.en, color = grad.a)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.align(Alignment.CenterHorizontally)) { SpeakRow(item.en, speaker) }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("Не вспомнил", { finish(false) }, Modifier.weight(1f))
                GradientButton("Вспомнил", { finish(true) }, Modifier.weight(1f), grad = grad)
            }
        }
    }
}

// ---------- 🧩 Собери фразу ----------

@Composable
private fun BuildCard(item: Item, speaker: Speaker, grad: Grad, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    val tokens = remember(item) { item.en.split(" ").filter { it.isNotBlank() } }
    val pool = remember(item) {
        val original = tokens.indices.toList()
        var p = original.shuffled()
        if (tokens.size > 1) {
            while (p == original) p = original.shuffled()
        }
        p
    }
    val chosen = remember(item) { mutableStateListOf<Int>() }
    var result by remember(item) { mutableStateOf<Boolean?>(null) }

    Column(Modifier.fillMaxSize()) {
        PromptCard("Собери по-английски", item.ru)
        Spacer(Modifier.height(16.dp))

        val shape = RoundedCornerShape(20.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    2.dp,
                    when (result) {
                        true -> Good
                        false -> Bad
                        null -> Color.Transparent
                    },
                    shape
                )
                .padding(10.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chosen.toList().forEach { i ->
                    WordChip(tokens[i], enabled = result == null) { chosen.remove(i) }
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pool.filter { it !in chosen }.forEach { i ->
                WordChip(tokens[i], enabled = result == null) {
                    chosen.add(i)
                    speaker.say(tokens[i])
                }
            }
        }

        Spacer(Modifier.weight(1f))

        val r = result
        if (r == null) {
            GradientButton(
                "Проверить",
                {
                    val ok = wordsOf(chosen.joinToString(" ") { tokens[it] }) == wordsOf(item.en)
                    result = ok
                    onAnswer(ok)
                    speaker.say(item.en)
                },
                Modifier.fillMaxWidth(),
                grad = grad,
                enabled = chosen.size == tokens.size
            )
        } else {
            ResultBanner(r, if (r) "Верно! 🎉" else "Правильно так:") {
                if (!r) Text(item.en, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(10.dp))
                SpeakRow(item.en, speaker)
            }
            Spacer(Modifier.height(12.dp))
            GradientButton("Дальше", onNext, Modifier.fillMaxWidth(), grad = grad)
        }
    }
}

// ---------- 👂 На слух ----------

@Composable
private fun ListenCard(
    item: Item,
    all: List<Item>,
    speaker: Speaker,
    grad: Grad,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
) {
    val options = remember(item) {
        val others = all.filter { it.ru != item.ru }.shuffled().distinctBy { it.ru }.take(3)
        (others + item).shuffled()
    }
    var picked by remember(item) { mutableStateOf<Item?>(null) }

    LaunchedEffect(item) {
        delay(400)
        speaker.say(item.en)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
            Hint("Послушай и выбери перевод", center = true)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.align(Alignment.CenterHorizontally)) { SpeakRow(item.en, speaker) }
            if (picked != null) {
                Spacer(Modifier.height(14.dp))
                BigText(item.en, color = grad.a, style = MaterialTheme.typography.headlineSmall)
            }
        }
        Spacer(Modifier.height(16.dp))

        options.forEach { opt ->
            val p = picked
            val state = when {
                p == null -> 0
                opt == item -> 1
                opt == p -> 2
                else -> 0
            }
            val shape = RoundedCornerShape(18.dp)
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(shape)
                    .background(
                        when (state) {
                            1 -> Good
                            2 -> Bad
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                    .clickable(enabled = p == null) {
                        picked = opt
                        onAnswer(opt == item)
                    }
                    .padding(18.dp)
            ) {
                Text(
                    opt.ru,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state == 0) MaterialTheme.colorScheme.onSurface else Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            GradientButton("Дальше", onNext, Modifier.fillMaxWidth(), grad = grad)
        }
    }
}

// ---------- ✍️ Диктант ----------

@Composable
private fun DictationCard(item: Item, speaker: Speaker, grad: Grad, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    var text by remember(item) { mutableStateOf("") }
    var result by remember(item) { mutableStateOf<Boolean?>(null) }
    val expected = remember(item) { wordsOf(item.en) }

    LaunchedEffect(item) {
        delay(400)
        speaker.say(item.en)
    }

    fun check(giveUp: Boolean = false) {
        if (result != null) return
        val ok = !giveUp && wordsOf(text) == expected
        result = ok
        onAnswer(ok)
        speaker.say(item.en)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
            Hint("Послушай и напиши по-английски", center = true)
            Hint("Слов: ${expected.size} · включи английскую раскладку", center = true)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.align(Alignment.CenterHorizontally)) { SpeakRow(item.en, speaker) }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { if (result == null) text = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            placeholder = { Text("Пиши то, что слышишь") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (text.isNotBlank()) check() }),
            textStyle = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))

        val r = result
        if (r == null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("Не знаю", { check(giveUp = true) }, Modifier.weight(1f))
                GradientButton("Проверить", { check() }, Modifier.weight(1f), grad = grad, enabled = text.isNotBlank())
            }
        } else {
            ResultBanner(r, if (r) "Всё верно! 🎉" else "Есть ошибки") {
                if (!r && text.isNotBlank()) {
                    val got = wordsOf(text)
                    val annotated = buildAnnotatedString {
                        append("Ты написал: ")
                        got.forEachIndexed { i, w ->
                            val okWord = i < expected.size && expected[i] == w
                            withStyle(SpanStyle(color = if (okWord) Good else Bad, fontWeight = FontWeight.Bold)) { append(w) }
                            append(" ")
                        }
                        if (got.size < expected.size) {
                            withStyle(SpanStyle(color = Bad)) { append("… не хватает слов: ${expected.size - got.size}") }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(annotated, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(8.dp))
                Text(item.en, style = MaterialTheme.typography.titleLarge)
                Hint(item.ru)
            }
            Spacer(Modifier.height(16.dp))
            GradientButton("Дальше", onNext, Modifier.fillMaxWidth(), grad = grad)
        }
    }
}

// ---------- 🎙 Скажи вслух / 🦜 Повтори ----------

/**
 * repeat = false: видишь русскую фразу, говоришь по-английски.
 * repeat = true: слышишь и видишь английскую фразу, повторяешь за диктором.
 * В зачёт идёт первая попытка, повторять можно сколько угодно.
 */
@Composable
private fun SpeakCard(
    item: Item,
    speaker: Speaker,
    recognizer: Recognizer,
    grad: Grad,
    repeat: Boolean,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
) {
    var scored by remember(item) { mutableStateOf<Scored?>(null) }
    var counted by remember(item) { mutableStateOf(false) }
    var revealed by remember(item) { mutableStateOf(repeat) }

    LaunchedEffect(item) {
        if (repeat) {
            delay(400)
            speaker.say(item.en)
        }
    }

    fun count(ok: Boolean) {
        if (counted) return
        counted = true
        onAnswer(ok)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (repeat) {
            SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
                Hint("Послушай и повтори так же", center = true)
                Spacer(Modifier.height(10.dp))
                BigText(item.en)
                Spacer(Modifier.height(6.dp))
                Hint(item.ru, center = true)
                Spacer(Modifier.height(12.dp))
                Box(Modifier.align(Alignment.CenterHorizontally)) { SpeakRow(item.en, speaker) }
            }
        } else {
            PromptCard("Скажи по-английски", item.ru)
        }

        Spacer(Modifier.height(12.dp))

        val sc = scored
        if (sc != null) {
            ResultBanner(sc.ok, if (sc.ok) "Отлично! ${sc.percent}% 🎉" else "Почти! ${sc.percent}%") {
                Spacer(Modifier.height(8.dp))
                ScoredText(sc)
                Spacer(Modifier.height(6.dp))
                Hint("Услышал: «${sc.heard}»")
                Spacer(Modifier.height(10.dp))
                SpeakRow(item.en, speaker)
            }
            Spacer(Modifier.height(4.dp))
        } else if (!repeat && revealed) {
            BigText(item.en, color = grad.a)
        }

        MicButton(recognizer, speaker, grad) { heard ->
            val result = scoreSpeech(item.en, heard)
            scored = result
            count(result.ok)
            speaker.say(item.en)
        }

        Spacer(Modifier.height(12.dp))
        if (counted) {
            GradientButton("Дальше", onNext, Modifier.fillMaxWidth(), grad = grad)
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!repeat) {
                    SoftButton("Подсказка", {
                        revealed = true
                        speaker.say(item.en)
                        count(false)
                    }, Modifier.weight(1f))
                }
                SoftButton("Пропустить", {
                    count(false)
                    onNext()
                }, Modifier.weight(1f))
            }
        }
        if (counted) {
            Spacer(Modifier.height(8.dp))
            Hint("Можно нажать на микрофон и попробовать ещё раз", center = true)
        }
    }
}
