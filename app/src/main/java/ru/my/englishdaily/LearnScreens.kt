package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val GrammarGrad get() = Grads.Build
private val SoundGrad get() = Grads.Repeat

// ---------- Список уроков грамматики ----------

@Composable
fun GrammarListScreen(store: ProgressStore, onOpen: (GrammarLesson) -> Unit, onExit: () -> Unit) {
    val levelTitles = mapOf(1 to "Основа", 2 to "Следующий шаг", 3 to "Продвинутый")
    Column(Modifier.fillMaxSize()) {
        TopBar("📐 Грамматика", onExit, right = "${Grammar.all.size} уроков")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Метод «шаг за шагом»", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        "Как в учебнике Бонк: короткое правило → таблица → примеры → много упражнений. " +
                            "Главное — трансформации: из утверждения делаешь вопрос и отрицание, пока не станет автоматом. " +
                            "Отвечай голосом или текстом."
                    )
                }
            }
            Grammar.all.groupBy { it.level }.forEach { (lvl, lessons) ->
                item {
                    Text(
                        levelTitles[lvl] ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(lessons, key = { it.id }) { lesson ->
                    val best = store.grammarBest(lesson.id)
                    val number = Grammar.all.indexOf(lesson) + 1
                    SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(lesson) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconTile(lesson.emoji, GrammarGrad, 48.dp)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text("$number. ${lesson.title}", style = MaterialTheme.typography.titleMedium)
                                Hint(if (best >= 0) "Лучший результат: $best%" else "${lesson.tasks.size} упражнений")
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
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ---------- Урок грамматики ----------

@Composable
fun GrammarLessonScreen(lesson: GrammarLesson, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    var practicing by remember { mutableStateOf(false) }
    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    if (practicing) {
        GrammarPractice(lesson, speaker, store, onExit = { practicing = false })
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("${lesson.emoji} ${lesson.title}", onExit)
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Правило", style = MaterialTheme.typography.titleMedium, color = GrammarGrad.b)
            Spacer(Modifier.height(6.dp))
            Text(lesson.explain, style = MaterialTheme.typography.bodyLarge)
        }
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Таблица", style = MaterialTheme.typography.titleMedium, color = GrammarGrad.b)
            Spacer(Modifier.height(6.dp))
            lesson.table.forEach { (en, ru) ->
                Row(Modifier.padding(vertical = 4.dp)) {
                    Text(en, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1.2f))
                    Spacer(Modifier.width(8.dp))
                    Hint(ru)
                }
            }
        }
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Примеры · нажми, чтобы послушать", style = MaterialTheme.typography.titleMedium, color = GrammarGrad.b)
            Spacer(Modifier.height(6.dp))
            lesson.examples.forEach { e ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { speaker.say(e.en) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(e.en, style = MaterialTheme.typography.titleMedium)
                        PronLine(e.en)
                        Hint(e.ru)
                    }
                    Text("🔊")
                }
            }
        }
        GradientButton("Упражнения · ${lesson.tasks.size}", { practicing = true }, Modifier.fillMaxWidth(), grad = GrammarGrad)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftButton("＋ Примеры в «Мои слова»", {
                lesson.examples.forEach { MyWords.add(it.en, it.ru) }
            }, Modifier.weight(1f), height = 48.dp)
        }
        Spacer(Modifier.height(12.dp))
    }
}

/** Поле ответа: можно напечатать или надиктовать. */
@Composable
private fun AnswerInput(
    value: String,
    onValue: (String) -> Unit,
    enabled: Boolean,
    recognizer: Recognizer,
    speaker: Speaker,
    grad: Grad,
    onSubmit: () -> Unit,
    onError: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ClearableField(
            value = value,
            onValue = { if (enabled) onValue(it) },
            placeholder = "Напиши или нажми 🎙",
            enabled = enabled,
            onDone = onSubmit,
            modifier = Modifier.weight(1f)
        )
        if (enabled) {
            Spacer(Modifier.width(8.dp))
            SmallMic(recognizer, speaker, grad, onText = onValue, onError = onError)
        }
    }
    if (recognizer.state == Recognizer.State.LISTENING) {
        Hint(if (recognizer.partial.isNotBlank()) "«${recognizer.partial}»" else "Говори по-английски…")
    }
}

