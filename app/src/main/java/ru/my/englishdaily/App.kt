@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class Mode(val title: String, val hint: String) {
    RECALL("⚡ Вспомни быстро", "Фраза по-русски — успей сказать её по-английски"),
    AUDIO("🎧 Аудиоурок", "Как Pimsleur: слушаешь, говоришь вслух, сверяешь. Можно не смотреть в экран"),
    BUILD("🧩 Собери фразу", "Собери английскую фразу из перемешанных слов"),
    LISTEN("👂 На слух", "Слушаешь английский — выбираешь перевод"),
    DICTATION("✍️ Диктант", "Слушаешь и пишешь. Сразу видно, что не расслышал"),
}

sealed interface Screen {
    data object Home : Screen
    data object DialogList : Screen
    data class Dialog(val dialogue: Dialogue) : Screen
    data class Browse(val deck: Deck) : Screen
    data class Session(val mode: Mode, val deck: Deck, val items: List<Item>) : Screen
    data class Audio(val deck: Deck, val items: List<Item>) : Screen
}

val Good = Color(0xFF2E9E4F)

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
            var deck by remember { mutableStateOf(Deck.COURSE) }
            var latestBuild by remember { mutableStateOf<Int?>(null) }

            LaunchedEffect(Unit) { latestBuild = Updater.latestBuild() }

            val goHome = { screen = Screen.Home }

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
                        latestBuild = latestBuild,
                        onLatestBuild = { latestBuild = it },
                        onDeck = { deck = it },
                        onStart = { mode ->
                            val d = if (mode == Mode.BUILD && deck == Deck.WORDS) Deck.COURSE else deck
                            screen = if (mode == Mode.AUDIO) {
                                Screen.Audio(d, store.buildSession(d.items, size = 20))
                            } else {
                                Screen.Session(mode, d, store.buildSession(d.items))
                            }
                        },
                        onBrowse = { screen = Screen.Browse(deck) },
                        onDialogs = { screen = Screen.DialogList },
                    )

                    Screen.DialogList -> {
                        BackHandler(onBack = goHome)
                        DialogListScreen(store, onOpen = { screen = Screen.Dialog(it) }, onExit = goHome)
                    }

                    is Screen.Dialog -> {
                        val back = { screen = Screen.DialogList }
                        BackHandler(onBack = back)
                        DialogScreen(s.dialogue, speaker, store, onExit = back)
                    }

                    is Screen.Browse -> {
                        BackHandler(onBack = goHome)
                        BrowseScreen(s.deck, store, speaker, onExit = goHome)
                    }

                    is Screen.Session -> {
                        BackHandler(onBack = goHome)
                        SessionScreen(s, speaker, store, onExit = goHome)
                    }

                    is Screen.Audio -> {
                        BackHandler(onBack = goHome)
                        AudioLessonScreen(s.items, speaker, onExit = goHome)
                    }
                }
            }
        }
    }
}

// ---------- Общие элементы ----------

@Composable
fun TopBar(title: String, onExit: () -> Unit, right: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onExit) { Text("← Назад") }
        Spacer(Modifier.width(4.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        if (right != null) Text(right, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SpeakRow(text: String, speaker: Speaker) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilledTonalButton(onClick = { speaker.say(text) }) { Text("🔊 Ещё раз") }
        FilledTonalButton(onClick = { speaker.say(text, slow = true) }) { Text("🐢 Медленно") }
    }
}

@Composable
fun WordChip(text: String, enabled: Boolean, onClick: () -> Unit) {
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

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/** Слова фразы без знаков препинания — для сравнения ответов. */
fun wordsOf(s: String): List<String> =
    s.lowercase()
        .replace('’', '\'')
        .replace(Regex("[^a-z0-9' ]"), " ")
        .replace("'", "")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

/** Русский текст без пояснений в скобках — для озвучки. */
fun ruForSpeech(s: String): String = s.replace(Regex("\\s*\\(.*?\\)"), "").trim()
