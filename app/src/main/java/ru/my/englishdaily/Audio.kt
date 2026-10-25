package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private enum class Phase(val emoji: String, val label: String) {
    LISTEN("🎧", "Слушай"),
    SAY("🗣", "Скажи по-английски вслух!"),
    ANSWER("✅", "Правильный ответ"),
    REPEAT("🔁", "Повтори за диктором"),
    DONE("🎉", "Урок окончен"),
}

/**
 * Аудиоурок в духе Pimsleur: русская фраза → пауза, чтобы сказать самому →
 * правильный ответ → ещё раз, чтобы повторить. Экран не гаснет.
 */
@Composable
fun AudioLessonScreen(items: List<Item>, speaker: Speaker, onExit: () -> Unit) {
    val grad = Mode.AUDIO.grad
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
        SessionBar(
            onExit = onExit,
            progress = if (items.isEmpty()) 1f else index.toFloat() / items.size,
            counter = if (index < items.size) "${index + 1}/${items.size}" else null,
            grad = grad
        )

        Spacer(Modifier.weight(1f))

        if (phase == Phase.DONE || index >= items.size) {
            IconTile(Phase.DONE.emoji, grad, 96.dp)
            Spacer(Modifier.height(20.dp))
            BigText(Phase.DONE.label)
            Spacer(Modifier.height(8.dp))
            Hint("Фраз: ${items.size}. Повтори урок завтра — запомнится крепче.", center = true)
            Spacer(Modifier.weight(1f))
            GradientButton("На главную", onExit, Modifier.fillMaxWidth(), grad = grad)
        } else {
            val item = items[index]
            val shown = if (playing) phase else null
            IconTile(shown?.emoji ?: "⏸", grad, 96.dp)
            Spacer(Modifier.height(16.dp))
            BigText(shown?.label ?: "Пауза", color = grad.a, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))

            SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
                BigText(item.ru, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(14.dp))
                val showEn = phase == Phase.ANSWER || phase == Phase.REPEAT || !playing
                BigText(if (showEn) item.en else "· · ·", color = grad.a)
            }

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SoftButton("⏮", { jump(index - 1) }, Modifier.width(72.dp), enabled = index > 0)
                GradientButton(
                    if (playing) "⏸ Пауза" else "▶ Продолжить",
                    {
                        if (playing) {
                            playing = false
                        } else {
                            playing = true
                            restart++
                        }
                    },
                    Modifier.weight(1f),
                    grad = grad
                )
                SoftButton("⏭", { jump(index + 1) }, Modifier.width(72.dp), enabled = index < items.size - 1)
            }
            Spacer(Modifier.height(12.dp))
            Hint("Экран не гаснет — можно положить телефон и просто слушать и говорить.", center = true)
        }
    }
}
