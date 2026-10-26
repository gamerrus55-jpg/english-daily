package ru.my.englishdaily

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlin.concurrent.thread

/**
 * Транскрипция по словарю произношений CMU (американский английский):
 * международная [IPA] и примерное звучание русскими буквами.
 */
object Pron {
    private var keys: Array<String> = emptyArray()
    private var vals: Array<String> = emptyArray()

    var ready by mutableStateOf(false)
        private set

    fun load(context: Context) {
        if (ready) return
        val app = context.applicationContext
        thread(name = "pron-load") {
            runCatching {
                val pairs = ArrayList<Pair<String, String>>(140_000)
                app.assets.open("cmudict.dict").bufferedReader().useLines { lines ->
                    lines.forEach { raw ->
                        val line = raw.substringBefore('#').trim()
                        if (line.isEmpty()) return@forEach
                        val sp = line.indexOf(' ')
                        if (sp <= 0) return@forEach
                        val word = line.substring(0, sp)
                        if ('(' in word) return@forEach // запасные варианты пропускаем
                        pairs.add(word to line.substring(sp + 1).trim())
                    }
                }
                pairs.sortBy { it.first }
                val k = Array(pairs.size) { pairs[it].first }
                val v = Array(pairs.size) { pairs[it].second }
                Handler(Looper.getMainLooper()).post {
                    keys = k
                    vals = v
                    ready = true
                }
            }
        }
    }

    private fun phones(word: String): List<String>? {
        if (keys.isEmpty()) return null
        val i = keys.binarySearch(word)
        if (i >= 0) return vals[i].split(' ')
        // Слова через дефис — по частям
        if ('-' in word) {
            val parts = word.split('-').filter { it.isNotEmpty() }.map { phones(it) ?: return null }
            return parts.flatten()
        }
        return null
    }

    private fun words(text: String): List<String> =
        text.lowercase().replace('’', '\'')
            .split(Regex("[^a-z'\\-]+"))
            .map { it.trim('\'', '-') }
            .filter { it.isNotEmpty() }

    private fun isVowel(p: String) = p.last().isDigit()
    private fun base(p: String) = p.trimEnd('0', '1', '2')

    // ---------- IPA ----------

    private val ipaMap = mapOf(
        "AA" to "ɑː", "AE" to "æ", "AO" to "ɔː", "AW" to "aʊ", "AY" to "aɪ", "EH" to "e", "EY" to "eɪ",
        "IH" to "ɪ", "IY" to "iː", "OW" to "oʊ", "OY" to "ɔɪ", "UH" to "ʊ", "UW" to "uː",
        "B" to "b", "CH" to "tʃ", "D" to "d", "DH" to "ð", "F" to "f", "G" to "ɡ", "HH" to "h", "JH" to "dʒ",
        "K" to "k", "L" to "l", "M" to "m", "N" to "n", "NG" to "ŋ", "P" to "p", "R" to "r", "S" to "s",
        "SH" to "ʃ", "T" to "t", "TH" to "θ", "V" to "v", "W" to "w", "Y" to "j", "Z" to "z", "ZH" to "ʒ",
    )

    private val onsets2 = setOf(
        "S P", "S T", "S K", "S M", "S N", "S L", "S W", "P R", "B R", "T R", "D R", "K R", "G R", "F R", "TH R",
        "P L", "B L", "K L", "G L", "F L", "S L", "K W", "T W", "SH R", "P Y", "B Y", "K Y", "F Y", "M Y", "HH Y",
    )

    /** Где начинается ударный слог: индекс фонемы, перед которой ставим знак ударения. */
    private fun stressStart(ph: List<String>, vowelIdx: Int): Int {
        var start = vowelIdx
        if (start > 0 && !isVowel(ph[start - 1])) {
            start--
            if (start > 0 && !isVowel(ph[start - 1]) && "${base(ph[start - 1])} ${base(ph[start])}" in onsets2) start--
        }
        return start
    }

    private fun ipaWord(ph: List<String>): String {
        val vowels = ph.indices.filter { isVowel(ph[it]) }
        val primary = vowels.firstOrNull { ph[it].endsWith("1") }
        val markAt = if (vowels.size > 1 && primary != null) stressStart(ph, primary) else -1
        return buildString {
            ph.forEachIndexed { i, p ->
                if (i == markAt) append('ˈ')
                val b = base(p)
                append(
                    when {
                        b == "AH" -> if (p.endsWith("1")) "ʌ" else "ə"
                        b == "ER" -> if (p.endsWith("1")) "ɜːr" else "ər"
                        else -> ipaMap[b] ?: ""
                    }
                )
            }
        }
    }

