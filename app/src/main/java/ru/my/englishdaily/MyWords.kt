package ru.my.englishdaily

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

/** Колода «Мои слова»: всё, что ты сам добавил из историй, сериалов и т. п. */
object MyWords {
    private lateinit var prefs: SharedPreferences
    private var cache: List<Item> = emptyList()

    /** Меняется при каждом изменении — чтобы экраны перерисовывались. */
    var version by mutableIntStateOf(0)
        private set

    val items: List<Item> get() = cache

    fun init(context: Context) {
        prefs = context.getSharedPreferences("mywords", Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString("items", "[]"))
        cache = (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            Item(o.getString("id"), o.getString("en"), o.getString("ru"))
        }
    }

    fun contains(en: String): Boolean = cache.any { it.en.equals(en.trim(), ignoreCase = true) }

    fun add(en: String, ru: String): Boolean {
        val e = en.trim()
        if (e.isEmpty() || contains(e)) return false
        cache = cache + Item("m:${e.lowercase()}", e, ru.trim().ifEmpty { "—" })
        save()
        return true
    }

    fun remove(id: String) {
        cache = cache.filter { it.id != id }
        save()
    }

    private fun save() {
        val arr = JSONArray()
        cache.forEach { arr.put(JSONObject().put("id", it.id).put("en", it.en).put("ru", it.ru)) }
        prefs.edit().putString("items", arr.toString()).apply()
        version++
    }
}

/** Словарь для перевода слов в историях: сначала словарь истории, потом общий. */
object Dictionary {
    private val basic = mapOf(
        "i" to "я", "you" to "ты, вы", "he" to "он", "she" to "она", "it" to "оно, это", "we" to "мы",
        "they" to "они", "me" to "меня, мне", "him" to "его, ему", "her" to "её, ей", "us" to "нас",
        "them" to "их, им", "my" to "мой", "your" to "твой, ваш", "his" to "его", "our" to "наш",
        "their" to "их", "the" to "(определённый артикль)", "a" to "(неопределённый артикль)",
        "an" to "(неопределённый артикль)", "is" to "есть, является", "are" to "есть, являются",
        "am" to "есть (я)", "was" to "был, была", "were" to "были", "be" to "быть", "been" to "был (бывал)",
        "at" to "в, у, на", "in" to "в", "on" to "на", "to" to "к, в, чтобы", "for" to "для, на",
        "of" to "(родительный падеж)", "with" to "с", "from" to "из, от", "about" to "о, про",
        "into" to "в (внутрь)", "and" to "и", "but" to "но", "or" to "или", "so" to "так, поэтому",
        "not" to "не", "no" to "нет", "yes" to "да", "this" to "этот, это", "that" to "тот, что",
        "there" to "там", "here" to "здесь", "then" to "потом, тогда", "when" to "когда", "what" to "что",
        "who" to "кто", "where" to "где", "how" to "как", "why" to "почему", "very" to "очень",
        "too" to "тоже, слишком", "all" to "все, всё", "one" to "один", "two" to "два", "three" to "три",
        "do" to "делать", "does" to "делает", "did" to "сделал", "doesn't" to "не (делает)",
        "don't" to "не (делай)", "didn't" to "не (сделал)", "can" to "может", "will" to "будет",
        "has" to "имеет, есть", "have" to "иметь", "had" to "имел", "says" to "говорит", "said" to "сказал",
        "go" to "идти", "goes" to "идёт", "today" to "сегодня", "now" to "сейчас", "day" to "день",
        "man" to "мужчина", "woman" to "женщина", "people" to "люди", "home" to "дом, домой",
        "house" to "дом", "work" to "работа", "good" to "хороший", "new" to "новый", "old" to "старый",
        "time" to "время", "after" to "после", "before" to "до, перед", "out" to "наружу", "up" to "вверх",
        "some" to "немного, некоторые", "any" to "какой-нибудь", "much" to "много", "more" to "больше",
        "it's" to "это (it is)", "i'm" to "я (I am)", "he's" to "он (he is)", "she's" to "она (she is)",
        "won't" to "не будет", "couldn't" to "не мог", "hadn't" to "не (сделал раньше)",
        "i'll" to "я (сделаю)", "we'll" to "мы (сделаем)",
    )

    private val common: Map<String, String> by lazy {
        Content.words.associate { it.en.lowercase() to it.ru } + basic
    }

    fun clean(word: String): String =
        word.lowercase().replace('’', '\'').trim { !it.isLetterOrDigit() && it != '\'' }

    fun lookup(word: String, glossary: Map<String, String>): String? {
        val w = clean(word)
        if (w.isEmpty()) return null
        val candidates = listOf(
            w,
            w.removeSuffix("'s"),
            w.removeSuffix("s"),
            w.removeSuffix("es"),
            w.removeSuffix("ed"),
            w.removeSuffix("d"),
            w.removeSuffix("ing"),
            w.removeSuffix("ing") + "e",
        )
        for (c in candidates) {
            glossary[c]?.let { return it }
            common[c]?.let { return it }
        }
        return null
    }
}
