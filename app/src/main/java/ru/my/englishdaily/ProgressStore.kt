package ru.my.englishdaily

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/**
 * Весь прогресс хранится на телефоне.
 *
 * Интервальные повторения (система «коробок»):
 * ошибся — карточка в коробке 0 и скоро вернётся;
 * вспомнил — переезжает в следующую коробку и вернётся через 1, 3, 7, 16, 35 дней.
 */
class ProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)
    private val intervalDays = longArrayOf(0, 1, 3, 7, 16, 35)
    private val dayMs = 24L * 60 * 60 * 1000

    private fun raw(id: String): String? = prefs.getString(id, null)
    private fun day(): Long = LocalDate.now().toEpochDay()

    fun isNew(id: String): Boolean = raw(id) == null

    fun box(id: String): Int = raw(id)?.substringBefore('|')?.toIntOrNull() ?: -1

    fun dueAt(id: String): Long = raw(id)?.substringAfter('|')?.toLongOrNull() ?: 0L

    /** «Уже знаю» — карточка уходит в последнюю коробку и не появится полгода. */
    fun markKnown(id: String) {
        val due = System.currentTimeMillis() + 180 * dayMs
        prefs.edit().putString(id, "5|$due").apply()
        activity(5)
    }

    fun answer(id: String, correct: Boolean) {
        val newBox = if (correct) (box(id) + 1).coerceIn(1, 5) else 0
        val now = System.currentTimeMillis()
        val due = if (newBox == 0) now + 60_000 else now + intervalDays[newBox] * dayMs
        prefs.edit().putString(id, "$newBox|$due").apply()
        activity(if (correct) 10 else 3)
    }

    /** Набор на одно занятие: сначала то, что пора повторить, потом немного новых. */
    fun buildSession(
        items: List<Item>,
        size: Int = 15,
        maxNew: Int = 8,
        ordered: Boolean = true,
        dueCap: Int = size,
    ): List<Item> {
        val now = System.currentTimeMillis()
        val allDue = items
            .filter { !isNew(it.id) && dueAt(it.id) <= now }
            .sortedBy { dueAt(it.id) }
        // Повторения занимают не больше dueCap мест — новые слова появляются сразу
        val due = allDue.take(minOf(size, dueCap))
        val newCount = (size - due.size).coerceIn(0, maxNew)
        val freshAll = items.filter { isNew(it.id) }
        val fresh = if (ordered) freshAll.take(newCount) else freshAll.shuffled().take(newCount)
        var session = due + fresh
        // Новых не хватило — добираем оставшимися повторениями
        if (session.size < size) session = session + allDue.drop(due.size).take(size - session.size)
        if (session.isNotEmpty()) return session.shuffled()
        // Всё выучено и повторять пока нечего — тренируем самые слабые
        return items.sortedBy { box(it.id) }.take(size).shuffled()
    }

    data class Stats(val fresh: Int, val due: Int, val learned: Int, val total: Int)

    fun stats(items: List<Item>): Stats {
        val now = System.currentTimeMillis()
        var fresh = 0
        var due = 0
        var learned = 0
        for (item in items) {
            if (isNew(item.id)) {
                fresh++
                continue
            }
            if (dueAt(item.id) <= now) due++
            if (box(item.id) >= 3) learned++
        }
        return Stats(fresh, due, learned, items.size)
    }

    // ---------- Диалоги, истории и прочие счётчики ----------

    fun listened(dialogueId: String): Int = prefs.getInt("_dl_$dialogueId", 0)

    fun markListened(dialogueId: String) {
        prefs.edit().putInt("_dl_$dialogueId", listened(dialogueId) + 1).apply()
        bump("dialogs")
        activity(15)
    }

    fun counter(name: String): Int = prefs.getInt("_c_$name", 0)

    /** Увеличить счётчик (истории, дневник, сообщения ИИ…) и начислить опыт. */
    fun bump(name: String, xp: Int = 0) {
        prefs.edit().putInt("_c_$name", counter(name) + 1).apply()
        if (xp > 0) activity(xp)
    }

    fun storyBest(id: String): Int = prefs.getInt("_st_$id", -1)

    fun saveStoryResult(id: String, percent: Int) {
        if (percent > storyBest(id)) prefs.edit().putInt("_st_$id", percent).apply()
        bump("stories", xp = 30)
    }

    fun grammarBest(id: String): Int = prefs.getInt("_gr_$id", -1)

    fun saveGrammarResult(id: String, percent: Int) {
        if (percent > grammarBest(id)) prefs.edit().putInt("_gr_$id", percent).apply()
        bump("grammar", xp = 25)
    }

    fun soundBest(id: String): Int = prefs.getInt("_snd_$id", -1)

    fun saveSoundResult(id: String, percent: Int) {
        if (percent > soundBest(id)) prefs.edit().putInt("_snd_$id", percent).apply()
        bump("sounds", xp = 15)
    }

    // ---------- Выбранная колода ----------

    var deck: Deck?
        get() = Deck.entries.firstOrNull { it.name == prefs.getString("_deck", null) }
        set(value) = prefs.edit().putString("_deck", value?.name).apply()

    // ---------- Уровень сложности ----------

    /** null — ещё не выбран (покажем выбор при запуске). */
    var level: Level?
        get() = Level.entries.firstOrNull { it.name == prefs.getString("_level", null) }
        set(value) = prefs.edit().putString("_level", value?.name).apply()

    // ---------- Опыт, уровни, активность по дням ----------

    val xp: Int get() = prefs.getInt("_xp", 0)

    /** Уровень и прогресс до следующего: (уровень, набрано на уровне, нужно на уровень). */
    fun xpLevel(): Triple<Int, Int, Int> {
        var level = 1
        var rest = xp
        var need = 100
        while (rest >= need) {
            rest -= need
            level++
            need = 100 + 50 * (level - 1)
        }
        return Triple(level, rest, need)
    }

    /** Сколько действий сделано сегодня — для кольца «цель дня». */
    fun today(): Int = prefs.getInt("_h_${day()}", 0)

    /** Активность за последние 7 дней, от старого к сегодняшнему. */
    fun week(): List<Int> {
        val d = day()
        return (6 downTo 0).map { prefs.getInt("_h_${d - it}", 0) }
    }

    private fun activity(xpGain: Int) {
        val d = day()
        val e = prefs.edit()
            .putInt("_xp", xp + xpGain)
            .putInt("_h_$d", today() + 1)
        val last = prefs.getLong("_last_day", -10)
        if (last != d) {
            val streak = if (last == d - 1) prefs.getInt("_streak", 0) + 1 else 1
            e.putLong("_last_day", d).putInt("_streak", streak)
            if (streak > prefs.getInt("_best_streak", 0)) e.putInt("_best_streak", streak)
        }
        e.apply()
    }

    fun streak(): Int {
        val last = prefs.getLong("_last_day", -10)
        return if (last >= day() - 1) prefs.getInt("_streak", 0) else 0
    }

    val bestStreak: Int get() = maxOf(prefs.getInt("_best_streak", 0), streak())

    // ---------- Настройки ----------

    var provider: Provider
        get() = Provider.entries.firstOrNull { it.name == prefs.getString("_provider", null) }
            ?: if (prefs.getString("_api_key", "").isNullOrBlank()) Provider.GIGACHAT else Provider.CLAUDE
        set(value) = prefs.edit().putString("_provider", value.name).apply()

    fun key(p: Provider): String {
        val legacy = if (p == Provider.CLAUDE) prefs.getString("_api_key", "").orEmpty() else ""
        return prefs.getString("_key_${p.name}", legacy).orEmpty()
    }

    fun setKey(p: Provider, value: String) {
        prefs.edit().putString("_key_${p.name}", value.trim()).apply()
    }

    /** Есть ли ключ у выбранного провайдера ИИ. */
    val aiReady: Boolean get() = key(provider).isNotBlank()

    // ---------- Дневник ----------

    data class DiaryEntry(val day: Long, val text: String, val corrected: String, val notes: String)

    fun diary(): List<DiaryEntry> {
        val arr = JSONArray(prefs.getString("_diary", "[]"))
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            DiaryEntry(o.getLong("day"), o.optString("text"), o.optString("corrected"), o.optString("notes"))
        }.sortedByDescending { it.day }
    }

    fun todayDiary(): DiaryEntry? = diary().firstOrNull { it.day == day() }

    fun saveDiary(text: String, corrected: String, notes: String) {
        val isNewDay = todayDiary() == null
        val list = diary().filter { it.day != day() } + DiaryEntry(day(), text, corrected, notes)
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("day", it.day).put("text", it.text).put("corrected", it.corrected).put("notes", it.notes))
        }
        prefs.edit().putString("_diary", arr.toString()).apply()
        if (isNewDay) bump("diary", xp = 20)
    }
}
