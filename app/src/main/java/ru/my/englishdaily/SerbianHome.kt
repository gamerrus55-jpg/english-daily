package ru.my.englishdaily

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Главный экран для сербского: колоды, тренировки, прогресс. Английский раздел не затрагивается. */
@Composable
fun SerbianHome(
    store: ProgressStore,
    speaker: Speaker,
    deck: Deck,
    onDeck: (Deck) -> Unit,
    onStart: (Mode) -> Unit,
    onBrowse: () -> Unit,
    onOpen: (Screen) -> Unit,
) {
    val context = LocalContext.current
    val stats = remember(deck) { store.stats(deck.items) }
    val streak = remember { store.streak() }
    val today = remember { store.today() }
    val level = remember { store.xpLevel() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Srpski Daily 🇷🇸", style = MaterialTheme.typography.headlineLarge)
                Hint("Сербский для русскоговорящих · латиница")
            }
            CircleButton("🏆") { onOpen(Screen.Progress) }
            Spacer(Modifier.width(8.dp))
            CircleButton("⚙️") { onOpen(Screen.Settings) }
            Spacer(Modifier.width(8.dp))
            CircleButton("🇬🇧") { Lang.switchTo(context as android.app.Activity, Lang.EN) }
        }

        HeroCard(
            today = today,
            streak = streak,
            stats = stats,
            level = level.first,
            levelProgress = level.second.toFloat() / level.third,
            onClick = { onOpen(Screen.Progress) }
        )

        speaker.problem?.let { TtsWarning(it) }

        SectionTitle("Колода для тренировок", null)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Deck.forLevel(store.level ?: Level.BEGINNER).forEach { d ->
                Pill(d.title, { onDeck(d) }, selected = d == deck)
            }
        }

        SoftCard(Modifier.fillMaxWidth()) {
            Text(deck.title, style = MaterialTheme.typography.titleLarge)
            Hint(Deck.describe(deck))
            Spacer(Modifier.height(10.dp))
            val share = if (stats.total == 0) 0f else stats.learned.toFloat() / stats.total
            PillProgress(share, Modifier.fillMaxWidth(), Grads.Roleplay)
            Spacer(Modifier.height(4.dp))
            Hint("${stats.total} карточек · выучено ${stats.learned} · новых ${stats.fresh} · повторить ${stats.due}")
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SoftButton("📖 Открыть", onBrowse, Modifier.weight(1f), height = 48.dp)
                GradientButton("▶ Тренировать", { onStart(Mode.RECALL) }, Modifier.weight(1f).height(48.dp), grad = Grads.Recall)
            }
        }

        SectionTitle("Режимы", "Все берут карточки из выбранной колоды")
        ModeGrid(listOf(Mode.RECALL, Mode.AUDIO, Mode.BUILD, Mode.SPEAK), deck, onStart)

        WideTile(
            emoji = "🔀",
            title = "Микс",
            hint = "Все типы заданий вперемешку: вспомни, на слух, диктант, вслух, собери фразу",
            grad = Grads.Hero,
            onClick = { onStart(Mode.MIX) }
        )
        WideTile(
            emoji = "🧠",
            title = "Читай и вспоминай",
            hint = "Отрывок с выделенными словами · подумай · впиши слова по памяти, без вариантов",
            grad = Grads.Recall,
            onClick = { onOpen(Screen.ReadList) }
        )

        Spacer(Modifier.height(24.dp))
    }
}
