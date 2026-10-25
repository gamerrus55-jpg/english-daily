package ru.my.englishdaily

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.resume

enum class Speed(val title: String, val rate: Float) {
    SLOW("🐢 Медленно", 0.6f),
    NORMAL("Обычно", 0.9f),
    FAST("🐇 Быстро", 1.15f),
}

/**
 * Озвучка через встроенный синтезатор речи Android (работает офлайн).
 * Три «голоса»: английский A, английский B (ниже тоном) и русский.
 */
class Speaker(context: Context) {

    enum class Problem { NO_ENGINE, NO_ENGLISH }

    private val app = context.applicationContext
    private val waiting = ConcurrentHashMap<String, () -> Unit>()
    private val counter = AtomicLong()

    var ready by mutableStateOf(false)
        private set
    var ruReady by mutableStateOf(false)
        private set
    var problem by mutableStateOf<Problem?>(null)
        private set
    var speed by mutableStateOf(Speed.NORMAL)

    private val listener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {}
        override fun onDone(utteranceId: String?) = release(utteranceId)

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) = release(utteranceId)
        override fun onError(utteranceId: String?, errorCode: Int) = release(utteranceId)
        override fun onStop(utteranceId: String?, interrupted: Boolean) = release(utteranceId)
    }

    private fun release(id: String?) {
        if (id != null) waiting.remove(id)?.invoke()
    }

    private val en: TextToSpeech = TextToSpeech(app) { status -> onEnInit(status) }
    private val en2: TextToSpeech = TextToSpeech(app) { status -> onEn2Init(status) }
    private val ru: TextToSpeech = TextToSpeech(app) { status -> onRuInit(status) }

    init {
        en.setOnUtteranceProgressListener(listener)
        en2.setOnUtteranceProgressListener(listener)
        ru.setOnUtteranceProgressListener(listener)
    }

    private fun bad(result: Int) =
        result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED

    private fun onEnInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            problem = Problem.NO_ENGINE
            return
        }
        if (bad(en.setLanguage(Locale.US))) {
            problem = Problem.NO_ENGLISH
        } else {
            en.setPitch(1.05f)
            problem = null
            ready = true
        }
    }

    private fun onEn2Init(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            en2.setLanguage(Locale.US)
            en2.setPitch(0.75f)
        }
    }

    private fun onRuInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ruReady = !bad(ru.setLanguage(Locale.forLanguageTag("ru-RU")))
        }
    }

    /** Сказать по-английски сразу (для кнопок). */
    fun say(text: String, slow: Boolean = false) {
        if (!ready) return
        stop()
        en.setSpeechRate(if (slow) 0.55f else speed.rate)
        en.speak(text, TextToSpeech.QUEUE_FLUSH, null, "s" + counter.incrementAndGet())
    }

    /** Сказать по-английски и дождаться конца фразы. */
    suspend fun speakEn(text: String, secondVoice: Boolean = false, rate: Float = speed.rate) {
        if (!ready) return
        await(if (secondVoice) en2 else en, text, rate)
    }

    /** Сказать по-русски и дождаться конца фразы. */
    suspend fun speakRu(text: String) {
        if (!ruReady) return
        await(ru, text, 1.0f)
    }

    private suspend fun await(tts: TextToSpeech, text: String, rate: Float) {
        suspendCancellableCoroutine<Unit> { cont ->
            val id = "w" + counter.incrementAndGet()
            waiting[id] = { if (cont.isActive) cont.resume(Unit) }
            cont.invokeOnCancellation {
                waiting.remove(id)
                tts.stop()
            }
            tts.setSpeechRate(rate)
            if (tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, id) != TextToSpeech.SUCCESS) {
                waiting.remove(id)?.invoke()
            }
        }
    }

    fun stop() {
        en.stop()
        en2.stop()
        ru.stop()
    }

    fun shutdown() {
        waiting.values.toList().forEach { it() }
        waiting.clear()
        en.shutdown()
        en2.shutdown()
        ru.shutdown()
    }
}
