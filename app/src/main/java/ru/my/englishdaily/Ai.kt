package ru.my.englishdaily

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/** Через кого работает собеседник-ИИ. */
enum class Provider(
    val title: String,
    val badge: String,
    val hint: String,
    val keyLabel: String,
    val siteUrl: String,
    val instructions: String,
) {
    GIGACHAT(
        "GigaChat (Сбер)", "Бесплатно",
        "365 млн токенов в год бесплатно, умные модели Max и Ultra. Работает в России.",
        "Ключ авторизации (Authorization key)",
        "https://developers.sber.ru/studio",
        "1) Открой developers.sber.ru/studio и войди через Сбер ID.\n" +
            "2) Создай проект «GigaChat API» — для физлиц тариф Freemium бесплатный.\n" +
            "3) В проекте нажми «Настройки API» → «Получить ключ».\n" +
            "4) Скопируй «Ключ авторизации» и вставь сюда."
    ),
    GROQ(
        "Groq", "Бесплатно",
        "Около 1000 запросов в день без карты, модель Llama 3.3 70B. Может не работать из России.",
        "API-ключ (gsk_…)",
        "https://console.groq.com/keys",
        "1) Открой console.groq.com и зарегистрируйся (Google или почта).\n" +
            "2) API Keys → Create API Key.\n" +
            "3) Скопируй ключ и вставь сюда."
    ),
    CLAUDE(
        "Claude (Anthropic)", "Платно",
        "Доли цента за ответ. Недоступен в России.",
        "API-ключ (sk-ant-…)",
        "https://console.anthropic.com/settings/keys",
        "1) Открой console.anthropic.com и зарегистрируйся.\n" +
            "2) Пополни баланс.\n" +
            "3) API Keys → Create Key, скопируй ключ и вставь сюда."
    ),
}

object Ai {
    data class Msg(val role: String, val text: String)

    private lateinit var app: Context

    fun init(context: Context) {
        app = context.applicationContext
    }

    /** Запрос к выбранному в настройках провайдеру. */
    suspend fun ask(store: ProgressStore, system: String, messages: List<Msg>, maxTokens: Int = 600): Result<String> =
        ask(store.provider, store.key(store.provider), system, messages, maxTokens)

