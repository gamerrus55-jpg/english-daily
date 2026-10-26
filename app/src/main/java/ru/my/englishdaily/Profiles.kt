package ru.my.englishdaily

import android.app.Activity
import android.content.Context
import java.io.File
import java.util.UUID

/**
 * Пользователи на одном устройстве. У каждого свой прогресс, свой id в рейтинге.
 * Первый пользователь хранится в старом файле "progress" — чтобы обновление ничего не потеряло.
 */
object Profiles {
    private const val LEGACY = "progress"

    private fun prefs(c: Context) = c.getSharedPreferences("profiles", Context.MODE_PRIVATE)

    /** Имя файла настроек текущего пользователя. */
    fun current(c: Context): String = prefs(c).getString("current", LEGACY) ?: LEGACY

    fun all(c: Context): List<String> {
        val set = prefs(c).getStringSet("all", null) ?: setOf(LEGACY)
        return (set + current(c)).toList().sorted()
    }

    fun nameOf(c: Context, file: String): String =
        c.getSharedPreferences(file, Context.MODE_PRIVATE).getString("_player_name", "").orEmpty()

    fun switchTo(activity: Activity, file: String) {
        prefs(activity).edit().putString("current", file).apply()
        activity.recreate()
    }

    /** Новый пользователь с чистым прогрессом. */
    fun createAndSwitch(activity: Activity) {
        val file = "progress_" + UUID.randomUUID().toString().take(8)
        val set = all(activity).toMutableSet().apply { add(file) }
        prefs(activity).edit().putStringSet("all", set).putString("current", file).apply()
        activity.recreate()
    }

    /**
     * Первый запуск на этом устройстве? Метка лежит в папке, которая не попадает в резервные копии Google:
     * если прогресс есть, а метки нет — данные приехали с другого устройства (или это обновление со старой версии).
     */
    fun checkNewDevice(c: Context): Boolean {
        val marker = File(c.noBackupFilesDir, "device_marker")
        if (marker.exists()) return false
        marker.writeText(System.currentTimeMillis().toString())
        return nameOf(c, current(c)).isNotBlank()
    }
}