@Composable
private fun GrammarPractice(lesson: GrammarLesson, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val tasks = remember(lesson) { lesson.tasks }
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var answer by remember(index) { mutableStateOf("") }
    var verdict by remember(index) { mutableStateOf<Boolean?>(null) }
    var picked by remember(index) { mutableIntStateOf(-1) }
    var error by remember(index) { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }
    val recognizer = rememberRecognizer()
    val grad = GrammarGrad
    val scope = rememberCoroutineScope()
    var judging by remember(index) { mutableStateOf(false) }
    var aiComment by remember(index) { mutableStateOf<String?>(null) }
    var selfAccepted by remember(index) { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { recognizer.cancel() } }

    Column(Modifier.fillMaxSize()) {
        SessionBar(
            onExit = onExit,
            progress = index.toFloat() / tasks.size,
            counter = if (index < tasks.size) "${index + 1}/${tasks.size}" else null,
            grad = grad
        )

        if (index >= tasks.size) {
            val percent = correct * 100 / tasks.size
            LaunchedEffect(Unit) {
                if (!saved) {
                    saved = true
                    store.saveGrammarResult(lesson.id, percent)
                }
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.align(Alignment.CenterHorizontally)) {
                ProgressRing(percent / 100f, 160.dp, 14.dp, MaterialTheme.colorScheme.surfaceVariant, grad.a) {
                    Text("$percent%", style = MaterialTheme.typography.headlineLarge)
                }
            }
            Spacer(Modifier.height(20.dp))
            BigText(if (percent >= 80) "Тема усвоена! 🎉" else "Повтори урок ещё раз 💪")
            Hint("Верно: $correct из ${tasks.size} · +25 опыта", center = true)
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("Ещё раз", {
                    index = 0
                    correct = 0
                    saved = false
                }, Modifier.weight(1f))
                GradientButton("К уроку", onExit, Modifier.weight(1f), grad = grad)
            }
        } else {

        val task = tasks[index]
        val expected: String = when (task) {
            is Task.Transform -> task.answer
            is Task.Translate -> task.answer
            is Task.Choice -> task.sentence.replace("___", task.options[task.correct].let { if (it == "-") "" else it })
                .replace("  ", " ").replace(" '", "'")
        }

        fun finish(ok: Boolean, comment: String? = null) {
            verdict = ok
            aiComment = comment
            if (ok) correct++
            speaker.say(if (ok) answer else Grammar.firstAnswer(expected))
        }

        fun check() {
            if (verdict != null || answer.isBlank() || judging) return
            // 1) Совпало с одним из образцов
            if (Grammar.accepts(answer, expected)) return finish(true)
            // 2) Проверка по смыслу через ИИ — правильных вариантов может быть много
            if (store.aiReady) {
                judging = true
                scope.launch {
                    val r = AnswerJudge.judge(store, task, expected, answer)
                    judging = false
                    r.onSuccess { (ok, comment) -> finish(ok, comment) }
                        .onFailure { finish(AnswerJudge.closeEnough(answer, expected)) }
                }
                return
            }
            // 3) Без ИИ — засчитываем, если ответ почти совпадает
            finish(AnswerJudge.closeEnough(answer, expected))
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
                when (task) {
                    is Task.Transform -> {
                        Hint(task.task, center = true)
                        Spacer(Modifier.height(10.dp))
                        BigText(task.from, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.align(Alignment.CenterHorizontally)) { Pill("🔊", { speaker.say(task.from) }) }
                    }
                    is Task.Translate -> {
                        Hint("Переведи на английский", center = true)
                        Spacer(Modifier.height(10.dp))
                        BigText(task.ru, style = MaterialTheme.typography.headlineSmall)
                    }
                    is Task.Choice -> {
                        Hint("Выбери, что вставить · ${task.ru}", center = true)
                        Spacer(Modifier.height(10.dp))
                        BigText(task.sentence, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            if (task is Task.Choice) {
                task.options.forEachIndexed { i, opt ->
                    val state = when {
                        picked < 0 -> 0
                        i == task.correct -> 1
                        i == picked -> 2
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
                            .clickable(enabled = picked < 0) {
                                picked = i
                                val ok = i == task.correct
                                verdict = ok
                                if (ok) correct++
                                speaker.say(expected)
                            }
                            .padding(18.dp)
                    ) {
                        Text(
                            if (opt == "-") "— (ничего)" else opt,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state == 0) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }
                }
            } else {
                AnswerInput(
                    value = answer,
                    onValue = { answer = it },
                    enabled = verdict == null,
                    recognizer = recognizer,
                    speaker = speaker,
                    grad = grad,
                    onSubmit = { check() },
                    onError = { error = it }
                )
                error?.let { Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium) }
            }

            val v = verdict
            if (v != null) {
                Spacer(Modifier.height(12.dp))
                ResultBanner(v, when {
                    selfAccepted -> "Засчитано ✋"
                    v && !Grammar.accepts(answer, expected) -> "Засчитано — твой вариант тоже верный 🎉"
                    v -> "Верно! 🎉"
                    else -> "Не совсем. Образец:"
                }) {
                    aiComment?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(4.dp))
                        Text("🤖 $it", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(4.dp))
                    if (v && !Grammar.accepts(answer, expected)) Hint("Вариант из урока:")
                    Text(Grammar.firstAnswer(expected), style = MaterialTheme.typography.titleLarge)
                    val others = expected.split("|").drop(1).map { it.trim() }.filter { it.isNotEmpty() }
                    if (others.isNotEmpty()) Hint("Тоже верно: " + others.joinToString(" / "))
                    Spacer(Modifier.height(8.dp))
                    SpeakRow(Grammar.firstAnswer(expected), speaker)
                    if (!v && task !is Task.Choice && answer.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Pill("✋ Мой ответ тоже верный — засчитать", {
                            verdict = true
                            selfAccepted = true
                            correct++
                        })
                    }
                }
            }
            if (judging) {
                Spacer(Modifier.height(12.dp))
                Hint("🤖 Проверяю твой вариант по смыслу…", center = true)
            }
        }

        Spacer(Modifier.height(10.dp))
        if (verdict == null && task !is Task.Choice) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("Не знаю", {
                    verdict = false
                    speaker.say(Grammar.firstAnswer(expected))
                }, Modifier.weight(1f))
                GradientButton("Проверить", { check() }, Modifier.weight(1f), grad = grad, enabled = answer.isNotBlank())
            }
        } else if (verdict != null) {
            GradientButton("Дальше", { index++ }, Modifier.fillMaxWidth(), grad = grad)
        }
        }
    }
}

