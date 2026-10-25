package ru.my.englishdaily

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Собеседник-ИИ через Claude API (модель Haiku — самая быстрая и дешёвая).
 * Ключ хранится только на телефоне.
 */
object Ai {
    const val MODEL = "claude-haiku-5-5"
    const val KEYS_URL = "https://console.anthropic.com/settings/keys"

    data class Msg(val role: String, val text: String)

    suspend fun ask(key: String, system: String, messages: List<Msg>, maxTokens: Int = 500): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (key.isBlank()) throw Exception("Сначала добавь API-ключ в настройках (⚙️ на главном экране).")
                val conn = URL("https://api.anthropic.com/v1/messages").openConnection() as HttpURLConnection
                try {
                    conn.requestMethod = "POST"
                    conn.doOutput = true
                    conn.connectTimeout = 15000
                    conn.readTimeout = 60000
                    conn.setRequestProperty("x-api-key", key)
                    conn.setRequestProperty("anthropic-version", "2023-06-01")
                    conn.setRequestProperty("content-type", "application/json")

                    val arr = JSONArray()
                    messages.forEach { arr.put(JSONObject().put("role", it.role).put("content", it.text)) }
                    val body = JSONObject()
                        .put("model", MODEL)
                        .put("max_tokens", maxTokens)
                        .put("system", system)
                        .put("messages", arr)
                    conn.outputStream.use { it.write(body.toString().toByteArray()) }

                    val code = conn.responseCode
                    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                    val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
                    if (code !in 200..299) throw Exception(explain(code, text))

                    val content = JSONObject(text).getJSONArray("content")
                    buildString {
                        for (i in 0 until content.length()) {
                            val c = content.getJSONObject(i)
                            if (c.optString("type") == "text") append(c.optString("text"))
                        }
                    }.trim()
                } finally {
                    conn.disconnect()
                }
            }.recoverCatching { e ->
                if (e is java.io.IOException) throw Exception("Нет связи с сервером. Проверь интернет.")
                throw e
            }
        }

    private fun explain(code: Int, body: String): String {
        val lower = body.lowercase()
        return when {
            code == 401 -> "Неверный API-ключ. Проверь его в настройках."
            code == 429 -> "Слишком много запросов. Подожди минуту."
            code == 400 && "credit" in lower -> "На счёте Anthropic закончились деньги. Пополни баланс в консоли."
            code == 529 || "overloaded" in lower -> "Сервер перегружен. Попробуй чуть позже."
            else -> "Ошибка сервера ($code). Попробуй ещё раз."
        }
    }

    /** Достаёт значение строки вида «ПРЕФИКС: текст» (может быть многострочным до следующего префикса). */
    fun field(text: String, name: String, all: List<String>): String {
        val start = Regex("(?m)^\\s*$name\\s*:").find(text) ?: return ""
        val rest = text.substring(start.range.last + 1)
        val next = all.filter { it != name }
            .mapNotNull { Regex("(?m)^\\s*$it\\s*:").find(rest)?.range?.first }
            .minOrNull() ?: rest.length
        return rest.substring(0, next).trim()
    }
}

/** Темы для разговора с ИИ. */
enum class ChatTopic(val emoji: String, val title: String, val prompt: String) {
    FREE("💬", "Свободная тема", "a free friendly chat about anything; start by asking how the user's day is going"),
    MEET("👋", "Знакомство", "meeting a new person at a party: names, where you are from, work, hobbies"),
    CAFE("☕", "В кафе", "you are a waiter in a cafe and the user is a customer ordering food and drinks"),
    SHOP("🛍", "Магазин", "you are a shop assistant in a clothes shop and the user wants to buy something"),
    TRAVEL("✈️", "Путешествие", "talking about travel: places, plans, hotels, transport"),
    JOB("💼", "Собеседование", "a simple, friendly job interview; you are the interviewer"),
    DOCTOR("🩺", "У врача", "you are a doctor and the user is a patient describing how they feel"),
    CARS("🚗", "Машины", "talking about cars, driving, repairs and roads"),
    MOVIES("🎬", "Фильмы и сериалы", "talking about favourite movies and TV series"),
}
