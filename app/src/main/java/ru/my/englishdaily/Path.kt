package ru.my.englishdaily

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Курс-путь: 80 уроков от A1 до C1.
 * Программа (тема + грамматика) задана здесь, а содержимое каждого урока
 * генерирует ИИ один раз и сохраняет на телефоне.
 */
data class PathUnit(
    val n: Int,
    val band: String,
    val emoji: String,
    val title: String,
    val topic: String,
    val grammar: String,
) {
    val id: String get() = "u$n"
}

object PathPlan {
    private fun u(n: Int, band: String, emoji: String, title: String, topic: String, grammar: String) =
        PathUnit(n, band, emoji, title, topic, grammar)

    val units: List<PathUnit> = listOf(
        // A1
        u(1, "A1", "👋", "Знакомство", "meeting people, saying where you are from and what you do", "verb to be: am / is / are"),
        u(2, "A1", "👨‍👩‍👧", "Семья", "family members and describing relatives", "have got / has got, possessive adjectives (my, your, his, her)"),
        u(3, "A1", "⏰", "Мой день", "daily routine from morning to evening", "Present Simple: affirmative, he/she + s"),
        u(4, "A1", "☕", "Кафе и еда", "ordering food and drinks in a cafe", "I'd like, some / any, can I have"),
        u(5, "A1", "🛒", "В магазине", "shopping for everyday things, prices", "How much / how many, this / that / these / those"),
        u(6, "A1", "🏙", "Город", "places in town and asking the way", "there is / there are, prepositions of place"),
        u(7, "A1", "🏠", "Мой дом", "rooms, furniture and household items", "prepositions in / on / under / next to"),
        u(8, "A1", "🗓", "Время и расписание", "telling the time, days, appointments", "prepositions of time: at / on / in"),
        u(9, "A1", "🌦", "Погода и одежда", "weather and what people are wearing now", "Present Continuous"),
        u(10, "A1", "🎸", "Хобби", "free time, likes and abilities", "like + -ing, can / can't"),
        u(11, "A1", "💼", "Работа", "jobs and what people do at work", "Present Simple questions and negatives (do / does)"),
        u(12, "A1", "📸", "Прошлые выходные", "what you did last weekend", "Past Simple of to be: was / were"),
        u(13, "A1", "🧳", "Поездка", "a short trip: transport, hotel, sightseeing", "Past Simple: regular and common irregular verbs"),
        u(14, "A1", "🤒", "Самочувствие", "feeling ill, simple health problems", "have a headache / a cold, should"),
        u(15, "A1", "🗺", "Планы", "plans for the weekend and holidays", "be going to"),
        // A2
        u(16, "A2", "🏨", "В отеле", "checking in, problems with the room, requests", "can / could for requests"),
        u(17, "A2", "🚆", "Транспорт", "buying tickets, delays, taxis and buses", "Past Simple questions and negatives"),
        u(18, "A2", "🧑‍🤝‍🧑", "Друзья", "describing people's appearance and character", "comparatives (-er, more)"),
        u(19, "A2", "🎉", "Праздники", "birthdays, celebrations, presents", "superlatives (the -est, the most)"),
        u(20, "A2", "🩺", "У врача", "describing symptoms and getting advice", "should / shouldn't, must / mustn't"),
        u(21, "A2", "👕", "Одежда", "buying clothes, sizes, trying on, returns", "too / enough"),
        u(22, "A2", "📞", "Звонок", "phone calls: leaving messages, making arrangements", "will for offers and quick decisions"),
        u(23, "A2", "🏡", "Жильё", "renting a flat, describing your home", "Present Perfect with ever / never"),
        u(24, "A2", "⚽", "Спорт", "sports, gym, healthy habits", "Present Perfect vs Past Simple"),
        u(25, "A2", "🎬", "Кино и сериалы", "films and series, giving opinions", "adjectives -ed / -ing (bored / boring)"),
        u(26, "A2", "🍝", "Ресторан", "booking a table, ordering, paying the bill", "much / many / a lot of, countable and uncountable"),
        u(27, "A2", "🧸", "Детство", "memories of childhood", "used to"),
        u(28, "A2", "📱", "Телефон и интернет", "apps, settings, tech problems", "imperatives and giving instructions"),
        u(29, "A2", "✈️", "Отпуск", "planning a holiday, booking", "will vs be going to"),
        u(30, "A2", "🔁", "Привычки", "habits and what's happening now", "Present Simple vs Present Continuous, adverbs of frequency"),
        // B1
        u(31, "B1", "🧑‍💼", "Собеседование", "job interview: experience and strengths", "Present Perfect with for / since"),
        u(32, "B1", "✉️", "Рабочая переписка", "work messages and polite requests", "polite requests: Could you…? Would you mind…?"),
        u(33, "B1", "😵", "Проблемы на работе", "things that went wrong at work", "Past Continuous vs Past Simple"),
        u(34, "B1", "💞", "Отношения", "dating, friendships and relationships", "Present Perfect Continuous"),
        u(35, "B1", "📰", "Новости", "everyday news and events", "passive voice: present and past"),
        u(36, "B1", "🏦", "Деньги и банк", "bank, cards, saving and spending", "first conditional"),
        u(37, "B1", "🔑", "Аренда", "renting, landlords, bills and rules", "have to / don't have to / mustn't"),
        u(38, "B1", "🥗", "Здоровый образ жизни", "diet, sleep, stress, sport", "gerund vs infinitive (enjoy doing, decide to do)"),
        u(39, "B1", "🧳", "Неприятности в поездке", "lost luggage, missed flights, problems abroad", "Past Perfect"),
        u(40, "B1", "💻", "Технологии", "gadgets and apps in everyday life", "relative clauses: who / which / that / where"),
        u(41, "B1", "📦", "Покупки онлайн", "ordering online, delivery, returns, refunds", "reported speech: statements"),
        u(42, "B1", "🔧", "Бытовые проблемы", "repairs, neighbours, broken things at home", "have something done"),
        u(43, "B1", "💭", "Мечты", "dreams and imaginary situations", "second conditional"),
        u(44, "B1", "🌳", "Город или деревня", "pros and cons of city and country life", "as … as, comparing things"),
        u(45, "B1", "🍳", "Готовим", "cooking, recipes, kitchen talk", "sequencing words and quantities"),
        u(46, "B1", "🚗", "Машина", "car problems, service, driving", "have something done / get something fixed"),
        u(47, "B1", "😊", "Чувства", "emotions and how you feel about things", "adjectives + prepositions (afraid of, good at)"),
        u(48, "B1", "📖", "Истории", "telling a story about something that happened", "narrative tenses"),
        u(49, "B1", "🎄", "Традиции", "holidays and traditions in different countries", "passive with traditions (is celebrated)"),
        u(50, "B1", "🎓", "Учёба", "courses, learning new skills", "may / might / could for possibility"),
        // B2
        u(51, "B2", "🤝", "Переговоры", "negotiating price and conditions", "diplomatic language and conditionals"),
        u(52, "B2", "😤", "Жалобы и сервис", "complaining effectively and getting results", "reported questions and requests"),
        u(53, "B2", "📈", "Карьера", "career plans and goals", "future continuous and future perfect"),
        u(54, "B2", "🗣", "Мнение и спор", "giving and defending opinions", "linking words: although, however, despite"),
        u(55, "B2", "😔", "Сожаления", "regrets and things you'd change", "I wish / if only"),
        u(56, "B2", "🔙", "Если бы тогда", "past mistakes and what could have happened", "third conditional"),
        u(57, "B2", "🕵️", "Догадки", "guessing what happened", "must have / can't have / might have"),
        u(58, "B2", "📢", "Слухи и новости", "rumours, news and gossip", "it is said that / he is believed to"),
        u(59, "B2", "💹", "Деньги и вложения", "money decisions, loans, investments", "mixed conditionals"),
        u(60, "B2", "🧘", "Стресс и отдых", "stress, burnout, relaxing", "common phrasal verbs"),
        u(61, "B2", "⚡", "Конфликты", "arguments and making up", "should have / shouldn't have"),
        u(62, "B2", "🏡", "Удалённая работа", "working from home, meetings online", "contrast and quantifiers"),
        u(63, "B2", "🌍", "Путевые истории", "dramatic travel stories", "past perfect continuous"),
        u(64, "B2", "🤖", "Будущее", "technology and the future", "future forms and predictions"),
        u(65, "B2", "☕", "Small talk", "chatting with colleagues and strangers", "question tags"),
        // C1
        u(66, "C1", "😂", "Юмор и сарказм", "jokes, sarcasm and banter", "informal spoken English"),
        u(67, "C1", "🗯", "Живая речь", "how people really talk: fillers, slang", "contractions and informal structures"),
        u(68, "C1", "🎯", "Убеждаем", "persuading people", "emphasis: what I need is…, it was … that"),
        u(69, "C1", "📊", "Деловая встреча", "running a meeting, disagreeing politely", "hedging and softening"),
        u(70, "C1", "🫥", "Трудные разговоры", "bad news, feedback, saying no", "softening language"),
        u(71, "C1", "🎭", "Истории из жизни", "telling a gripping personal story", "narrative techniques and tenses"),
        u(72, "C1", "💼", "Идиомы работы", "office life idioms", "idioms in context"),
        u(73, "C1", "❤️", "Идиомы отношений", "love, friendship and family idioms", "idioms in context"),
        u(74, "C1", "⚖️", "Дебаты", "debating a controversial topic", "concession: admittedly, granted, even so"),
        u(75, "C1", "🎤", "Выступление", "giving a short presentation", "signposting language"),
        u(76, "C1", "🧾", "Права потребителя", "consumer rights, formal complaints", "formal vs informal register"),
        u(77, "C1", "🏥", "Медицина подробно", "detailed conversation with a doctor", "passive and medical collocations"),
        u(78, "C1", "📜", "Юридические ситуации", "contracts, fines, insurance — in plain words", "legal vs everyday meaning of words"),
        u(79, "C1", "🌐", "Культурные различия", "manners and culture shock", "modal verbs of obligation and advice in context"),
        u(80, "C1", "🏁", "Свободный разговор", "anything: review of everything", "mixed tenses review"),
    )

