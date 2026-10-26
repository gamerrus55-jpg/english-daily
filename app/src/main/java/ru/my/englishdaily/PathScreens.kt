package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val PathGrad get() = Grads.Hero

private fun bandGrad(band: String): Grad = when (band) {
    "A1" -> Grads.Build
    "A2" -> Grads.Repeat
    "B1" -> Grads.Audio
    "B2" -> Grads.Dictation
    else -> Grads.Speak
}

// ---------- Карта курса ----------

@Composable
fun PathMapScreen(store: ProgressStore, onOpen: (PathUnit) -> Unit, onExit: () -> Unit) {
    @Suppress("UNUSED_VARIABLE") val v = PathStore.version
    val current = remember { PathProgress.current(store) }
    val listState = rememberLazyListState()
    var lockedHint by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Прокручиваем к текущему уроку (+ заголовки уровней)
        val idx = PathPlan.units.indexOf(current) + PathPlan.bands.indexOf(current.band) + 2
        listState.scrollToItem((idx - 1).coerceAtLeast(0))
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("🗺 Курс A1 → C1", onExit, right = "${PathPlan.units.count { PathProgress.done(store, it) }}/${PathPlan.units.size}")
        lockedHint?.let {
            Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
        }
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Как устроен курс", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        "80 уроков от A1 до C1. В каждом: живые слова с примерами, грамматика с упражнениями и диалог. " +
                            "Содержимое урока создаёт ИИ, когда ты его открываешь, и сохраняет на телефоне. " +
                            "Прошёл три шага — открывается следующий урок. Один урок в день — и через несколько месяцев ты на другом уровне."
                    )
                }
            }
            PathPlan.bands.forEach { band ->
                item(key = "band_$band") {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)) {
                        IconTile(band, bandGrad(band), 36.dp)
                        Spacer(Modifier.width(10.dp))
                        val units = PathPlan.units.filter { it.band == band }
                        Text(
                            "Уровень $band · ${units.count { PathProgress.done(store, it) }}/${units.size}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                items(PathPlan.units.filter { it.band == band }, key = { it.id }) { unit ->
                    val unlocked = PathProgress.unlocked(store, unit)
                    val done = PathProgress.done(store, unit)
                    val steps = PathProgress.stepsDone(store, unit)
                    val isCurrent = unit == current
                    SoftCard(
                        Modifier
                            .fillMaxWidth()
                            .alpha(if (unlocked) 1f else 0.5f),
                        onClick = {
                            if (unlocked) onOpen(unit)
                            else lockedHint = "Урок ${unit.n} откроется, когда пройдёшь урок ${unit.n - 1}."
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconTile(if (unlocked) unit.emoji else "🔒", if (isCurrent) PathGrad else bandGrad(unit.band), 48.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${unit.n}. ${unit.title}", style = MaterialTheme.typography.titleMedium)
                                Hint(unit.grammar)
                                if (unlocked && !done && steps > 0) {
                                    Spacer(Modifier.height(4.dp))
                                    PillProgress(steps / 3f, Modifier.fillMaxWidth(), bandGrad(unit.band), height = 6.dp)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                when {
                                    done -> "✅"
                                    isCurrent -> "▶"
                                    unlocked -> "$steps/3"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ---------- Урок ----------

@Composable
fun PathUnitScreen(
    unit: PathUnit,
    store: ProgressStore,
    speaker: Speaker,
    onLearn: (Mode, List<Item>) -> Unit,
    onGrammar: (GrammarLesson) -> Unit,
    onDialog: (Dialogue) -> Unit,
    onRoleplay: (Dialogue) -> Unit,
    onNext: (PathUnit) -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit,
) {
    @Suppress("UNUSED_VARIABLE") val v = PathStore.version
    val scope = rememberCoroutineScope()
    var working by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var openedWord by remember { mutableStateOf<String?>(null) }
    val grad = bandGrad(unit.band)

    val vocab = PathStore.vocab(unit)
    val lesson = PathStore.grammar(unit)
    val dialogue = PathStore.dialogue(unit)

    fun generate(parts: List<String>) {
        if (working != null) return
        error = null
        scope.launch {
            for (part in parts) {
                if (PathStore.has(unit, part) && part !in listOf("regen_grammar", "regen_dialog")) continue
                working = when (part) {
                    "vocab" -> "Подбираю слова…"
                    "grammar" -> "Пишу грамматику и упражнения…"
                    else -> "Сочиняю диалог…"
                }
                val r = PathGen.generate(store, unit, part)
                if (r.isFailure) {
                    error = r.exceptionOrNull()?.message
                    break
                }
            }
            working = null
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("Урок ${unit.n} · ${unit.band}", onExit)

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(grad.brush)
                .padding(20.dp)
        ) {
            Text(unit.emoji, style = MaterialTheme.typography.displaySmall)
            Text(unit.title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text("Грамматика: ${unit.grammar}", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            val steps = PathProgress.stepsDone(store, unit)
            PillProgress(steps / 3f, Modifier.fillMaxWidth(), Grads.Book, height = 8.dp)
            Spacer(Modifier.height(4.dp))
            Text("Пройдено шагов: $steps из 3", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }

        val missing = listOf("vocab", "grammar", "dialog").filter { !PathStore.has(unit, it) }
        if (missing.isNotEmpty()) {
            SoftCard(Modifier.fillMaxWidth()) {
                Text(if (missing.size == 3) "Урок ещё не создан" else "Урок создан не полностью", style = MaterialTheme.typography.titleMedium)
                Hint("ИИ подготовит слова, грамматику и диалог именно под этот урок и твой уровень. Это займёт около минуты, потом урок сохранится на телефоне.")
                Spacer(Modifier.height(10.dp))
                if (!store.aiReady) {
                    Text("Нужен бесплатный ключ GigaChat.", color = Bad, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    GradientButton("Открыть настройки", onSettings, Modifier.fillMaxWidth())
                } else {
                    GradientButton(working ?: "✨ Создать урок", { generate(listOf("vocab", "grammar", "dialog")) }, Modifier.fillMaxWidth(), grad = grad, enabled = working == null)
                }
            }
        }
        if (working != null && missing.isEmpty()) Hint(working!!)
        error?.let { Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium) }

        // Шаг 1: слова
        if (vocab.isNotEmpty()) {
            val (learned, total) = PathProgress.vocabCount(store, unit)
            StepHeader(1, "Слова урока", PathProgress.vocabDone(store, unit), "$learned из $total отработано")
            SoftCard(Modifier.fillMaxWidth()) {
                Hint("Нажми на слово — услышишь его и увидишь пример. Потом потренируй: шаг засчитается, когда отработаешь 80% слов.")
                Spacer(Modifier.height(6.dp))
                vocab.forEach { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                speaker.say(item.en)
                                openedWord = if (openedWord == item.id) null else item.id
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.en, style = MaterialTheme.typography.titleMedium)
                            PronLine(item.en)
                            Hint(item.ru)
                        }
                        Text(if (store.box(item.id) >= 1) "✅" else "🔊")
                    }
                    if (openedWord == item.id) UsageBlock(item, speaker, store)
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton("⚡ Учить", { onLearn(Mode.RECALL, vocab.shuffled()) }, Modifier.weight(1f), grad = grad)
                    SoftButton("🎧 Аудио", { onLearn(Mode.AUDIO, vocab) }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SoftButton("👂 На слух", { onLearn(Mode.LISTEN, vocab.shuffled()) }, Modifier.weight(1f), height = 48.dp)
                    SoftButton("🎙 Вслух", { onLearn(Mode.SPEAK, vocab.shuffled()) }, Modifier.weight(1f), height = 48.dp)
                }
            }
        }

        // Шаг 2: грамматика
        if (lesson != null) {
            StepHeader(2, "Грамматика", PathProgress.grammarDone(store, unit), store.grammarBest(lesson.id).let { if (it >= 0) "лучший результат $it%" else "нужно 60%+" })
            SoftCard(Modifier.fillMaxWidth(), onClick = { onGrammar(lesson) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile("📐", Grads.Build, 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                        Hint("Правило, таблица, примеры и ${lesson.tasks.size} упражнений")
                    }
                    Text("›", style = MaterialTheme.typography.headlineSmall)
                }
            }
            if (!PathProgress.grammarDone(store, unit)) {
                Pill("↻ Создать другой вариант", { PathStore.delete(unit, "grammar"); generate(listOf("grammar")) })
            }
        }

        // Шаг 3: диалог
        if (dialogue != null) {
            StepHeader(3, "Диалог", PathProgress.dialogDone(store, unit), "прослушай целиком")
            SoftCard(Modifier.fillMaxWidth()) {
                Text(dialogue.title, style = MaterialTheme.typography.titleMedium)
                Hint("${dialogue.lines.size} реплик · два голоса · можно сыграть роль")
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton("▶ Слушать", { onDialog(dialogue) }, Modifier.weight(1f), grad = Grads.Dialogs)
                    SoftButton("🎭 Сыграть", { onRoleplay(dialogue) }, Modifier.weight(1f))
                }
            }
            if (!PathProgress.dialogDone(store, unit)) {
                Pill("↻ Другой диалог", { PathStore.delete(unit, "dialog"); generate(listOf("dialog")) })
            }
        }

        if (PathProgress.done(store, unit)) {
            val next = PathPlan.units.getOrNull(unit.n)
            SoftCard(Modifier.fillMaxWidth()) {
                BigText("Урок пройден! 🎉")
                Hint("Слова урока будут возвращаться на повторение в колоде «🗺 Слова из уроков».", center = true)
                if (next != null) {
                    Spacer(Modifier.height(10.dp))
                    GradientButton("Дальше: ${next.n}. ${next.title}", { onNext(next) }, Modifier.fillMaxWidth(), grad = bandGrad(next.band))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StepHeader(n: Int, title: String, done: Boolean, hint: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
        Text(if (done) "✅" else "$n️⃣", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        Hint(hint)
    }
}

/** Карточка «Сегодня» на главном экране. */
@Composable
fun TodayCard(store: ProgressStore, dueCount: Int, onOpen: (PathUnit) -> Unit, onReview: () -> Unit) {
    @Suppress("UNUSED_VARIABLE") val v = PathStore.version
    val unit = PathProgress.current(store)
    val steps = PathProgress.stepsDone(store, unit)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(bandGrad(unit.band).brush)
            .clickable { onOpen(unit) }
            .padding(20.dp)
    ) {
        Text("📅 На сегодня", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(unit.emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Урок ${unit.n}: ${unit.title}", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text("${unit.band} · ${unit.grammar}", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        PillProgress(steps / 3f, Modifier.fillMaxWidth(), Grads.Book, height = 8.dp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .clickable { onOpen(unit) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) { Text(if (steps == 0) "▶ Начать урок" else "▶ Продолжить · $steps/3", color = Color(0xFF1E1B4B), style = MaterialTheme.typography.labelLarge) }
            if (dueCount > 0) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onReview)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) { Text("🔁 Повторить $dueCount", color = Color.White, style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
}
