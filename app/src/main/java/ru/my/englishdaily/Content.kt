package ru.my.englishdaily

data class Item(val id: String, val en: String, val ru: String, val group: String = "")

enum class Deck(val title: String, val singleWords: Boolean = false) {
    CORE("🧱 500 главных слов", singleWords = true),
    PATH("🗺 Слова из уроков"),
    COURSE("Курс"),
    PHRASES("Фразы"),
    WORDS("Слова", singleWords = true),
    SLANG("Живая речь"),
    DIALOGS("Из диалогов"),
    COMPLEX("Сложные предложения"),
    B1_WORDS("Слова B1", singleWords = true),
    B2_WORDS("Слова B2: жизнь", singleWords = true),
    ADV_PHRASES("Фразы B2+"),
    PHRASAL("Фразовые глаголы"),
    IDIOMS("Идиомы"),
    MULTI("Одно слово — разные смыслы", singleWords = true),
    MINE("⭐ Мои слова");

    val items: List<Item>
        get() = when (this) {
            CORE -> Core.items
            PATH -> PathStore.allVocab()
            COURSE -> Course.items
            PHRASES -> Content.phrases
            WORDS -> Content.words
            SLANG -> Slang.items
            DIALOGS -> Dialogues.items
            COMPLEX -> Complex.items
            B1_WORDS -> Vocab.b1
            B2_WORDS -> Vocab.b2
            ADV_PHRASES -> Vocab.phrases
            PHRASAL -> Vocab.phrasal
            IDIOMS -> Idioms.items
            MULTI -> Usages.multiDeck
            MINE -> MyWords.items
        }

    /** Курс проходится строго по порядку, остальные колоды — вперемешку. */
    val ordered: Boolean get() = this == COURSE || this == CORE

    companion object {
        /** Колоды под уровень: сначала подходящие, слишком простые для продвинутых скрыты. */
        fun forLevel(level: Level): List<Deck> = when (level) {
            Level.BEGINNER -> listOf(CORE, PATH, COURSE, PHRASES, WORDS, SLANG, DIALOGS, B1_WORDS, MULTI, MINE)
            Level.INTERMEDIATE -> listOf(PATH, B1_WORDS, MULTI, COMPLEX, PHRASAL, SLANG, DIALOGS, IDIOMS, ADV_PHRASES, COURSE, PHRASES, MINE)
            Level.ADVANCED -> listOf(PATH, COMPLEX, B2_WORDS, MULTI, ADV_PHRASES, PHRASAL, IDIOMS, DIALOGS, SLANG, B1_WORDS, MINE)
        }

        /** Пояснение к колоде — показываем, когда её выбирают. */
        fun describe(d: Deck): String = when (d) {
            CORE -> "Самые частые слова английского — с них начинается всё. По порядку частоты"
            PATH -> "Все слова из пройденных уроков курса A1 → C1 — для повторения"
            COURSE -> "32 разговорные конструкции от «I want to…» до «I used to…», по порядку"
            PHRASES -> "Бытовые фразы на каждый день"
            WORDS -> "Самые нужные слова для начала"
            SLANG -> "gonna, wanna, gotta и живые разговорные фразы"
            DIALOGS -> "Реплики из всех диалогов"
            COMPLEX -> "Длинные предложения с разными временами: Past Perfect, Future Perfect, условия, I wish, should have…"
            B1_WORDS -> "Слова среднего уровня: achieve, afford, reliable, eventually…"
            B2_WORDS -> "Живые слова на каждый день: leftovers, refund, commute, flat tyre, fed up, show off…"
            ADV_PHRASES -> "Фразы для спора, мнения и вежливой беседы"
            PHRASAL -> "Фразовые глаголы в живых предложениях"
            IDIOMS -> "Идиомы и устойчивые выражения"
            MULTI -> "case, charge, fine, bill, claim… Одно слово в быту, на работе и в суде — с примерами"
            MINE -> "Твои слова из историй, сериалов и разговорника"
        }
    }
}

/** Стартовый набор. Чтобы добавить своё — допиши строку "english" to "русский". */
object Content {