    suspend fun ask(provider: Provider, key: String, system: String, messages: List<Msg>, maxTokens: Int = 600): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (key.isBlank()) throw Exception("Сначала добавь ключ в настройках (⚙️ на главном экране).")
                when (provider) {
                    Provider.CLAUDE -> claude(key, system, messages, maxTokens)
                    Provider.GROQ -> groq(key, system, messages, maxTokens)
                    Provider.GIGACHAT -> gigachat(key, system, messages, maxTokens)
                }.trim()
            }.recoverCatching { e ->
                when (e) {
                    is javax.net.ssl.SSLException ->
                        throw Exception("Не удалось установить защищённое соединение. Обнови приложение или проверь интернет.")
                    is java.io.IOException -> throw Exception("Нет связи с сервером. Проверь интернет.")
                    else -> throw e
                }
            }
        }

    // ---------- Общий HTTP ----------

    private class HttpError(val code: Int, val body: String) : Exception("HTTP $code")

    private fun post(
        url: String,
        headers: Map<String, String>,
        body: String,
        ssl: SSLSocketFactory? = null,
    ): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            if (ssl != null && conn is HttpsURLConnection) conn.sslSocketFactory = ssl
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 90000
            headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            conn.outputStream.use { it.write(body.toByteArray()) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw HttpError(code, text)
            return text
        } finally {
            conn.disconnect()
        }
    }

    private fun get(url: String, headers: Map<String, String>): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw HttpError(code, text)
            return text
        } finally {
            conn.disconnect()
        }
    }

    private fun explain(provider: Provider, e: HttpError): Exception {
        val lower = e.body.lowercase()
        val msg = when {
            e.code == 401 -> "Неверный ключ. Проверь его в настройках."
            e.code == 402 -> "Закончился бесплатный лимит или деньги на счёте."
            e.code == 403 -> "Сервис ${provider.title} недоступен из твоего региона. Выбери GigaChat в настройках."
            e.code == 429 -> "Слишком много запросов. Подожди минуту."
            e.code == 400 && "credit" in lower -> "На счёте закончились деньги."
            e.code == 529 || "overloaded" in lower -> "Сервер перегружен. Попробуй чуть позже."
            else -> "Ошибка сервера (${e.code}). Попробуй ещё раз."
        }
        return Exception(msg)
    }

    private fun openAiMessages(system: String, messages: List<Msg>): JSONArray {
        val arr = JSONArray()
        arr.put(JSONObject().put("role", "system").put("content", system))
        messages.forEach { arr.put(JSONObject().put("role", it.role).put("content", it.text)) }
        return arr
    }

    private fun openAiText(json: String): String =
        JSONObject(json).getJSONArray("choices").getJSONObject(0).getJSONObject("message").optString("content")

    // ---------- Claude ----------

    private fun claude(key: String, system: String, messages: List<Msg>, maxTokens: Int): String {
        val arr = JSONArray()
        messages.forEach { arr.put(JSONObject().put("role", it.role).put("content", it.text)) }
        val body = JSONObject()
            .put("model", "claude-haiku-5-5")
            .put("max_tokens", maxTokens)
            .put("system", system)
            .put("messages", arr)
        val text = try {
            post(
                "https://api.anthropic.com/v1/messages",
                mapOf("x-api-key" to key, "anthropic-version" to "2023-06-01", "content-type" to "application/json"),
                body.toString()
            )
        } catch (e: HttpError) {
            throw explain(Provider.CLAUDE, e)
        }
        val content = JSONObject(text).getJSONArray("content")
        return buildString {
            for (i in 0 until content.length()) {
                val c = content.getJSONObject(i)
                if (c.optString("type") == "text") append(c.optString("text"))
            }
        }
    }

    // ---------- Groq (формат OpenAI) ----------

    @Volatile private var groqModel: String? = null

    private fun pickGroqModel(key: String): String {
        groqModel?.let { return it }
        val preferred = listOf("llama-3.3-70b-versatile", "openai/gpt-oss-120b", "qwen/qwen3-32b", "llama-3.1-8b-instant")
        val available = try {
            val json = JSONObject(get("https://api.groq.com/openai/v1/models", mapOf("Authorization" to "Bearer $key")))
            val data = json.getJSONArray("data")
            (0 until data.length()).map { data.getJSONObject(it).getString("id") }
        } catch (e: HttpError) {
            throw explain(Provider.GROQ, e)
        }
        val model = preferred.firstOrNull { it in available }
            ?: available.firstOrNull { "70b" in it }
            ?: available.firstOrNull { "llama" in it }
            ?: available.first()
        groqModel = model
        return model
    }

    private fun groq(key: String, system: String, messages: List<Msg>, maxTokens: Int): String {
        val model = pickGroqModel(key)
        val body = JSONObject()
            .put("model", model)
            .put("max_tokens", if (model.startsWith("openai/gpt-oss")) maxTokens * 3 else maxTokens)
            .put("temperature", 0.7)
            .put("messages", openAiMessages(system, messages))
        val text = try {
            post(
                "https://api.groq.com/openai/v1/chat/completions",
                mapOf("Authorization" to "Bearer $key", "Content-Type" to "application/json"),
                body.toString()
            )
        } catch (e: HttpError) {
            if (e.code == 404) groqModel = null
            throw explain(Provider.GROQ, e)
        }
        return openAiText(text)
    }

    // ---------- GigaChat ----------

    @Volatile private var gigaToken: String? = null
    @Volatile private var gigaTokenUntil: Long = 0
    @Volatile private var gigaTokenKey: String = ""
    @Volatile private var gigaModelIndex = 0
    private val gigaModels = listOf("GigaChat-2-Max", "GigaChat-2-Pro", "GigaChat-2")

    /** Сертификаты НУЦ Минцифры — без них Android не доверяет серверам Сбера. */
    @Volatile private var ruSsl: SSLSocketFactory? = null

    private fun ruSslFactory(): SSLSocketFactory? {
        ruSsl?.let { return it }
        val cf = CertificateFactory.getInstance("X.509")
        val certs = listOf("russian_root_ca.crt", "russian_sub_ca.crt").flatMap { name ->
            try {
                app.assets.open(name).use { input -> cf.generateCertificates(input).filterIsInstance<X509Certificate>() }
            } catch (e: Exception) {
                emptyList()
            }
        }
        if (certs.isEmpty()) return null
        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        certs.forEachIndexed { i, c -> ks.setCertificateEntry("ru$i", c) }
        // Плюс системные сертификаты — на случай, если Сбер сменит цепочку
        try {
            val system = KeyStore.getInstance("AndroidCAStore")
            system.load(null, null)
            for (alias in system.aliases()) {
                val c = system.getCertificate(alias)
                if (c != null) ks.setCertificateEntry("sys_$alias", c)
            }
        } catch (_: Exception) {
        }
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(ks)
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(null, tmf.trustManagers, null)
        return ctx.socketFactory.also { ruSsl = it }
    }

    private fun gigaAccessToken(key: String, ssl: SSLSocketFactory?): String {
        val now = System.currentTimeMillis()
        val cached = gigaToken
        if (cached != null && gigaTokenKey == key && now < gigaTokenUntil - 60_000) return cached
        val text = try {
            post(
                "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                mapOf(
                    "Content-Type" to "application/x-www-form-urlencoded",
                    "Accept" to "application/json",
                    "RqUID" to UUID.randomUUID().toString(),
                    "Authorization" to "Basic ${key.removePrefix("Basic ").trim()}"
                ),
                "scope=" + URLEncoder.encode("GIGACHAT_API_PERS", "UTF-8"),
                ssl
            )
        } catch (e: HttpError) {
            throw if (e.code == 400 || e.code == 401) Exception("GigaChat не принял ключ. Скопируй «Ключ авторизации» целиком.")
            else explain(Provider.GIGACHAT, e)
        }
        val json = JSONObject(text)
        val token = json.getString("access_token")
        gigaToken = token
        gigaTokenKey = key
        gigaTokenUntil = json.optLong("expires_at", now + 25 * 60_000)
        return token
    }

    private fun gigachat(key: String, system: String, messages: List<Msg>, maxTokens: Int): String {
        val ssl = ruSslFactory()
        val token = try {
            gigaAccessToken(key, ssl)
        } catch (e: javax.net.ssl.SSLException) {
            throw Exception(
                if (ssl == null) "Нет сертификата Минцифры для GigaChat. Обнови приложение до последней версии."
                else "Не удалось защищённо подключиться к GigaChat. Проверь интернет."
            )
        }
        while (true) {
            val model = gigaModels[gigaModelIndex.coerceIn(0, gigaModels.lastIndex)]
            val body = JSONObject()
                .put("model", model)
                .put("max_tokens", maxTokens)
                .put("temperature", 0.7)
                .put("messages", openAiMessages(system, messages))
            try {
                val text = post(
                    "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
                    mapOf(
                        "Authorization" to "Bearer $token",
                        "Content-Type" to "application/json",
                        "Accept" to "application/json"
                    ),
                    body.toString(),
                    ssl
                )
                return openAiText(text)
            } catch (e: HttpError) {
                // Закончился лимит у умной модели или она недоступна — пробуем следующую
                if ((e.code == 402 || e.code == 404) && gigaModelIndex < gigaModels.lastIndex) {
                    gigaModelIndex++
                    continue
                }
                if (e.code == 401) gigaToken = null
                throw explain(Provider.GIGACHAT, e)
            }
        }
    }

    /** Какая модель GigaChat сейчас используется. */
    val gigaModel: String get() = gigaModels[gigaModelIndex.coerceIn(0, gigaModels.lastIndex)]

    // ---------- Разбор ответа ----------

    /** Достаёт значение строки вида «ПРЕФИКС: текст» (может быть многострочным до следующего префикса). */
    fun field(text: String, name: String, all: List<String>): String {
        val clean = text.replace("**", "")
        val start = Regex("(?m)^\\s*$name\\s*:").find(clean) ?: return ""
        val rest = clean.substring(start.range.last + 1)
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
    DEBATE("⚔️", "Дебаты", "a friendly debate: pick a common everyday controversial topic (e.g. remote work, social media, cities vs countryside), take the opposite side to the user and ask them to defend their opinion"),
    MEETING("📊", "Рабочая встреча", "a work meeting: you are a colleague discussing a project, deadlines and problems"),
    SMALLTALK("🍻", "Small talk", "casual small talk with a native speaker at a party: weather, weekend plans, hobbies, jokes"),
    STORY("📖", "Расскажи историю", "the user tells you a story from their life; ask follow-up questions and react naturally"),
}
