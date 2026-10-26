package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------- Список диалогов ----------

@Composable
fun DialogListScreen(store: ProgressStore, roleplay: Boolean, onOpen: (Dialogue) -> Unit, onExit: () -> Unit) {
    val grad = if (roleplay) Grads.Roleplay else Grads.Dialogs
    var band by remember { mutableIntStateOf(DialogLevels.forUser(store.level ?: Level.BEGINNER)) }
    val list = Dialogues.everything.filter { band == 0 || it.level == band }
    Column(Modifier.fillMaxSize()) {
        TopBar(if (roleplay) "🎭 Ролевая игра" else "🎬 Диалоги", onExit)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Все", { band = 0 }, selected = band == 0, grad = grad)
            DialogLevels.short.forEach { (lvl, name) ->
                val count = Dialogues.everything.count { it.level == lvl }
                Pill("$name · $count", { band = lvl }, selected = band == lvl, grad = grad)
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text(
                        if (band == 0) "Как заниматься" else DialogLevels.names[band] ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Hint(
                        when {
                            band == 4 -> "Живая речь, как в жизни: перебивания, «you know», «I mean», идиомы и юмор. " +
                                "Сначала слушай на обычной скорости без текста, потом — на быстрой."
                            roleplay -> "Выбери диалог и свою роль. Приложение говорит за собеседника, " +
                                "а ты отвечаешь в микрофон. Сначала прослушай диалог в «Диалогах»."
                            else -> "1) Послушай 2 раза без текста — угадай, о чём речь.\n" +
                                "2) Включи текст и послушай ещё раз.\n" +
                                "3) Включи «Пауза для повтора» и повторяй вслух.\n" +
                                "4) Через пару дней — быстрая скорость без текста."
                        }
                    )
                }
            }
            var lastLevel = -1
            list.forEach { d ->
                if (band == 0 && d.level != lastLevel) {
                    lastLevel = d.level
                    item(key = "h${d.level}") {
                        Text(
                            DialogLevels.names[d.level] ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
                item(key = d.id) {
                    val times = store.listened(d.id)
                    SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(d) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconTile(d.emoji, grad, 52.dp)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(d.title, style = MaterialTheme.typography.titleMedium)
                                Hint(
                                    "${DialogLevels.short[d.level]} · ${d.lines.size} реплик · " +
                                        if (times == 0) "ещё не слушал" else "прослушан: $times"
                                )
                            }
                            Text(
                                if (times >= 3) "✅" else "›",
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

// ---------- Пузырь реплики ----------

@Composable
private fun Bubble(
    who: Int,
    mine: Boolean = false,
    active: Boolean = false,
    grad: Grad,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val right = who == 1
    val shape = RoundedCornerShape(
        topStart = 22.dp, topEnd = 22.dp,
        bottomStart = if (right) 22.dp else 6.dp,
        bottomEnd = if (right) 6.dp else 22.dp
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (right) Arrangement.End else Arrangement.Start) {
        val base = Modifier
            .fillMaxWidth(0.86f)
            .clip(shape)
            .then(
                when {
                    active -> Modifier.background(grad.brush)
                    mine -> Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    right -> Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                    else -> Modifier.background(MaterialTheme.colorScheme.surface)
                }
            )
        Column(
            (if (onClick != null) base.clickable(onClick = onClick) else base).padding(14.dp),
            content = content
        )
    }
}

// ---------- Прослушивание диалога ----------

@Composable
fun DialogScreen(d: Dialogue, speaker: Speaker, store: ProgressStore, onRoleplay: () -> Unit, onExit: () -> Unit) {
    val grad = Grads.Dialogs
    var showEn by remember { mutableStateOf(false) }
    var showRu by remember { mutableStateOf(false) }
    var shadow by remember { mutableStateOf(false) }
    var current by remember { mutableIntStateOf(-1) }
    var playFrom by remember { mutableStateOf<Int?>(null) }
    var single by remember { mutableStateOf(false) }
    var playToken by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    LaunchedEffect(playToken) {
        val start = playFrom ?: return@LaunchedEffect
        val end = if (single) start else d.lines.lastIndex
        delay(200)
        for (i in start..end) {
            current = i
            val line = d.lines[i]
            speaker.speakEn(line.en, secondVoice = line.who == 1)
            val words = line.en.split(" ").size
            delay(if (shadow) 1200L + 450L * words else 450L)
        }
        if (!single && start == 0) store.markListened(d.id)
        current = -1
        playFrom = null
    }

    fun play(from: Int, one: Boolean) {
        playFrom = from
        single = one
        playToken++
    }

    fun stopPlaying() {
        playFrom = null
        current = -1
        playToken++
        speaker.stop()
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("${d.emoji} ${d.title}", onExit)

        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Speed.entries.forEach { sp ->
                Pill(sp.title, { speaker.speed = sp }, selected = speaker.speed == sp, grad = grad)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Текст", { showEn = !showEn }, selected = showEn, grad = grad)
            Pill("Перевод", { showRu = !showRu }, selected = showRu, grad = grad)
            Pill("Пауза для повтора", { shadow = !shadow }, selected = shadow, grad = grad)
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(d.lines) { i, line ->
                val active = current == i
                val textColor = if (active) Color.White else MaterialTheme.colorScheme.onSurface
                Bubble(line.who, active = active, grad = grad, onClick = { play(i, true) }) {
                    Text(
                        if (line.who == 1) "B" else "A",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (showEn) line.en else "🔊 " + "•".repeat(line.en.split(" ").size.coerceAtMost(12)),
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                    if (showRu) {
                        Text(
                            line.ru,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (active) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (playFrom == null) {
                GradientButton("▶ Слушать", { play(0, false) }, Modifier.weight(1f), grad = grad)
            } else {
                SoftButton("⏹ Стоп", { stopPlaying() }, Modifier.weight(1f))
            }
            GradientButton("🎭 Сыграть роль", onRoleplay, Modifier.weight(1f), grad = Grads.Roleplay)
        }
    }
}

// ---------- 🎭 Ролевая игра ----------

@Composable
fun RoleplayScreen(d: Dialogue, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val grad = Grads.Roleplay
    var role by remember { mutableStateOf<Int?>(null) }
    var pos by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    val results = remember { mutableStateMapOf<Int, Scored>() }
    val recognizer = rememberRecognizer()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            speaker.stop()
            recognizer.cancel()
        }
    }

    // Реплики собеседника звучат сами, на своих — ждём тебя
    LaunchedEffect(role, pos) {
        val r = role ?: return@LaunchedEffect
        if (pos >= d.lines.size) {
            store.markListened(d.id)
            return@LaunchedEffect
        }
        val line = d.lines[pos]
        if (line.who != r) {
            delay(350)
            speaker.speakEn(line.en, secondVoice = line.who == 1)
            delay(300)
            pos++
        }
    }
    LaunchedEffect(pos, results.size) {
        if (role != null) listState.animateScrollToItem(pos.coerceAtMost(d.lines.size))
    }

    Column(Modifier.fillMaxSize()) {
        val r = role
        TopBar("🎭 ${d.title}", onExit, right = if (r != null) "Ты — ${if (r == 0) "A" else "B"}" else null)

        if (r == null) {
            Spacer(Modifier.height(8.dp))
            Text("Выбери свою роль", style = MaterialTheme.typography.headlineSmall)
            Hint("Ты будешь говорить реплики этого героя в микрофон.")
            Spacer(Modifier.height(16.dp))
            listOf(0, 1).forEach { who ->
                val first = d.lines.first { it.who == who }
                SoftCard(Modifier.fillMaxWidth().padding(vertical = 6.dp), onClick = { role = who }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(if (who == 0) "A" else "B", grad, 52.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(if (who == 0) "Начинаю разговор" else "Отвечаю", style = MaterialTheme.typography.titleMedium)
                            Hint("Первая фраза: «${first.ru}»")
                        }
                    }
                }
            }
        } else {

        LazyColumn(Modifier.weight(1f), state = listState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(d.lines.take((pos + 1).coerceAtMost(d.lines.size))) { i, line ->
                val mine = line.who == r
                val res = results[i]
                Bubble(line.who, mine = mine, active = !mine && i == pos, grad = grad, onClick = {
                    if (!mine || res != null) speaker.say(line.en)
                }) {
                    val onActive = !mine && i == pos
                    Text(
                        if (mine) "Ты" else if (line.who == 1) "B" else "A",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (onActive) Color.White else MaterialTheme.colorScheme.primary
                    )
                    when {
                        !mine -> {
                            Text(line.en, style = MaterialTheme.typography.titleMedium, color = if (onActive) Color.White else MaterialTheme.colorScheme.onSurface)
                            Text(
                                line.ru,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (onActive) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        res != null -> {
                            ScoredText(res)
                            Hint("${res.percent}% · «${res.heard}» · 🔊 нажми, чтобы послушать")
                        }
                        else -> {
                            Text("Скажи: ${line.ru}", style = MaterialTheme.typography.titleMedium)
                            if (showHint && i == pos) {
                                Text(line.en, style = MaterialTheme.typography.bodyLarge, color = grad.a)
                            }
                        }
                    }
                }
            }
            if (pos >= d.lines.size) {
                item {
                    val avg = if (results.isEmpty()) 0 else results.values.map { it.percent }.average().toInt()
                    SoftCard(Modifier.fillMaxWidth()) {
                        BigText("Диалог сыгран! 🎉")
                        Spacer(Modifier.height(4.dp))
                        Hint("Средняя точность: $avg%", center = true)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        val line = d.lines.getOrNull(pos)
        when {
            line == null -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Ещё раз", {
                        results.clear()
                        pos = 0
                        showHint = false
                    }, Modifier.weight(1f))
                    GradientButton("Готово", onExit, Modifier.weight(1f), grad = grad)
                }
            }
            line.who != r -> {
                Hint("Слушай собеседника…", center = true)
                Spacer(Modifier.height(40.dp))
            }
            else -> {
                val res = results[pos]
                MicButton(recognizer, speaker, grad) { heard ->
                    val sc = scoreSpeech(line.en, heard)
                    results[pos] = sc
                    if (sc.ok) {
                        scope.launch {
                            delay(900)
                            showHint = false
                            pos++
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("💡 Подсказка", {
                        showHint = true
                        speaker.say(line.en)
                    }, Modifier.weight(1f), height = 48.dp)
                    SoftButton(if (res != null) "Дальше ›" else "Пропустить", {
                        showHint = false
                        pos++
                    }, Modifier.weight(1f), height = 48.dp)
                }
            }
        }
        }
    }
}