    val phrases: List<Item> = listOf(
        // Знакомство и вежливость
        "Hello! How are you?" to "Привет! Как дела?",
        "I'm fine, thank you." to "Хорошо, спасибо.",
        "Nice to meet you." to "Приятно познакомиться.",
        "What is your name?" to "Как тебя зовут?",
        "My name is Alex." to "Меня зовут Алекс.",
        "Where are you from?" to "Откуда ты?",
        "I am from Russia." to "Я из России.",
        "Thank you very much." to "Большое спасибо.",
        "You are welcome." to "Пожалуйста (в ответ на «спасибо»).",
        "Excuse me." to "Простите (чтобы обратиться).",
        "I am sorry." to "Мне жаль. / Извините.",
        "Sorry, I'm late." to "Извините, я опоздал.",
        "No problem." to "Без проблем.",
        "See you tomorrow." to "Увидимся завтра.",
        "Have a nice day!" to "Хорошего дня!",
        "Good luck!" to "Удачи!",

        // Когда не понимаешь
        "I don't understand." to "Я не понимаю.",
        "Can you repeat, please?" to "Можете повторить, пожалуйста?",
        "Speak slowly, please." to "Говорите медленнее, пожалуйста.",
        "I speak a little English." to "Я немного говорю по-английски.",
        "What does this mean?" to "Что это значит?",
        "How do you say this in English?" to "Как это сказать по-английски?",
        "Do you speak Russian?" to "Вы говорите по-русски?",

        // Хочу
        "I want to eat." to "Я хочу есть.",
        "I want to drink." to "Я хочу пить.",
        "I want to sleep." to "Я хочу спать.",
        "I want to go home." to "Я хочу домой.",
        "I want to learn English." to "Я хочу выучить английский.",
        "I want a cup of coffee." to "Я хочу чашку кофе.",
        "I don't want to go." to "Я не хочу идти.",
        "Do you want to go with me?" to "Хочешь пойти со мной?",
        "What do you want?" to "Чего ты хочешь?",

        // Нужно
        "I need help." to "Мне нужна помощь.",
        "I need a doctor." to "Мне нужен врач.",
        "I need to go." to "Мне нужно идти.",
        "I need more time." to "Мне нужно больше времени.",
        "I need to buy a ticket." to "Мне нужно купить билет.",
        "Do you need help?" to "Тебе нужна помощь?",
        "I don't need it." to "Мне это не нужно.",

        // Могу / можно
        "Can I pay by card?" to "Можно оплатить картой?",
        "Can I sit here?" to "Можно здесь сесть?",
        "Can I ask a question?" to "Можно задать вопрос?",
        "Can you help me?" to "Можете мне помочь?",
        "Can you show me?" to "Можете показать?",
        "Can you call a taxi?" to "Можете вызвать такси?",
        "I can do it." to "Я могу это сделать.",
        "I can't find my phone." to "Я не могу найти свой телефон.",
        "I can't sleep." to "Я не могу уснуть.",

        // Вопросы
        "Where is the toilet?" to "Где туалет?",
        "Where is the station?" to "Где вокзал?",
        "Where is the nearest pharmacy?" to "Где ближайшая аптека?",
        "How much is it?" to "Сколько это стоит?",
        "How much does the ticket cost?" to "Сколько стоит билет?",
        "What time is it?" to "Который час?",
        "When does the shop open?" to "Когда открывается магазин?",
        "When does the train leave?" to "Когда отправляется поезд?",
        "How do I get to the airport?" to "Как добраться до аэропорта?",
        "Is it far from here?" to "Это далеко отсюда?",
        "What is this?" to "Что это?",
        "Who is this?" to "Кто это?",
        "Why are you sad?" to "Почему ты грустный?",
        "How old are you?" to "Сколько тебе лет?",
        "What do you do?" to "Чем ты занимаешься?",
        "Are you busy?" to "Ты занят?",
        "Are you ready?" to "Ты готов?",
        "Is everything okay?" to "Всё в порядке?",

        // Нравится
        "I like it." to "Мне это нравится.",
        "I don't like it." to "Мне это не нравится.",
        "I like coffee." to "Я люблю кофе.",
        "I like to read books." to "Я люблю читать книги.",
        "Do you like music?" to "Тебе нравится музыка?",
        "I love my family." to "Я люблю свою семью.",

        // У меня есть / я
        "I have a car." to "У меня есть машина.",
        "I have no money." to "У меня нет денег.",
        "I have a question." to "У меня есть вопрос.",
        "I have a headache." to "У меня болит голова.",
        "Do you have water?" to "У вас есть вода?",
        "I am tired." to "Я устал.",
        "I am hungry." to "Я голоден.",
        "I am cold." to "Мне холодно.",
        "I am at home." to "Я дома.",
        "I am at work." to "Я на работе.",
        "I am lost." to "Я заблудился.",
        "I'm not sure." to "Я не уверен.",
        "I'm busy now." to "Я сейчас занят.",

        // Каждый день
        "I get up at seven." to "Я встаю в семь.",
        "I go to work by bus." to "Я езжу на работу на автобусе.",
        "I work every day." to "Я работаю каждый день.",
        "I am going to the shop." to "Я иду в магазин.",
        "I will call you later." to "Я позвоню тебе позже.",
        "I was at home yesterday." to "Вчера я был дома.",
        "I didn't know." to "Я не знал.",
        "I forgot." to "Я забыл.",
        "I'll be back soon." to "Я скоро вернусь.",
        "Wait a minute." to "Подожди минутку.",
        "Let's go." to "Пойдём.",
        "Let's eat." to "Давай поедим.",
        "Let's meet tomorrow." to "Давай встретимся завтра.",
        "It's late." to "Уже поздно.",
        "It's cold today." to "Сегодня холодно.",
        "It's raining." to "Идёт дождь.",
        "It's very expensive." to "Это очень дорого.",
        "It's my car." to "Это моя машина.",

        // Магазин и кафе
        "I would like a tea, please." to "Я бы хотел чай, пожалуйста.",
        "The bill, please." to "Счёт, пожалуйста.",
        "A table for two, please." to "Столик на двоих, пожалуйста.",
        "Without sugar, please." to "Без сахара, пожалуйста.",
        "I'm just looking." to "Я просто смотрю.",
        "Do you have a smaller size?" to "Есть размер поменьше?",
        "I'll take it." to "Я возьму это.",

        // Мнение
        "I think so." to "Думаю, да.",
        "I don't think so." to "Не думаю.",
        "I agree." to "Я согласен.",
        "That's right." to "Верно.",
        "That's a good idea." to "Хорошая идея.",
        "It doesn't matter." to "Это не важно.",
        "Of course." to "Конечно.",
        "Maybe later." to "Может быть, позже.",
        "I know." to "Я знаю.",
        "I don't know." to "Я не знаю.",
        "Me too." to "Я тоже.",
    ).map { (en, ru) -> Item("p:$en", en, ru) }

