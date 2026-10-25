@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.my.englishdaily

import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class Mode(val title: String, val hint: String) {
    RECALL("⚡ Вспомни быстро", "Видишь фразу по-русски — скажи её по-английски, пока не кончилось время"),
    BUILD("🧩 Собери фразу", "Собери английскую фразу из перемешанных слов"),
    LISTEN("👂 На слух", "Послушай и выбери правильный перевод"),
}

sealed interface Screen {
    data object Home : Screen
    data object Browse : Screen
    data class Session(val mode: Mode, val deck: Deck, val items: List<Item>) : Screen
}

private val Good = Color(0xFF2E9E4F)

@Composable
fun App(speaker: Speaker, store: ProgressStore) {
    val dark = isSystemInDarkTheme()
    val scheme = if (dark) {
        darkColorScheme(primary = Color(0xFF9DB2FF), secondaryContainer = Color(0xFF2B3566))
    } else {
        lightColorScheme(primary = Color(0xFF3557D6), secondaryContainer = Color(0xFFDDE3FF))
    }
    MaterialTheme(colorScheme = scheme) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            var screen by remember { mutableStateOf<Screen>(Screen.Home) }
            var deck by rememberSaveable { mutableStateOf(Deck.PHRASES) }
            Box(
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(16.dp)
            ) {
                when (val s = screen) {
                    Screen.Home -> HomeScreen(
                        store = store,
                        speaker = speaker,
                        deck = deck,
                        onDeck = { deck = it },
                        onStart = { mode ->
                            // Собирать из слов можно только фразы
                            val d = if (mode == Mode.BUILD) Deck.PHRASES else deck
                            screen = Screen.Session(mode, d, store.buildSession(d.items))
                        },
                        onBrowse = { screen = Screen.Browse },
                    )

                    Screen.Browse -> {
                        BackHandler { screen = Screen.Home }
                        BrowseScreen(deck, store, speaker, onExit = { screen = Screen.Home })
                    }

                    is Screen.Session -> {
                        BackHandler { screen = Screen.Home }
                        SessionScreen(s, speaker, store, onExit = { screen = Screen.Home })
                    }
                }
            }
        }
    }
}

// ---------------- Главный экран ----------------

