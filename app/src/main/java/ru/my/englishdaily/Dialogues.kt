package ru.my.englishdaily

/** Реплика: who = 0 — первый голос (A), 1 — второй голос (B). */
data class Line(val who: Int, val en: String, val ru: String)

data class Dialogue(val id: String, val emoji: String, val title: String, val lines: List<Line>, val level: Int = 1)

private fun a(en: String, ru: String) = Line(0, en, ru)
private fun b(en: String, ru: String) = Line(1, en, ru)

object Dialogues {

    val all: List<Dialogue> = listOf(
        Dialogue(
            "cafe", "☕", "В кафе", listOf(
                a("Hi! What can I get you?", "Привет! Что вам предложить?"),
                b("Hi! Can I get a large latte, please?", "Здравствуйте! Можно большой латте?"),
                a("Sure. For here or to go?", "Конечно. Здесь или с собой?"),
                b("To go, please.", "С собой, пожалуйста."),
                a("Anything else?", "Что-нибудь ещё?"),
                b("Yeah, one of those muffins.", "Да, один из тех кексов."),
                a("That'll be six fifty.", "С вас шесть пятьдесят."),
                b("Can I pay by card?", "Можно картой?"),
                a("Of course. Here's your receipt.", "Конечно. Вот ваш чек."),
                b("Thanks, have a good one!", "Спасибо, всего доброго!"),
            )
        ),
        Dialogue(
            "meet", "👋", "Знакомство", listOf(
                a("Hi, I'm Mike. I don't think we've met.", "Привет, я Майк. Кажется, мы не знакомы."),
                b("Hi Mike, I'm Alex. Nice to meet you.", "Привет, Майк, я Алекс. Приятно познакомиться."),
                a("Nice to meet you too. Where are you from?", "Взаимно. Ты откуда?"),
                b("I'm from Russia. And you?", "Я из России. А ты?"),
                a("I'm from Canada. What do you do?", "Я из Канады. Чем занимаешься?"),
                b("I work in sales. How about you?", "Я работаю в продажах. А ты?"),
                a("I'm a teacher. How long have you been here?", "Я учитель. Ты здесь давно?"),
                b("Just a couple of days.", "Всего пару дней."),
                a("Well, if you need anything, just let me know.", "Ну, если что-то понадобится, обращайся."),
                b("Thanks, that's really nice of you.", "Спасибо, очень мило с твоей стороны."),
            )
        ),
        Dialogue(
            "shop", "🛍", "Магазин одежды", listOf(
                a("Hi there! Can I help you find anything?", "Здравствуйте! Помочь вам что-то найти?"),
                b("I'm just looking, thanks.", "Я просто смотрю, спасибо."),
                a("No problem. Let me know if you need anything.", "Без проблем. Если что-то понадобится, скажите."),
                b("Actually, do you have this jacket in a larger size?", "Вообще-то, у вас есть эта куртка размером побольше?"),
                a("Let me check. What size are you?", "Сейчас посмотрю. Какой у вас размер?"),
                b("Large, I think.", "Думаю, L."),
                a("Here you go. The fitting room is over there.", "Вот, держите. Примерочная вон там."),
                b("It fits perfectly. How much is it?", "Сидит идеально. Сколько стоит?"),
                a("It's on sale today, forty dollars.", "Сегодня со скидкой, сорок долларов."),
                b("Great, I'll take it.", "Отлично, беру."),
            )
        ),
        Dialogue(
            "taxi", "🚕", "Такси", listOf(
                a("Where to?", "Куда едем?"),
                b("To the airport, please.", "В аэропорт, пожалуйста."),
                a("Which terminal?", "Какой терминал?"),
                b("Terminal two. How long will it take?", "Второй. Сколько ехать?"),
                a("About thirty minutes, if there's no traffic.", "Около тридцати минут, если не будет пробок."),
                b("Okay. I'm in a bit of a hurry.", "Хорошо. Я немного спешу."),
                a("Don't worry, we'll make it.", "Не волнуйтесь, успеем."),
                b("Can you stop here, please?", "Остановите здесь, пожалуйста."),
                a("Sure. That's twenty-five dollars.", "Конечно. С вас двадцать пять долларов."),
                b("Here you go. Keep the change.", "Вот, держите. Сдачи не надо."),
            )
        ),
        Dialogue(
            "phone", "📞", "Звонок другу", listOf(
                a("Hey! What's up?", "Привет! Как дела?"),
                b("Not much. Are you free tonight?", "Да ничего. Ты сегодня вечером свободен?"),
                a("I think so. Why?", "Вроде да. А что?"),
                b("Do you wanna watch the game at my place?", "Хочешь посмотреть матч у меня?"),
                a("Sounds good! What time?", "Звучит отлично! Во сколько?"),
                b("It starts at eight. Come at seven thirty.", "Начало в восемь. Приходи в полвосьмого."),
                a("Should I bring anything?", "Мне что-нибудь принести?"),
                b("Maybe some snacks.", "Может, чего-нибудь пожевать."),
                a("Got it. See you later!", "Понял. До встречи!"),
                b("See ya!", "Пока!"),
            )
        ),
        Dialogue(
            "restaurant", "🍽", "Ресторан", listOf(
                a("Good evening. A table for two?", "Добрый вечер. Столик на двоих?"),
                b("Yes, please. By the window, if possible.", "Да, пожалуйста. У окна, если можно."),
                a("Of course. Here are your menus. Are you ready to order?", "Конечно. Вот меню. Готовы сделать заказ?"),
                b("Yes. I'll have the steak, please.", "Да. Мне стейк, пожалуйста."),
                a("How would you like it cooked?", "Какой прожарки?"),
                b("Medium, please.", "Средней, пожалуйста."),
                a("Would you like something to drink?", "Что будете пить?"),
                b("Just water, please.", "Просто воду, пожалуйста."),
                a("How was everything?", "Как вам всё?"),
                b("It was delicious. Can we get the check, please?", "Очень вкусно. Можно счёт, пожалуйста?"),
                a("Sure, I'll be right back.", "Конечно, сейчас принесу."),
            )
        ),
        Dialogue(
            "hotel", "🏨", "Гостиница", listOf(
                a("Good evening. Do you have a reservation?", "Добрый вечер. У вас есть бронь?"),
                b("Yes, under the name Ivanov.", "Да, на фамилию Иванов."),
                a("Let me see. Yes, two nights, a double room.", "Сейчас посмотрю. Да, две ночи, двухместный номер."),
                b("That's right. What time is breakfast?", "Верно. Во сколько завтрак?"),
                a("From seven to ten in the restaurant downstairs.", "С семи до десяти в ресторане внизу."),
                b("Is there Wi-Fi in the room?", "В номере есть вайфай?"),
                a("Yes, the password is on your key card.", "Да, пароль на вашей ключ-карте."),
                b("And what time is check-out?", "А во сколько выезд?"),
                a("Check-out is at noon.", "Выезд в полдень."),
                b("Perfect, thank you.", "Отлично, спасибо."),
            )
        ),
        Dialogue(
            "doctor", "🩺", "У врача", listOf(
                a("Hello, what seems to be the problem?", "Здравствуйте, на что жалуетесь?"),
                b("I've had a bad headache for two days.", "У меня два дня сильно болит голова."),
                a("Do you have a fever?", "Температура есть?"),
                b("Yes, a little, and I feel tired all the time.", "Да, небольшая, и я всё время чувствую усталость."),
                a("Are you taking any medicine?", "Принимаете какие-нибудь лекарства?"),
                b("Just some painkillers.", "Только обезболивающее."),
                a("It looks like a cold. Rest and drink a lot of water.", "Похоже на простуду. Отдыхайте и пейте много воды."),
                b("Do I need to take anything?", "Мне нужно что-нибудь принимать?"),
                a("I'll write you a prescription.", "Я выпишу вам рецепт."),
                b("Thank you, doctor.", "Спасибо, доктор."),
            )
        ),
        Dialogue(
            "car", "🔧", "Автосервис", listOf(
                a("Hi, how can I help you?", "Здравствуйте, чем могу помочь?"),
                b("My car is making a strange noise.", "Машина издаёт странный звук."),
                a("When does it happen?", "Когда это происходит?"),
                b("Mostly when I brake.", "В основном, когда торможу."),
                a("It could be the brakes. Let me take a look.", "Возможно, тормоза. Давайте посмотрю."),
                b("How long will it take?", "Сколько это займёт?"),
                a("About an hour. You can wait here if you like.", "Около часа. Можете подождать здесь, если хотите."),
                b("How much will it cost?", "Сколько это будет стоить?"),
                a("I'll tell you after I check it.", "Скажу, когда проверю."),
                b("Okay, I'll wait.", "Хорошо, я подожду."),
            )
        ),
        Dialogue(
            "work", "💼", "На работе", listOf(
                a("Morning! Did you get my email?", "Доброе утро! Ты получил моё письмо?"),
                b("Yes, I did. Sorry, I haven't answered yet.", "Да, получил. Извини, ещё не ответил."),
                a("No worries. Can we talk about the project today?", "Ничего страшного. Можем сегодня обсудить проект?"),
                b("Sure. How about after lunch?", "Конечно. Как насчёт после обеда?"),
                a("Two o'clock works for me.", "Мне подходит в два."),
                b("Great. Should I invite the others?", "Отлично. Позвать остальных?"),
                a("Yes, please. And bring the latest numbers.", "Да, пожалуйста. И захвати последние цифры."),
                b("Will do. See you at two.", "Сделаю. Увидимся в два."),
            )
        ),
    )