    // ---------- Русскими буквами ----------

    private val ruVowel = mapOf(
        "AA" to "а", "AE" to "э", "AO" to "о", "AW" to "ау", "AY" to "ай", "EH" to "э", "EY" to "эй",
        "IH" to "и", "IY" to "и", "OW" to "оу", "OY" to "ой", "UH" to "у", "UW" to "у",
    )
    private val ruCons = mapOf(
        "B" to "б", "CH" to "ч", "D" to "д", "DH" to "ð", "F" to "ф", "G" to "г", "HH" to "х", "JH" to "дж",
        "K" to "к", "L" to "л", "M" to "м", "N" to "н", "NG" to "нг", "P" to "п", "R" to "р", "S" to "с",
        "SH" to "ш", "T" to "т", "TH" to "θ", "V" to "в", "W" to "у", "Z" to "з", "ZH" to "ж",
    )

    private fun ruWord(ph: List<String>): String {
        val vowels = ph.count { isVowel(it) }
        val sb = StringBuilder()
        var i = 0
        while (i < ph.size) {
            val p = ph[i]
            val b = base(p)
            val stressed = vowels > 1 && p.endsWith("1")
            if (isVowel(p)) {
                val v = when (b) {
                    "AH" -> if (p.endsWith("1")) "а" else "э"
                    "ER" -> if (p.endsWith("1")) "ё" else "э"
                    else -> ruVowel[b] ?: ""
                }
                sb.append(v.first())
                if (stressed) sb.append('́')
                sb.append(v.drop(1))
                if (b == "ER") sb.append("р")
            } else if (b == "Y" && i + 1 < ph.size && isVowel(ph[i + 1])) {
                // Й + гласная → я, ю, е
                val next = ph[i + 1]
                val nb = base(next)
                val ns = vowels > 1 && next.endsWith("1")
                val v = when (nb) {
                    "UW", "UH" -> "ю"
                    "AA", "AH", "AE" -> "я"
                    "EH" -> "е"
                    "OW" -> "йоу"
                    "ER" -> "йё"
                    else -> "й" + (ruVowel[nb] ?: "")
                }
                sb.append(v.first())
                if (ns && v.length == 1) sb.append('́')
                if (v.length > 1) {
                    sb.append(v[1])
                    if (ns) sb.append('́')
                    sb.append(v.drop(2))
                }
                if (nb == "ER") sb.append("р")
                i++
            } else if (b == "Y") {
                sb.append("й")
            } else if (b == "NG" && i + 1 < ph.size && base(ph[i + 1]) in setOf("K", "G")) {
                sb.append("н") // think → θинк, а не θингк
            } else {
                sb.append(ruCons[b] ?: "")
            }
            i++
        }
        return sb.toString()
    }

    /** Международная транскрипция фразы, или null, если хоть одно слово неизвестно. */
    fun ipa(text: String): String? {
        val list = words(text).map { phones(it) ?: return null }
        if (list.isEmpty()) return null
        return list.joinToString(" ") { ipaWord(it) }
    }

    /** Звучание русскими буквами (с ударением), или null. */
    fun ru(text: String): String? {
        val list = words(text).map { phones(it) ?: return null }
        if (list.isEmpty()) return null
        return list.joinToString(" ") { ruWord(it) }
    }
}

/**
 * Строка транскрипции под английским словом или фразой: [ʃiːp] · «шип».
 * Показывается, если включена в настройках и все слова есть в словаре.
 */
@Composable
fun PronLine(text: String, modifier: Modifier = Modifier, center: Boolean = false) {
    if (!Pron.ready) return
    if (text.split(" ").size > 10) return
    val store = LocalStore.current
    val ipa = if (store.showIpa) Pron.ipa(text) else null
    val ru = if (store.showRuPron) Pron.ru(text) else null
    if (ipa == null && ru == null) return
    val line = listOfNotNull(ipa?.let { "[$it]" }, ru?.let { "«$it»" }).joinToString("  ·  ")
    Text(
        line,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        modifier = if (center) modifier.fillMaxWidth() else modifier
    )
}
