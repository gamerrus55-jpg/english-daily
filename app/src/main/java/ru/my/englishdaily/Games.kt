package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class Game(val emoji: String, val title: String, val hint: String, val grad: Grad) {
    BLITZ("⚡", "Блиц 60 секунд", "Верный ли перевод? Отвечай как можно быстрее, серия умножает очки", Grads.Recall),
    PAIRS("🔗", "Найди пары", "Соедини слово и перевод на скорость", Grads.Build),
    TYPE("⌨️", "Скоростной ввод", "Видишь перевод — печатай английский. 90 секунд", Grads.Dictation),
}

// ---------- ⚡ Блиц: верно или неверно ----------

private data class BlitzCard(val item: Item, val shownRu: String, val isTrue: Boolean)

@Composable
fun BlitzGame(deck: Deck, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val grad = Game.BLITZ.grad
    val pool = remember(deck) { deck.items.filter { it.ru != "—" }.shuffled() }
    var running by remember { mutableStateOf(false) }
    var left by remember { mutableIntStateOf(60) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var answered by remember { mutableIntStateOf(0) }
    var card by remember { mutableStateOf<BlitzCard?>(null) }
    var flash by remember { mutableStateOf<Boolean?>(null) }
    val mistakes = remember { mutableStateListOf<Item>() }
    var finished by remember { mutableStateOf(false) }
    var position by remember { mutableIntStateOf(0) }

    fun nextCard() {
        if (pool.size < 2) return
        val item = pool[position % pool.size]
        position++
        val isTrue = Math.random() < 0.5
        val shown = if (isTrue) item.ru else pool.filter { it.ru != item.ru }.random().ru
        card = BlitzCard(item, shown, isTrue)
    }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (left > 0) {
            delay(1000)
            left--
        }
        running = false
        finished = true
        store.saveBest("blitz", score)
        mistakes.distinctBy { it.id }.forEach { store.answer(it.id, false) }
    }
    LaunchedEffect(flash) {
        if (flash != null) {
            delay(250)
            flash = null
        }
    }

    fun answer(saysTrue: Boolean) {
        val c = card ?: return
        if (!running) return
        val ok = saysTrue == c.isTrue
        answered++
        if (ok) {
            streak++
            score += 10 * (1 + streak / 5)
        } else {
            streak = 0
            mistakes.add(c.item)
        }
        flash = ok
        nextCard()
    }

    Column(Modifier.fillMaxSize()) {
        SessionBar(onExit, left / 60f, "⏱ $left", grad)

        when {
            pool.size < 4 -> {
                Spacer(Modifier.weight(1f))
                BigText("В колоде мало карточек")
                Hint("Выбери другую колоду на главном экране.", center = true)
                Spacer(Modifier.weight(1f))
            }

            finished -> {
                Text("Время вышло! ⏱", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScoreTile("Очки", "$score", Modifier.weight(1f))
                    ScoreTile("Ответов", "$answered", Modifier.weight(1f))
                    ScoreTile("Рекорд", "${store.best("blitz")}", Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                if (mistakes.isNotEmpty()) {
                    Text("Ошибки — вернутся на повторение", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(mistakes.distinctBy { it.id }) { m -> WordRow(m, speaker) }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                    BigText("Без единой ошибки! 🎉")
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Ещё раз", {
                        left = 60; score = 0; streak = 0; answered = 0
                        mistakes.clear(); finished = false
                        nextCard(); running = true
                    }, Modifier.weight(1f))
                    GradientButton("Готово", onExit, Modifier.weight(1f), grad = grad)
                }
            }

            !running -> {
                Spacer(Modifier.weight(1f))
                IconTile("⚡", grad, 96.dp)
                Spacer(Modifier.height(16.dp))
                BigText("Блиц 60 секунд")
                Spacer(Modifier.height(8.dp))
                Hint(
                    "Слово и перевод. Верно — жми ✓, неверно — ✗. Каждые 5 верных подряд — очки за ответ растут. " +
                        "Рекорд: ${store.best("blitz")}",
                    center = true
                )
                Spacer(Modifier.weight(1f))
                GradientButton("Старт", {
                    nextCard(); running = true
                }, Modifier.fillMaxWidth(), grad = grad)
            }

            else -> {
                val c = card
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Очки: $score", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    if (streak >= 3) Text("🔥 ×${1 + streak / 5} · серия $streak", style = MaterialTheme.typography.labelLarge, color = grad.b)
                }
                Spacer(Modifier.height(14.dp))
                if (c != null) {
                    val border = when (flash) {
                        true -> Good
                        false -> Bad
                        null -> MaterialTheme.colorScheme.outlineVariant
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(3.dp, border, RoundedCornerShape(28.dp))
                            .clickable { speaker.say(c.item.en) }
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        BigText(c.item.en, color = grad.b)
                        PronLine(c.item.en, center = true)
                        Spacer(Modifier.height(18.dp))
                        Text("=", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(18.dp))
                        BigText(c.shownRu, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(10.dp))
                        Hint("🔊 нажми, чтобы услышать", center = true)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigChoice("✗ Неверно", Bad, Modifier.weight(1f)) { answer(false) }
                    BigChoice("✓ Верно", Good, Modifier.weight(1f)) { answer(true) }
                }
            }
        }
    }
}

@Composable
private fun BigChoice(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .height(76.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun ScoreTile(label: String, value: String, modifier: Modifier) {
    SoftCard(modifier, radius = 20.dp) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Hint(label)
    }
}

@Composable
private fun WordRow(item: Item, speaker: Speaker) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { speaker.say(item.en) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.en, style = MaterialTheme.typography.titleMedium)
            Hint(item.ru)
        }
        Text("🔊")
    }
}

