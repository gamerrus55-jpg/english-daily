package ru.my.englishdaily

/**
 * Сложные предложения с разными временами и конструкциями —
 * для среднего и продвинутого уровня. Темы разные: работа, дом, путешествия, отношения, деньги, здоровье.
 */
object Complex {
    private fun g(group: String, vararg list: Pair<String, String>) =
        list.map { (en, ru) -> Item("cx:$en", en, ru, group) }

    val items: List<Item> =
        g(
            "Past Continuous — процесс в прошлом",
            "I was driving home when my phone rang." to "Я ехал домой, когда зазвонил телефон.",
            "While she was cooking, the kids were watching cartoons." to "Пока она готовила, дети смотрели мультики.",
            "What were you doing at eight last night?" to "Что ты делал вчера в восемь вечера?",
            "It was raining heavily, so we stayed inside." to "Шёл сильный дождь, поэтому мы остались дома.",
            "Sorry, I wasn't listening. Could you repeat that?" to "Извини, я не слушал. Можешь повторить?",
            "We were having dinner when the lights went out." to "Мы ужинали, когда погас свет.",
        ) + g(
            "Past Perfect — раньше другого прошлого",
            "By the time we arrived, the film had already started." to "Когда мы пришли, фильм уже начался.",
            "She told me she had never been abroad before." to "Она сказала, что никогда раньше не была за границей.",
            "I realized I had left my keys at the office." to "Я понял, что оставил ключи в офисе.",
            "He had worked there for ten years before he got promoted." to "Он проработал там десять лет, прежде чем его повысили.",
            "The train had left before we got to the station." to "Поезд ушёл до того, как мы добрались до вокзала.",
            "Had you met him before the party?" to "Ты встречал его до вечеринки?",
        ) + g(
            "Present Perfect Continuous — длится до сих пор",
            "I've been learning English for three months." to "Я учу английский уже три месяца.",
            "She's been working on this project since January." to "Она работает над этим проектом с января.",
            "How long have you been waiting?" to "Сколько ты уже ждёшь?",
            "It's been raining all day." to "Весь день идёт дождь.",
            "You look tired. Have you been sleeping well?" to "Ты выглядишь уставшим. Ты нормально спишь?",
            "We've been thinking about moving to another city." to "Мы подумываем о переезде в другой город.",
        ) + g(
            "Past Perfect Continuous — длилось до момента в прошлом",
            "He had been driving for six hours, so he was exhausted." to "Он был за рулём шесть часов, поэтому был измотан.",
            "They had been dating for two years before they got married." to "Они встречались два года, прежде чем пожениться.",
            "My eyes hurt because I had been staring at the screen all day." to "Глаза болели, потому что я весь день смотрел в экран.",
            "She had been waiting for an hour when he finally called." to "Она ждала уже час, когда он наконец позвонил.",
        ) + g(
            "Future Continuous и Future Perfect",
            "This time tomorrow I'll be flying to Istanbul." to "Завтра в это время я буду лететь в Стамбул.",
            "Don't call at nine — I'll be putting the kids to bed." to "Не звони в девять — я буду укладывать детей.",
            "Will you be using the car tonight?" to "Ты будешь пользоваться машиной вечером?",
            "By the end of the year, I'll have saved enough for a new car." to "К концу года я накоплю на новую машину.",
            "By the time you get home, I'll have cooked dinner." to "К твоему возвращению я уже приготовлю ужин.",
            "Next month we'll have been married for ten years." to "В следующем месяце будет десять лет, как мы женаты.",
            "Will you have finished the report by Friday?" to "Ты закончишь отчёт к пятнице?",
        ) + g(
            "Условные предложения: если бы…",
            "If I had more free time, I would learn to play the guitar." to "Если бы у меня было больше свободного времени, я бы научился играть на гитаре.",
            "What would you do if you lost your job tomorrow?" to "Что бы ты сделал, если бы завтра потерял работу?",
            "If I had known about the traffic, I would have left earlier." to "Если бы я знал о пробках, я бы выехал раньше.",
            "If she hadn't missed the bus, she wouldn't have been late." to "Если бы она не пропустила автобус, она бы не опоздала.",
            "We would have won if our goalkeeper hadn't got injured." to "Мы бы выиграли, если бы наш вратарь не получил травму.",
            "If I had taken that job, I would be living in Moscow now." to "Если бы я тогда согласился на ту работу, сейчас бы жил в Москве.",
            "If he weren't so stubborn, he would have apologized already." to "Не будь он таким упрямым, он бы уже извинился.",
        ) + g(
            "Сожаления: I wish, if only",
            "I wish I spoke English fluently." to "Жаль, что я не говорю по-английски свободно.",
            "I wish I hadn't said that." to "Зря я это сказал.",
            "If only we had more time!" to "Если бы только у нас было больше времени!",
            "I wish you would stop interrupting me." to "Хоть бы ты перестал меня перебивать.",
            "She wishes she had studied medicine." to "Она жалеет, что не пошла учиться на врача.",
        ) + g(
            "Модальные + have done: должен был, наверное",
            "You should have told me earlier." to "Тебе следовало сказать мне раньше.",
            "She must have forgotten about our meeting." to "Она, должно быть, забыла о нашей встрече.",
            "They can't have left already — their car is still here." to "Не может быть, чтобы они уже ушли — их машина ещё здесь.",
            "I could have helped you if you had asked." to "Я мог бы помочь, если бы ты попросил.",
            "He might have missed the train." to "Возможно, он опоздал на поезд.",
            "We shouldn't have eaten so much." to "Не стоило нам так много есть.",
        ) + g(
            "Страдательный залог в разных временах",
            "The meeting has been postponed until next week." to "Встречу перенесли на следующую неделю.",
            "My car is being repaired at the moment." to "Мою машину сейчас ремонтируют.",
            "The bridge will have been built by 2030." to "Мост построят к 2030 году.",
            "I was told that the flight had been cancelled." to "Мне сказали, что рейс отменили.",
            "She hates being interrupted." to "Она терпеть не может, когда её перебивают.",
        ) + g(
            "Косвенная речь",
            "He said he would call me as soon as he got home." to "Он сказал, что позвонит, как только доберётся домой.",
            "She asked me whether I had ever been to London." to "Она спросила, был ли я когда-нибудь в Лондоне.",
            "The doctor told me to stop working so late." to "Врач велел мне перестать работать допоздна.",
            "They promised that the parcel would arrive on Monday." to "Они пообещали, что посылка придёт в понедельник.",
        ) + g(
            "Привычки: used to, would, be used to",
            "I used to hate coffee, but now I can't live without it." to "Раньше я ненавидел кофе, а теперь не могу без него жить.",
            "When we were kids, we would spend every summer at our grandma's." to "В детстве мы каждое лето проводили у бабушки.",
            "I'm not used to getting up so early." to "Я не привык вставать так рано.",
            "You'll soon get used to driving on the left." to "Ты скоро привыкнешь ездить по левой стороне.",
        ) + g(
            "Сложные конструкции",
            "The woman who lives next door works as a pilot." to "Женщина, которая живёт по соседству, работает пилотом.",
            "That's the restaurant where we had our first date." to "Это ресторан, где у нас было первое свидание.",
            "Not only did he forget my birthday, but he also didn't apologize." to "Мало того что он забыл мой день рождения, так ещё и не извинился.",
            "I'd only just sat down when the phone rang." to "Я только сел, как зазвонил телефон.",
            "We'd barely left the house when it started to rain." to "Мы едва вышли из дома, как начался дождь.",
            "It's high time we bought a new fridge." to "Давно пора купить новый холодильник.",
            "Whatever happens, I'll be on your side." to "Что бы ни случилось, я на твоей стороне.",
            "The more you practise, the easier it gets." to "Чем больше практикуешься, тем легче становится.",
        )
}