    /** С какого урока начинать по уровню. */
    fun startFor(level: Level): Int = when (level) {
        Level.BEGINNER -> 1
        Level.INTERMEDIATE -> 31
        Level.ADVANCED -> 51
    }

    val bands = listOf("A1", "A2", "B1", "B2", "C1")
}

/** Сгенерированное содержимое урока, сохранённое на телефоне. */
object PathStore {
    private lateinit var dir: File

    /** Меняется при каждом сохранении — чтобы экраны перерисовывались. */
    var version by mutableIntStateOf(0)
        private set

    fun init(context: Context) {
        dir = File(context.filesDir, "path").apply { mkdirs() }
    }

    private fun file(unit: PathUnit, part: String) = File(dir, "${unit.id}_$part.json")

    fun has(unit: PathUnit, part: String) = file(unit, part).exists()

    fun load(unit: PathUnit, part: String): JSONObject? =
        runCatching { JSONObject(file(unit, part).readText()) }.getOrNull()

    fun save(unit: PathUnit, part: String, json: JSONObject) {
        file(unit, part).writeText(json.toString())
        version++
        vocabCache = null
    }

    fun delete(unit: PathUnit, part: String) {
        file(unit, part).delete()
        version++
        vocabCache = null
    }

    // ---------- Разбор ----------