    val words: List<Item> = listOf(
        // Самые нужные
        "yes" to "да", "no" to "нет", "please" to "пожалуйста", "thank you" to "спасибо",
        "sorry" to "извините", "hello" to "привет", "goodbye" to "до свидания",
        "here" to "здесь", "there" to "там", "now" to "сейчас",
        "today" to "сегодня", "tomorrow" to "завтра", "yesterday" to "вчера",

        // Вопросительные
        "what" to "что", "who" to "кто", "where" to "где", "when" to "когда",
        "why" to "почему", "how" to "как", "how much" to "сколько (стоит)",

        // Глаголы
        "want" to "хотеть", "need" to "нуждаться", "have" to "иметь",
        "go" to "идти, ехать", "come" to "приходить", "see" to "видеть",
        "look" to "смотреть", "know" to "знать", "think" to "думать",
        "take" to "брать", "give" to "давать", "do" to "делать",
        "make" to "создавать, изготавливать", "eat" to "есть (кушать)", "drink" to "пить",
        "sleep" to "спать", "work" to "работать", "buy" to "покупать",
        "sell" to "продавать", "pay" to "платить", "help" to "помогать",
        "understand" to "понимать", "speak" to "говорить", "say" to "сказать",
        "tell" to "рассказывать", "ask" to "спрашивать", "answer" to "отвечать",
        "wait" to "ждать", "find" to "находить", "open" to "открывать",
        "close" to "закрывать", "start" to "начинать", "finish" to "заканчивать",
        "like" to "нравиться", "love" to "любить", "live" to "жить",
        "call" to "звонить", "write" to "писать", "read" to "читать",
        "learn" to "учить", "forget" to "забывать", "remember" to "помнить",
        "try" to "пытаться", "use" to "использовать", "leave" to "уходить",
        "stay" to "оставаться", "sit" to "сидеть", "stand" to "стоять",
        "walk" to "гулять, идти пешком", "drive" to "водить машину", "fly" to "лететь",
        "meet" to "встречать", "show" to "показывать", "send" to "отправлять",
        "get" to "получать", "put" to "класть", "turn" to "поворачивать",
        "play" to "играть", "listen" to "слушать", "hear" to "слышать",
        "feel" to "чувствовать", "cook" to "готовить", "wash" to "мыть",
        "change" to "менять", "repeat" to "повторять",

        // Существительные
        "water" to "вода", "food" to "еда", "bread" to "хлеб", "coffee" to "кофе",
        "tea" to "чай", "milk" to "молоко", "money" to "деньги", "time" to "время",
        "day" to "день", "night" to "ночь", "morning" to "утро", "evening" to "вечер",
        "week" to "неделя", "year" to "год", "house" to "дом (здание)",
        "home" to "дом (где живёшь)", "room" to "комната", "door" to "дверь",
        "window" to "окно", "car" to "машина", "bus" to "автобус", "train" to "поезд",
        "street" to "улица", "city" to "город", "shop" to "магазин", "job" to "работа",
        "friend" to "друг", "family" to "семья", "mother" to "мама", "father" to "папа",
        "child" to "ребёнок", "man" to "мужчина", "woman" to "женщина",
        "people" to "люди", "phone" to "телефон", "ticket" to "билет", "price" to "цена",
        "question" to "вопрос", "name" to "имя", "hand" to "рука", "head" to "голова",
        "doctor" to "врач", "hospital" to "больница", "pharmacy" to "аптека",
        "station" to "вокзал, станция", "airport" to "аэропорт", "hotel" to "гостиница",
        "toilet" to "туалет", "road" to "дорога", "weather" to "погода",
        "rain" to "дождь", "sun" to "солнце", "dog" to "собака", "cat" to "кошка",
        "book" to "книга", "word" to "слово",

        // Прилагательные
        "good" to "хороший", "bad" to "плохой", "big" to "большой", "small" to "маленький",
        "new" to "новый", "old" to "старый", "hot" to "горячий, жаркий", "cold" to "холодный",
        "expensive" to "дорогой", "cheap" to "дешёвый", "fast" to "быстрый",
        "slow" to "медленный", "easy" to "лёгкий", "difficult" to "трудный",
        "right" to "правильный", "wrong" to "неправильный", "tired" to "уставший",
        "hungry" to "голодный", "happy" to "счастливый", "busy" to "занятой",
        "free" to "свободный", "ready" to "готовый", "beautiful" to "красивый",
        "near" to "близко", "far" to "далеко",

        // Наречия и связки
        "very" to "очень", "always" to "всегда", "never" to "никогда",
        "sometimes" to "иногда", "often" to "часто", "again" to "снова",
        "already" to "уже", "maybe" to "может быть", "together" to "вместе",
        "because" to "потому что", "but" to "но", "and" to "и", "or" to "или",

        // Числа
        "one" to "один", "two" to "два", "three" to "три", "four" to "четыре",
        "five" to "пять", "six" to "шесть", "seven" to "семь", "eight" to "восемь",
        "nine" to "девять", "ten" to "десять", "twenty" to "двадцать", "hundred" to "сто",
    ).map { (en, ru) -> Item("w:$en", en, ru) }
}
