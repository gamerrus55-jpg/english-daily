package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate

private enum class Board(val title: String, val unit: String, val value: (PlayerStats) -> Int) {
    XP("⭐ Опыт", "XP", { it.xp }),
    WEEK("📅 За неделю", "действий", { it.week }),
    STREAK("🔥 Серия", "дн.", { it.streak }),
    LEARNED("✅ Выучено", "карт.", { it.learned }),
}

@Composable
fun LeaderboardScreen(store: ProgressStore, onSettings: () -> Unit, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    var players by remember { mutableStateOf<List<PlayerStats>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var board by remember { mutableStateOf(Board.XP) }

    fun refresh() {
        if (loading) return
        loading = true
        error = null
        scope.launch {
            Leaderboard.upload(store, force = true).onFailure { error = it.message }
            Leaderboard.fetch()
                .onSuccess { list ->
                    // Себя показываем со свежими цифрами, даже если отправка не удалась
                    val me = Leaderboard.myStats(store)
                    players = list.filter { it.id != me.id } + me
                }
                .onFailure { error = it.message }
            loading = false
        }
    }

    LaunchedEffect(Unit) { if (Leaderboard.enabled) refresh() }

    Column(Modifier.fillMaxSize()) {
        TopBar("🏆 Рейтинг", onExit, right = if (loading) "обновляю…" else null)

        if (!Leaderboard.enabled) {
            SoftCard(Modifier.fillMaxWidth()) {
                Text("Рейтинг скоро заработает", style = MaterialTheme.typography.titleMedium)
                Hint("Его нужно один раз подключить при сборке приложения. Как только подключим — здесь появятся все, кто пользуется приложением.")
            }
        } else {

        Row(
            Modifier.horizontalScrollSafe(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Board.entries.forEach { b -> Pill(b.title, { board = b }, selected = board == b) }
        }
        Spacer(Modifier.height(12.dp))

        val list = players
        when {
            list == null && error == null -> {
                Spacer(Modifier.weight(1f))
                Hint("Загружаю…", center = true)
                Spacer(Modifier.weight(1f))
            }

            list == null -> {
                Spacer(Modifier.weight(1f))
                Text(error ?: "", color = Bad, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
                GradientButton("Повторить", { refresh() }, Modifier.fillMaxWidth())
                Spacer(Modifier.weight(1f))
            }

            else -> {
                val sorted = list.sortedWith(compareByDescending<PlayerStats> { board.value(it) }.thenByDescending { it.xp })
                if (sorted.size >= 2) Podium(sorted.take(3), board, store.playerId)
                Spacer(Modifier.height(12.dp))
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(sorted, key = { _, p -> p.id }) { i, p -> PlayerRow(i + 1, p, board, p.id == store.playerId) }
                    item {
                        Spacer(Modifier.height(8.dp))
                        Hint("Чтобы друг появился здесь, отправь ему ссылку на приложение — при первом входе он введёт имя.")
                        error?.let { Text(it, color = Bad, style = MaterialTheme.typography.bodySmall) }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("✏️ Имя", onSettings, Modifier.weight(1f), height = 48.dp)
                    GradientButton(if (loading) "Обновляю…" else "🔄 Обновить", { refresh() }, Modifier.weight(1f))
                }
            }
        }
        }
    }
}

@Composable
private fun Podium(top: List<PlayerStats>, board: Board, me: String) {
    val medals = listOf("🥇", "🥈", "🥉")
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Grads.Hero.brush)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        val order = listOfNotNull(top.getOrNull(1)?.let { 1 to it }, top.getOrNull(0)?.let { 0 to it }, top.getOrNull(2)?.let { 2 to it })
        order.forEach { (place, p) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(medals[place], style = MaterialTheme.typography.headlineLarge)
                Text(
                    p.name + if (p.id == me) " (ты)" else "",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text("${board.value(p)} ${board.unit}", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth(0.8f)
                        .height(if (place == 0) 46.dp else if (place == 1) 32.dp else 22.dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                )
            }
        }
    }
}

@Composable
private fun PlayerRow(place: Int, p: PlayerStats, board: Board, isMe: Boolean) {
    val shape = RoundedCornerShape(18.dp)
    val today = LocalDate.now().toEpochDay()
    val seen = when (val d = today - p.lastDay) {
        0L -> "сегодня"
        1L -> "вчера"
        else -> if (p.lastDay == 0L) "" else "$d дн. назад"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$place", style = MaterialTheme.typography.titleLarge, modifier = Modifier.width(32.dp))
        Column(Modifier.weight(1f)) {
            Text(p.name + if (isMe) " · ты" else "", style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Hint(
                listOfNotNull(
                    p.level.ifBlank { null },
                    "🔥 ${p.streak}",
                    "⭐ ${p.xp}",
                    "✅ ${p.learned}",
                    seen.ifBlank { null },
                ).joinToString(" · ")
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${board.value(p)}", style = MaterialTheme.typography.titleLarge)
            Text(board.unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Экран ввода имени при первом запуске. */
@Composable
fun NamePicker(initial: String, onDone: (String) -> Unit) {
    var name by remember { mutableStateOf(initial) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        IconTile("👋", Grads.Hero, 72.dp)
        Text("Как тебя зовут?", style = MaterialTheme.typography.headlineMedium)
        Hint("Имя увидят другие в общем рейтинге. Без паролей и регистрации — только имя. Поменять можно в ⚙️ настройках.")
        ClearableField(
            value = name,
            onValue = { if (it.length <= 24) name = it },
            placeholder = "Например, Лёша",
            onDone = { if (name.isNotBlank()) onDone(name.trim()) },
            modifier = Modifier.fillMaxWidth()
        )
        GradientButton("Продолжить", { onDone(name.trim()) }, Modifier.fillMaxWidth(), enabled = name.isNotBlank())
    }
}

@Composable
private fun Modifier.horizontalScrollSafe(): Modifier =
    this.then(Modifier.horizontalScroll(rememberScrollState()))