    fun vocab(unit: PathUnit): List<Item> {
        val o = load(unit, "vocab") ?: return emptyList()
        val arr = o.optJSONArray("items") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val it = arr.getJSONObject(i)
            val en = it.optString("en").trim()
            if (en.isEmpty()) return@mapNotNull null
            val item = Item("p${unit.n}:$en", en, it.optString("ru").trim(), "${unit.emoji} ${unit.title}")
            val ex = it.optString("example").trim()
            if (ex.isNotEmpty()) {
                Usages.register(item, Usage(ex, it.optString("exampleRu").trim(), "💬 " + it.optString("context", "в разговоре").trim()))
            }
            item
        }
    }

    private var vocabCache: List<Item>? = null

    /** Все слова из начатых уроков — колода «Слова из уроков». */
    fun allVocab(): List<Item> = vocabCache ?: PathPlan.units.flatMap { vocab(it) }.also { vocabCache = it }

    fun grammar(unit: PathUnit): GrammarLesson? {
        val o = load(unit, "grammar") ?: return null
        val table = o.optJSONArray("table")?.let { t ->
            (0 until t.length()).mapNotNull { i ->
                val row = t.optJSONArray(i)
                if (row != null && row.length() >= 2) row.optString(0) to row.optString(1)
                else t.optJSONObject(i)?.let { it.optString("form") to it.optString("meaning") }
            }
        } ?: emptyList()
        val examples = o.optJSONArray("examples")?.let { e ->
            (0 until e.length()).map { i -> e.getJSONObject(i).let { Example(it.optString("en"), it.optString("ru")) } }
        } ?: emptyList()
        val tasks = o.optJSONArray("tasks")?.let { t ->
            (0 until t.length()).mapNotNull { i ->
                val x = t.getJSONObject(i)
                when (x.optString("type")) {
                    "transform" -> Task.Transform(x.optString("from"), x.optString("task"), x.optString("answer"))
                        .takeIf { it.from.isNotBlank() && it.answer.isNotBlank() }
                    "translate" -> Task.Translate(x.optString("ru"), x.optString("answer"))
                        .takeIf { it.ru.isNotBlank() && it.answer.isNotBlank() }
                    "choice" -> {
                        val opts = x.optJSONArray("options")?.let { a -> (0 until a.length()).map { a.optString(it) } } ?: emptyList()
                        val correct = x.optInt("correct", -1)
                        if (opts.size >= 2 && correct in opts.indices && "___" in x.optString("sentence"))
                            Task.Choice(x.optString("sentence"), opts, correct, x.optString("ru"))
                        else null
                    }
                    else -> null
                }
            }
        } ?: emptyList()
        if (tasks.isEmpty()) return null
        return GrammarLesson(
            "path_${unit.id}", unit.emoji, o.optString("title").ifBlank { unit.grammar }, 0,
            o.optString("explain"), table, examples, tasks
        )
    }

    fun dialogue(unit: PathUnit): Dialogue? {
        val o = load(unit, "dialog") ?: return null
        val arr = o.optJSONArray("lines") ?: return null
        val lines = (0 until arr.length()).mapNotNull { i ->
            val l = arr.getJSONObject(i)
            val en = l.optString("en").trim()
            if (en.isEmpty()) null else Line(l.optInt("who", i % 2).coerceIn(0, 1), en, l.optString("ru").trim())
        }
        if (lines.size < 2) return null
        val level = when (unit.band) { "A1", "A2" -> 1; "B1" -> 2; "B2" -> 3; else -> 4 }
        return Dialogue("path_${unit.id}", unit.emoji, o.optString("title").ifBlank { unit.title }, lines, level)
    }
}

