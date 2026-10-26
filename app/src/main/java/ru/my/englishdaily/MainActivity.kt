package ru.my.englishdaily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    private lateinit var speaker: Speaker
    private lateinit var store: ProgressStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MyWords.init(this)
        Ai.init(this)
        PathStore.init(this)
        speaker = Speaker(this)
        store = ProgressStore(this)
        store.initFirstBuild(BuildConfig.VERSION_CODE)
        setContent { App(speaker, store) }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }
}