// ---------- 🔗 Найди пары ----------

@Composable
fun PairsGame(deck: Deck, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val grad = Game.PAIRS.grad
    val rounds = 4
    val perRound = 5
    val pool = remember(deck) { deck.items.filter { it.ru != "—" }.distinctBy { it.ru }.shuffled() }
    var round by remember { mutableIntStateOf(0) }
    var started by remember { mutableStateOf(false) }
    var startAt by remember { mutableLongStateOf(0L) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var errors by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val matched = remember { mutableStateListOf<String>() }
    var leftPick by remember { mutableStateOf<Item?>(null) }
    var wrongFlash by remember { mutableStateOf<String?>(null) }

    val roundItems = remember(round, started) {
        pool.drop(round * perRound).take(perRound).ifEmpty { pool.shuffled().take(perRound) }
    }
    val rightOrder = remember(roundItems) { roundItems.shuffled() }

    LaunchedEffect(started, finished) {
        while (started && !finished) {
            elapsed = System.currentTimeMillis() - startAt
            delay(100)
        }
    }
    LaunchedEffect(wrongFlash) {
        if (wrongFlash != null) {
            delay(400)
            wrongFlash = null
        }
    }

    fun pickRight(item: Item) {
        val l = leftPick ?: return
        if (l.id == item.id) {
            matched.add(item.id)
            leftPick = null
            if (roundItems.all { it.id in matched }) {
                if (round + 1 >= rounds || pool.size <= (round + 1) * perRound) {
                    finished = true
                    elapsed = System.currentTimeMillis() - startAt
                    val seconds = (elapsed / 1000).toInt() + errors * 3
                    store.saveBest("pairs_time", seconds, lowerIsBetter = true)
                    store.bump("games", xp = 20)
                } else {
                    round++
                    matched.clear()
                }
            }
        } else {
            errors++
            wrongFlash = item.id
            store.answer(l.id, false)
            leftPick = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        SessionBar(
            onExit,
            if (finished) 1f else round.toFloat() / rounds,
            "⏱ ${elapsed / 1000}с",
            grad
        )
        when {
            pool.size < perRound -> {
                Spacer(Modifier.weight(1f))
                BigText("В колоде мало карточек")
                Hint("Выбери другую колоду на главном экране.", center = true)
                Spacer(Modifier.weight(1f))
            }

            !started -> {
                Spacer(Modifier.weight(1f))
                IconTile("🔗", grad, 96.dp)
                Spacer(Modifier.height(16.dp))
                BigText("Найди пары")
                Spacer(Modifier.height(8.dp))
                val best = store.best("pairs_time")
                Hint(
                    "$rounds раунда по $perRound пар. Нажми английское слово, потом его перевод. Ошибка — +3 секунды." +
                        if (best > 0) "\nРекорд: $best с" else "",
                    center = true
                )
                Spacer(Modifier.weight(1f))
                GradientButton("Старт", {
                    started = true
                    startAt = System.currentTimeMillis()
                }, Modifier.fillMaxWidth(), grad = grad)
            }

            finished -> {
                val seconds = (elapsed / 1000).toInt() + errors * 3
                Spacer(Modifier.weight(1f))
                BigText("$seconds с", style = MaterialTheme.typography.displayLarge)
                Hint("Время ${elapsed / 1000} с + ошибки $errors × 3 с · рекорд ${store.best("pairs_time")} с", center = true)
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Ещё раз", {
                        round = 0; errors = 0; matched.clear(); finished = false
                        startAt = System.currentTimeMillis()
                    }, Modifier.weight(1f))
                    GradientButton("Готово", onExit, Modifier.weight(1f), grad = grad)
                }
            }

            else -> {
                Hint("Раунд ${round + 1} из $rounds · ошибок: $errors")
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        roundItems.forEach { item ->
                            PairTile(
                                text = item.en,
                                done = item.id in matched,
                                selected = leftPick?.id == item.id,
                                wrong = false,
                                grad = grad,
                                modifier = Modifier.weight(1f)
                            ) {
                                leftPick = item
                                speaker.say(item.en)
                            }
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        rightOrder.forEach { item ->
                            PairTile(
                                text = item.ru,
                                done = item.id in matched,
                                selected = false,
                                wrong = wrongFlash == item.id,
                                grad = grad,
                                modifier = Modifier.weight(1f)
                            ) { pickRight(item) }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PairTile(
    text: String,
    done: Boolean,
    selected: Boolean,
    wrong: Boolean,
    grad: Grad,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                when {
                    done -> Modifier.background(Good.copy(alpha = 0.15f))
                    selected -> Modifier.background(grad.brush)
                    wrong -> Modifier.background(Bad)
                    else -> Modifier.background(MaterialTheme.colorScheme.surface)
                }
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(enabled = !done, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (done) "✓" else text,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            color = when {
                done -> Good
                selected || wrong -> Color.White
                else -> MaterialTheme.colorScheme.onSurface
            },
            maxLines = 4
        )
    }
}

// ---------- ⌨️ Скоростной ввод ----------

@Composable
fun TypeGame(deck: Deck, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val grad = Game.TYPE.grad
    val pool = remember(deck) { deck.items.filter { it.ru != "—" }.shuffled() }
    var running by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var left by remember { mutableIntStateOf(90) }
    var index by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf(false) }
    val skipped = remember { mutableStateListOf<Item>() }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (left > 0) {
            delay(1000)
            left--
        }
        running = false
        finished = true
        store.saveBest("type", score)
        skipped.forEach { store.answer(it.id, false) }
    }

    val item = pool.getOrNull(index % pool.size.coerceAtLeast(1))

    fun next() {
        index++
        input = ""
        hint = false
    }

    Column(Modifier.fillMaxSize()) {
        SessionBar(onExit, left / 90f, "⏱ $left", grad)
        when {
            pool.isEmpty() -> {
                Spacer(Modifier.weight(1f))
                BigText("Колода пустая")
                Spacer(Modifier.weight(1f))
            }

            finished -> {
                Spacer(Modifier.weight(1f))
                BigText("$score", style = MaterialTheme.typography.displayLarge)
                Hint("правильных ответов · рекорд ${store.best("type")}", center = true)
                if (skipped.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text("Пропущено — вернётся на повторение", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(Modifier.heightIn(max = 260.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(skipped.toList()) { WordRow(it, speaker) }
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("Ещё раз", {
                        left = 90; score = 0; skipped.clear(); finished = false; input = ""; running = true
                    }, Modifier.weight(1f))
                    GradientButton("Готово", onExit, Modifier.weight(1f), grad = grad)
                }
            }

            !running -> {
                Spacer(Modifier.weight(1f))
                IconTile("⌨️", grad, 96.dp)
                Spacer(Modifier.height(16.dp))
                BigText("Скоростной ввод")
                Hint("90 секунд. Видишь перевод — печатай английский. Ответ засчитается сам, как только напишешь верно. Рекорд: ${store.best("type")}", center = true)
                Spacer(Modifier.weight(1f))
                GradientButton("Старт", { running = true }, Modifier.fillMaxWidth(), grad = grad)
            }

            item != null -> {
                Text("Очки: $score", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
                    BigText(item.ru, style = MaterialTheme.typography.headlineSmall)
                    if (hint) {
                        Spacer(Modifier.height(8.dp))
                        BigText(item.en.take((item.en.length / 2).coerceAtLeast(2)) + "…", color = grad.a, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(Modifier.height(12.dp))
                ClearableField(
                    value = input,
                    onValue = {
                        input = it
                        if (Grammar.accepts(it, item.en)) {
                            score++
                            speaker.say(item.en)
                            next()
                        }
                    },
                    placeholder = "Пиши по-английски…",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SoftButton("💡 Подсказка", { hint = true }, Modifier.weight(1f), height = 48.dp)
                    SoftButton("Пропустить ›", {
                        skipped.add(item)
                        speaker.say(item.en)
                        next()
                    }, Modifier.weight(1f), height = 48.dp)
                }
            }
        }
    }
}