/** Генерация содержимого урока через ИИ. */
object PathGen {
    private const val RULES = """
The learner is a Russian speaker. Use natural, everyday spoken English that people really use in daily life.
Avoid bookish, literary or academic words. Russian translations must be natural Russian.
Reply with valid JSON only, no markdown, no comments."""

    private fun levelHint(band: String) = when (band) {
        "A1" -> "Level A1: very simple, short sentences, the most common words."
        "A2" -> "Level A2: simple sentences, common everyday words."
        "B1" -> "Level B1: everyday words that learners usually don't know yet, common phrasal verbs."
        "B2" -> "Level B2: natural spoken English, phrasal verbs, collocations, still everyday."
        else -> "Level C1: how native speakers really talk — idioms, fillers (you know, I mean), humour, natural rhythm; still everyday, not bookish."
    }

    private fun vocabPrompt(u: PathUnit) = """
$RULES
${levelHint(u.band)}
Topic: ${u.topic}.
Create 12 useful words or short phrases for this topic that a learner at this level probably doesn't know yet.
For each give: Russian translation, ONE natural example sentence, its Russian translation, and the context where it is used (in Russian, 1–3 words, e.g. "в разговоре", "на работе", "в магазине", "у врача", "официально").
JSON: {"items":[{"en":"...","ru":"...","example":"...","exampleRu":"...","context":"..."}]}
""".trimIndent()

    private fun grammarPrompt(u: PathUnit) = """
$RULES
${levelHint(u.band)}
Grammar point: ${u.grammar}. Use examples about: ${u.topic}.
Write a short lesson in the style of a step-by-step textbook: a clear explanation in Russian (3–6 sentences, compare with Russian where helpful), a small table of forms, 6 examples, and 10 exercises:
4 of type "transform" (change a sentence: make a question, negative, change tense etc.; "task" is the instruction in Russian),
3 of type "translate" (Russian sentence to translate into English),
3 of type "choice" (sentence with ___ and 3 options, "correct" is the index of the right option).
In "answer" you can give several correct variants separated by " | " (with and without contractions).
JSON: {"title":"...","explain":"...","table":[["form","meaning in Russian"]],"examples":[{"en":"...","ru":"..."}],
"tasks":[{"type":"transform","from":"...","task":"...","answer":"..."},{"type":"translate","ru":"...","answer":"..."},{"type":"choice","sentence":"... ___ ...","options":["a","b","c"],"correct":0,"ru":"..."}]}
""".trimIndent()

