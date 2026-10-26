package ru.my.englishdaily

/**
 * Диалоги по уровням CEFR:
 * 1 — A1–A2 (простые разговорники), 2 — B1, 3 — B2, 4 — C1 «как в жизни»:
 * живая речь, слова-паразиты, перебивания, юмор, идиомы.
 */
object DialogLevels {
    val names = mapOf(
        1 to "A1–A2 · Простые",
        2 to "B1 · Средние",
        3 to "B2 · Продвинутые",
        4 to "C1 · Как в жизни",
    )
    val short = mapOf(1 to "A1–A2", 2 to "B1", 3 to "B2", 4 to "C1")

    /** С какого уровня диалогов начинать по умолчанию. */
    fun forUser(level: Level): Int = when (level) {
        Level.BEGINNER -> 1
        Level.INTERMEDIATE -> 2
        Level.ADVANCED -> 3
    }
}

object LiveDialogues {
    private fun a(en: String, ru: String) = Line(0, en, ru)
    private fun b(en: String, ru: String) = Line(1, en, ru)

    /** B1. */
    val b1: List<Dialogue> = listOf(
        Dialogue(
            "pharmacy", "💊", "В аптеке", listOf(
                a("Hi, how can I help you today?", "Здравствуйте, чем могу помочь?"),
                b("I've had a sore throat since yesterday, and it's getting worse.", "У меня со вчерашнего дня болит горло, и становится хуже."),
                a("Do you have a temperature as well?", "Температура тоже есть?"),
                b("A little. I'd rather not take antibiotics if I can avoid it.", "Небольшая. Я бы не хотел принимать антибиотики, если можно обойтись."),
                a("Then I'd recommend these lozenges and a spray. Take them three times a day.", "Тогда советую эти пастилки и спрей. Три раза в день."),
                b("Are there any side effects I should know about?", "Есть побочные эффекты, о которых стоит знать?"),
                a("Not really, but don't take them on an empty stomach.", "Особо нет, но не принимайте натощак."),
                b("Got it. And if it doesn't get better?", "Понял. А если не станет лучше?"),
                a("If you're not feeling better in three days, you should see a doctor.", "Если за три дня не полегчает, стоит сходить к врачу."),
                b("Thanks, that's really helpful.", "Спасибо, очень помогли."),
            ), level = 2
        ),
        Dialogue(
            "neighbour", "🏢", "Шумный сосед", listOf(
                a("Hi, sorry to bother you. I live downstairs.", "Привет, извини за беспокойство. Я живу этажом ниже."),
                b("Oh, hi! Is everything okay?", "О, привет! Что-то случилось?"),
                a("Well, the music was quite loud last night, and I couldn't sleep.", "Ну, вчера ночью музыка была довольно громкой, и я не мог уснуть."),
                b("Oh no, I'm so sorry. We had a few friends over for my birthday.", "Ой, прости, пожалуйста. У нас были друзья на мой день рождения."),
                a("Happy birthday! I don't mind a party now and then, but it went on till three.", "С днём рождения! Я не против вечеринки иногда, но это длилось до трёх."),
                b("You're absolutely right. It won't happen again, I promise.", "Ты абсолютно прав. Это не повторится, обещаю."),
                a("Thanks. Maybe just turn it down after eleven?", "Спасибо. Может, просто делать потише после одиннадцати?"),
                b("Of course. And next time, you should come too!", "Конечно. А в следующий раз приходи тоже!"),
                a("Ha, maybe I will. Have a good one!", "Ха, может, и приду. Всего хорошего!"),
            ), level = 2
        ),
        Dialogue(
            "checkin", "🛄", "Регистрация на рейс", listOf(
                a("Good morning. Can I see your passport, please?", "Доброе утро. Ваш паспорт, пожалуйста."),
                b("Here you are. I'm flying to Dubai.", "Вот, пожалуйста. Я лечу в Дубай."),
                a("Are you checking in any bags today?", "Будете сдавать багаж?"),
                b("Just this one. Is it under the weight limit?", "Только этот. Он укладывается в лимит по весу?"),
                a("It's twenty-four kilos, so you're fine. Would you like a window or an aisle seat?", "Двадцать четыре килограмма, всё в порядке. Место у окна или у прохода?"),
                b("A window seat, please. Is the flight on time?", "У окна, пожалуйста. Рейс вовремя?"),
                a("I'm afraid it's been delayed by about forty minutes.", "Боюсь, он задерживается примерно на сорок минут."),
                b("That's a shame. Which gate does it leave from?", "Жаль. С какого выхода?"),
                a("Gate twelve. Boarding starts at ten fifteen.", "Выход двенадцать. Посадка начинается в десять пятнадцать."),
                b("Great, thanks for your help.", "Отлично, спасибо за помощь."),
            ), level = 2
        ),
        Dialogue(
            "weekend", "🏞", "Планы на выходные", listOf(
                a("Have you got any plans for the weekend?", "Есть планы на выходные?"),
                b("Not yet. I was thinking of going hiking if the weather's good.", "Пока нет. Думал сходить в поход, если будет хорошая погода."),
                a("Sounds great. Where are you planning to go?", "Звучит здорово. Куда собираешься?"),
                b("There's a lake about an hour from the city. Have you been there?", "Есть озеро примерно в часе от города. Ты там был?"),
                a("Yes, last summer. The view is amazing, but the road is pretty bad.", "Да, прошлым летом. Вид потрясающий, но дорога довольно плохая."),
                b("Good to know. Would you like to come with us?", "Хорошо, что сказал. Хочешь с нами?"),
                a("I'd love to, but I promised to help my parents with their garden.", "С удовольствием бы, но я обещал помочь родителям в саду."),
                b("No worries. Maybe next time.", "Ничего страшного. Может, в следующий раз."),
                a("Definitely. Send me some photos!", "Обязательно. Пришли фотки!"),
            ), level = 2
        ),
    )

