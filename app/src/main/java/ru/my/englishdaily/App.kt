package ru.my.englishdaily

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class Mode(val emoji: String, val title: String, val hint: String, val grad: Grad) {
    RECALL("⚡", "Вспомни быстро", "Успей сказать фразу, пока идёт таймер", Grads.Recall),
    AUDIO("🎧", "Аудиоурок", "Слушай и говори, не глядя в экран", Grads.Audio),
    BUILD("🧩", "Собери фразу", "Из перемешанных слов", Grads.Build),
    SPEAK("🎙", "Скажи вслух", "Переведи голосом — приложение проверит", Grads.Speak),
    LISTEN("👂", "На слух", "Услышал — выбери перевод", Grads.Listen),
    DICTATION("✍️", "Диктант", "Услышал — напиши", Grads.Dictation),
    REPEAT("🦜", "Повтори", "За диктором, с оценкой произношения", Grads.Repeat),
}

sealed interface Screen {
    data object Home : Screen
    data class DialogList(val roleplay: Boolean) : Screen
    data class Dialog(val dialogue: Dialogue) : Screen
    data class Roleplay(val dialogue: Dialogue) : Screen
    data class Browse(val deck: Deck) : Screen
    data class Session(val mode: Mode, val deck: Deck, val items: List<Item>) : Screen
    data class Audio(val deck: Deck, val items: List<Item>) : Screen
    data object StoryList : Screen
    data class StoryView(val story: Story) : Screen
    data object Diary : Screen
    data object AiChat : Screen
    data object Settings : Screen
    data object Progress : Screen
}

@Composable
fun App(speaker: Speaker, store: ProgressStore) {
    AppTheme {
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
                    .padding(horizontal = 18.dp, vertical = 12.dp)
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
                                Screen.Session(mode, d, store.buildSession(d.items, size = if (mode == Mode.SPEAK || mode == Mode.REPEAT) 10 else 15))
                            }
                        },
                        onBrowse = { screen = Screen.Browse(deck) },
                        onDialogs = { roleplay -> screen = Screen.DialogList(roleplay) },
                        onOpen = { screen = it },
                    )

                    is Screen.DialogList -> {
                        BackHandler(onBack = goHome)
                        DialogListScreen(
                            store, s.roleplay,
                            onOpen = { d -> screen = if (s.roleplay) Screen.Roleplay(d) else Screen.Dialog(d) },
                            onExit = goHome
                        )
                    }

                    is Screen.Dialog -> {
                        val back = { screen = Screen.DialogList(false) }
                        BackHandler(onBack = back)
                        DialogScreen(
                            s.dialogue, speaker, store,
                            onRoleplay = { screen = Screen.Roleplay(s.dialogue) },
                            onExit = back
                        )
                    }

                    is Screen.Roleplay -> {
                        val back = { screen = Screen.DialogList(true) }
                        BackHandler(onBack = back)
                        RoleplayScreen(s.dialogue, speaker, store, onExit = back)
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

                    Screen.StoryList -> {
                        BackHandler(onBack = goHome)
                        StoryListScreen(store, onOpen = { screen = Screen.StoryView(it) }, onExit = goHome)
                    }

                    is Screen.StoryView -> {
                        val back = { screen = Screen.StoryList }
                        BackHandler(onBack = back)
                        StoryScreen(s.story, speaker, store, onExit = back)
                    }

                    Screen.Diary -> {
                        BackHandler(onBack = goHome)
                        DiaryScreen(store, speaker, onSettings = { screen = Screen.Settings }, onExit = goHome)
                    }

                    Screen.AiChat -> {
                        BackHandler(onBack = goHome)
                        AiChatScreen(store, speaker, onSettings = { screen = Screen.Settings }, onExit = goHome)
                    }

                    Screen.Settings -> {
                        BackHandler(onBack = goHome)
                        SettingsScreen(store, onExit = goHome)
                    }

                    Screen.Progress -> {
                        BackHandler(onBack = goHome)
                        ProgressScreen(store, onExit = goHome)
                    }
                }
            }
        }
    }
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
