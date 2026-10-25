@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SessionScreen(s: Screen.Session, speaker: Speaker, store: ProgressStore, onExit: () -> Unit) {
    val queue = remember(s) { mutableStateListOf<Item>().apply { addAll(s.items) } }
    val retried = remember(s) { mutableSetOf<String>() }
    var index by remember(s) { mutableIntStateOf(0) }
    var correct by remember(s) { mutableIntStateOf(0) }
    var answered by remember(s) { mutableIntStateOf(0) }

    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    fun onResult(item: Item, ok: Boolean) {
        store.answer(item.id, ok)
        answered++
        if (ok) {
            correct++
        } else if (retried.add(item.id)) {
            // Ошибся — карточка вернётся в конце этого же занятия
            queue.add(item)
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar(
            s.mode.title + " · " + s.deck.title,
            onExit,
            right = if (index < queue.size) "${index + 1}/${queue.size}" else null
        )
        LinearProgressIndicator(
            progress = { if (queue.isEmpty()) 1f else index.toFloat() / queue.size },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        if (index >= queue.size) {
            FinishView(correct, answered, onExit)
        } else {
            val item = queue[index]
            key(index) {
                when (s.mode) {
                    Mode.RECALL -> RecallCard(item, speaker) { ok ->
                        onResult(item, ok)
                        index++
                    }

                    Mode.BUILD -> BuildCard(
                        item, speaker,
                        onAnswer = { ok -> onResult(item, ok) },
                        onNext = { index++ }
                    )

                    Mode.LISTEN -> ListenCard(
                        item, s.deck.items, speaker,
                        onAnswer = { ok -> onResult(item, ok) },
                        onNext = { index++ }
                    )

                    Mode.DICTATION -> DictationCard(
                        item, speaker,
                        onAnswer = { ok -> onResult(item, ok) },
                        onNext = { index++ }
                    )

                    Mode.AUDIO -> Text("Аудиоурок открывается отдельно")
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.FinishView(correct: Int, answered: Int, onExit: () -> Unit) {
    Spacer(Modifier.weight(1f))
    Text("🎉", style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.CenterHorizontally))
    Text(
        "Готово!",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Text(
        "Правильно: $correct из $answered",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(Modifier.weight(1f))
    Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("На главную") }
}

// ---------- ⚡ Вспомни быстро ----------

@Composable
private fun RecallCard(item: Item, speaker: Speaker, onDone: (Boolean) -> Unit) {
    val totalMs = 3000L + 600L * item.en.split(" ").size
    var revealed by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    var left by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()
        while (!revealed) {
            val elapsed = System.currentTimeMillis() - start
            left = (1f - elapsed.toFloat() / totalMs).coerceAtLeast(0f)
            if (elapsed >= totalMs) {
                revealed = true
                break
            }
            delay(50)
        }
    }
    LaunchedEffect(revealed) {
        if (revealed) speaker.say(item.en)
    }

    fun finish(ok: Boolean) {
        if (done) return
        done = true
        onDone(ok)
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Как сказать по-английски?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (item.group.isNotEmpty()) {
            Text(item.group, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        Text(item.ru, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))

        if (!revealed) {
            LinearProgressIndicator(
                progress = { left },
                modifier = Modifier.fillMaxWidth(),
                color = if (left < 0.3f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Скажи вслух, потом проверь себя", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Button(onClick = { revealed = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Показать ответ")
            }
        } else {
            Text(
                item.en,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            SpeakRow(item.en, speaker)
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { finish(false) }, modifier = Modifier.weight(1f)) {
                    Text("Не вспомнил")
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { finish(true) }, modifier = Modifier.weight(1f)) {
                    Text("Вспомнил")
                }
            }
        }
    }
}

// ---------- 🧩 Собери фразу ----------

@Composable
private fun BuildCard(item: Item, speaker: Speaker, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    val tokens = remember(item) { item.en.split(" ").filter { it.isNotBlank() } }
    val pool = remember(item) {
        val original = tokens.indices.toList()
        var p = original.shuffled()
        if (tokens.size > 1) {
            while (p == original) p = original.shuffled()
        }
        p
    }
    val chosen = remember(item) { mutableStateListOf<Int>() }
    var result by remember(item) { mutableStateOf<Boolean?>(null) }

    Column(Modifier.fillMaxSize()) {
        Text("Собери по-английски:", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(item.ru, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
        ) {
            FlowRow(
                Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chosen.toList().forEach { i ->
                    WordChip(tokens[i], enabled = result == null) { chosen.remove(i) }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pool.filter { it !in chosen }.forEach { i ->
                WordChip(tokens[i], enabled = result == null) {
                    chosen.add(i)
                    speaker.say(tokens[i])
                }
            }
        }

        Spacer(Modifier.weight(1f))

        val r = result
        if (r == null) {
            Button(
                onClick = {
                    val ok = wordsOf(chosen.joinToString(" ") { tokens[it] }) == wordsOf(item.en)
                    result = ok
                    onAnswer(ok)
                    speaker.say(item.en)
                },
                enabled = chosen.size == tokens.size,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Проверить") }
        } else {
            Text(
                if (r) "✅ Верно!" else "❌ Правильно так:",
                style = MaterialTheme.typography.titleLarge,
                color = if (r) Good else MaterialTheme.colorScheme.error
            )
            if (!r) {
                Text(item.en, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            SpeakRow(item.en, speaker)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Дальше") }
        }
    }
}

// ---------- 👂 На слух ----------

@Composable
private fun ListenCard(
    item: Item,
    all: List<Item>,
    speaker: Speaker,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
) {
    val options = remember(item) {
        val others = all.filter { it.ru != item.ru }.shuffled().distinctBy { it.ru }.take(3)
        (others + item).shuffled()
    }
    var picked by remember(item) { mutableStateOf<Item?>(null) }

    LaunchedEffect(item) {
        delay(400)
        speaker.say(item.en)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Послушай и выбери перевод", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        SpeakRow(item.en, speaker)
        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            Text(
                item.en,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
        }

        options.forEach { opt ->
            val p = picked
            val highlighted = p != null && (opt == item || opt == p)
            val bg = when {
                p == null -> MaterialTheme.colorScheme.surfaceVariant
                opt == item -> Good
                opt == p -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Surface(
                onClick = {
                    if (picked == null) {
                        picked = opt
                        onAnswer(opt == item)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                color = bg,
                contentColor = if (highlighted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Text(opt.ru, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        if (picked != null) {
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Дальше") }
        }
    }
}

// ---------- ✍️ Диктант ----------

@Composable
private fun DictationCard(item: Item, speaker: Speaker, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    var text by remember(item) { mutableStateOf("") }
    var result by remember(item) { mutableStateOf<Boolean?>(null) }
    val expected = remember(item) { wordsOf(item.en) }
    val errorColor = MaterialTheme.colorScheme.error

    LaunchedEffect(item) {
        delay(400)
        speaker.say(item.en)
    }

    fun check(giveUp: Boolean = false) {
        if (result != null) return
        val ok = !giveUp && wordsOf(text) == expected
        result = ok
        onAnswer(ok)
        speaker.say(item.en)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Послушай и напиши по-английски", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "Слов: ${expected.size} · переключи клавиатуру на английский",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        SpeakRow(item.en, speaker)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { if (result == null) text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Пиши то, что слышишь") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (text.isNotBlank()) check() }),
            textStyle = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))

        val r = result
        if (r == null) {
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { check(giveUp = true) }, modifier = Modifier.weight(1f)) {
                    Text("Не знаю")
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { check() }, enabled = text.isNotBlank(), modifier = Modifier.weight(1f)) {
                    Text("Проверить")
                }
            }
        } else {
            Text(
                if (r) "✅ Всё верно!" else "❌ Есть ошибки",
                style = MaterialTheme.typography.titleLarge,
                color = if (r) Good else errorColor
            )
            if (!r && text.isNotBlank()) {
                val got = wordsOf(text)
                val annotated = buildAnnotatedString {
                    append("Ты написал: ")
                    got.forEachIndexed { i, w ->
                        val okWord = i < expected.size && expected[i] == w
                        withStyle(SpanStyle(color = if (okWord) Good else errorColor)) { append(w) }
                        append(" ")
                    }
                    if (got.size < expected.size) {
                        withStyle(SpanStyle(color = errorColor)) {
                            append("… не хватает слов: ${expected.size - got.size}")
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(annotated, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.en, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(item.ru, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Дальше") }
        }
    }
}