    /** B2 — дополнение к продвинутым диалогам. */
    val b2: List<Dialogue> = listOf(
        Dialogue(
            "raise", "💰", "Разговор о повышении", listOf(
                a("Thanks for finding the time. I know you're busy.", "Спасибо, что нашли время. Знаю, вы заняты."),
                b("Not at all. What did you want to discuss?", "Что вы. Что хотели обсудить?"),
                a("I'd like to talk about my salary. I've taken on a lot more responsibility this year.", "Я хотел бы поговорить о зарплате. В этом году у меня стало гораздо больше обязанностей."),
                b("That's true. You've been leading the new project since spring.", "Это правда. Вы ведёте новый проект с весны."),
                a("Exactly. And I've looked into what people in similar roles are earning.", "Именно. И я изучил, сколько получают люди на похожих должностях."),
                b("I appreciate that. What figure did you have in mind?", "Ценю это. Какую сумму вы имели в виду?"),
                a("I was hoping for around a fifteen percent increase.", "Я рассчитывал примерно на пятнадцать процентов прибавки."),
                b("That's a bit more than the budget allows right now, I'm afraid.", "Боюсь, сейчас это немного больше, чем позволяет бюджет."),
                a("I understand. Would it be possible to review it again in six months?", "Понимаю. Можно ли вернуться к этому через полгода?"),
                b("Let's meet halfway: ten percent now, and we'll revisit it in the summer.", "Давайте сойдёмся посередине: десять процентов сейчас, а летом вернёмся к вопросу."),
                a("That sounds fair. Thank you.", "Звучит справедливо. Спасибо."),
            ), level = 3
        ),
        Dialogue(
            "fenderbender", "🚗", "Мелкое ДТП", listOf(
                a("Are you okay? That was quite a bump.", "Вы в порядке? Хороший был удар."),
                b("I'm fine, just a bit shaken. You stopped really suddenly.", "Я в порядке, просто немного потрясён. Вы очень резко остановились."),
                a("A kid ran out into the road. I didn't have a choice.", "Ребёнок выбежал на дорогу. У меня не было выбора."),
                b("I see. Well, the damage doesn't look too bad, to be honest.", "Понятно. Ну, честно говоря, повреждения не такие уж серьёзные."),
                a("Your bumper's scratched, though. Shall we exchange insurance details?", "Но ваш бампер поцарапан. Обменяемся данными страховки?"),
                b("Yes, let's do that. Should we call the police as well?", "Да, давайте. Полицию тоже вызвать?"),
                a("I don't think we need to if nobody's hurt. We can just fill in the form.", "Думаю, не нужно, раз никто не пострадал. Можем просто заполнить бланк."),
                b("Fine by me. Let me take a few photos first, just in case.", "Меня устраивает. Сначала сделаю пару фото, на всякий случай."),
                a("Good idea. Sorry again for all the trouble.", "Хорошая мысль. Ещё раз извините за неприятности."),
                b("Don't worry, these things happen.", "Не переживайте, бывает."),
            ), level = 3
        ),
    )

