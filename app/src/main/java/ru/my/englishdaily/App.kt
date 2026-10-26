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
    data class Session(val mode: Mode, val deck: Deck, val items: List<Item>, val back: Screen? = null) : Screen
    data class Audio(val deck: Deck, val items: List<Item>, val back: Screen? = null) : Screen
    data object PathMap : Screen
    data class UnitView(val n: Int) : Screen
    data class PathGrammar(val n: Int, val lesson: GrammarLesson) : Screen
    data class PathDialog(val n: Int, val dialogue: Dialogue) : Screen
    data class PathRoleplay(val n: Int, val dialogue: Dialogue) : Screen
    data object StoryList : Screen
    data class StoryView(val story: Story) : Screen
    data object Diary : Screen
    data object AiChat : Screen
    data object Settings : Screen
    data object Progress : Screen
    data object GrammarList : Screen
    data class GrammarView(val lesson: GrammarLesson) : Screen
    data object SoundList : Screen
    data class SoundView(val topic: SoundTopic) : Screen
    data object Monologue : Screen
    data object LevelPick : Screen
    data class GameView(val game: Game, val deck: Deck) : Screen
    data object WhatsNewList : Screen
    data object Rating : Screen
}

/** Хранилище прогресса — доступно любому экрану. */
val LocalStore = staticCompositionLocalOf<ProgressStore> { error("ProgressStore не передан") }

