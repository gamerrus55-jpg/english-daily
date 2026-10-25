@file:OptIn(ExperimentalMaterial3Api::class)

package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private enum class Phase(val label: String) {
    LISTEN("🎧 Слушай"),
    SAY("🗣 Скажи по-английски вслух!"),
    ANSWER("✅ Правильный ответ"),
    REPEAT("🔁 Повтори за диктором"),
    DONE("🎉 Урок окончен"),
}

/**
 * Аудиоурок в духе Pimsleur: русская фраза → пауза, чтобы сказать самому →
 * правильный ответ → ещё раз, чтобы повторить. Экран не гаснет.
 */
@Composable
fun AudioLessonScreen(items: List<Item>, speaker: Speaker, onExit: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(true) }
    var restart by remember { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf(Phase.LISTEN) }

    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
            speaker.stop()
        }
    }

    LaunchedEffect(playing, restart) {
        if (!playing) {
            speaker.stop()
            return@LaunchedEffect
        }
        delay(300)
        while (index < items.size) {
            val item = items[index]
            val words = item.en.split(" ").size

            phase = Phase.LISTEN
            if (speaker.ruReady) speaker.speakRu(ruForSpeech(item.ru)) else delay(2500)

            phase = Phase.SAY
            delay(2500L + 400L * words)

            phase = Phase.ANSWER
            speaker.speakEn(item.en)
            delay(900)

            phase = Phase.REPEAT
            speaker.speakEn(item.en)
            delay(1500L + 300L * words)

            index++
        }
        phase = Phase.DONE
        playing = false
    }

    fun jump(to: Int) {
        index = to.coerceIn(0, items.size - 1)
        playing = true
        restart++
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        TopBar(
            Mode.AUDIO.title,
            onExit,
            right = if (index < items.size) "${index + 1}/${items.size}" else null
        )
        LinearProgressIndicator(
            progress = { if (items.isEmpty()) 1f else index.toFloat() / items.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        if (phase == Phase.DONE || index >= items.size) {
            Text(Phase.DONE.label, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Фраз: ${items.size}. Повтори урок завтра — запомнится крепче.", textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("На главную") }
        } else {
            val item = items[index]
            Text(
                if (playing) phase.label else "⏸ Пауза",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Text(item.ru, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            val showEn = phase == Phase.ANSWER || phase == Phase.REPEAT || !playing
            Text(
                if (showEn) item.en else "…",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { jump(index - 1) }, enabled = index > 0) { Text("⏮") }
                Button(onClick = {
                    if (playing) {
                        playing = false
                    } else {
                        playing = true
                        restart++
                    }
                }) { Text(if (playing) "⏸ Пауза" else "▶ Продолжить") }
                OutlinedButton(onClick = { jump(index + 1) }, enabled = index < items.size - 1) { Text("⏭") }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Экран не гаснет. Можно положить телефон и просто слушать и говорить.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
