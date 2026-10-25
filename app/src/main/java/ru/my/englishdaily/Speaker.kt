package ru.my.englishdaily

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/** Озвучка английского через встроенный синтезатор речи Android (работает офлайн). */
class Speaker(context: Context) : TextToSpeech.OnInitListener {

    enum class Problem { NO_ENGINE, NO_ENGLISH }

    private val tts = TextToSpeech(context.applicationContext, this)

    var ready by mutableStateOf(false)
        private set
    var problem by mutableStateOf<Problem?>(null)
        private set

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            problem = Problem.NO_ENGINE
            return
        }
        val result = tts.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            problem = Problem.NO_ENGLISH
        } else {
            problem = null
            ready = true
        }
    }

    fun say(text: String, slow: Boolean = false) {
        if (!ready) return
        tts.setSpeechRate(if (slow) 0.55f else 0.9f)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utt" + System.nanoTime())
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
