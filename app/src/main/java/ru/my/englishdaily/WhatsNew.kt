package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Что нового в приложении.
 * Чтобы отметить новинку в следующем обновлении — добавь строку в [WhatsNew.all]
 * с номером сборки, в которой она появилась. Значок пропадёт, когда пользователь откроет раздел.
 */
data class Feature(
    val key: String,
    val build: Int,
    val emoji: String,
    val title: String,
    val desc: String,
)

object WhatsNew {
    val all: List<Feature> = listOf(
        Feature("songs", 19, "🎵", "Мои песни", "Вставь текст любимой песни — ИИ переведёт каждую строку и разберёт сленг, идиомы и сокращения. Нажми на слово — перевод и транскрипция."),
        Feature("wordtap", 19, "👆", "Нажми на слово", "В диалогах, ролевой игре, чате с ИИ и примерах: перевод в контексте, транскрипция и «＋ В повторение» — слово вернётся в карточке «На сегодня»."),
        Feature("pron", 17, "🔤", "Транскрипция", "Под словами и фразами — международная транскрипция [ʃiːp] и звучание русскими буквами «шип» с ударением. Включается и выключается в ⚙️."),
        Feature("judge", 16, "✅", "Свой вариант ответа тоже засчитывается", "В грамматике ИИ проверяет ответ по смыслу, а не дословно. И есть кнопка «✋ Мой ответ тоже верный»."),
        Feature("profiles", 15, "👥", "Несколько пользователей", "У каждого свой прогресс. Переключиться или добавить нового — в ⚙️ настройках. Имена в рейтинге теперь не повторяются."),
        Feature("path", 12, "🗺", "Курс A1 → C1: 80 уроков", "Каждый день — новый урок: живые слова с примерами, грамматика с упражнениями и диалог. Прошёл — открывается следующий."),
        Feature("audio_fix", 12, "🎧", "Аудиоурок больше не повторяется", "Пройденные фразы отмечаются, и в следующий раз будут новые."),
        Feature("rating", 11, "🏆", "Общий рейтинг", "Вводишь имя — и видишь себя и других: опыт, активность за неделю, серия дней, выучено карточек."),
        Feature("usage", 10, "📝", "Слова в контексте", "У каждого слова — фразы с пометкой, где так говорят: в разговоре, на работе, в магазине, в суде. Кнопка «🤖 Ещё примеры»."),
        Feature("multi", 10, "🔀", "Одно слово — разные смыслы", "Новая колода: case, charge, fine, bill, claim… в быту, на работе и в юридическом языке."),
        Feature("dialog_levels", 9, "🎬", "Диалоги по уровням A1–C1", "Простые разговорники, средние, продвинутые и уровень «как в жизни». Фильтр по уровню вверху списка."),
        Feature("live_dialogs", 9, "🗣", "11 новых живых диалогов", "Аптека, шумный сосед, повышение зарплаты, ДТП, свидание, спор о деньгах, встреча через 10 лет…"),
        Feature("roleplay_levels", 9, "🎭", "Ролевая игра по уровням", "Все новые диалоги можно сыграть голосом."),
        Feature("b2_life", 9, "🏠", "Слова B2: жизнь", "Вместо книжных слов — живые бытовые: refund, leftovers, commute, flat tyre, fed up…"),
        Feature("whatsnew", 9, "🔔", "Значок новинок", "После обновления новые разделы отмечаются меткой NEW, а здесь — список изменений."),
        Feature("games", 8, "🎮", "Игры на скорость", "Блиц 60 секунд, «Найди пары» и скоростной ввод."),
        Feature("complex", 8, "🧩", "Сложные предложения", "Колода с разными временами: Past Perfect, Future Perfect, I wish, should have…"),
        Feature("tenses", 8, "📐", "6 уроков по сложным временам", "В разделе «Грамматика», группа «Продвинутый»."),
        Feature("clear", 8, "✕", "Очистка текста", "Кнопка ✕ в полях ввода и «Убрать последнюю фразу» в дневнике и монологе."),
    )

    /** Меняется при каждой отметке — чтобы экраны перерисовывались. */
    var version by mutableIntStateOf(0)
        private set

    fun isNew(store: ProgressStore, key: String): Boolean {
        @Suppress("UNUSED_VARIABLE") val v = version
        val f = all.firstOrNull { it.key == key } ?: return false
        return f.build > store.firstBuild && !store.isSeen(key)
    }

    fun unseen(store: ProgressStore): List<Feature> = all.filter { isNew(store, it.key) }

    fun markSeen(store: ProgressStore, vararg keys: String) {
        keys.forEach { store.markSeen(it) }
        version++
    }

    fun markAll(store: ProgressStore) {
        all.forEach { store.markSeen(it.key) }
        version++
    }
}

/** Красная метка NEW. */
@Composable
fun NewBadge() {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Bad)
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text("NEW", color = Color.White, fontSize = 10.sp, style = MaterialTheme.typography.labelSmall)
    }
}

/** Колокольчик с красным кружком-счётчиком. */
@Composable
fun BellButton(count: Int, onClick: () -> Unit) {
    Box {
        CircleButton("🔔", onClick)
        if (count > 0) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Bad),
                contentAlignment = Alignment.Center
            ) {
                Text("$count", color = Color.White, fontSize = 11.sp, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/** Где в приложении находится новинка. */
fun featureTarget(key: String): Screen? = when (key) {
    "dialog_levels", "live_dialogs" -> Screen.DialogList(false)
    "roleplay_levels" -> Screen.DialogList(true)
    "tenses" -> Screen.GrammarList
    "rating" -> Screen.Rating
    "path" -> Screen.PathMap
    "songs" -> Screen.SongList
    else -> null
}

@Composable
fun WhatsNewScreen(store: ProgressStore, onOpen: (Screen) -> Unit, onExit: () -> Unit) {
    val fresh = WhatsNew.unseen(store)
    val older = WhatsNew.all.filter { it !in fresh && it.build > store.firstBuild }
    Column(Modifier.fillMaxSize()) {
        TopBar("🔔 Что нового", onExit, right = "версия ${BuildConfig.VERSION_NAME}")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (fresh.isEmpty()) {
                item { Hint("Новинок нет — ты всё посмотрел ✓") }
            } else {
                item { Hint("Нажми на новинку — откроется нужный раздел, и метка пропадёт.") }
            }
            items(fresh, key = { it.key }) { f -> FeatureCard(f, true) {
                WhatsNew.markSeen(store, f.key)
                featureTarget(f.key)?.let(onOpen)
            } }
            if (older.isNotEmpty()) {
                item {
                    Text("Уже просмотрено", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
                }
                items(older, key = { "o_" + it.key }) { f -> FeatureCard(f, false) { featureTarget(f.key)?.let(onOpen) } }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
        if (fresh.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            GradientButton("Понятно, отметить всё", { WhatsNew.markAll(store) }, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FeatureCard(f: Feature, fresh: Boolean, onClick: () -> Unit) {
    SoftCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconTile(f.emoji, if (fresh) Grads.Hero else Grads.Book, 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(f.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f, fill = false))
                    if (fresh) {
                        Spacer(Modifier.width(6.dp))
                        NewBadge()
                    }
                }
                Hint(f.desc)
            }
        }
    }
}
