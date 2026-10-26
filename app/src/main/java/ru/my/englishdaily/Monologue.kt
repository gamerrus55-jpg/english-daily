package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun monologueSystem(level: Level) = """
You are an experienced English speaking coach. The student is a Russian speaker, level ${level.cefr}.
They spoke for about a minute on a topic; you get the speech-recognition transcript (it may contain recognition errors — ignore obvious ones).
Answer in EXACTLY this format:
SCORE: <a number from 1 to 10 for fluency and accuracy>
LEVEL: <estimated CEFR level, e.g. B1>
BETTER: <a corrected and more natural version of what they said, same length or shorter>
NOTES: <3–5 short tips in Russian, each on its own line starting with "• ": main grammar mistakes, better words or phrasal verbs for their level, and one thing they did well>
""".trimIndent()

/**
 * Монолог: тема → говоришь минуту → видишь текст своей речи → ИИ даёт разбор.
 * Распознавание речи на Android прерывается на паузах, поэтому перезапускаем его, пока идёт время.
 */
@Composable
fun MonologueScreen(store: ProgressStore, speaker: Speaker, onSettings: () -> Unit, onExit: () -> Unit) {
    val level = store.level ?: Level.BEGINNER
    val grad = Grads.Speak
    val seconds = if (level == Level.BEGINNER) 45 else if (level == Level.INTERMEDIATE) 60 else 90
    val topics = remember(level) { Monologue.topics.getValue(level) }
    var topic by remember { mutableStateOf(topics.random()) }
    var running by remember { mutableStateOf(false) }
    var left by remember { mutableIntStateOf(seconds) }
    val segments = remember { mutableStateListOf<String>() }
    val transcript = segments.joinToString(" ")
    var feedback by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<String?>(null) }
    val recognizer = rememberRecognizer()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            recognizer.cancel()
            speaker.stop()
        }
    }

    fun listen() {
        if (!running) return
        recognizer.start(
            onHeard = { alts ->
                val heard = alts.first()
                segments.add(heard.replaceFirstChar { it.uppercase() } + ".")
                if (running) scope.launch { delay(150); listen() }
            },
            onFail = {
                // Пауза в речи — просто слушаем дальше
                if (running) scope.launch { delay(300); listen() }
            }
        )
    }

    val permission = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            running = true
            listen()
        } else {
            info = "Нужен доступ к микрофону."
        }
    }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (left > 0 && running) {
            delay(1000)
            left--
        }
        running = false
        recognizer.stop()
    }

    val words = transcript.split(Regex("\\s+")).count { it.isNotBlank() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBar("🎤 Монолог", onExit, right = "${level.emoji} ${level.cefr}")
        SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
            Hint("Говори без остановки ${seconds} секунд на тему:", center = true)
            Spacer(Modifier.height(10.dp))
            BigText(topic, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("🔊", { speaker.say(topic) })
                Pill("🎲 Другая тема", {
                    if (!running) {
                        topic = (topics - topic).random()
                        segments.clear()
                        feedback = null
                        left = seconds
                    }
                })
            }
        }
        Spacer(Modifier.height(16.dp))

        if (running || transcript.isEmpty()) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ProgressRing(left.toFloat() / seconds, 150.dp, 12.dp, MaterialTheme.colorScheme.surfaceVariant, grad.a) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$left", style = MaterialTheme.typography.headlineLarge)
                        Hint("секунд")
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        if (transcript.isNotEmpty()) {
            SoftCard(Modifier.fillMaxWidth()) {
                Text("Твоя речь · $words слов", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(transcript, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("⌫ Убрать последнюю фразу", { if (segments.isNotEmpty()) segments.removeAt(segments.lastIndex) })
                    if (!running) Pill("🔊", { speaker.say(transcript) })
                }
                if (recognizer.state == Recognizer.State.LISTENING && recognizer.partial.isNotBlank()) {
                    Hint("«${recognizer.partial}»")
                }
            }
            Spacer(Modifier.height(12.dp))
        } else if (running) {
            Hint(if (recognizer.partial.isNotBlank()) "«${recognizer.partial}»" else "Говори…", center = true)
        }

        if (!running) {
            if (transcript.isEmpty()) {
                GradientButton("🎙 Начать", {
                    speaker.stop()
                    left = seconds
                    feedback = null
                    val has = androidx.core.content.ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (has) {
                        running = true
                        listen()
                    } else {
                        permission.launch(android.Manifest.permission.RECORD_AUDIO)
                    }
                }, Modifier.fillMaxWidth(), grad = grad)
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Заново", {
                        segments.clear()
                        feedback = null
                        left = seconds
                    }, Modifier.weight(1f))
                    GradientButton(if (busy) "Анализирую…" else "✨ Разбор от ИИ", {
                        if (busy) return@GradientButton
                        if (!store.aiReady) {
                            onSettings()
                            return@GradientButton
                        }
                        busy = true
                        info = null
                        scope.launch {
                            val r = Ai.ask(
                                store, monologueSystem(level),
                                listOf(Ai.Msg("user", "Topic: $topic\nTranscript: $transcript")), maxTokens = 900
                            )
                            busy = false
                            r.onSuccess {
                                feedback = it
                                store.bump("monologue", xp = 25)
                            }.onFailure { info = it.message }
                        }
                    }, Modifier.weight(1f), grad = grad)
                }
            }
        } else {
            SoftButton("⏹ Закончить раньше", {
                running = false
                recognizer.stop()
            }, Modifier.fillMaxWidth())
        }

        info?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium)
        }

        feedback?.let { text ->
            val names = listOf("SCORE", "LEVEL", "BETTER", "NOTES")
            val score = Ai.field(text, "SCORE", names)
            val cefr = Ai.field(text, "LEVEL", names)
            val better = Ai.field(text, "BETTER", names)
            val notes = Ai.field(text, "NOTES", names).ifEmpty { if (better.isEmpty()) text else "" }
            Spacer(Modifier.height(14.dp))
            SoftCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile("⭐", grad, 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Оценка: ${score.ifEmpty { "—" }} / 10", style = MaterialTheme.typography.titleLarge)
                        if (cefr.isNotEmpty()) Hint("Уровень речи: $cefr")
                    }
                }
                if (better.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("✏️ Как сказать лучше", style = MaterialTheme.typography.titleMedium, color = Good)
                    Spacer(Modifier.height(4.dp))
                    Text(better, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    SpeakRow(better, speaker)
                }
                if (notes.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ---------- Выбор уровня ----------

@Composable
fun LevelPicker(current: Level?, onPick: (Level) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Какой у тебя английский?", style = MaterialTheme.typography.headlineMedium)
        Hint("Приложение подстроит скорость речи, таймеры, собеседника-ИИ и подскажет, с чего начать. Уровень можно поменять в настройках.")
        Spacer(Modifier.height(4.dp))
        Level.entries.forEach { lvl ->
            SoftCard(Modifier.fillMaxWidth(), onClick = { onPick(lvl) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(lvl.emoji, if (lvl == current) Grads.Roleplay else Grads.Hero, 52.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${lvl.title} · ${lvl.cefr}", style = MaterialTheme.typography.titleMedium)
                        Hint(lvl.hint)
                    }
                    if (lvl == current) Text("✓", style = MaterialTheme.typography.headlineSmall, color = Good)
                }
            }
        }
    }
}
