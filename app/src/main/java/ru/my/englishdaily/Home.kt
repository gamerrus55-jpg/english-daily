@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.my.englishdaily

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    store: ProgressStore,
    speaker: Speaker,
    deck: Deck,
    latestBuild: Int?,
    onLatestBuild: (Int?) -> Unit,
    onDeck: (Deck) -> Unit,
    onStart: (Mode) -> Unit,
    onBrowse: () -> Unit,
    onDialogs: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val stats = remember(deck) { store.stats(deck.items) }
    val streak = remember { store.streak() }
    var updateNote by remember { mutableStateOf<String?>(null) }

    fun openDownload() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Updater.DOWNLOAD_URL)))
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("English Daily", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            if (streak > 0) "🔥 Дней подряд: $streak" else "Начни серию — позанимайся сегодня 15 минут",
            style = MaterialTheme.typography.titleMedium
        )

        if (latestBuild != null && latestBuild > Updater.currentBuild) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Вышла новая версия (сборка $latestBuild)", fontWeight = FontWeight.SemiBold)
                    Text("Скачай и установи поверх — прогресс сохранится.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { openDownload() }) { Text("⬇️ Скачать обновление") }
                }
            }
        }

        speaker.problem?.let { TtsWarning(it) }
        if (speaker.ready && !speaker.ruReady) {
            Text(
                "Русский голос не найден — в аудиоуроке русская фраза будет только на экране.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("План на 15 минут", fontWeight = FontWeight.SemiBold)
                Text(
                    "1) ⚡ Вспомни быстро или 🎧 Аудиоурок — 5 мин\n" +
                        "2) 🎬 Один диалог — 5 мин\n" +
                        "3) ✍️ Диктант или 👂 На слух — 5 мин",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        SectionTitle("ЭТАП 1 · ГОВОРЮ ФРАЗАМИ")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Deck.entries.forEach { d ->
                FilterChip(selected = d == deck, onClick = { onDeck(d) }, label = { Text(d.title) })
            }
        }
        if (deck == Deck.COURSE) {
            Text(
                "${Course.patternCount} разговорных конструкций, новые фразы идут по порядку",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCell("Повторить", stats.due)
                StatCell("Новых", stats.fresh)
                StatCell("Выучено", stats.learned)
            }
        }

        listOf(Mode.RECALL, Mode.AUDIO, Mode.BUILD).forEach { ModeCard(it, deck, onStart) }

        SectionTitle("ЭТАП 2 · ПОНИМАЮ НА СЛУХ")
        Card(onClick = onDialogs, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("🎬 Диалоги", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${Dialogues.all.size} жизненных сценок: кафе, такси, врач, работа… Два голоса, три скорости, повтор за диктором",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        listOf(Mode.LISTEN, Mode.DICTATION).forEach { ModeCard(it, deck, onStart) }
        Text(
            "Совет: для слуха выбери колоды «Живая речь» и «Из диалогов».",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedButton(onClick = onBrowse, modifier = Modifier.fillMaxWidth()) {
            Text("📖 Разговорник: ${deck.title.lowercase()}")
        }

        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Версия ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {
                updateNote = "Проверяю…"
                scope.launch {
                    val latest = Updater.latestBuild()
                    onLatestBuild(latest)
                    updateNote = when {
                        latest == null -> "Нет связи с GitHub. Проверь интернет."
                        latest > Updater.currentBuild -> "Есть обновление — карточка вверху."
                        else -> "У тебя последняя версия."
                    }
                }
            }) { Text("Проверить обновление") }
        }
        updateNote?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ModeCard(mode: Mode, deck: Deck, onStart: (Mode) -> Unit) {
    val note = if (mode == Mode.BUILD && deck == Deck.WORDS) " (из курса)" else ""
    Card(onClick = { onStart(mode) }, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(mode.title + note, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(mode.hint, style = MaterialTheme.typography.bodyMedium)
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

// ---------- Разговорник ----------

@Composable
fun BrowseScreen(deck: Deck, store: ProgressStore, speaker: Speaker, onExit: () -> Unit) {
    val items = deck.items
    Column(Modifier.fillMaxSize()) {
        TopBar("📖 ${deck.title}", onExit, right = "${items.size}")
        Text(
            "Нажми на строку, чтобы услышать. Точки — прогресс, ✅ — выучено.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(items, key = { _, it -> it.id }) { i, item ->
                if (item.group.isNotEmpty() && (i == 0 || items[i - 1].group != item.group)) {
                    Text(
                        item.group,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }
                val box = store.box(item.id)
                Surface(onClick = { speaker.say(item.en) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
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
