@file:OptIn(ExperimentalLayoutApi::class)

package ru.my.englishdaily

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/** Песня, которую пользователь добавил сам. Тексты хранятся только на телефоне. */
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val lines: List<String>,
    val translation: List<String> = emptyList(),
    val notes: List<SongNote> = emptyList(),
)

/** Выражение из песни с объяснением. */
data class SongNote(val en: String, val ru: String, val explain: String)

object SongStore {
    var version by mutableIntStateOf(0)
        private set

    private fun file(c: Context) = File(c.filesDir, "songs_${Profiles.current(c)}.json")

    fun all(c: Context): List<Song> = runCatching {
        val arr = JSONArray(file(c).readText())
        (0 until arr.length()).map { i -> parse(arr.getJSONObject(i)) }
    }.getOrDefault(emptyList())

    fun get(c: Context, id: String): Song? = all(c).firstOrNull { it.id == id }

    private fun parse(o: JSONObject): Song {
        fun strs(a: JSONArray?) = a?.let { arr -> (0 until arr.length()).map { arr.optString(it) } } ?: emptyList()
        val notes = o.optJSONArray("notes")?.let { a ->
            (0 until a.length()).map { i -> a.getJSONObject(i).let { SongNote(it.optString("en"), it.optString("ru"), it.optString("explain")) } }
        } ?: emptyList()
        return Song(o.optString("id"), o.optString("title"), o.optString("artist"), strs(o.optJSONArray("lines")), strs(o.optJSONArray("translation")), notes)
    }

    private fun toJson(s: Song) = JSONObject()
        .put("id", s.id).put("title", s.title).put("artist", s.artist)
        .put("lines", JSONArray(s.lines)).put("translation", JSONArray(s.translation))
        .put("notes", JSONArray(s.notes.map { JSONObject().put("en", it.en).put("ru", it.ru).put("explain", it.explain) }))

    fun save(c: Context, song: Song) {
        val list = all(c).filter { it.id != song.id } + song
        file(c).writeText(JSONArray(list.map { toJson(it) }).toString())
        version++
    }

    fun delete(c: Context, id: String) {
        file(c).writeText(JSONArray(all(c).filter { it.id != id }.map { toJson(it) }).toString())
        version++
    }

    fun create(c: Context, title: String, artist: String, text: String): Song {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val song = Song(UUID.randomUUID().toString().take(8), title.trim().ifBlank { "Без названия" }, artist.trim(), lines)
        save(c, song)
        return song
    }
}

/** Перевод и разбор песни через ИИ. */
object SongAi {
    private val system = """
You help a Russian speaker learn English through songs the user added for personal study.
Translate each numbered line into natural Russian (keep the meaning, not word-for-word), keeping the same numbering.
Then pick 6–12 of the most useful expressions from the song: slang, contractions (gonna, ain't, wanna), idioms, phrasal verbs,
interesting grammar — and explain each briefly in Russian (what it means, how it's used in everyday speech).
Reply with valid JSON only:
{"translation":["перевод строки 1","перевод строки 2", ...],"notes":[{"en":"expression","ru":"перевод","explain":"коротко по-русски"}]}
""".trimIndent()

    suspend fun analyze(store: ProgressStore, song: Song): Result<Song> = runCatching {
        if (!store.aiReady) throw Exception("Для перевода и разбора нужен бесплатный ключ GigaChat — добавь его в ⚙️ настройках.")
        val numbered = song.lines.mapIndexed { i, l -> "${i + 1}. $l" }.joinToString("\n")
        val text = Ai.ask(store, system, listOf(Ai.Msg("user", numbered)), maxTokens = 3500).getOrThrow()
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) throw Exception("ИИ ответил не в том формате. Попробуй ещё раз.")
        val o = JSONObject(text.substring(start, end + 1))
        val tr = o.optJSONArray("translation")?.let { a ->
            (0 until a.length()).map { a.optString(it).replace(Regex("^\\s*\\d+[.)]\\s*"), "") }
        } ?: emptyList()
        val notes = o.optJSONArray("notes")?.let { a ->
            (0 until a.length()).mapNotNull { i ->
                a.optJSONObject(i)?.let { SongNote(it.optString("en"), it.optString("ru"), it.optString("explain")) }
            }
        } ?: emptyList()
        if (tr.isEmpty() && notes.isEmpty()) throw Exception("ИИ ничего не прислал. Попробуй ещё раз.")
        song.copy(translation = tr, notes = notes)
    }
}

// ---------- Экраны ----------

private val SongGrad get() = Grads.Speak

