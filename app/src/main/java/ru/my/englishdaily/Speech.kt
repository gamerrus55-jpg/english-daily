package ru.my.englishdaily

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

// ---------- Распознавание речи ----------

class Recognizer(context: Context) {

    enum class State { IDLE, LISTENING, THINKING }

    val available: Boolean = SpeechRecognizer.isRecognitionAvailable(context)
    private val sr: SpeechRecognizer? = if (available) SpeechRecognizer.createSpeechRecognizer(context) else null

    var state by mutableStateOf(State.IDLE)
        private set
    var level by mutableFloatStateOf(0f)
        private set
    var partial by mutableStateOf("")
        private set

    private var onHeard: ((List<String>) -> Unit)? = null
    private var onFail: ((String) -> Unit)? = null

    init {
        sr?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                state = State.LISTENING
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                level = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                state = State.THINKING
                level = 0f
            }

            override fun onError(error: Int) {
                state = State.IDLE
                level = 0f
                onFail?.invoke(errorText(error))
            }

            override fun onResults(results: Bundle?) {
                state = State.IDLE
                level = 0f
                val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                if (list.isEmpty()) onFail?.invoke(errorText(SpeechRecognizer.ERROR_NO_MATCH))
                else onHeard?.invoke(list)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun start(onHeard: (List<String>) -> Unit, onFail: (String) -> Unit) {
        val r = sr ?: return onFail("На телефоне нет распознавания речи. Установи приложение Google.")
        this.onHeard = onHeard
        this.onFail = onFail
        partial = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        state = State.LISTENING
        r.startListening(intent)
    }

    fun stop() {
        sr?.stopListening()
    }

    fun cancel() {
        sr?.cancel()
        state = State.IDLE
        level = 0f
    }

    fun destroy() {
        sr?.destroy()
    }

    private fun errorText(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
            "Не расслышал. Говори громче и ближе к телефону."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет доступа к микрофону."
        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT, SpeechRecognizer.ERROR_SERVER ->
            "Нужен интернет (или скачай английский для офлайн-распознавания в настройках Google)."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Микрофон занят, нажми ещё раз."
        else -> "Не получилось распознать (код $code). Попробуй ещё раз."
    }
}

@Composable
fun rememberRecognizer(): Recognizer {
    val context = LocalContext.current
    val r = remember { Recognizer(context.applicationContext) }
    DisposableEffect(Unit) { onDispose { r.destroy() } }
    return r
}

// ---------- Оценка сказанного ----------

data class Scored(val tokens: List<String>, val tokenOk: List<Boolean>, val score: Float, val heard: String) {
    val ok: Boolean get() = score >= 0.8f
    val percent: Int get() = (score * 100).toInt()
}

private val numberWords = mapOf(
    0 to "zero", 1 to "one", 2 to "two", 3 to "three", 4 to "four", 5 to "five", 6 to "six",
    7 to "seven", 8 to "eight", 9 to "nine", 10 to "ten", 11 to "eleven", 12 to "twelve",
    13 to "thirteen", 14 to "fourteen", 15 to "fifteen", 16 to "sixteen", 17 to "seventeen",
    18 to "eighteen", 19 to "nineteen", 20 to "twenty", 30 to "thirty", 40 to "forty",
    50 to "fifty", 60 to "sixty", 70 to "seventy", 80 to "eighty", 90 to "ninety",
)

private fun numberToWords(n: Int): List<String> = when {
    n in numberWords -> listOf(numberWords.getValue(n))
    n in 21..99 -> listOf(numberWords.getValue(n / 10 * 10), numberWords.getValue(n % 10))
    n == 100 -> listOf("hundred")
    else -> listOf(n.toString())
}

private val expansions = mapOf(
    "im" to "i am", "youre" to "you are", "theyre" to "they are",
    "hes" to "he is", "shes" to "she is", "its" to "it is", "thats" to "that is",
    "whats" to "what is", "wheres" to "where is", "hows" to "how is", "theres" to "there is",
    "heres" to "here is", "lets" to "let us", "dont" to "do not", "doesnt" to "does not",
    "didnt" to "did not", "cant" to "can not", "cannot" to "can not", "wont" to "will not",
    "isnt" to "is not", "arent" to "are not", "wasnt" to "was not", "havent" to "have not",
    "hasnt" to "has not", "ill" to "i will", "youll" to "you will",
    "thatll" to "that will", "ive" to "i have", "youve" to "you have", "weve" to "we have",
    "id" to "i would", "wanna" to "want to", "gonna" to "going to", "gotta" to "got to",
    "ok" to "okay", "wifi" to "wi fi", "gimme" to "give me", "lemme" to "let me",
    "dunno" to "do not know", "kinda" to "kind of", "sorta" to "sort of",
    "werent" to "were not", "shouldnt" to "should not", "couldnt" to "could not", "wouldnt" to "would not",
    "mustnt" to "must not", "itll" to "it will", "theyll" to "they will", "theyve" to "they have",
    "youd" to "you would", "hed" to "he would", "wed" to "we would", "theyd" to "they would",
    "shouldve" to "should have", "wouldve" to "would have", "couldve" to "could have",
)

/** Слова фразы в «каноническом» виде: без сокращений, числа словами. */
fun canonWords(s: String): List<String> {
    val prepared = s.replace(Regex("\\$(\\d+)"), "$1 dollars")
    return wordsOf(prepared).flatMap { w ->
        val n = w.toIntOrNull()
        when {
            n != null -> numberToWords(n)
            w in expansions -> expansions.getValue(w).split(" ")
            else -> listOf(w)
        }
    }
}

/** Сравнивает сказанное с образцом и подсвечивает каждое слово образца. */
fun scoreSpeech(expected: String, alternatives: List<String>): Scored {
    val tokens = expected.split(" ").filter { it.isNotBlank() }
    val owner = mutableListOf<Int>()
    val flat = mutableListOf<String>()
    tokens.forEachIndexed { t, tok ->
        canonWords(tok).forEach { w ->
            flat.add(w)
            owner.add(t)
        }
    }
    var best: Scored? = null
    for (alt in alternatives.ifEmpty { listOf("") }) {
        val heard = canonWords(alt)
        // Наибольшая общая подпоследовательность: какие слова образца прозвучали по порядку
        val dp = Array(flat.size + 1) { IntArray(heard.size + 1) }
        for (i in flat.indices.reversed()) {
            for (j in heard.indices.reversed()) {
                dp[i][j] = if (flat[i] == heard[j]) dp[i + 1][j + 1] + 1 else maxOf(dp[i + 1][j], dp[i][j + 1])
            }
        }
        val matched = BooleanArray(flat.size)
        var i = 0
        var j = 0
        while (i < flat.size && j < heard.size) {
            when {
                flat[i] == heard[j] -> {
                    matched[i] = true; i++; j++
                }
                dp[i + 1][j] >= dp[i][j + 1] -> i++
                else -> j++
            }
        }
        val okTokens = tokens.indices.map { t -> owner.indices.filter { owner[it] == t }.all { matched[it] } }
        val score = if (flat.isEmpty()) 1f else matched.count { it }.toFloat() / flat.size
        val candidate = Scored(tokens, okTokens, score, alt)
        if (best == null || candidate.score > best.score) best = candidate
    }
    return best!!
}

/** Образец, где каждое слово зелёное (сказал) или красное (пропустил/ошибся). */
@Composable
fun ScoredText(s: Scored) {
    val text = buildAnnotatedString {
        s.tokens.forEachIndexed { i, tok ->
            withStyle(SpanStyle(color = if (s.tokenOk[i]) Good else Bad, fontWeight = FontWeight.Bold)) { append(tok) }
            if (i < s.tokens.lastIndex) append(" ")
        }
    }
    Text(text, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

// ---------- Кнопка микрофона ----------

/**
 * Большая кнопка микрофона с пульсацией по громкости голоса.
 * Сама спрашивает разрешение на микрофон.
 */
@Composable
fun MicButton(
    recognizer: Recognizer,
    speaker: Speaker,
    grad: Grad,
    enabled: Boolean = true,
    onHeard: (List<String>) -> Unit,
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    fun begin() {
        error = null
        speaker.stop()
        recognizer.start(onHeard = onHeard, onFail = { error = it })
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) begin() else error = "Без доступа к микрофону этот режим не работает. Разреши его в настройках."
    }

    fun onTap() {
        if (!enabled) return
        when (recognizer.state) {
            Recognizer.State.LISTENING -> recognizer.stop()
            Recognizer.State.THINKING -> {}
            Recognizer.State.IDLE -> {
                val has = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
                if (has) begin() else permission.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val listening = recognizer.state == Recognizer.State.LISTENING
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val voice by animateFloatAsState(if (listening) recognizer.level else 0f, label = "voice")
    val ringScale = if (listening) pulse + voice * 0.35f else 1f

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
            Box(
                Modifier
                    .size(110.dp)
                    .scale(ringScale)
                    .clip(CircleShape)
                    .background(grad.a.copy(alpha = if (listening) 0.25f else 0.12f))
            )
            Box(
                Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(if (enabled) grad.brush else androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color.Gray, Color.Gray)))
                    .clickable(enabled = enabled) { onTap() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (recognizer.state) {
                        Recognizer.State.LISTENING -> "⏹"
                        Recognizer.State.THINKING -> "…"
                        Recognizer.State.IDLE -> "🎙"
                    },
                    fontSize = 36.sp,
                    color = Color.White
                )
            }
        }
        Text(
            when (recognizer.state) {
                Recognizer.State.LISTENING -> if (recognizer.partial.isNotBlank()) "«${recognizer.partial}»" else "Говори…"
                Recognizer.State.THINKING -> "Распознаю…"
                Recognizer.State.IDLE -> if (enabled) "Нажми и скажи по-английски" else ""
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        error?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, color = Bad, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}
