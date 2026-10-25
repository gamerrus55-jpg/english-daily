package ru.my.englishdaily

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalTime

private const val DAILY_GOAL = 30

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
    onDialogs: (roleplay: Boolean) -> Unit,
    onOpen: (Screen) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val stats = remember(deck) { store.stats(deck.items) }
    val streak = remember { store.streak() }
    val today = remember { store.today() }
    val level = remember { store.level() }
    var updateNote by remember { mutableStateOf<String?>(null) }

    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Доброе утро"
            in 12..16 -> "Добрый день"
            in 17..22 -> "Добрый вечер"
            else -> "Доброй ночи"
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("$greeting 👋", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("English Daily", style = MaterialTheme.typography.headlineLarge)
            }
            CircleButton("🏆") { onOpen(Screen.Progress) }
            Spacer(Modifier.width(8.dp))
            CircleButton("⚙️") { onOpen(Screen.Settings) }
        }

        HeroCard(today = today, streak = streak, stats = stats, level = level.first, levelProgress = level.second.toFloat() / level.third, onClick = { onOpen(Screen.Progress) })

        if (latestBuild != null && latestBuild > Updater.currentBuild) {
            SoftCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile("⬇️", Grads.Hero, 44.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Новая версия", style = MaterialTheme.typography.titleMedium)
                        Hint("Установи поверх — прогресс сохранится")
                    }
                }
                Spacer(Modifier.height(12.dp))
                GradientButton("Скачать обновление", {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Updater.DOWNLOAD_URL)))
                }, Modifier.fillMaxWidth())
            }
        }

        speaker.problem?.let { TtsWarning(it) }
        if (speaker.ready && !speaker.ruReady) {
            Hint("Русский голос не найден — в аудиоуроке русская фраза будет только на экране.")
        }

        // Выбор колоды
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Deck.entries.forEach { d -> Pill(d.title, { onDeck(d) }, selected = d == deck) }
        }

        SectionTitle("Этап 1 · Говорю фразами", "Строй фразы быстро и без запинок")
        ModeGrid(listOf(Mode.RECALL, Mode.AUDIO, Mode.BUILD, Mode.SPEAK), deck, onStart)

        SectionTitle("Этап 2 · Понимаю на слух", "Живые диалоги, диктант и разговорные сокращения")
        WideTile(
            emoji = "🎬",
            title = "Диалоги",
            hint = "${Dialogues.all.size} сценок: кафе, такси, врач, работа… Два голоса, три скорости",
            grad = Grads.Dialogs,
            onClick = { onDialogs(false) }
        )
        ModeGrid(listOf(Mode.LISTEN, Mode.DICTATION), deck, onStart)

        SectionTitle("Этап 3 · Говорю свободно", "Микрофон проверит, как ты говоришь")
        WideTile(
            emoji = "🎭",
            title = "Ролевая игра",
            hint = "Ты — один из героев диалога, приложение — второй",
            grad = Grads.Roleplay,
            onClick = { onDialogs(true) }
        )
        ModeGrid(listOf(Mode.REPEAT), deck, onStart)

        SectionTitle("Этап 4 · Истории и чтение", "Слушай, читай с переводом слов, отвечай на вопросы")
        WideTile(
            emoji = "📚",
            title = "Истории",
            hint = "${Stories.all.size} историй трёх уровней · вопросы голосом · перевод по нажатию на слово",
            grad = Grads.Recall,
            onClick = { onOpen(Screen.StoryList) }
        )

        SectionTitle("Этап 5 · Свободная речь", "Говори своими словами")
        WideTile(
            emoji = "📝",
            title = "Мой день",
            hint = "Дневник на английском: пиши или надиктуй, ИИ поправит ошибки",
            grad = Grads.Roleplay,
            onClick = { onOpen(Screen.Diary) }
        )
        WideTile(
            emoji = "🤖",
            title = "Собеседник-ИИ",
            hint = "Разговор голосом на любую тему с исправлением ошибок",
            grad = Grads.Hero,
            onClick = { onOpen(Screen.AiChat) }
        )

        WideTile(
            emoji = "📖",
            title = "Разговорник",
            hint = "${deck.title}: все фразы с озвучкой и прогрессом",
            grad = Grads.Book,
            onClick = onBrowse
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Hint("Версия ${BuildConfig.VERSION_NAME}")
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                updateNote = "Проверяю…"
                scope.launch {
                    val latest = Updater.latestBuild()
                    onLatestBuild(latest)
                    updateNote = when {
                        latest == null -> "Нет связи с GitHub. Проверь интернет."
                        latest > Updater.currentBuild -> "Есть обновление — карточка вверху."
                        else -> "У тебя последняя версия ✓"
                    }
                }
            }) { Text("Проверить обновление") }
        }
        updateNote?.let { Hint(it) }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun HeroCard(
    today: Int,
    streak: Int,
    stats: ProgressStore.Stats,
    level: Int,
    levelProgress: Float,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Grads.Hero.brush)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(
                progress = today.toFloat() / DAILY_GOAL,
                size = 92.dp,
                stroke = 10.dp,
                track = Color.White.copy(alpha = 0.22f),
                fill = Color.White
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$today", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text("из $DAILY_GOAL", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text("Цель на сегодня", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (today >= DAILY_GOAL) "Выполнена! 🎉" else "$DAILY_GOAL карточек",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (streak > 0) "🔥 $streak ${daysWord(streak)} подряд" else "🔥 Начни серию сегодня",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeroStat("Повторить", stats.due, Modifier.weight(1f))
            HeroStat("Новых", stats.fresh, Modifier.weight(1f))
            HeroStat("Выучено", stats.learned, Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Уровень $level", color = Color.White, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.22f))
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(levelProgress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text("🏆 ›", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun daysWord(n: Int): String {
    val m10 = n % 10
    val m100 = n % 100
    return when {
        m10 == 1 && m100 != 11 -> "день"
        m10 in 2..4 && m100 !in 12..14 -> "дня"
        else -> "дней"
    }
}

@Composable
private fun HeroStat(label: String, value: Int, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$value", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Text(label, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ModeGrid(modes: List<Mode>, deck: Deck, onStart: (Mode) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        modes.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { mode ->
                    val note = if (mode == Mode.BUILD && deck == Deck.WORDS) " (из курса)" else ""
                    SoftCard(
                        Modifier
                            .weight(1f)
                            .height(158.dp),
                        onClick = { onStart(mode) }
                    ) {
                        IconTile(mode.emoji, mode.grad)
                        Spacer(Modifier.height(12.dp))
                        Text(mode.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            mode.hint + note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WideTile(emoji: String, title: String, hint: String, grad: Grad, onClick: () -> Unit) {
    SoftCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconTile(emoji, grad, 52.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Hint(hint)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TtsWarning(problem: Speaker.Problem) {
    val context = LocalContext.current
    SoftCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconTile("🔇", Grads.Speak, 44.dp)
            Spacer(Modifier.width(14.dp))
            Text(
                if (problem == Speaker.Problem.NO_ENGLISH)
                    "Нет английского голоса — озвучки не будет. Скачай его."
                else
                    "Не найден синтезатор речи. Установи «Синтезатор речи Google» из Play Маркета.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        GradientButton("Открыть настройки", {
            val intent = if (problem == Speaker.Problem.NO_ENGLISH)
                Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            else
                Intent("com.android.settings.TTS_SETTINGS")
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }, Modifier.fillMaxWidth(), grad = Grads.Speak)
    }
}

// ---------- Разговорник ----------

@Composable
fun BrowseScreen(deck: Deck, store: ProgressStore, speaker: Speaker, onExit: () -> Unit) {
    val version = MyWords.version
    val items = remember(deck, version) { deck.items }
    val mine = deck == Deck.MINE
    var newEn by remember { mutableStateOf("") }
    var newRu by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        TopBar("📖 ${deck.title}", onExit, right = "${items.size}")
        Hint("Нажми на фразу, чтобы услышать. Точки — прогресс, ✅ — выучено.")
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (mine) {
                item {
                    SoftCard(Modifier.fillMaxWidth()) {
                        Text("＋ Добавить слово или фразу", style = MaterialTheme.typography.titleMedium)
                        Hint("Например, фразу из сериала. Она попадёт во все тренировки.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newEn, onValueChange = { newEn = it },
                            label = { Text("По-английски") }, singleLine = true,
                            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newRu, onValueChange = { newRu = it },
                            label = { Text("Перевод (можно не писать)") }, singleLine = true,
                            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        GradientButton("Добавить", {
                            if (MyWords.add(newEn, newRu)) {
                                speaker.say(newEn)
                                newEn = ""
                                newRu = ""
                            }
                        }, Modifier.fillMaxWidth(), enabled = newEn.isNotBlank())
                    }
                }
                if (items.isEmpty()) {
                    item { Hint("Пока пусто. Добавляй слова здесь или в «Историях» — нажми на слово и «＋ В мои слова».") }
                }
            }
            itemsIndexed(items, key = { _, item -> item.id }) { i, item ->
                if (item.group.isNotEmpty() && (i == 0 || items[i - 1].group != item.group)) {
                    Text(
                        item.group,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 14.dp, bottom = 2.dp)
                    )
                }
                val box = store.box(item.id)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { speaker.say(item.en) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.en, style = MaterialTheme.typography.titleMedium)
                        Hint(item.ru)
                    }
                    Text(
                        when {
                            box < 0 -> ""
                            box >= 3 -> "✅"
                            else -> "•".repeat(box + 1)
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("🔊")
                    if (mine) {
                        Spacer(Modifier.width(10.dp))
                        Text("🗑", modifier = Modifier.clickable { MyWords.remove(item.id) })
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}
