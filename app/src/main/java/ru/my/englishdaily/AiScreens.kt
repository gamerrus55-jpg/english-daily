package ru.my.englishdaily

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------- Маленькая кнопка микрофона: распознанный текст отдаётся в поле ввода ----------

@Composable
fun SmallMic(recognizer: Recognizer, speaker: Speaker, grad: Grad, onText: (String) -> Unit, onError: (String) -> Unit) {
    val context = LocalContext.current
    val permission = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            speaker.stop()
            recognizer.start({ onText(it.first()) }, onError)
        } else {
            onError("Нужен доступ к микрофону.")
        }
    }
    val listening = recognizer.state != Recognizer.State.IDLE
    Box(
        Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(grad.brush)
            .clickable {
                if (listening) {
                    recognizer.stop()
                } else {
                    val has = androidx.core.content.ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (has) {
                        speaker.stop()
                        recognizer.start({ onText(it.first()) }, onError)
                    } else {
                        permission.launch(android.Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(if (listening) "⏹" else "🎙", fontSize = 22.sp, color = Color.White)
    }
}

// ---------- Настройки ИИ ----------

@Composable
fun SettingsScreen(store: ProgressStore, onLevel: () -> Unit, onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var provider by remember { mutableStateOf(store.provider) }
    var key by remember(provider) { mutableStateOf(store.key(provider)) }
    var status by remember(provider) { mutableStateOf<String?>(null) }
    var checking by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBar("⚙️ Настройки", onExit)
        var nameEdit by remember { mutableStateOf(store.playerName) }
        Text("Имя в рейтинге", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ClearableField(
                value = nameEdit,
                onValue = { if (it.length <= 24) nameEdit = it },
                placeholder = "Твоё имя",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            GradientButton("OK", {
                if (nameEdit.isNotBlank()) {
                    store.playerName = nameEdit
                    scope.launch { Leaderboard.upload(store, force = true) }
                }
            }, Modifier.width(80.dp), enabled = nameEdit.isNotBlank() && nameEdit.trim() != store.playerName)
        }
        Spacer(Modifier.height(18.dp))
        val lvl = store.level ?: Level.BEGINNER
        SoftCard(Modifier.fillMaxWidth(), onClick = onLevel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTile(lvl.emoji, Grads.Roleplay, 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Уровень: ${lvl.title} · ${lvl.cefr}", style = MaterialTheme.typography.titleMedium)
                    Hint("Нажми, чтобы поменять")
                }
                Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Собеседник-ИИ", style = MaterialTheme.typography.titleLarge)
        Hint("Нужен для разговора с ИИ и проверки дневника. Всё остальное работает без него.")
        Spacer(Modifier.height(12.dp))

        Provider.entries.forEach { p ->
            val selected = p == provider
            SoftCard(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                onClick = { provider = p }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(
                        if (selected) "✓" else if (p == Provider.GIGACHAT) "🟢" else if (p == Provider.GROQ) "⚡" else "🟣",
                        if (selected) Grads.Roleplay else Grads.Book,
                        40.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                p.badge,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (p.badge == "Бесплатно") Good else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Hint(p.hint)
                        if (store.key(p).isNotBlank()) Text("🔑 ключ сохранён", style = MaterialTheme.typography.labelSmall, color = Good)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        SoftCard(Modifier.fillMaxWidth()) {
            Text("Как получить ключ", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Hint(provider.instructions + "\n\nКлюч хранится только на этом телефоне.")
            Spacer(Modifier.height(10.dp))
            SoftButton("Открыть сайт", {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(provider.siteUrl)))
            }, Modifier.fillMaxWidth(), height = 48.dp)
        }
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = key,
            onValueChange = { key = it.trim() },
            label = { Text(provider.keyLabel) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftButton("Удалить", {
                key = ""
                store.setKey(provider, "")
                status = "Ключ удалён."
            }, Modifier.weight(1f))
            GradientButton(if (checking) "Проверяю…" else "Сохранить", {
                if (checking) return@GradientButton
                store.setKey(provider, key)
                store.provider = provider
                checking = true
                status = null
                val p = provider
                scope.launch {
                    val r = Ai.ask(p, key, "Reply with one word.", listOf(Ai.Msg("user", "Say OK")), maxTokens = 10)
                    checking = false
                    status = r.fold({ "✅ Ключ работает! Можно разговаривать." }, { "❌ ${it.message}" })
                }
            }, Modifier.weight(1f))
        }
        status?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ---------- Собеседник-ИИ ----------

private data class ChatLine(val mine: Boolean, val en: String, val ru: String = "", val fix: String = "")

private fun levelStyle(level: Level): String = when (level) {
    Level.BEGINNER -> "The learner is a beginner (A1–A2). Use very simple, everyday spoken English, short sentences, no difficult words. Reply with 1–3 short sentences, then ask ONE simple question."
    Level.INTERMEDIATE -> "The learner is intermediate (B1). Use natural everyday English with common phrasal verbs, but avoid rare words. Reply with 2–4 sentences, then ask a question."
    Level.ADVANCED -> "The learner is advanced (B2+) but lacks speaking practice. Talk like a native speaker in everyday life: natural spoken English, phrasal verbs, common idioms, humour, follow-up questions that make them explain and argue. Use words people actually say in daily life — avoid bookish, literary or academic vocabulary. Reply with 2–5 sentences."
}

private fun chatSystem(topic: ChatTopic, level: Level) = """
You are a friendly English conversation partner for a Russian-speaking learner.
${levelStyle(level)}
Situation: ${topic.prompt}.
Rules:
- If the user writes in Russian or gets stuck, help with a simple English phrase they can say.
Always answer in EXACTLY this format:
REPLY: <your English reply>
FIX: <if the user's last message had grammar or word mistakes, write the corrected natural English sentence; otherwise write OK>
RU: <Russian translation of your reply>
""".trimIndent()

@Composable
fun AiChatScreen(store: ProgressStore, speaker: Speaker, onSettings: () -> Unit, onExit: () -> Unit) {
    var topic by remember { mutableStateOf<ChatTopic?>(null) }
    val t = topic
    if (t == null) {
        Column(Modifier.fillMaxSize()) {
            TopBar("🤖 Собеседник", onExit)
            if (!store.aiReady) {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Нужен API-ключ", style = MaterialTheme.typography.titleMedium)
                    Hint("Чтобы разговаривать с ИИ, добавь бесплатный ключ GigaChat в настройках. Это 3 минуты.")
                    Spacer(Modifier.height(10.dp))
                    GradientButton("Открыть настройки", onSettings, Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(12.dp))
            }
            Text("Выбери тему", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ChatTopic.entries) { ct ->
                    SoftCard(Modifier.fillMaxWidth(), onClick = { topic = ct }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconTile(ct.emoji, Grads.Hero, 44.dp)
                            Spacer(Modifier.width(14.dp))
                            Text(ct.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    } else {
        ChatView(t, store, speaker, onExit = { topic = null })
    }
}

@Composable
private fun ChatView(topic: ChatTopic, store: ProgressStore, speaker: Speaker, onExit: () -> Unit) {
    val grad = Grads.Hero
    val lines = remember { mutableStateListOf<ChatLine>() }
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showRu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val recognizer = rememberRecognizer()

    DisposableEffect(Unit) {
        onDispose {
            speaker.stop()
            recognizer.cancel()
        }
    }

    fun history(): List<Ai.Msg> {
        val msgs = mutableListOf(Ai.Msg("user", "(Start the conversation now.)"))
        lines.forEach { msgs.add(Ai.Msg(if (it.mine) "user" else "assistant", if (it.mine) it.en else "REPLY: ${it.en}")) }
        return msgs
    }

    fun request() {
        busy = true
        error = null
        scope.launch {
            val r = Ai.ask(store, chatSystem(topic, store.level ?: Level.BEGINNER), history())
            busy = false
            r.onSuccess { text ->
                val names = listOf("REPLY", "FIX", "RU")
                val reply = Ai.field(text, "REPLY", names).ifEmpty { text }
                val fix = Ai.field(text, "FIX", names)
                val ru = Ai.field(text, "RU", names)
                // Исправление относится к последней реплике пользователя
                val lastMine = lines.indexOfLast { it.mine }
                if (lastMine >= 0 && fix.isNotBlank() && !fix.equals("OK", ignoreCase = true) &&
                    wordsOf(fix) != wordsOf(lines[lastMine].en)
                ) {
                    lines[lastMine] = lines[lastMine].copy(fix = fix)
                }
                lines.add(ChatLine(false, reply, ru))
                speaker.say(reply)
            }.onFailure { error = it.message }
        }
    }

    fun send() {
        val text = input.trim()
        if (text.isEmpty() || busy) return
        lines.add(ChatLine(true, text))
        input = ""
        store.bump("chat", xp = 5)
        request()
    }

    LaunchedEffect(Unit) { if (lines.isEmpty()) request() }
    LaunchedEffect(lines.size, busy) {
        if (lines.isNotEmpty()) listState.animateScrollToItem(lines.size)
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("${topic.emoji} ${topic.title}", onExit)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Перевод", { showRu = !showRu }, selected = showRu, grad = grad)
            Pill("Начать заново", {
                lines.clear()
                speaker.stop()
                request()
            })
        }
        Spacer(Modifier.height(10.dp))

        LazyColumn(Modifier.weight(1f), state = listState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(lines) { _, line ->
                val shape = RoundedCornerShape(
                    topStart = 22.dp, topEnd = 22.dp,
                    bottomStart = if (line.mine) 22.dp else 6.dp,
                    bottomEnd = if (line.mine) 6.dp else 22.dp
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (line.mine) Arrangement.End else Arrangement.Start) {
                    Column(
                        Modifier
                            .fillMaxWidth(0.86f)
                            .clip(shape)
                            .background(if (line.mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                            .clickable { speaker.say(line.en) }
                            .padding(14.dp)
                    ) {
                        Text(line.en, style = MaterialTheme.typography.titleMedium)
                        if (!line.mine && showRu && line.ru.isNotBlank()) Hint(line.ru)
                        if (line.mine && line.fix.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text("✏️ Лучше так: ${line.fix}", style = MaterialTheme.typography.bodyMedium, color = Good)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text("🔊 нажми, чтобы послушать", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (busy) {
                item { Hint("Собеседник печатает…") }
            }
        }

        error?.let {
            Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ClearableField(
                value = input,
                onValue = { input = it },
                placeholder = "Ответь по-английски…",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            if (input.isBlank()) {
                SmallMic(recognizer, speaker, grad, onText = { input = it }, onError = { error = it })
            } else {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(grad.brush)
                        .clickable { send() },
                    contentAlignment = Alignment.Center
                ) { Text("➤", fontSize = 22.sp, color = Color.White) }
            }
        }
        if (recognizer.state == Recognizer.State.LISTENING) {
            Hint(if (recognizer.partial.isNotBlank()) "«${recognizer.partial}»" else "Говори по-английски…")
        }
    }
}

// ---------- Дневник «Мой день» ----------

private val diaryStarters = listOf(
    "Today I…", "I got up at…", "I'm going to…", "Yesterday I…", "I like…", "I need to…", "I want to…", "I'm tired because…",
)

private fun diarySystem(level: Level) = """
You are a kind English teacher for a Russian-speaking student, level ${level.cefr}. The student wrote a short diary entry in English.
${if (level == Level.ADVANCED) "Also suggest more natural, native-like phrasing and idioms where appropriate." else "Keep the corrected version simple."}
Answer in EXACTLY this format:
CORRECTED: <the whole entry corrected into simple, natural English>
NOTES: <2–4 short notes in Russian about the most important mistakes, each on its own line starting with "• ". If there are no mistakes, write "• Ошибок нет, отлично!">
""".trimIndent()

@Composable
fun DiaryScreen(store: ProgressStore, speaker: Speaker, onSettings: () -> Unit, onExit: () -> Unit) {
    val grad = Grads.Roleplay
    val todayEntry = remember { store.todayDiary() }
    var text by remember { mutableStateOf(todayEntry?.text.orEmpty()) }
    var corrected by remember { mutableStateOf(todayEntry?.corrected.orEmpty()) }
    var notes by remember { mutableStateOf(todayEntry?.notes.orEmpty()) }
    var busy by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<String?>(null) }
    var past by remember { mutableStateOf(store.diary().filter { it.day != LocalDate.now().toEpochDay() }) }
    val scope = rememberCoroutineScope()
    val recognizer = rememberRecognizer()
    val fmt = remember { DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru")) }

    DisposableEffect(Unit) {
        onDispose {
            speaker.stop()
            recognizer.cancel()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopBar("📝 Мой день", onExit)
        Hint("Напиши или надиктуй 3–5 предложений о своём дне. Используй конструкции из курса.")
        Spacer(Modifier.height(10.dp))
        Row(Modifier.horizontalScrollSafe(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            diaryStarters.forEach { st ->
                Pill(st, {
                    val prefix = st.removeSuffix("…")
                    text = (if (text.isBlank()) "" else text.trimEnd() + " ") + prefix + " "
                })
            }
        }
        Spacer(Modifier.height(10.dp))
        ClearableField(
            value = text,
            onValue = { text = it },
            placeholder = "Today I got up at seven…",
            minLines = 4,
            maxLines = 10,
            modifier = Modifier.fillMaxWidth()
        )
        if (text.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Pill("⌫ Убрать последнюю фразу", { text = dropLastSentence(text) })
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallMic(recognizer, speaker, grad, onText = { heard ->
                val sentence = heard.replaceFirstChar { it.uppercase() }.let { if (it.endsWith(".")) it else "$it." }
                text = (if (text.isBlank()) "" else text.trimEnd() + " ") + sentence
            }, onError = { info = it })
            Column(Modifier.weight(1f)) {
                Text("Надиктовать фразу", style = MaterialTheme.typography.titleMedium)
                Hint(
                    if (recognizer.state == Recognizer.State.LISTENING)
                        (if (recognizer.partial.isNotBlank()) "«${recognizer.partial}»" else "Говори…")
                    else "Нажми и скажи одно предложение"
                )
            }
            Pill("🔊", { if (text.isNotBlank()) speaker.say(text) })
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftButton("Сохранить", {
                if (text.isNotBlank()) {
                    store.saveDiary(text.trim(), corrected, notes)
                    info = "Сохранено ✓"
                }
            }, Modifier.weight(1f))
            GradientButton(if (busy) "Проверяю…" else "✨ Проверить с ИИ", {
                if (busy || text.isBlank()) return@GradientButton
                if (!store.aiReady) {
                    onSettings()
                    return@GradientButton
                }
                busy = true
                info = null
                scope.launch {
                    val r = Ai.ask(store, diarySystem(store.level ?: Level.BEGINNER), listOf(Ai.Msg("user", text.trim())), maxTokens = 700)
                    busy = false
                    r.onSuccess { answer ->
                        val names = listOf("CORRECTED", "NOTES")
                        corrected = Ai.field(answer, "CORRECTED", names)
                        notes = Ai.field(answer, "NOTES", names)
                        store.saveDiary(text.trim(), corrected, notes)
                    }.onFailure { info = it.message }
                }
            }, Modifier.weight(1f), grad = grad)
        }
        info?.let {
            Spacer(Modifier.height(8.dp))
            Hint(it)
        }

        if (corrected.isNotBlank()) {
            Spacer(Modifier.height(14.dp))
            SoftCard(Modifier.fillMaxWidth()) {
                Text("✏️ Как лучше", style = MaterialTheme.typography.titleMedium, color = Good)
                Spacer(Modifier.height(6.dp))
                Text(corrected, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                SpeakRow(corrected, speaker)
                if (notes.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (past.isNotEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("Прошлые записи", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            past.take(30).forEach { e ->
                SoftCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    onClick = { speaker.say(e.corrected.ifBlank { e.text }) }
                ) {
                    Hint(LocalDate.ofEpochDay(e.day).format(fmt) + " · 🔊 нажми, чтобы послушать")
                    Spacer(Modifier.height(4.dp))
                    Text(e.corrected.ifBlank { e.text }, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

/** Горизонтальная прокрутка для ряда подсказок. */
@Composable
private fun Modifier.horizontalScrollSafe(): Modifier =
    this.then(Modifier.horizontalScroll(rememberScrollState()))
