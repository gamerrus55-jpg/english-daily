@file:OptIn(ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val THINK_MS = 10_000

// ---------- Список отрывков ----------

@Composable
fun ReadListScreen(store: ProgressStore, onOpen: (RrPassage) -> Unit, onExit: () -> Unit) {
    val list = remember { ReadRecall.forLang(Lang.active) }
    val now = remember { System.currentTimeMillis() }
    Column(Modifier.fillMaxSize()) {
        TopBar("🧠 Читай и вспоминай", onExit)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Как это работает", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        "1) Прочитай отрывок: новые слова выделены, нажми на слово — увидишь перевод.\n" +
                            "2) Подумай: текст скроется на 10 секунд, попробуй вспомнить слова.\n" +
                            "3) Впиши слова сам — без вариантов. Чем сильнее усилие вспомнить, тем крепче запоминается."
                    )
                }
            }
            list.groupBy { it.level }.forEach { (lvl, ps) ->
                item {
                    Text(
                        ReadRecall.levelNames[lvl] ?: "Уровень $lvl",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                ps.forEach { p ->
                    item(key = p.id) {
                        val due = p.targets.count { store.isNew(it.cardId) || store.dueAt(it.cardId) <= now }
                        SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(p) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconTile(p.emoji, Grads.Recall, 52.dp)
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(p.title, style = MaterialTheme.typography.titleMedium)
                                    Hint("${p.targets.size} слов" + if (due > 0) " · 🔁 повторить: $due" else " · всё свежо")
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ---------- Текст с выделенными словами ----------

private enum class Chip { HIGHLIGHT, BLANK, CURRENT, OK, BAD }

@Composable
private fun RrText(
    p: RrPassage,
    stateOf: (RrSeg.Target) -> Chip,
    shown: (RrSeg.Target) -> String,
    onTap: ((RrSeg.Target) -> Unit)? = null,
) {
    val green = Color(0xFF2E9E5B)
    FlowRow(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        p.segments.forEach { seg ->
            when (seg) {
                is RrSeg.Plain -> Regex("\\s*\\S+\\s*").findAll(seg.text).forEach {
                    Text(it.value, style = MaterialTheme.typography.bodyLarge)
                }
                is RrSeg.Target -> {
                    val color = when (stateOf(seg)) {
                        Chip.HIGHLIGHT, Chip.CURRENT -> MaterialTheme.colorScheme.primary
                        Chip.BLANK -> MaterialTheme.colorScheme.onSurfaceVariant
                        Chip.OK -> green
                        Chip.BAD -> MaterialTheme.colorScheme.error
                    }
                    var mod: Modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.14f))
                    if (onTap != null) mod = mod.clickable { onTap(seg) }
                    Text(
                        shown(seg),
                        color = color,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = mod.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/** Подсказка-лестница: 1 — первая буква, 2 — половина букв. */
private fun hintFor(word: String, level: Int): String {
    val letters = word.count { it.isLetter() }
    val keep = if (level <= 1) 1 else (letters + 1) / 2
    var seen = 0
    return word.map { c ->
        if (!c.isLetter()) c
        else if (seen++ < keep) c else '_'
    }.joinToString(" ")
}

// ---------- Занятие ----------

private enum class Phase { READ, THINK, RECALL, DONE }

@Composable
fun ReadRecallScreen(p: RrPassage, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    var round by remember { mutableStateOf(0) }
    key(round) {
        var phase by remember { mutableStateOf(Phase.READ) }
        val results = remember { mutableStateListOf<Boolean?>().apply { repeat(p.targets.size) { add(null) } } }

        when (phase) {
            Phase.READ -> ReadPhase(p, speaker, onExit) { phase = Phase.THINK }
            Phase.THINK -> ThinkPhase(p, onExit) { phase = Phase.RECALL }
            Phase.RECALL -> RecallPhase(p, speaker, store, results, onExit) { phase = Phase.DONE }
            Phase.DONE -> DonePhase(p, results, speaker, onExit) { round++ }
        }
    }
}

@Composable
private fun ReadPhase(p: RrPassage, speaker: Speaker, onExit: () -> Unit, onNext: () -> Unit) {
    var selected by remember { mutableStateOf<RrSeg.Target?>(null) }
    Column(Modifier.fillMaxSize()) {
        TopBar("${p.emoji} ${p.title}", onExit, right = "1/3 читаем")
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftCard(Modifier.fillMaxWidth()) {
                RrText(p, stateOf = { Chip.HIGHLIGHT }, shown = { it.word }, onTap = { selected = it; speaker.say(it.word) })
            }
            val sel = selected
            if (sel != null) {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text(sel.word, style = MaterialTheme.typography.titleLarge)
                    Text(sel.ru, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Hint("Нажми на выделенное слово — увидишь перевод и услышишь, как оно звучит")
            }
            SoftButton("🔊 Послушать весь текст", { speaker.say(p.plain) }, Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(10.dp))
        GradientButton("Дальше — вспоминать", onNext, Modifier.fillMaxWidth().height(52.dp), grad = Grads.Recall)
    }
}

@Composable
private fun ThinkPhase(p: RrPassage, onExit: () -> Unit, onNext: () -> Unit) {
    var left by remember { mutableStateOf(THINK_MS) }
    LaunchedEffect(Unit) {
        while (left > 0) {
            delay(100)
            left -= 100
        }
    }
    Column(Modifier.fillMaxSize()) {
        TopBar("${p.emoji} ${p.title}", onExit, right = "2/3 думаем")
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("🤔", style = MaterialTheme.typography.displayMedium)
            Text("Подумай, не торопись", style = MaterialTheme.typography.headlineSmall)
            Hint("Текст скрыт. Попробуй вспомнить в уме, какие слова были выделены. Вот их значения:", center = true)
            SoftCard(Modifier.fillMaxWidth()) {
                p.targets.forEach { Text("• ${it.ru}", style = MaterialTheme.typography.titleMedium) }
            }
            PillProgress(1f - left.coerceAtLeast(0) / THINK_MS.toFloat(), Modifier.fillMaxWidth(), Grads.Recall)
        }
        Spacer(Modifier.height(10.dp))
        if (left <= 0) {
            GradientButton("Вспоминать слова", onNext, Modifier.fillMaxWidth().height(52.dp), grad = Grads.Recall)
        } else {
            Hint("Ещё ${left / 1000 + 1} с…", center = true)
        }
    }
}

@Composable
private fun RecallPhase(
    p: RrPassage,
    speaker: Speaker,
    store: ProgressStore,
    results: MutableList<Boolean?>,
    onExit: () -> Unit,
    onFinish: () -> Unit,
) {
    var cur by remember { mutableStateOf(0) }
    var input by remember { mutableStateOf("") }
    var hints by remember { mutableStateOf(0) }
    var wrongs by remember { mutableStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val t = p.targets[cur]

    fun finish(ok: Boolean) {
        results[cur] = ok
        store.answer(t.cardId, ok)
        revealed = true
    }

    fun check() {
        if (input.isBlank() || revealed) return
        if (wordsOf(input) == wordsOf(t.word)) {
            finish(wrongs == 0 && hints <= 1)
            feedback = "✅ Верно!"
            speaker.say(t.word)
        } else {
            wrongs++
            if (wrongs >= 2) {
                finish(false)
                feedback = "Правильно: ${t.word}"
                speaker.say(t.word)
            } else {
                feedback = "Не совсем. Попробуй ещё раз или возьми подсказку."
            }
        }
    }

    fun hint() {
        if (revealed) return
        hints++
        if (hints >= 3) {
            finish(false)
            feedback = "Правильно: ${t.word}"
            speaker.say(t.word)
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("${p.emoji} ${p.title}", onExit, right = "3/3 · ${cur + 1} из ${p.targets.size}")
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftCard(Modifier.fillMaxWidth()) {
                RrText(
                    p,
                    stateOf = {
                        when {
                            it.idx < cur || (it.idx == cur && revealed) -> if (results[it.idx] == true) Chip.OK else Chip.BAD
                            it.idx == cur -> Chip.CURRENT
                            else -> Chip.BLANK
                        }
                    },
                    shown = {
                        if (it.idx < cur || (it.idx == cur && revealed)) it.word
                        else "_".repeat(maxOf(4, it.word.length))
                    }
                )
            }
            SoftCard(Modifier.fillMaxWidth()) {
                Hint("Вспомни слово, которое значит:")
                Text(t.ru, style = MaterialTheme.typography.headlineSmall)
                if (hints in 1..2 && !revealed) {
                    Spacer(Modifier.height(6.dp))
                    Text(hintFor(t.word, hints), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    enabled = !revealed,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Впиши слово") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { check() }),
                )
                feedback?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        if (!revealed) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoftButton("💡 Подсказка", { hint() }, Modifier.weight(1f), height = 52.dp)
                GradientButton("Проверить", { check() }, Modifier.weight(1f).height(52.dp), grad = Grads.Recall)
            }
        } else {
            val last = cur == p.targets.size - 1
            GradientButton(
                if (last) "Готово" else "Дальше",
                {
                    if (last) {
                        onFinish()
                    } else {
                        cur++
                        input = ""
                        hints = 0
                        wrongs = 0
                        revealed = false
                        feedback = null
                    }
                },
                Modifier.fillMaxWidth().height(52.dp),
                grad = Grads.Recall
            )
        }
    }
}

@Composable
private fun DonePhase(p: RrPassage, results: List<Boolean?>, speaker: Speaker, onExit: () -> Unit, onAgain: () -> Unit) {
    val ok = results.count { it == true }
    Column(Modifier.fillMaxSize()) {
        TopBar("${p.emoji} ${p.title}", onExit)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (ok == p.targets.size) "🎉" else "💪", style = MaterialTheme.typography.displayMedium)
            Text("$ok из ${p.targets.size} слов вспомнил сам", style = MaterialTheme.typography.headlineSmall)
            Hint("Слова, которые не получились, вернутся в повторение раньше остальных.", center = true)
            SoftCard(Modifier.fillMaxWidth()) {
                p.targets.forEachIndexed { i, t ->
                    Text(
                        (if (results[i] == true) "✅ " else "🔁 ") + "${t.word} — ${t.ru}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            SoftButton("🔊 Послушать текст", { speaker.say(p.plain) }, Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SoftButton("К списку", onExit, Modifier.weight(1f), height = 52.dp)
            GradientButton("Ещё раз", onAgain, Modifier.weight(1f).height(52.dp), grad = Grads.Recall)
        }
    }
}
