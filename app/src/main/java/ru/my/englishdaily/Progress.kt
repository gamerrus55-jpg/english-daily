package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class Badge(val emoji: String, val title: String, val desc: String, val unlocked: Boolean)

fun badges(store: ProgressStore): List<Badge> {
    val learned = Deck.entries.flatMap { it.items }.distinctBy { it.id }.count { store.box(it.id) >= 3 }
    val dialogsDone = Dialogues.everything.count { store.listened(it.id) > 0 }
    val storiesDone = Stories.everything.count { store.storyBest(it.id) >= 0 }
    val best = store.bestStreak
    val xp = store.xp
    return listOf(
        Badge("👣", "Первый шаг", "Пройди первое занятие", xp > 0),
        Badge("🔥", "Три дня подряд", "Занимайся 3 дня без пропусков", best >= 3),
        Badge("📅", "Неделя", "7 дней подряд", best >= 7),
        Badge("🏅", "Месяц", "30 дней подряд", best >= 30),
        Badge("🌱", "10 фраз", "Выучи 10 карточек", learned >= 10),
        Badge("🌿", "50 фраз", "Выучи 50 карточек", learned >= 50),
        Badge("🌳", "100 фраз", "Выучи 100 карточек", learned >= 100),
        Badge("🎬", "Слушатель", "Прослушай 5 разных диалогов", dialogsDone >= 5),
        Badge("📚", "Читатель", "Пройди 3 истории", storiesDone >= 3),
        Badge("📖", "Все истории", "Пройди все истории", storiesDone >= Stories.everything.size),
        Badge("📝", "Дневник", "Сделай 7 записей в дневнике", store.counter("diary") >= 7),
        Badge("🤖", "Собеседник", "Отправь ИИ 20 сообщений", store.counter("chat") >= 20),
        Badge("⭐", "Коллекционер", "Добавь 20 своих слов", MyWords.items.size >= 20),
        Badge("🚀", "1000 опыта", "Набери 1000 XP", xp >= 1000),
    )
}

@Composable
fun ProgressScreen(store: ProgressStore, onExit: () -> Unit) {
    val (level, inLevel, need) = remember { store.xpLevel() }
    val week = remember { store.week() }
    val list = remember { badges(store) }
    val learned = remember { Deck.entries.flatMap { it.items }.distinctBy { it.id }.count { store.box(it.id) >= 3 } }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TopBar("🏆 Прогресс", onExit)

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Grads.Hero.brush)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(inLevel.toFloat() / need, 96.dp, 10.dp, Color.White.copy(alpha = 0.22f), Color.White) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$level", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                        Text("уровень", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.width(18.dp))
                Column {
                    Text("${store.xp} XP", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    Text("До уровня ${level + 1}: ${need - inLevel} XP", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("🔥 Рекорд серии: ${store.bestStreak}", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        SoftCard(Modifier.fillMaxWidth()) {
            Text("Активность за неделю", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            val max = (week.maxOrNull() ?: 0).coerceAtLeast(1)
            val today = LocalDate.now()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                week.forEachIndexed { i, n ->
                    val date = today.minusDays((6 - i).toLong())
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$n", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height((90f * n / max).coerceAtLeast(4f).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (n > 0) Grads.Hero.brush else androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surfaceVariant))
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("ru")),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (i == 6) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Выучено", "$learned", Modifier.weight(1f))
            StatTile("Мои слова", "${MyWords.items.size}", Modifier.weight(1f))
            StatTile("Дневник", "${store.counter("diary")}", Modifier.weight(1f))
        }

        Text("Достижения · ${list.count { it.unlocked }} из ${list.size}", style = MaterialTheme.typography.titleLarge)
        list.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { b ->
                    SoftCard(
                        Modifier
                            .weight(1f)
                            .height(132.dp)
                            .alpha(if (b.unlocked) 1f else 0.45f)
                    ) {
                        IconTile(if (b.unlocked) b.emoji else "🔒", if (b.unlocked) Grads.Recall else Grads.Book, 40.dp)
                        Spacer(Modifier.height(8.dp))
                        Text(b.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Text(b.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier) {
    SoftCard(modifier) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Hint(label)
    }
}