@Composable
fun HomeScreen(
    store: ProgressStore,
    speaker: Speaker,
    deck: Deck,
    onDeck: (Deck) -> Unit,
    onStart: (Mode) -> Unit,
    onBrowse: () -> Unit,
) {
    val stats = remember(deck) { store.stats(deck.items) }
    val streak = remember { store.streak() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("English Daily", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            if (streak > 0) "🔥 Дней подряд: $streak" else "Начни серию — позанимайся сегодня 5 минут",
            style = MaterialTheme.typography.titleMedium
        )

        speaker.problem?.let { TtsWarning(it) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Deck.entries.forEach { d ->
                FilterChip(selected = d == deck, onClick = { onDeck(d) }, label = { Text(d.title) })
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatCell("Повторить", stats.due)
                StatCell("Новых", stats.fresh)
                StatCell("Выучено", stats.learned)
            }
        }

        Mode.entries.forEach { mode ->
            val note = if (mode == Mode.BUILD && deck == Deck.WORDS) " (только фразы)" else ""
            Card(onClick = { onStart(mode) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(mode.title + note, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(mode.hint, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        OutlinedButton(onClick = onBrowse, modifier = Modifier.fillMaxWidth()) {
            Text("📖 Все ${deck.title.lowercase()} с озвучкой")
        }
    }
}

@Composable
private fun StatCell(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$value", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun TtsWarning(problem: Speaker.Problem) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (problem == Speaker.Problem.NO_ENGLISH)
                    "Нет английского голоса. Скачай его, иначе озвучки не будет."
                else
                    "Не найден синтезатор речи. Установи «Синтезатор речи Google» (Speech Services by Google) из Play Маркета.",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val intent = if (problem == Speaker.Problem.NO_ENGLISH)
                    Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                else
                    Intent("com.android.settings.TTS_SETTINGS")
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }) { Text("Открыть настройки") }
        }
    }
}

// ---------------- Занятие ----------------

@Composable
fun SessionScreen(s: Screen.Session, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val queue = remember(s) { mutableStateListOf<Item>().apply { addAll(s.items) } }
    val retried = remember(s) { mutableSetOf<String>() }
    var index by remember(s) { mutableIntStateOf(0) }
    var correct by remember(s) { mutableIntStateOf(0) }
    var answered by remember(s) { mutableIntStateOf(0) }

    fun onResult(item: Item, ok: Boolean) {
        store.answer(item.id, ok)
        answered++
        if (ok) {
            correct++
        } else if (retried.add(item.id)) {
            // Ошибся — карточка вернётся в конце этого же занятия
            queue.add(item)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExit) { Text("← Домой") }
            Spacer(Modifier.weight(1f))
            if (index < queue.size) {
                Text("${index + 1} / ${queue.size}", style = MaterialTheme.typography.labelLarge)
            }
        }
        LinearProgressIndicator(
            progress = { if (queue.isEmpty()) 1f else index.toFloat() / queue.size },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        if (index >= queue.size) {
            FinishView(correct, answered, onExit)
        } else {
            val item = queue[index]
            key(index) {
                when (s.mode) {
                    Mode.RECALL -> RecallCard(item, speaker) { ok ->
                        onResult(item, ok)
                        index++
                    }

                    Mode.BUILD -> BuildCard(
                        item, speaker,
                        onAnswer = { ok -> onResult(item, ok) },
                        onNext = { index++ }
                    )

                    Mode.LISTEN -> ListenCard(
                        item, s.deck.items, speaker,
                        onAnswer = { ok -> onResult(item, ok) },
                        onNext = { index++ }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.FinishView(correct: Int, answered: Int, onExit: () -> Unit) {
    Spacer(Modifier.weight(1f))
    Text("🎉", style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.CenterHorizontally))
    Text(
        "Готово!",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Text(
        "Правильно с первого раза: $correct из $answered",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.weight(1f))
    Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("На главную") }
}

@Composable
private fun SpeakRow(text: String, speaker: Speaker) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilledTonalButton(onClick = { speaker.say(text) }) { Text("🔊 Ещё раз") }
        FilledTonalButton(onClick = { speaker.say(text, slow = true) }) { Text("🐢 Медленно") }
    }
}

// Режим 1: вспомни быстро
@Composable
private fun RecallCard(item: Item, speaker: Speaker, onDone: (Boolean) -> Unit) {
    val totalMs = if (item.id.startsWith("w:")) 5000L else 8000L
    var revealed by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    var left by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (!revealed) {
            val elapsed = System.currentTimeMillis() - start
            left = (1f - elapsed.toFloat() / totalMs).coerceAtLeast(0f)
            if (elapsed >= totalMs) {
                revealed = true
                break
            }
            delay(50)
        }
    }
    LaunchedEffect(revealed) {
        if (revealed) speaker.say(item.en)
    }

    fun finish(ok: Boolean) {
        if (done) return
        done = true
        onDone(ok)
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Как сказать по-английски?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text(item.ru, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))

        if (!revealed) {
            LinearProgressIndicator(
                progress = { left },
                modifier = Modifier.fillMaxWidth(),
                color = if (left < 0.3f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Скажи вслух, потом проверь себя", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Button(onClick = { revealed = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Показать ответ")
            }
        } else {
            Text(
                item.en,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            SpeakRow(item.en, speaker)
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { finish(false) }, modifier = Modifier.weight(1f)) {
                    Text("Не вспомнил")
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { finish(true) }, modifier = Modifier.weight(1f)) {
                    Text("Вспомнил")
                }
            }
        }
    }
}

// Режим 2: собери фразу
private fun normalize(s: String): String =
    s.lowercase().replace(Regex("[^a-z0-9' ]"), "").replace(Regex("\\s+"), " ").trim()

@Composable
private fun BuildCard(item: Item, speaker: Speaker, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    val tokens = remember(item) { item.en.split(" ").filter { it.isNotBlank() } }
    val pool = remember(item) {
        val original = tokens.indices.toList()
        var p = original.shuffled()
        if (tokens.size > 1) {
            while (p == original) p = original.shuffled()
        }
        p
    }
    val chosen = remember(item) { mutableStateListOf<Int>() }
    var result by remember(item) { mutableStateOf<Boolean?>(null) }

    Column(Modifier.fillMaxSize()) {
        Text("Собери по-английски:", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(item.ru, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
        ) {
            FlowRow(
                Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chosen.toList().forEach { i ->
                    WordChip(tokens[i], enabled = result == null) { chosen.remove(i) }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pool.filter { it !in chosen }.forEach { i ->
                WordChip(tokens[i], enabled = result == null) {
                    chosen.add(i)
                    speaker.say(tokens[i])
                }
            }
        }

        Spacer(Modifier.weight(1f))

        val r = result
        if (r == null) {
            Button(
                onClick = {
                    val ok = normalize(chosen.joinToString(" ") { tokens[it] }) == normalize(item.en)
                    result = ok
                    onAnswer(ok)
                    speaker.say(item.en)
                },
                enabled = chosen.size == tokens.size,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Проверить") }
        } else {
            Text(
                if (r) "✅ Верно!" else "❌ Правильно так:",
                style = MaterialTheme.typography.titleLarge,
                color = if (r) Good else MaterialTheme.colorScheme.error
            )
            if (!r) {
                Text(item.en, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            SpeakRow(item.en, speaker)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Дальше") }
        }
    }
}

@Composable
private fun WordChip(text: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}

// Режим 3: на слух
@Composable
private fun ListenCard(
    item: Item,
    all: List<Item>,
    speaker: Speaker,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
) {
    val options = remember(item) {
        val others = all.filter { it.ru != item.ru }.shuffled().distinctBy { it.ru }.take(3)
        (others + item).shuffled()
    }
    var picked by remember(item) { mutableStateOf<Item?>(null) }

    LaunchedEffect(item) {
        delay(400)
        speaker.say(item.en)
    }

    Column(Modifier.fillMaxSize()) {
        Text("Послушай и выбери перевод", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        SpeakRow(item.en, speaker)
        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            Text(
                item.en,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
        }

        options.forEach { opt ->
            val p = picked
            val bg = when {
                p == null -> MaterialTheme.colorScheme.surfaceVariant
                opt == item -> Good
                opt == p -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val fg = if (p != null && (opt == item || opt == p)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            Surface(
                onClick = {
                    if (picked == null) {
                        picked = opt
                        onAnswer(opt == item)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                color = bg,
                contentColor = fg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(opt.ru, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(Modifier.weight(1f))
        if (picked != null) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Дальше") }
        }
    }
}

// ---------------- Разговорник ----------------

@Composable
fun BrowseScreen(deck: Deck, store: ProgressStore, speaker: Speaker, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExit) { Text("← Домой") }
            Spacer(Modifier.weight(1f))
            Text(deck.title, style = MaterialTheme.typography.titleLarge)
        }
        Text(
            "Нажми на строку, чтобы услышать. Точки — прогресс, ✅ — выучено.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyColumn(Modifier.fillMaxSize()) {
            items(deck.items, key = { it.id }) { item ->
                val box = store.box(item.id)
                Surface(onClick = { speaker.say(item.en) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.en, style = MaterialTheme.typography.titleMedium)
                            Text(
                                item.ru,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            when {
                                box < 0 -> ""
                                box >= 3 -> "✅"
                                else -> "•".repeat(box + 1)
                            },
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("🔊")
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