// ---------- Курс звуков ----------

@Composable
fun SoundListScreen(store: ProgressStore, onOpen: (SoundTopic) -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopBar("👂 Звуки", onExit)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Зачем это", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        "В английском есть звуки, которых нет в русском. Пока ухо их не различает, ship и sheep звучат одинаково — " +
                            "и понимать живую речь трудно. Прочитай правило, послушай пары, потом пройди тест на слух."
                    )
                }
            }
            items(Sounds.all, key = { it.id }) { topic ->
                val best = store.soundBest(topic.id)
                SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(topic) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(topic.emoji, SoundGrad, 48.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(topic.title, style = MaterialTheme.typography.titleMedium)
                            Hint(
                                topic.pairs.take(3).joinToString(" · ") { "${it.first.en}/${it.second.en}" } +
                                    if (best >= 0) " · $best%" else ""
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
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SoundTopicScreen(topic: SoundTopic, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    var testing by remember { mutableStateOf(false) }
    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    if (testing) {
        SoundTest(topic, speaker, store, onExit = { testing = false })
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("${topic.emoji} ${topic.title}", onExit)
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Как произносить", style = MaterialTheme.typography.titleMedium, color = SoundGrad.b)
            Spacer(Modifier.height(6.dp))
            Text(topic.rule, style = MaterialTheme.typography.bodyLarge)
        }
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Пары · нажимай и сравнивай", style = MaterialTheme.typography.titleMedium, color = SoundGrad.b)
            Spacer(Modifier.height(8.dp))
            topic.pairs.forEach { (a, b) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(a, b).forEach { w ->
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { speaker.say(w.en, slow = true) }
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔊 ${w.en}", style = MaterialTheme.typography.titleMedium)
                            PronLine(w.en)
                            Hint(w.ru)
                        }
                    }
                }
            }
        }
        GradientButton("Тест на слух", { testing = true }, Modifier.fillMaxWidth(), grad = SoundGrad)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SoundTest(topic: SoundTopic, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val rounds = remember(topic) {
        (topic.pairs + topic.pairs).shuffled().take(10).map { pair -> pair to listOf(true, false).random() }
    }
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var picked by remember(index) { mutableStateOf<Example?>(null) }
    var saved by remember { mutableStateOf(false) }
    val grad = SoundGrad

    Column(Modifier.fillMaxSize()) {
        SessionBar(onExit, index.toFloat() / rounds.size, if (index < rounds.size) "${index + 1}/${rounds.size}" else null, grad)

        if (index >= rounds.size) {
            val percent = correct * 100 / rounds.size
            LaunchedEffect(Unit) {
                if (!saved) {
                    saved = true
                    store.saveSoundResult(topic.id, percent)
                }
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.align(Alignment.CenterHorizontally)) {
                ProgressRing(percent / 100f, 160.dp, 14.dp, MaterialTheme.colorScheme.surfaceVariant, grad.a) {
                    Text("$percent%", style = MaterialTheme.typography.headlineLarge)
                }
            }
            Spacer(Modifier.height(20.dp))
            BigText(if (percent >= 80) "Ухо различает! 🎉" else "Послушай пары ещё раз 👂")
            Spacer(Modifier.weight(1f))
            GradientButton("Готово", onExit, Modifier.fillMaxWidth(), grad = grad)
        } else {

        val (pair, first) = rounds[index]
        val target = if (first) pair.first else pair.second
        LaunchedEffect(index) {
            delay(400)
            speaker.say(target.en)
        }

        Spacer(Modifier.height(10.dp))
        SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
            Hint("Какое слово прозвучало?", center = true)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("🔊 Ещё раз", { speaker.say(target.en) })
                Pill("🐢 Медленно", { speaker.say(target.en, slow = true) })
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(pair.first, pair.second).forEach { w ->
                val p = picked
                val bg = when {
                    p == null -> MaterialTheme.colorScheme.surface
                    w == target -> Good
                    w == p -> Bad
                    else -> MaterialTheme.colorScheme.surface
                }
                Column(
                    Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(bg)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                        .clickable(enabled = p == null) {
                            picked = w
                            if (w == target) correct++
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val onColor = if (p != null && (w == target || w == p)) Color.White else MaterialTheme.colorScheme.onSurface
                    Text(w.en, style = MaterialTheme.typography.headlineSmall, color = onColor)
                    Text(w.ru, style = MaterialTheme.typography.bodyMedium, color = onColor)
                }
            }
        }
        if (picked != null) {
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SoftButton("🔊 ${pair.first.en}", { speaker.say(pair.first.en, slow = true) }, Modifier.weight(1f), height = 48.dp)
                SoftButton("🔊 ${pair.second.en}", { speaker.say(pair.second.en, slow = true) }, Modifier.weight(1f), height = 48.dp)
            }
            Spacer(Modifier.weight(1f))
            GradientButton("Дальше", { index++ }, Modifier.fillMaxWidth(), grad = grad)
        }
        }
    }
}