    private fun dialogPrompt(u: PathUnit, words: List<String>) = """
$RULES
${levelHint(u.band)}
Write a realistic dialogue between two people (A = who 0, B = who 1) about: ${u.topic}.
10–12 lines. Make it feel like real life${if (u.band == "C1" || u.band == "B2") ": interruptions, reactions, humour, natural fillers" else ""}.
Naturally use some of these words: ${words.take(8).joinToString(", ")}.
Also use the grammar: ${u.grammar}.
JSON: {"title":"short title in Russian","lines":[{"who":0,"en":"...","ru":"..."}]}
""".trimIndent()

    private fun extractJson(text: String): JSONObject {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) throw Exception("ИИ ответил не в том формате. Попробуй ещё раз.")
        return JSONObject(text.substring(start, end + 1))
    }

    private suspend fun ask(store: ProgressStore, prompt: String, tokens: Int): JSONObject {
        var last: Exception? = null
        repeat(2) {
            val r = Ai.ask(store, "You are an experienced English teacher who writes lesson materials.", listOf(Ai.Msg("user", prompt)), maxTokens = tokens)
            val text = r.getOrElse { e -> throw e }
            try {
                return extractJson(text)
            } catch (e: Exception) {
                last = e
            }
        }
        throw last ?: Exception("Не получилось сгенерировать урок.")
    }

    suspend fun generate(store: ProgressStore, unit: PathUnit, part: String): Result<Unit> = runCatching {
        if (!store.aiReady) throw Exception("Для новых уроков нужен бесплатный ключ GigaChat — добавь его в ⚙️ настройках.")
        val json = when (part) {
            "vocab" -> ask(store, vocabPrompt(unit), 2200).also {
                if ((it.optJSONArray("items")?.length() ?: 0) < 3) throw Exception("ИИ прислал слишком мало слов. Попробуй ещё раз.")
            }
            "grammar" -> ask(store, grammarPrompt(unit), 3000)
            "dialog" -> ask(store, dialogPrompt(unit, PathStore.vocab(unit).map { it.en }), 2200)
            else -> throw IllegalArgumentException(part)
        }
        PathStore.save(unit, part, json)
        if (part == "grammar" && PathStore.grammar(unit) == null) {
            PathStore.delete(unit, part)
            throw Exception("ИИ прислал урок без упражнений. Попробуй ещё раз.")
        }
        if (part == "dialog" && PathStore.dialogue(unit) == null) {
            PathStore.delete(unit, part)
            throw Exception("ИИ прислал пустой диалог. Попробуй ещё раз.")
        }
    }
}

/** Прогресс по урокам пути. */
object PathProgress {
    fun vocabDone(store: ProgressStore, unit: PathUnit): Boolean {
        val items = PathStore.vocab(unit)
        return items.isNotEmpty() && items.count { store.box(it.id) >= 1 } >= (items.size * 0.8).toInt()
    }

    fun vocabCount(store: ProgressStore, unit: PathUnit): Pair<Int, Int> {
        val items = PathStore.vocab(unit)
        return items.count { store.box(it.id) >= 1 } to items.size
    }

    fun grammarDone(store: ProgressStore, unit: PathUnit) = store.grammarBest("path_${unit.id}") >= 60
    fun dialogDone(store: ProgressStore, unit: PathUnit) = store.listened("path_${unit.id}") > 0

    fun stepsDone(store: ProgressStore, unit: PathUnit): Int =
        listOf(vocabDone(store, unit), grammarDone(store, unit), dialogDone(store, unit)).count { it }

    fun done(store: ProgressStore, unit: PathUnit) = stepsDone(store, unit) == 3

    /** Открыт ли урок: стартовый для уровня, все до него, и следующий после пройденного. */
    fun unlocked(store: ProgressStore, unit: PathUnit): Boolean {
        val start = PathPlan.startFor(store.level ?: Level.BEGINNER)
        if (unit.n <= start) return true
        val prev = PathPlan.units.getOrNull(unit.n - 2) ?: return true
        return done(store, prev)
    }

    /** Текущий урок — первый открытый и не пройденный, начиная со стартового. */
    fun current(store: ProgressStore): PathUnit {
        val start = PathPlan.startFor(store.level ?: Level.BEGINNER)
        return PathPlan.units.drop(start - 1).firstOrNull { !done(store, it) } ?: PathPlan.units.last()
    }
}