/** Уроки грамматики по сложным временам — продолжение курса «шаг за шагом». */
object TenseLessons {
    private fun ex(en: String, ru: String) = Example(en, ru)
    private fun tr(from: String, task: String, answer: String) = Task.Transform(from, task, answer)
    private fun tl(ru: String, answer: String) = Task.Translate(ru, answer)
    private fun ch(sentence: String, ru: String, correct: Int, vararg options: String) =
        Task.Choice(sentence, options.toList(), correct, ru)

    val lessons: List<GrammarLesson> = listOf(
        GrammarLesson(
            "pastcont", "🎞", "Past Continuous: что происходило", 3,
            "Past Continuous описывает процесс в определённый момент прошлого: «в восемь я ужинал». " +
                "Формула: was / were + глагол с -ing. Часто стоит рядом с Past Simple: длинное действие (Continuous) прерывает короткое (Simple): " +
                "I was sleeping when you called — «Я спал, когда ты позвонил». Со словом while — два процесса одновременно.",
            listOf(
                "I / he / she was working" to "работал (процесс)", "we / you / they were working" to "работали",
                "Were you sleeping?" to "вопрос", "I wasn't listening" to "отрицание",
                "I was reading when he came" to "процесс + событие",
            ),
            listOf(
                ex("I was watching TV when the doorbell rang.", "Я смотрел телевизор, когда позвонили в дверь."),
                ex("What were you doing yesterday at six?", "Что ты делал вчера в шесть?"),
                ex("While I was cooking, he was setting the table.", "Пока я готовил, он накрывал на стол."),
                ex("It was snowing all night.", "Всю ночь шёл снег."),
                ex("They weren't working at that time.", "В то время они не работали."),
                ex("She cut her finger while she was chopping onions.", "Она порезала палец, когда резала лук."),
            ),
            listOf(
                tr("I read a book at nine last night.", "Скажи «в девять я читал (процесс)»", "I was reading a book at nine last night. | At nine last night I was reading a book."),
                tr("They were playing tennis.", Q, "Were they playing tennis?"),
                tr("She was sleeping.", NEG, "She was not sleeping. | She wasn't sleeping."),
                tl("Когда ты позвонил, я ехал на работу.", "When you called, I was driving to work. | I was driving to work when you called. | When you called, I was going to work. | I was going to work when you called."),
                tl("Что вы делали вчера вечером?", "What were you doing last night? | What were you doing yesterday evening?"),
                tl("Пока я спал, шёл дождь.", "While I was sleeping, it was raining. | It was raining while I was sleeping."),
                ch("I ___ a shower when the phone rang.", "Я принимал душ, когда зазвонил телефон.", 1, "took", "was taking", "have taken"),
                ch("While we ___ dinner, the lights went out.", "Пока мы ужинали, погас свет.", 2, "had", "have", "were having"),
                ch("He ___ his leg while he was skiing.", "Он сломал ногу, когда катался на лыжах.", 0, "broke", "was breaking", "breaks"),
            )
        ),
        GrammarLesson(
            "pastperf", "⏪", "Past Perfect: случилось ещё раньше", 3,
            "Past Perfect нужен, когда в прошлом два события и важно, какое было раньше: " +
                "When I arrived, the train had left — «Когда я пришёл, поезд уже ушёл». " +
                "Формула: had + третья форма глагола, одинаково для всех лиц. Слова-подсказки: already, by the time, before, after, never… before.",
            listOf(
                "I had finished (I'd finished)" to "уже закончил (раньше)", "Had you seen it before?" to "вопрос",
                "She hadn't eaten" to "отрицание", "By the time we came, he had left" to "к моменту…",
                "I realized I had forgotten…" to "понял, что забыл",
            ),
            listOf(
                ex("When we got to the cinema, the film had already started.", "Когда мы пришли в кино, фильм уже начался."),
                ex("I had never seen snow before I moved here.", "До переезда сюда я никогда не видел снега."),
                ex("She was upset because she had lost her phone.", "Она расстроилась, потому что потеряла телефон."),
                ex("Had you finished work by six?", "Ты закончил работу к шести?"),
                ex("He hadn't told anyone about it.", "Он никому об этом не рассказывал."),
                ex("After they had eaten, they went for a walk.", "Поев, они пошли гулять."),
            ),
            listOf(
                tr("The bus left. Then I arrived at the stop.", "Объедини: When I arrived…", "When I arrived at the stop, the bus had left. | When I arrived at the stop, the bus had already left."),
                tr("She had seen this film.", Q, "Had she seen this film?"),
                tr("We had met before.", NEG, "We had not met before. | We hadn't met before."),
                tl("Я понял, что забыл паспорт.", "I realized I had forgotten my passport. | I realised I had forgotten my passport. | I realized that I had forgotten my passport."),
                tl("Когда он пришёл, мы уже поели.", "When he came, we had already eaten. | When he arrived, we had already eaten."),
                tl("Она никогда раньше не летала на самолёте.", "She had never flown before. | She had never flown on a plane before."),
                ch("By the time I woke up, everyone ___.", "Когда я проснулся, все уже ушли.", 2, "left", "has left", "had left"),
                ch("He ___ me that he had found a new job.", "Он сказал мне, что нашёл новую работу.", 0, "told", "had told", "has told"),
                ch("I didn't recognize her because she ___ her hair.", "Я её не узнал — она сменила причёску.", 1, "changed", "had changed", "has changed"),
            )
        ),
        GrammarLesson(
            "ppcont", "⏱", "Present Perfect Continuous: как долго уже", 3,
            "Это время отвечает на вопрос «как долго?» и показывает действие, которое началось в прошлом и длится до сих пор (или только что закончилось и виден результат): " +
                "I've been working here for five years — «Я работаю здесь уже пять лет». " +
                "Формула: have / has been + глагол с -ing. С for (в течение) и since (с какого-то момента). " +
                "Внимание: в русском здесь настоящее время («работаю»), а в английском — нет!",
            listOf(
                "I have been waiting (I've been waiting)" to "жду уже…", "She has been learning" to "учит уже…",
                "How long have you been…?" to "как долго?", "for two hours / since Monday" to "в течение / с",
                "You look tired — have you been running?" to "виден результат",
            ),
            listOf(
                ex("I've been waiting for you for an hour!", "Я жду тебя уже час!"),
                ex("How long have you been living here?", "Сколько ты здесь живёшь?"),
                ex("It's been snowing since morning.", "С утра идёт снег."),
                ex("She's been studying English since school.", "Она учит английский со школы."),
                ex("Your eyes are red. Have you been crying?", "У тебя красные глаза. Ты плакала?"),
                ex("We haven't been sleeping well lately.", "Мы в последнее время плохо спим."),
            ),
            listOf(
                tr("I work here. I started three years ago.", "Скажи «работаю здесь уже три года»", "I have been working here for three years. | I've been working here for three years."),
                tr("You have been waiting.", "Спроси «как долго?»", "How long have you been waiting?"),
                tr("He has been sleeping well.", NEG, "He has not been sleeping well. | He hasn't been sleeping well."),
                tl("Я учу английский уже два года.", "I have been learning English for two years. | I've been learning English for two years. | I've been studying English for two years. | I have been studying English for two years."),
                tl("Сколько ты уже здесь работаешь?", "How long have you been working here?"),
                tl("С понедельника идёт дождь.", "It has been raining since Monday. | It's been raining since Monday."),
                ch("I ___ for you since five o'clock!", "Я жду тебя с пяти часов!", 2, "wait", "am waiting", "have been waiting"),
                ch("She has been working here ___ 2019.", "Она работает здесь с 2019 года.", 1, "for", "since", "from"),
                ch("They've been travelling ___ two months.", "Они путешествуют уже два месяца.", 0, "for", "since", "during"),
            )
        ),
        GrammarLesson(
            "futadv", "🛫", "Future Continuous и Future Perfect", 3,
            "Future Continuous — процесс в определённый момент будущего: This time tomorrow I'll be lying on the beach — «Завтра в это время я буду лежать на пляже». Формула: will be + -ing. " +
                "Future Perfect — что будет уже сделано к моменту в будущем: By Friday I'll have finished — «К пятнице я уже закончу». Формула: will have + третья форма. " +
                "Подсказки: this time tomorrow, at 5 pm tomorrow — Continuous; by Friday, by the time, by then — Perfect.",
            listOf(
                "I'll be working at 10" to "буду работать (процесс)", "Will you be using…?" to "вежливый вопрос о планах",
                "I'll have finished by 6" to "закончу к 6", "By then we'll have left" to "к тому времени уже уедем",
                "won't have done" to "ещё не сделаю",
            ),
            listOf(
                ex("Don't call me at seven, I'll be driving.", "Не звони в семь, я буду за рулём."),
                ex("This time next week we'll be swimming in the sea.", "Через неделю в это время мы будем купаться в море."),
                ex("Will you be needing anything else?", "Вам ещё что-нибудь понадобится?"),
                ex("By the end of the month, I'll have paid off the loan.", "К концу месяца я погашу кредит."),
                ex("The film will have started by the time we get there.", "К нашему приходу фильм уже начнётся."),
                ex("I won't have finished the report by Monday.", "К понедельнику я отчёт не закончу."),
            ),
            listOf(
                tr("I will work at ten tomorrow.", "Скажи «буду в процессе работы»", "I will be working at ten tomorrow. | I'll be working at ten tomorrow. | At ten tomorrow I'll be working."),
                tr("I will finish the project. Deadline: Friday.", "Скажи «закончу к пятнице»", "I will have finished the project by Friday. | I'll have finished the project by Friday."),
                tr("They will have left.", Q, "Will they have left?"),
                tl("Завтра в это время я буду лететь в Москву.", "This time tomorrow I will be flying to Moscow. | This time tomorrow I'll be flying to Moscow. | I'll be flying to Moscow this time tomorrow."),
                tl("К вечеру я всё сделаю.", "I will have done everything by the evening. | I'll have done everything by the evening. | I'll have done everything by evening."),
                tl("Ты будешь пользоваться компьютером?", "Will you be using the computer?"),
                ch("At 8 pm tomorrow I ___ dinner with my parents.", "Завтра в 8 вечера я буду ужинать с родителями.", 1, "will have", "will be having", "have had"),
                ch("By 2030 they ___ the new airport.", "К 2030 году они построят новый аэропорт.", 2, "will build", "will be building", "will have built"),
                ch("Call me after six — I ___ work by then.", "Звони после шести — к тому времени я закончу работу.", 0, "will have finished", "will be finishing", "finish"),
            )
        ),
        GrammarLesson(
            "cond3", "🔙", "Если бы тогда… (third conditional) и I wish", 3,
            "Third conditional — о прошлом, которое уже не изменить: If I had known, I would have helped — «Если бы я знал, я бы помог». " +
                "Формула: If + had + 3-я форма, would have + 3-я форма. " +
                "I wish + Past Simple — сожаление о настоящем (I wish I had a car — «жаль, что у меня нет машины»). " +
                "I wish + Past Perfect — сожаление о прошлом (I wish I hadn't said that — «зря я это сказал»).",
            listOf(
                "If I had known, I would have come" to "если бы знал — пришёл бы", "If she hadn't…, she wouldn't have…" to "отрицание",
                "I wish I knew" to "жаль, что не знаю (сейчас)", "I wish I had known" to "жаль, что не знал (тогда)",
                "If only…!" to "если бы только…!",
            ),
            listOf(
                ex("If I had left earlier, I wouldn't have missed the flight.", "Если бы я вышел раньше, я бы не опоздал на рейс."),
                ex("She would have passed the exam if she had studied more.", "Она бы сдала экзамен, если бы больше занималась."),
                ex("What would you have done in my place?", "Что бы ты сделал на моём месте?"),
                ex("I wish I lived closer to the sea.", "Жаль, что я живу не у моря."),
                ex("I wish I had listened to my parents.", "Жаль, что я не послушал родителей."),
                ex("If only I hadn't sold that car!", "Зачем я только продал ту машину!"),
            ),
            listOf(
                tr("I didn't know about the party, so I didn't come.", "Скажи через «If I had known…»", "If I had known about the party, I would have come. | If I'd known about the party, I would have come. | If I had known about the party, I'd have come."),
                tr("I don't have a car.", "Скажи через «I wish…»", "I wish I had a car."),
                tr("I said it. Now I regret it.", "Скажи через «I wish…» (о прошлом)", "I wish I hadn't said it. | I wish I had not said it. | I wish I hadn't said that."),
                tl("Если бы ты позвонил, я бы помог.", "If you had called, I would have helped. | If you'd called, I would have helped. | If you had called, I'd have helped."),
                tl("Жаль, что я не говорю по-английски.", "I wish I spoke English. | I wish I could speak English."),
                tl("Жаль, что я не поехал с вами.", "I wish I had gone with you. | I wish I'd gone with you."),
                ch("If we ___ the map, we wouldn't have got lost.", "Если бы мы взяли карту, мы бы не заблудились.", 2, "took", "would take", "had taken"),
                ch("I wish I ___ more time now.", "Жаль, что у меня сейчас мало времени.", 0, "had", "have", "had had"),
                ch("She would have come if you ___ her.", "Она бы пришла, если бы ты её пригласил.", 1, "invited", "had invited", "would invite"),
            )
        ),
        GrammarLesson(
            "modalperf", "🕵️", "Should have, must have, can't have", 3,
            "Модальный глагол + have + 3-я форма — способ говорить о прошлом с оценкой: " +
                "should have done — «надо было сделать» (а не сделал); shouldn't have done — «не стоило»; " +
                "must have done — «наверняка сделал»; can't have done — «не может быть, чтобы сделал»; " +
                "might / could have done — «возможно, сделал»; could have done — ещё и «мог бы (но не сделал)».",
            listOf(
                "You should have told me" to "надо было сказать", "I shouldn't have eaten so much" to "не стоило",
                "He must have forgotten" to "наверняка забыл", "She can't have seen us" to "не может быть, что видела",
                "They might have left" to "возможно, ушли",
            ),
            listOf(
                ex("You should have taken an umbrella.", "Надо было взять зонт."),
                ex("I shouldn't have stayed up so late.", "Не стоило мне так поздно ложиться."),
                ex("The ground is wet. It must have rained.", "Земля мокрая. Наверное, был дождь."),
                ex("He can't have finished already — he only started an hour ago.", "Не может быть, что он уже закончил, — он начал всего час назад."),
                ex("She might have missed the bus.", "Возможно, она пропустила автобус."),
                ex("You could have warned me!", "Мог бы и предупредить!"),
            ),
            listOf(
                tr("You didn't call me. That was a mistake.", "Скажи «тебе надо было позвонить»", "You should have called me. | You should've called me."),
                tr("I'm sure he forgot.", "Скажи через «must have»", "He must have forgotten."),
                tr("It's impossible that she saw us.", "Скажи через «can't have»", "She can't have seen us. | She cannot have seen us."),
                tl("Не стоило мне это покупать.", "I shouldn't have bought it. | I should not have bought it. | I shouldn't have bought this."),
                tl("Наверное, они уже приехали.", "They must have arrived already. | They must have already arrived. | They must have arrived."),
                tl("Возможно, он потерял телефон.", "He might have lost his phone. | He may have lost his phone. | He could have lost his phone."),
                ch("The lights are off. They ___ gone to bed.", "Свет выключен. Наверное, они легли спать.", 1, "should have", "must have", "can't have"),
                ch("You ___ told me! I would have helped.", "Надо было мне сказать! Я бы помог.", 0, "should have", "must have", "might have"),
                ch("He ___ stolen it — he was with me all day.", "Он не мог это украсть — он весь день был со мной.", 2, "must have", "should have", "can't have"),
            )
        ),
    )

    private const val NEG = "Сделай отрицание"
    private const val Q = "Задай общий вопрос"
}