/** Проверка свободного ответа: ИИ оценивает смысл и грамматику, а не дословное совпадение. */
object AnswerJudge {
    private val system = """
You check answers of a Russian-speaking English learner in a grammar exercise.
Many different answers can be correct. Accept the student's answer if it is grammatically correct, natural,
fulfils the task and keeps the meaning — even if the wording differs from the reference answers.
Small typos that don't change the word are OK. Reject only real grammar mistakes, wrong meaning, or if the task was not done
(for example, the task asked for a question or a specific tense and the student didn't use it).
Answer in EXACTLY this format:
OK: yes or no
COMMENT: one short sentence in Russian — if yes, optionally a tip; if no, what exactly is wrong
""".trimIndent()

    suspend fun judge(store: ProgressStore, task: Task, expected: String, answer: String): Result<Pair<Boolean, String>> {
        val taskText = when (task) {
            is Task.Transform -> "Task: ${task.task}\nSentence: ${task.from}"
            is Task.Translate -> "Task: translate into English\nRussian: ${task.ru}"
            is Task.Choice -> "Task: fill the gap\nSentence: ${task.sentence}"
        }
        val prompt = "$taskText\nReference answers: ${expected.split("|").joinToString(" / ") { it.trim() }}\nStudent's answer: $answer"
        return Ai.ask(store, system, listOf(Ai.Msg("user", prompt)), maxTokens = 150).map { text ->
            val names = listOf("OK", "COMMENT")
            val ok = Ai.field(text, "OK", names).lowercase().startsWith("y")
            ok to Ai.field(text, "COMMENT", names)
        }
    }

    /** Без ИИ: почти дословное совпадение (одно-два слова отличаются). */
    fun closeEnough(answer: String, expected: String): Boolean {
        val a = canonWords(answer)
        if (a.isEmpty()) return false
        return expected.split("|").any { variant ->
            val e = canonWords(variant.trim())
            val common = e.count { it in a }
            e.isNotEmpty() && common >= e.size - 1 && a.size <= e.size + 2 && e.size >= 3
        }
    }
}
