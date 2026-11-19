package ru.my.englishdaily

import android.app.Activity
import android.content.Context

/**
 * Язык изучения. Интерфейс и переводы всегда русские.
 * Для сербского используется латиница (ekavica).
 * У каждого языка свои колоды; прогресс карточек общий, id сербских карточек начинаются с "sr:".
 */
enum class Lang(
    val code: String,
    val title: String,
    val ruName: String,
    /** Языки озвучки по порядку: если первого нет в телефоне, пробуем следующий. */
    val ttsTags: List<String>,
    val sttTag: String,
) {
    EN("en", "🇬🇧 Английский", "английского", listOf("en-US"), "en-US"),
    SR("sr", "🇷🇸 Сербский", "сербского", listOf("sr-RS", "sr-Latn-RS", "hr-HR", "bs-BA"), "sr-RS");

    companion object {
        private const val PREFS = "app_language"

        @Volatile
        var active: Lang = EN
            private set

        fun load(c: Context) {
            val code = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("lang", "en")
            active = entries.firstOrNull { it.code == code } ?: EN
        }

        fun switchTo(activity: Activity, lang: Lang) {
            activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("lang", lang.code).apply()
            active = lang
            activity.recreate()
        }
    }
}

private val srCyr = mapOf(
    'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d", 'ђ' to "dj", 'е' to "e", 'ж' to "z",
    'з' to "z", 'и' to "i", 'ј' to "j", 'к' to "k", 'л' to "l", 'љ' to "lj", 'м' to "m", 'н' to "n",
    'њ' to "nj", 'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t", 'ћ' to "c", 'у' to "u",
    'ф' to "f", 'х' to "h", 'ц' to "c", 'ч' to "c", 'џ' to "dz", 'ш' to "s",
)

private val srLat = mapOf('č' to "c", 'ć' to "c", 'š' to "s", 'ž' to "z", 'đ' to "dj")

/**
 * Сербский текст для сравнения ответов: кириллица → латиница, без диакритики.
 * Распознавание речи может вернуть и кириллицу, и латиницу — после этой функции они совпадают.
 */
fun srFold(s: String): String {
    val sb = StringBuilder()
    for (ch in s.lowercase()) {
        val rep = srCyr[ch] ?: srLat[ch]
        if (rep != null) sb.append(rep) else sb.append(ch)
    }
    return sb.toString()
}

fun srWords(s: String): List<String> =
    srFold(s)
        .replace(Regex("[^a-z0-9 ]"), " ")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