    /** Базовые и сложные диалоги вместе. */
    val everything: List<Dialogue> get() = all + AdvancedDialogues.all

    /** Все реплики как карточки — для тренировок «на слух», «диктант» и т. д. */
    val items: List<Item> get() = basicItems + AdvancedDialogues.items

    private val basicItems: List<Item> = all.flatMap { d ->
        d.lines.mapIndexed { i, l -> Item("d:${d.id}:$i", l.en, l.ru, "${d.emoji} ${d.title}") }
    }
}

/** Живая разговорная речь: сокращения и фразы, которые слышишь постоянно. */
object Slang {
    val items: List<Item> = listOf(
        "I'm gonna call you." to "Я тебе позвоню. (gonna = going to)",
        "I wanna go home." to "Хочу домой. (wanna = want to)",
        "I gotta go." to "Мне пора. (gotta = have got to)",
        "Do you wanna grab a bite?" to "Хочешь перекусить?",
        "I dunno." to "Не знаю. (dunno = don't know)",
        "Lemme see." to "Дай посмотреть. (lemme = let me)",
        "Gimme a sec." to "Дай секунду. (gimme = give me)",
        "Whatcha doing?" to "Что делаешь? (whatcha = what are you)",
        "How's it going?" to "Как оно? Как дела?",
        "What's up?" to "Как жизнь? Что нового?",
        "Not much." to "Да ничего особенного.",
        "See ya!" to "Пока! Увидимся!",
        "Take care." to "Береги себя. (при прощании)",
        "Gotcha." to "Понял. (got you)",
        "No way!" to "Да ладно! Не может быть!",
        "For real?" to "Серьёзно?",
        "I'm kinda tired." to "Я как-то устал. (kinda = kind of)",
        "It's sorta weird." to "Это как-то странно. (sorta = sort of)",
        "Come on, let's go!" to "Давай, пошли!",
        "Yep." to "Ага.",
        "Nope." to "Не-а.",
        "Sure thing." to "Конечно, без проблем.",
        "My bad." to "Моя вина, извини.",
        "No worries." to "Не парься, всё нормально.",
        "Hang on." to "Погоди.",
        "Hold on a sec." to "Секундочку.",
        "I'm good, thanks." to "Не надо, спасибо. (или: у меня всё хорошо)",
        "That's awesome!" to "Это круто!",
        "Never mind." to "Не важно, забудь.",
        "Sounds good." to "Звучит неплохо. Договорились.",
        "I'm in." to "Я в деле. Я с вами.",
        "Long time no see!" to "Сто лет не виделись!",
        "Catch you later." to "Ещё увидимся.",
        "What's going on?" to "Что происходит?",
        "It's no big deal." to "Ничего страшного. Подумаешь.",
        "You bet!" to "Ещё бы! Конечно!",
    ).map { (en, ru) -> Item("s:$en", en, ru) }
}
