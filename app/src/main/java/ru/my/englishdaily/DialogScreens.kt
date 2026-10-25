@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DialogListScreen(store: ProgressStore, onOpen: (Dialogue) -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TopBar("🎬 Диалоги", onExit)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Как заниматься", fontWeight = FontWeight.SemiBold)
                        Text(
                            "1) Послушай 2 раза без текста — угадай, о чём речь.\n" +
                                "2) Включи английский текст и послушай ещё раз.\n" +
                                "3) Включи «Пауза для повтора» и повторяй каждую реплику вслух.\n" +
                                "4) Через пару дней — на быстрой скорости без текста.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            items(Dialogues.all, key = { it.id }) { d ->
                val times = store.listened(d.id)
                Card(onClick = { onOpen(d) }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(d.emoji, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(d.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                "${d.lines.size} реплик · " +
                                    if (times == 0) "ещё не слушал" else "прослушан $times раз",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (times >= 3) Text("✅")
                    }
                }
            }
        }
    }
}

@Composable
fun DialogScreen(d: Dialogue, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
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

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Speed.entries.forEach { sp ->
                FilterChip(
                    selected = speaker.speed == sp,
                    onClick = { speaker.speed = sp },
                    label = { Text(sp.title) }
                )
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            FilterChip(selected = showEn, onClick = { showEn = !showEn }, label = { Text("Текст") })
            FilterChip(selected = showRu, onClick = { showRu = !showRu }, label = { Text("Перевод") })
            FilterChip(selected = shadow, onClick = { shadow = !shadow }, label = { Text("Пауза для повтора") })
        }
        Spacer(Modifier.height(8.dp))

        if (playFrom == null) {
            Button(onClick = { play(0, false) }, modifier = Modifier.fillMaxWidth()) {
                Text("▶ Слушать весь диалог")
            }
        } else {
            OutlinedButton(onClick = { stopPlaying() }, modifier = Modifier.fillMaxWidth()) {
                Text(if (shadow) "⏹ Стоп · повторяй за диктором" else "⏹ Стоп")
            }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(d.lines) { i, line ->
                val active = current == i
                val isB = line.who == 1
                Surface(
                    onClick = { play(i, true) },
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        active -> MaterialTheme.colorScheme.primaryContainer
                        isB -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = if (isB) 32.dp else 0.dp, end = if (isB) 0.dp else 32.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            if (isB) "B" else "A",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            if (showEn) line.en else "🔊 " + "•".repeat(line.en.split(" ").size.coerceAtMost(12)),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (showRu) {
                            Text(
                                line.ru,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