    /** C1 — живая речь «как в жизни». */
    val c1: List<Dialogue> = listOf(
        Dialogue(
            "date", "☕", "Как прошло свидание", listOf(
                a("Okay, you have to tell me everything. How did the date go?", "Так, рассказывай всё. Как прошло свидание?"),
                b("Honestly? It was a bit of a rollercoaster.", "Честно? Это были американские горки."),
                a("Oh no. Good rollercoaster or bad rollercoaster?", "Ой. Хорошие горки или плохие?"),
                b("Both, I guess. He turned up twenty minutes late, which, you know, not a great start.", "И то и другое, наверное. Он опоздал на двадцать минут, что, сама понимаешь, не лучшее начало."),
                a("Seriously? Did he at least have a decent excuse?", "Серьёзно? У него хоть было нормальное оправдание?"),
                b("Something about his car not starting. But then he was so funny and down-to-earth that I sort of forgave him.", "Что-то про то, что машина не завелась. Но потом он был таким смешным и простым, что я его вроде как простила."),
                a("Sounds like you've already got a soft spot for him.", "Похоже, он тебе уже нравится."),
                b("Maybe. I mean, we ended up talking till midnight, so that's got to mean something, right?", "Может быть. Ну то есть мы проболтали до полуночи, это же что-то значит, да?"),
                a("It definitely does. So, are you seeing him again?", "Определённо. Так вы ещё увидитесь?"),
                b("He asked me out for Saturday. I'm playing it cool, though.", "Он пригласил меня на субботу. Но я делаю вид, что мне всё равно."),
                a("Yeah, right. You've been grinning this whole time!", "Ага, конечно. Ты всё это время улыбаешься до ушей!"),
            ), level = 4
        ),
        Dialogue(
            "money", "💸", "Спор о деньгах", listOf(
                a("Did you see the credit card bill? It's way higher than last month.", "Ты видел счёт по кредитке? Он намного больше, чем в прошлом месяце."),
                b("Yeah, I know. That's partly the new tyres. We couldn't put that off any longer.", "Да, знаю. Отчасти это новые шины. Дальше откладывать было нельзя."),
                a("Fair enough, but what about all these food deliveries? We said we'd cut back.", "Ладно, но что насчёт всех этих доставок еды? Мы же договорились сократить."),
                b("I've been working late almost every night. I just haven't had the energy to cook.", "Я почти каждый вечер работаю допоздна. У меня просто нет сил готовить."),
                a("I get that, I really do. But it adds up. We're never going to save for the trip at this rate.", "Я понимаю, правда. Но это накапливается. Такими темпами мы никогда не накопим на поездку."),
                b("Okay, point taken. What if we cooked a few big meals on Sundays and froze them?", "Ладно, убедила. А что если по воскресеньям готовить побольше и замораживать?"),
                a("That could actually work. And maybe we set a limit for takeaways, like twice a week?", "Это может сработать. И, может, поставим лимит на еду навынос, ну, два раза в неделю?"),
                b("Deal. Sorry, I didn't realize it had got so out of hand.", "Договорились. Прости, я не понимал, что всё так вышло из-под контроля."),
                a("It's fine. I'm not having a go at you, I just want us to be on the same page.", "Ничего. Я на тебя не наезжаю, просто хочу, чтобы мы были заодно."),
                b("We are. Now, can we please talk about something that isn't money?", "Так и есть. А теперь можно, пожалуйста, поговорить о чём-нибудь, кроме денег?"),
            ), level = 4
        ),
        Dialogue(
            "pitch", "🚀", "Продаём идею начальнику", listOf(
                a("So, you mentioned you had an idea you wanted to run by me?", "Ты говорил, что хочешь обсудить со мной одну идею?"),
                b("Yeah. Bear with me, it's a bit out there, but I think it could save us a ton of time.", "Да. Потерпи, она немного необычная, но, думаю, может сэкономить нам кучу времени."),
                a("Go on, I'm all ears.", "Давай, я весь внимание."),
                b("Right now we're spending about two days a month just chasing late invoices by hand.", "Сейчас мы тратим около двух дней в месяц только на то, чтобы вручную выбивать просроченные счета."),
                a("Tell me about it. It drives everyone up the wall.", "И не говори. Это всех бесит."),
                b("So, what if we automated the reminders? I've already put together a rough prototype.", "Так вот, а что если автоматизировать напоминания? Я уже собрал черновой прототип."),
                a("Hang on, you built this in your own time?", "Подожди, ты сделал это в своё свободное время?"),
                b("Pretty much. It's nothing fancy, but it's been running on a small group of clients for a couple of weeks.", "Практически. Ничего особенного, но он уже пару недель работает на небольшой группе клиентов."),
                a("And? Has it made a difference?", "И как? Есть толк?"),
                b("Nearly everyone in that group has paid on time. That's never happened before.", "Почти все в этой группе заплатили вовремя. Такого раньше не бывало."),
                a("Okay, you've got my attention. Put together a short proposal and let's take it to the team on Monday.", "Ладно, ты меня заинтересовал. Подготовь короткое предложение, и в понедельник покажем команде."),
            ), level = 4
        ),
        Dialogue(
            "reunion", "🍻", "Встреча через 10 лет", listOf(
                a("No way! Is that really you? It's been, what, ten years?", "Не может быть! Это правда ты? Сколько прошло, лет десять?"),
                b("Give or take! You haven't changed a bit, you know.", "Плюс-минус! Ты ни капли не изменился, знаешь."),
                a("Liar. I've got twice as much grey hair. So, what have you been up to?", "Врунишка. У меня вдвое больше седины. Ну, чем занимался всё это время?"),
                b("Oh, where do I start? I moved to Kazan, got married, and now we've got two little monsters.", "Ох, с чего бы начать? Переехал в Казань, женился, и теперь у нас два маленьких монстра."),
                a("Congratulations! I always knew you'd be the first of us to settle down.", "Поздравляю! Я всегда знал, что ты первым из нас остепенишься."),
                b("Says the guy who swore he'd never leave his hometown. What happened there?", "Кто бы говорил! Ты же клялся, что никогда не уедешь из родного города. Что случилось?"),
                a("Long story. I got an offer I couldn't turn down, and one thing led to another.", "Долгая история. Мне сделали предложение, от которого нельзя было отказаться, а дальше одно за другим."),
                b("Well, it clearly worked out for you. We really should keep in touch this time.", "Что ж, у тебя явно всё сложилось. В этот раз нам правда надо не теряться."),
                a("Absolutely. Let's not leave it another ten years. Give me your number.", "Однозначно. Давай не ждать ещё десять лет. Давай номер."),
                b("Deal. And next time, the drinks are on me.", "Договорились. И в следующий раз угощаю я."),
            ), level = 4
        ),
        Dialogue(
            "complaintcall", "📞", "Жалоба по телефону", listOf(
                a("Thank you for calling. You're speaking with Mark. How can I help?", "Спасибо за звонок. Вас слушает Марк. Чем могу помочь?"),
                b("Hi Mark. I'll try to keep this short, but I'm honestly at the end of my tether.", "Здравствуйте, Марк. Постараюсь коротко, но я, честно говоря, на пределе."),
                a("I'm sorry to hear that. What seems to be the problem?", "Жаль это слышать. В чём проблема?"),
                b("I've been charged twice for the same order, and this is the third time I'm calling about it.", "С меня дважды списали деньги за один заказ, и я звоню по этому поводу уже третий раз."),
                a("That shouldn't have happened, and I apologize. Could I have your order number?", "Такого не должно было случиться, приношу извинения. Назовите, пожалуйста, номер заказа."),
                b("It's 4-7-1-9. And please don't tell me it'll be sorted within ten working days again.", "4-7-1-9. И, пожалуйста, не говорите мне снова, что всё решится за десять рабочих дней."),
                a("I can see the duplicate payment. I'm processing the refund right now, while you're on the line.", "Вижу двойной платёж. Я оформляю возврат прямо сейчас, пока вы на линии."),
                b("Right. Well, that's more like it. When will I actually see the money?", "Так. Вот это уже другой разговор. Когда деньги реально вернутся?"),
                a("It usually takes two to three days, depending on your bank. I'll also add a discount to your account for the trouble.", "Обычно два-три дня, в зависимости от банка. И я добавлю на ваш счёт скидку за неудобства."),
                b("Thanks, Mark. You're the first person who's actually listened.", "Спасибо, Марк. Вы первый, кто меня действительно выслушал."),
            ), level = 4
        ),
    )

    val all: List<Dialogue> get() = b1 + b2 + c1
}
