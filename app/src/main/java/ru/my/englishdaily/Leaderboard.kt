package ru.my.englishdaily

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate

/** Статистика одного игрока в общем рейтинге. */
data class PlayerStats(
    val id: String,
    val name: String,
    val level: String,
    val xp: Int,
    val streak: Int,
    val bestStreak: Int,
    val learned: Int,
    val week: Int,
    val today: Int,
    val lastDay: Long,
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("name", name).put("level", level).put("xp", xp)
        .put("streak", streak).put("bestStreak", bestStreak).put("learned", learned)
        .put("week", week).put("today", today).put("lastDay", lastDay)

    companion object {
        fun from(o: JSONObject) = PlayerStats(
            o.optString("id"), o.optString("name", "?"), o.optString("level"),
            o.optInt("xp"), o.optInt("streak"), o.optInt("bestStreak"), o.optInt("learned"),
            o.optInt("week"), o.optInt("today"), o.optLong("lastDay"),
        )
    }
}

/**
 * Общий рейтинг. Каждый телефон хранит свой файл players/<id>.json
 * в отдельном закрытом репозитории GitHub. Ни паролей, ни регистрации — только имя.
 * Ключ доступа подставляется при сборке и даёт права только на этот репозиторий.
 */
object Leaderboard {
    private val token: String get() = BuildConfig.STATS_TOKEN
    private val repo: String get() = BuildConfig.STATS_REPO

    val enabled: Boolean get() = token.isNotBlank()

    private fun open(url: String, method: String = "GET"): HttpURLConnection {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = method
        c.connectTimeout = 10000
        c.readTimeout = 20000
        c.setRequestProperty("Authorization", "Bearer $token")
        c.setRequestProperty("Accept", "application/vnd.github+json")
        c.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        return c
    }

    private fun read(c: HttpURLConnection): Pair<Int, String> {
        val code = c.responseCode
        val body = (if (code in 200..299) c.inputStream else c.errorStream)
            ?.bufferedReader()?.use { it.readText() }.orEmpty()
        c.disconnect()
        return code to body
    }

    fun myStats(store: ProgressStore): PlayerStats {
        val learned = Deck.entries.flatMap { it.items }.distinctBy { it.id }.count { store.box(it.id) >= 3 }
        return PlayerStats(
            id = store.playerId,
            name = store.playerName,
            level = store.level?.cefr ?: "",
            xp = store.xp,
            streak = store.streak(),
            bestStreak = store.bestStreak,
            learned = learned,
            week = store.week().sum(),
            today = store.today(),
            lastDay = LocalDate.now().toEpochDay(),
        )
    }

    /** Отправить свою статистику. Не чаще раза в 2 минуты, если не force. */
    suspend fun upload(store: ProgressStore, force: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (!enabled || store.playerName.isBlank()) return@runCatching
            val now = System.currentTimeMillis()
            if (!force && now - store.lastUpload < 120_000) return@runCatching
            val url = "https://api.github.com/repos/$repo/contents/players/${store.playerId}.json"
            val content = Base64.encodeToString(
                myStats(store).toJson().toString(2).toByteArray(), Base64.NO_WRAP
            )

            fun put(sha: String?): Pair<Int, String> {
                val c = open(url, "PUT")
                c.doOutput = true
                c.setRequestProperty("Content-Type", "application/json")
                val body = JSONObject().put("message", "stats: ${store.playerName}").put("content", content)
                if (sha != null) body.put("sha", sha)
                c.outputStream.use { it.write(body.toString().toByteArray()) }
                return read(c)
            }

            var (code, body) = put(store.statsSha.ifBlank { null })
            if (code == 409 || code == 422) {
                // Узнаём актуальную версию файла и пробуем ещё раз
                val (gc, gb) = read(open(url))
                val sha = if (gc == 200) JSONObject(gb).optString("sha") else null
                val r = put(sha)
                code = r.first
                body = r.second
            }
            if (code !in 200..299) throw Exception("Не удалось отправить статистику ($code)")
            store.statsSha = JSONObject(body).optJSONObject("content")?.optString("sha").orEmpty()
            store.lastUpload = now
        }
    }

    /** Загрузить всех игроков. */
    suspend fun fetch(): Result<List<PlayerStats>> = withContext(Dispatchers.IO) {
        runCatching {
            if (!enabled) throw Exception("Рейтинг ещё не подключён")
            val (code, body) = read(open("https://api.github.com/repos/$repo/contents/players"))
            if (code == 404) return@runCatching emptyList()
            if (code !in 200..299) throw Exception("Не удалось загрузить рейтинг ($code)")
            val files = JSONArray(body)
            (0 until files.length())
                .map { files.getJSONObject(it) }
                .filter { it.optString("name").endsWith(".json") }
                .mapNotNull { f ->
                    runCatching {
                        val c = open(f.getString("url"))
                        c.setRequestProperty("Accept", "application/vnd.github.raw")
                        val (fc, fb) = read(c)
                        if (fc == 200) PlayerStats.from(JSONObject(fb)) else null
                    }.getOrNull()
                }
        }
    }
}