@Composable
fun App(speaker: Speaker, store: ProgressStore, askWho: Boolean = false) {
    CompositionLocalProvider(LocalStore provides store) {
    AppTheme {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            var screen by remember { mutableStateOf<Screen>(Screen.Home) }
            var latestBuild by remember { mutableStateOf<Int?>(null) }
            var level by remember { mutableStateOf(store.level) }
            var playerName by remember { mutableStateOf(store.playerName) }
            var whoAsked by remember { mutableStateOf(!askWho) }
            val activity = androidx.compose.ui.platform.LocalContext.current as android.app.Activity
            var deck by remember {
                val lvl = store.level ?: Level.BEGINNER
                mutableStateOf(store.deck?.takeIf { it in Deck.forLevel(lvl) } ?: Deck.forLevel(lvl).first())
            }

            fun chooseDeck(d: Deck) {
                deck = d
                store.deck = d
            }

            fun applyLevel(picked: Level) {
                store.level = picked
                level = picked
                if (deck !in Deck.forLevel(picked)) chooseDeck(Deck.forLevel(picked).first())
            }

            /** Набор карточек под уровень: продвинутым больше новых и меньше повторов. */
            fun session(d: Deck, size: Int): List<Item> {
                val (maxNew, dueCap) = when (level ?: Level.BEGINNER) {
                    Level.BEGINNER -> 8 to size
                    Level.INTERMEDIATE -> 10 to size * 2 / 3
                    Level.ADVANCED -> size to size / 2
                }
                return store.buildSession(d.items, size, maxNew, ordered = d.ordered, dueCap = dueCap)
            }

            // Уровень влияет на скорость речи, таймеры и колоду по умолчанию
            LaunchedEffect(level) {
                val l = level ?: return@LaunchedEffect
                CurrentLevel.value = l
                speaker.speed = if (l == Level.ADVANCED) Speed.FAST else Speed.NORMAL
            }

            LaunchedEffect(Unit) { latestBuild = Updater.latestBuild() }

            val goHome = { screen = Screen.Home }

            Box(
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                if (!whoAsked) {
                    WhoIsHere(
                        name = playerName,
                        onContinue = { whoAsked = true },
                        onNew = { Profiles.createAndSwitch(activity) }
                    )
                } else if (playerName.isBlank()) {
                    NamePicker("", store) { name ->
                        store.playerName = name
                        playerName = name
                    }
                } else if (level == null) {
                    LevelPicker(null) { picked ->
                        applyLevel(picked)
                        chooseDeck(Deck.forLevel(picked).first())
                    }
                } else when (val s = screen) {
                    Screen.Home -> HomeScreen(
                        store = store,
                        speaker = speaker,
                        deck = deck,
                        latestBuild = latestBuild,
                        onLatestBuild = { latestBuild = it },
                        onDeck = { chooseDeck(it) },
                        onStart = { mode ->
                            // Из отдельных слов фразу не собрать — берём фразы своего уровня
                            val d = if (mode == Mode.BUILD && deck.singleWords) {
                                if (level == Level.BEGINNER) Deck.COURSE else Deck.ADV_PHRASES
                            } else deck
                            screen = if (mode == Mode.AUDIO) {
                                Screen.Audio(d, session(d, 20))
                            } else {
                                Screen.Session(mode, d, session(d, if (mode == Mode.SPEAK || mode == Mode.REPEAT) 10 else 15))
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
                        val back = { screen = s.back ?: Screen.Home }
                        BackHandler(onBack = back)
                        SessionScreen(s, speaker, store, onExit = back)
                    }

                    is Screen.Audio -> {
                        val back = { screen = s.back ?: Screen.Home }
                        BackHandler(onBack = back)
                        AudioLessonScreen(s.items, speaker, onExit = back)
                    }

                    Screen.PathMap -> {
                        BackHandler(onBack = goHome)
                        PathMapScreen(store, onOpen = { screen = Screen.UnitView(it.n) }, onExit = goHome)
                    }

                    is Screen.UnitView -> {
                        val unit = PathPlan.units[s.n - 1]
                        val back = { screen = Screen.PathMap }
                        BackHandler(onBack = back)
                        PathUnitScreen(
                            unit, store, speaker,
                            onLearn = { mode, items ->
                                screen = if (mode == Mode.AUDIO) Screen.Audio(Deck.PATH, items, back = s)
                                else Screen.Session(mode, Deck.PATH, items, back = s)
                            },
                            onGrammar = { screen = Screen.PathGrammar(s.n, it) },
                            onDialog = { screen = Screen.PathDialog(s.n, it) },
                            onRoleplay = { screen = Screen.PathRoleplay(s.n, it) },
                            onNext = { screen = Screen.UnitView(it.n) },
                            onSettings = { screen = Screen.Settings },
                            onExit = back
                        )
                    }

                    is Screen.PathGrammar -> {
                        val back = { screen = Screen.UnitView(s.n) }
                        BackHandler(onBack = back)
                        GrammarLessonScreen(s.lesson, speaker, store, onExit = back)
                    }

                    is Screen.PathDialog -> {
                        val back = { screen = Screen.UnitView(s.n) }
                        BackHandler(onBack = back)
                        DialogScreen(s.dialogue, speaker, store, onRoleplay = { screen = Screen.PathRoleplay(s.n, s.dialogue) }, onExit = back)
                    }

                    is Screen.PathRoleplay -> {
                        val back = { screen = Screen.UnitView(s.n) }
                        BackHandler(onBack = back)
                        RoleplayScreen(s.dialogue, speaker, store, onExit = back)
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
                        SettingsScreen(store, onLevel = { screen = Screen.LevelPick }, onExit = goHome)
                    }

                    Screen.LevelPick -> {
                        BackHandler(onBack = { screen = Screen.Settings })
                        LevelPicker(level) { picked ->
                            applyLevel(picked)
                            chooseDeck(Deck.forLevel(picked).first())
                            screen = Screen.Home
                        }
                    }

                    Screen.GrammarList -> {
                        BackHandler(onBack = goHome)
                        GrammarListScreen(store, onOpen = { screen = Screen.GrammarView(it) }, onExit = goHome)
                    }

                    is Screen.GrammarView -> {
                        val back = { screen = Screen.GrammarList }
                        BackHandler(onBack = back)
                        GrammarLessonScreen(s.lesson, speaker, store, onExit = back)
                    }

                    Screen.SoundList -> {
                        BackHandler(onBack = goHome)
                        SoundListScreen(store, onOpen = { screen = Screen.SoundView(it) }, onExit = goHome)
                    }

                    is Screen.SoundView -> {
                        val back = { screen = Screen.SoundList }
                        BackHandler(onBack = back)
                        SoundTopicScreen(s.topic, speaker, store, onExit = back)
                    }

                    Screen.Rating -> {
                        BackHandler(onBack = goHome)
                        LeaderboardScreen(store, onSettings = { screen = Screen.Settings }, onExit = goHome)
                    }

                    Screen.WhatsNewList -> {
                        BackHandler(onBack = goHome)
                        WhatsNewScreen(store, onOpen = { screen = it }, onExit = goHome)
                    }

                    is Screen.GameView -> {
                        BackHandler(onBack = goHome)
                        when (s.game) {
                            Game.BLITZ -> BlitzGame(s.deck, speaker, store, onExit = goHome)
                            Game.PAIRS -> PairsGame(s.deck, speaker, store, onExit = goHome)
                            Game.TYPE -> TypeGame(s.deck, speaker, store, onExit = goHome)
                        }
                    }

                    Screen.Monologue -> {
                        BackHandler(onBack = goHome)
                        MonologueScreen(store, speaker, onSettings = { screen = Screen.Settings }, onExit = goHome)
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
