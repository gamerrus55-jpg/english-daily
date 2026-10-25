package ru.my.englishdaily

import android.content.Context
import java.time.LocalDate

/**
 * Интервальные повторения (система «коробок»).
 * Ошибся — карточка в коробке 0 и скоро вернётся.
 * Вспомнил — переезжает в следующую коробку и вернётся через 1, 3, 7, 16, 35 дней.
 */
class ProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)
    private val intervalDays = longArrayOf(0, 1, 3, 7, 16, 35)
    private val dayMs = 24L * 60 * 60 * 1000

    private fun raw(id: String): String? = prefs.getString(id, null)

    fun isNew(id: String): Boolean = raw(id) == null

    fun box(id: String): Int = raw(id)?.substringBefore('|')?.toIntOrNull() ?: -1

    fun dueAt(id: String): Long = raw(id)?.substringAfter('|')?.toLongOrNull() ?: 0L

    fun answer(id: String, correct: Boolean) {
        val newBox = if (correct) (box(id) + 1).coerceIn(1, 5) else 0
        val now = System.currentTimeMillis()
        val due = if (newBox == 0) now + 60_000 else now + intervalDays[newBox] * dayMs
        prefs.edit().putString(id, "$newBox|$due").apply()
        markActiveToday()
    }

    /** Набор на одно занятие: сначала то, что пора повторить, потом немного новых. */
    fun buildSession(items: List<Item>, size: Int = 15, maxNew: Int = 8): List<Item> {
        val now = System.currentTimeMillis()
        val due = items
            .filter { !isNew(it.id) && dueAt(it.id) <= now }
            .sortedBy { dueAt(it.id) }
            .take(size)
        val newCount = (size - due.size).coerceIn(0, maxNew)
        val fresh = items.filter { isNew(it.id) }.take(newCount)
        val session = due + fresh
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

    fun streak(): Int {
        val today = LocalDate.now().toEpochDay()
        val last = prefs.getLong("_last_day", -10)
        return if (last >= today - 1) prefs.getInt("_streak", 0) else 0
    }

    private fun markActiveToday() {
        val today = LocalDate.now().toEpochDay()
        val last = prefs.getLong("_last_day", -10)
        if (last == today) return
        val streak = if (last == today - 1) prefs.getInt("_streak", 0) + 1 else 1
        prefs.edit().putLong("_last_day", today).putInt("_streak", streak).apply()
    }
}