@Composable
fun SongListScreen(onOpen: (String) -> Unit, onExit: () -> Unit) {
    val context = LocalContext.current
    @Suppress("UNUSED_VARIABLE") val v = SongStore.version
    val songs = SongStore.all(context)
    var adding by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("🎵 Мои песни", onExit)
        if (!adding) {
            SoftCard(Modifier.fillMaxWidth()) {
                Text("Учим английский по песням", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Hint(
                    "Найди текст любимой песни (например, на сайте с текстами песен), скопируй и вставь сюда. " +
                        "ИИ переведёт каждую строку и объяснит сленг и выражения. " +
                        "Тексты хранятся только на твоём телефоне."
                )
                Spacer(Modifier.height(10.dp))
                GradientButton("＋ Добавить песню", { adding = true }, Modifier.fillMaxWidth(), grad = SongGrad)
            }
        } else {
            SoftCard(Modifier.fillMaxWidth()) {
                Text("Новая песня", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                ClearableField(title, { title = it }, "Название", Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                ClearableField(artist, { artist = it }, "Исполнитель", Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                ClearableField(text, { text = it }, "Вставь текст песни на английском", Modifier.fillMaxWidth(), minLines = 6, maxLines = 14)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Отмена", { adding = false }, Modifier.weight(1f))
                    GradientButton("Сохранить", {
                        val song = SongStore.create(context, title, artist, text)
                        title = ""; artist = ""; text = ""; adding = false
                        onOpen(song.id)
                    }, Modifier.weight(1f), grad = SongGrad, enabled = text.lines().count { it.isNotBlank() } >= 2)
                }
            }
        }
        songs.forEach { s ->
            SoftCard(Modifier.fillMaxWidth(), onClick = { onOpen(s.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile("🎵", SongGrad, 48.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.title, style = MaterialTheme.typography.titleMedium)
                        Hint(listOfNotNull(s.artist.ifBlank { null }, "${s.lines.size} строк", if (s.translation.isNotEmpty()) "переведена" else null).joinToString(" · "))
                    }
                    Text("›", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SongScreen(id: String, store: ProgressStore, speaker: Speaker, onExit: () -> Unit) {
    val context = LocalContext.current
    @Suppress("UNUSED_VARIABLE") val v = SongStore.version
    @Suppress("UNUSED_VARIABLE") val mw = MyWords.version
    val song = SongStore.get(context, id)
    val scope = rememberCoroutineScope()
    var tab by remember { mutableIntStateOf(0) }
    var showRu by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var word by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    if (song == null) {
        Column { TopBar("🎵 Песня", onExit); Hint("Песня не найдена.") }
        return
    }

    fun analyze() {
        if (busy) return
        busy = true
        error = null
        scope.launch {
            SongAi.analyze(store, song)
                .onSuccess { SongStore.save(context, it) }
                .onFailure { error = it.message }
            busy = false
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar("🎵 ${song.title}", onExit)
        if (song.artist.isNotBlank()) Hint(song.artist)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("📝 Текст", { tab = 0 }, selected = tab == 0, grad = SongGrad)
            Pill("💡 Разбор · ${song.notes.size}", { tab = 1 }, selected = tab == 1, grad = SongGrad)
            if (song.translation.isNotEmpty() && tab == 0) Pill("Перевод", { showRu = !showRu }, selected = showRu, grad = SongGrad)
        }
        Spacer(Modifier.height(10.dp))

        if (song.translation.isEmpty()) {
            SoftCard(Modifier.fillMaxWidth()) {
                Text("Перевести и разобрать", style = MaterialTheme.typography.titleMedium)
                Hint("ИИ переведёт каждую строку и выберет самые полезные выражения — сленг, идиомы, сокращения.")
                Spacer(Modifier.height(8.dp))
                GradientButton(if (busy) "Перевожу… (до минуты)" else "✨ Перевести и разобрать", { analyze() }, Modifier.fillMaxWidth(), grad = SongGrad, enabled = !busy)
            }
            Spacer(Modifier.height(8.dp))
        }
        error?.let { Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium) }

        if (tab == 0) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(song.lines) { i, line ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        FlowRow(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            line.split(" ").forEach { w ->
                                Text(
                                    "$w ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (word != null && Dictionary.clean(w) == Dictionary.clean(word!!)) SongGrad.a else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            word = w
                                            speaker.say(Dictionary.clean(w))
                                        }
                                )
                            }
                        }
                        PronLine(line)
                        if (showRu) song.translation.getOrNull(i)?.takeIf { it.isNotBlank() }?.let { Hint(it) }
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Pill("🔊", { speaker.say(line) })
                            Pill("🐢", { speaker.say(line, slow = true) })
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(10.dp))
                    if (confirmDelete) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SoftButton("Не удалять", { confirmDelete = false }, Modifier.weight(1f), height = 48.dp)
                            GradientButton("Удалить песню", { SongStore.delete(context, song.id); onExit() }, Modifier.weight(1f), grad = Grads.Speak)
                        }
                    } else {
                        Pill("🗑 Удалить песню", { confirmDelete = true })
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            // Карточка слова
            word?.let { w ->
                val clean = Dictionary.clean(w)
                val ru = Dictionary.lookup(w, song.notes.associate { it.en.lowercase() to it.ru })
                SoftCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(clean, style = MaterialTheme.typography.headlineSmall, color = SongGrad.a)
                            PronLine(clean)
                            Text(ru ?: "нет в словаре — смотри перевод строки", style = MaterialTheme.typography.bodyLarge)
                        }
                        CircleButton("✕") { word = null }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Pill("🔊", { speaker.say(clean) })
                        val inMine = MyWords.contains(clean)
                        Pill(if (inMine) "⭐ В моих словах" else "＋ В мои слова", { if (!inMine) MyWords.add(clean, ru ?: "") }, selected = inMine, grad = SongGrad)
                    }
                }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (song.notes.isEmpty()) {
                    item { Hint("Разбора пока нет — нажми «✨ Перевести и разобрать».") }
                }
                items(song.notes) { n ->
                    SoftCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(n.en, style = MaterialTheme.typography.titleMedium, color = SongGrad.a)
                                PronLine(n.en)
                                Text(n.ru, style = MaterialTheme.typography.bodyLarge)
                            }
                            Pill("🔊", { speaker.say(n.en) })
                        }
                        if (n.explain.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Hint(n.explain)
                        }
                        Spacer(Modifier.height(8.dp))
                        val inMine = MyWords.contains(n.en)
                        Pill(if (inMine) "⭐ В моих словах" else "＋ В мои слова", { if (!inMine) MyWords.add(n.en, n.ru) }, selected = inMine, grad = SongGrad)
                    }
                }
                item {
                    if (song.notes.isNotEmpty()) {
                        Pill("↻ Разобрать заново", { analyze() })
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
