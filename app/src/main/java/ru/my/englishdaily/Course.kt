package ru.my.englishdaily

/**
 * Курс: 30 разговорных конструкций. В каждую подставляются разные слова.
 * Порядок важен — новые фразы выдаются сверху вниз, от простого к сложному.
 * Формат подстановки: "английская вставка" to "полная фраза по-русски".
 */
object Course {

    private class Pattern(val title: String, val en: String, val fills: List<Pair<String, String>>)

    private val patterns = listOf(
        Pattern(
            "Я хочу…", "I want to {}.", listOf(
                "eat" to "Я хочу есть.",
                "rest" to "Я хочу отдохнуть.",
                "go home" to "Я хочу домой.",
                "talk to you" to "Я хочу с тобой поговорить.",
                "try it" to "Я хочу это попробовать.",
                "watch a movie" to "Я хочу посмотреть фильм.",
                "see the sea" to "Я хочу увидеть море.",
                "change my job" to "Я хочу сменить работу.",
                "travel more" to "Я хочу больше путешествовать.",
                "buy a new car" to "Я хочу купить новую машину.",
                "understand English" to "Я хочу понимать английский.",
                "stay here" to "Я хочу остаться здесь.",
            )
        ),
        Pattern(
            "Я не хочу…", "I don't want to {}.", listOf(
                "wait" to "Я не хочу ждать.",
                "be late" to "Я не хочу опаздывать.",
                "work today" to "Я не хочу сегодня работать.",
                "talk about it" to "Я не хочу об этом говорить.",
                "get up early" to "Я не хочу рано вставать.",
                "spend money" to "Я не хочу тратить деньги.",
                "argue" to "Я не хочу спорить.",
                "go there alone" to "Я не хочу идти туда один.",
                "lose my job" to "Я не хочу потерять работу.",
                "drive at night" to "Я не хочу ездить ночью.",
                "eat right now" to "Я не хочу есть прямо сейчас.",
                "miss the bus" to "Я не хочу опоздать на автобус.",
            )
        ),
        Pattern(
            "Мне нужно…", "I need to {}.", listOf(
                "go" to "Мне нужно идти.",
                "fix my car" to "Мне нужно починить машину.",
                "see a doctor" to "Мне нужно сходить к врачу.",
                "get some sleep" to "Мне нужно поспать.",
                "think about it" to "Мне нужно подумать.",
                "pay the bills" to "Мне нужно оплатить счета.",
                "find a job" to "Мне нужно найти работу.",
                "charge my phone" to "Мне нужно зарядить телефон.",
                "leave early today" to "Мне сегодня нужно уйти пораньше.",
                "buy some food" to "Мне нужно купить еды.",
                "call my friend" to "Мне нужно позвонить другу.",
                "wash my car" to "Мне нужно помыть машину.",
            )
        ),
        Pattern(
            "Можно мне…? (разрешение)", "Can I {}?", listOf(
                "come in" to "Можно войти?",
                "sit here" to "Можно здесь сесть?",
                "ask you something" to "Можно тебя кое о чём спросить?",
                "pay by card" to "Можно оплатить картой?",
                "try it on" to "Можно это примерить?",
                "see the menu" to "Можно посмотреть меню?",
                "use your phone" to "Можно воспользоваться твоим телефоном?",
                "open the window" to "Можно открыть окно?",
                "park here" to "Здесь можно парковаться?",
                "have some water" to "Можно мне воды?",
                "call you later" to "Можно я тебе позже позвоню?",
                "take this" to "Можно это взять?",
            )
        ),
        Pattern(
            "Ты можешь…? (просьба)", "Can you {}?", listOf(
                "help me" to "Можешь мне помочь?",
                "say it again" to "Можешь сказать ещё раз?",
                "speak slower" to "Можешь говорить медленнее?",
                "show me the way" to "Можешь показать дорогу?",
                "wait a minute" to "Можешь подождать минутку?",
                "call me back" to "Можешь мне перезвонить?",
                "pick me up" to "Можешь меня забрать?",
                "turn it off" to "Можешь это выключить?",
                "send me the photo" to "Можешь прислать мне фото?",
                "write it down" to "Можешь это записать?",
                "take a picture of us" to "Можешь нас сфотографировать?",
                "close the door" to "Можешь закрыть дверь?",
            )
        ),
        Pattern(
            "Я собираюсь… (планы)", "I'm going to {}.", listOf(
                "call him" to "Я собираюсь ему позвонить.",
                "buy a new phone" to "Я собираюсь купить новый телефон.",
                "stay at home tonight" to "Я собираюсь остаться дома сегодня вечером.",
                "go to bed" to "Я собираюсь лечь спать.",
                "cook dinner" to "Я собираюсь приготовить ужин.",
                "start tomorrow" to "Я собираюсь начать завтра.",
                "take a shower" to "Я собираюсь принять душ.",
                "visit my parents" to "Я собираюсь навестить родителей.",
                "sell my car" to "Я собираюсь продать машину.",
                "learn to swim" to "Я собираюсь научиться плавать.",
                "watch the game" to "Я собираюсь посмотреть матч.",
                "clean the house" to "Я собираюсь убраться дома.",
            )
        ),
        Pattern(
            "Ты собираешься…?", "Are you going to {}?", listOf(
                "come" to "Ты собираешься прийти?",
                "eat that" to "Ты собираешься это есть?",
                "buy it" to "Ты собираешься это купить?",
                "tell him" to "Ты собираешься ему сказать?",
                "work tomorrow" to "Ты завтра собираешься работать?",
                "stay long" to "Ты собираешься надолго остаться?",
                "call her" to "Ты собираешься ей звонить?",
                "move" to "Ты собираешься переезжать?",
                "drive" to "Ты собираешься сесть за руль?",
                "go out tonight" to "Ты собираешься куда-то сегодня вечером?",
            )
        ),
        Pattern(
            "Я должен…", "I have to {}.", listOf(
                "work tomorrow" to "Завтра я должен работать.",
                "get up at six" to "Я должен вставать в шесть.",
                "finish this today" to "Я должен закончить это сегодня.",
                "pay for it" to "Я должен за это заплатить.",
                "be there at ten" to "Я должен быть там в десять.",
                "pick up my son" to "Я должен забрать сына.",
                "go to the bank" to "Я должен сходить в банк.",
                "tell you something" to "Я должен тебе кое-что сказать.",
                "take my medicine" to "Я должен принять лекарство.",
                "answer the phone" to "Я должен ответить на звонок.",
                "go back" to "Я должен вернуться.",
                "study tonight" to "Сегодня вечером я должен заниматься.",
            )
        ),
        Pattern(
            "У вас есть…?", "Do you have {}?", listOf(
                "a pen" to "У вас есть ручка?",
                "any water" to "У вас есть вода?",
                "a room for tonight" to "У вас есть номер на сегодня?",
                "this in black" to "У вас есть такое в чёрном?",
                "a smaller size" to "У вас есть размер поменьше?",
                "a minute" to "У тебя есть минутка?",
                "a phone charger" to "У вас есть зарядка для телефона?",
                "any questions" to "У вас есть вопросы?",
                "Wi-Fi here" to "Здесь есть вайфай?",
                "kids" to "У тебя есть дети?",
                "plans for tonight" to "У тебя есть планы на вечер?",
                "any change" to "У вас есть мелочь?",
            )
        ),
        Pattern(
            "Здесь рядом есть…?", "Is there {} near here?", listOf(
                "a pharmacy" to "Здесь рядом есть аптека?",
                "a bank" to "Здесь рядом есть банк?",
                "a supermarket" to "Здесь рядом есть супермаркет?",
                "a cafe" to "Здесь рядом есть кафе?",
                "a bus stop" to "Здесь рядом есть остановка?",
                "a gas station" to "Здесь рядом есть заправка?",
                "a hotel" to "Здесь рядом есть гостиница?",
                "a toilet" to "Здесь рядом есть туалет?",
                "an ATM" to "Здесь рядом есть банкомат?",
                "a parking lot" to "Здесь рядом есть парковка?",
                "a hospital" to "Здесь рядом есть больница?",
                "a good restaurant" to "Здесь рядом есть хороший ресторан?",
            )
        ),
        Pattern(
            "Как мне…?", "How do I {}?", listOf(
                "get to the center" to "Как мне добраться до центра?",
                "get to the airport" to "Как добраться до аэропорта?",
                "turn it on" to "Как это включить?",
                "pay for parking" to "Как оплатить парковку?",
                "buy a ticket" to "Как купить билет?",
                "say it in English" to "Как это сказать по-английски?",
                "call a taxi" to "Как вызвать такси?",
                "open this" to "Как это открыть?",
                "get there" to "Как туда добраться?",
                "find you" to "Как тебя найти?",
                "use this app" to "Как пользоваться этим приложением?",
                "change the language" to "Как поменять язык?",
            )
        ),
        Pattern(
            "Во сколько…?", "What time {}?", listOf(
                "do you open" to "Во сколько вы открываетесь?",
                "do you close" to "Во сколько вы закрываетесь?",
                "does it start" to "Во сколько начало?",
                "does the bus come" to "Во сколько приходит автобус?",
                "is breakfast" to "Во сколько завтрак?",
                "do you finish work" to "Во сколько ты заканчиваешь работу?",
                "do you get up" to "Во сколько ты встаёшь?",
                "should I come" to "Во сколько мне прийти?",
                "is the meeting" to "Во сколько встреча?",
                "does the train leave" to "Во сколько отходит поезд?",
            )
        ),
        Pattern(
            "Я бы хотел… (вежливо)", "I'd like {}.", listOf(
                "a coffee, please" to "Я бы хотел кофе, пожалуйста.",
                "to order" to "Я бы хотел сделать заказ.",
                "the check, please" to "Я бы хотел счёт, пожалуйста.",
                "to book a table" to "Я бы хотел забронировать столик.",
                "a room for two nights" to "Я бы хотел номер на две ночи.",
                "to pay by card" to "Я бы хотел оплатить картой.",
                "some water" to "Я бы хотел воды.",
                "to try this" to "Я бы хотел это попробовать.",
                "to speak to the manager" to "Я бы хотел поговорить с менеджером.",
                "a ticket to London" to "Я бы хотел билет до Лондона.",
                "to return this" to "Я бы хотел это вернуть.",
                "a bit more time" to "Мне бы ещё немного времени.",
            )
        ),
        Pattern(
            "Давай…", "Let's {}.", listOf(
                "go" to "Пойдём.",
                "eat something" to "Давай что-нибудь поедим.",
                "take a break" to "Давай сделаем перерыв.",
                "meet at six" to "Давай встретимся в шесть.",
                "talk later" to "Давай поговорим позже.",
                "watch a movie" to "Давай посмотрим фильм.",
                "go for a walk" to "Давай прогуляемся.",
                "try again" to "Давай попробуем ещё раз.",
                "take a taxi" to "Давай возьмём такси.",
                "stay home" to "Давай останемся дома.",
                "do it together" to "Давай сделаем это вместе.",
                "order pizza" to "Давай закажем пиццу.",
            )
        ),
        Pattern(
            "Мне нравится…", "I like {}.", listOf(
                "walking in the park" to "Мне нравится гулять в парке.",
                "cooking" to "Я люблю готовить.",
                "this song" to "Мне нравится эта песня.",
                "your car" to "Мне нравится твоя машина.",
                "watching football" to "Я люблю смотреть футбол.",
                "driving at night" to "Мне нравится ездить ночью.",
                "this place" to "Мне нравится это место.",
                "working with you" to "Мне нравится с тобой работать.",
                "spicy food" to "Я люблю острую еду.",
                "fishing" to "Я люблю рыбалку.",
                "this weather" to "Мне нравится такая погода.",
                "learning English" to "Мне нравится учить английский.",
            )
        ),
        Pattern(
            "Мне не нравится…", "I don't like {}.", listOf(
                "waiting" to "Не люблю ждать.",
                "this weather" to "Мне не нравится эта погода.",
                "getting up early" to "Не люблю рано вставать.",
                "crowds" to "Не люблю толпы.",
                "his attitude" to "Мне не нравится его отношение.",
                "cold coffee" to "Не люблю холодный кофе.",
                "driving in the city" to "Не люблю ездить по городу.",
                "this idea" to "Мне не нравится эта идея.",
                "loud music" to "Не люблю громкую музыку.",
                "being late" to "Не люблю опаздывать.",
            )
        ),
        Pattern(
            "Я… (состояние)", "I'm {}.", listOf(
                "tired" to "Я устал.",
                "hungry" to "Я голоден.",
                "a bit nervous" to "Я немного нервничаю.",
                "bored" to "Мне скучно.",
                "busy right now" to "Я сейчас занят.",
                "on my way" to "Я уже еду.",
                "almost there" to "Я почти на месте.",
                "not ready yet" to "Я ещё не готов.",
                "so happy for you" to "Я так за тебя рад.",
                "sorry about that" to "Прости за это.",
                "looking for a job" to "Я ищу работу.",
                "just kidding" to "Я просто шучу.",
            )
        ),
        Pattern(
            "Это…", "It's {}.", listOf(
                "too expensive" to "Это слишком дорого.",
                "not a problem" to "Это не проблема.",
                "very cold today" to "Сегодня очень холодно.",
                "too late" to "Слишком поздно.",
                "my fault" to "Это моя вина.",
                "not my car" to "Это не моя машина.",
                "so beautiful" to "Это так красиво.",
                "up to you" to "Решать тебе.",
                "time to go" to "Пора идти.",
                "hard to say" to "Трудно сказать.",
                "not far from here" to "Это недалеко отсюда.",
                "a long story" to "Это долгая история.",
            )
        ),
        Pattern(
            "Я думаю…", "I think {}.", listOf(
                "you're right" to "Думаю, ты прав.",
                "it's a good idea" to "Думаю, это хорошая идея.",
                "we're late" to "Кажется, мы опаздываем.",
                "I'm lost" to "Кажется, я заблудился.",
                "it will rain" to "Думаю, будет дождь.",
                "he's busy" to "Думаю, он занят.",
                "it's too expensive" to "Думаю, это слишком дорого.",
                "I can do it" to "Думаю, я справлюсь.",
                "we should go" to "Думаю, нам пора.",
                "I forgot my keys" to "Кажется, я забыл ключи.",
            )
        ),
        Pattern(
            "Хочешь…?", "Do you want to {}?", listOf(
                "come with us" to "Хочешь пойти с нами?",
                "grab a coffee" to "Хочешь выпить кофе?",
                "watch a movie tonight" to "Хочешь посмотреть фильм вечером?",
                "try it" to "Хочешь попробовать?",
                "go for a walk" to "Хочешь прогуляться?",
                "talk about it" to "Хочешь об этом поговорить?",
                "eat something" to "Хочешь что-нибудь съесть?",
                "play a game" to "Хочешь сыграть?",
                "stay for dinner" to "Останешься на ужин?",
                "meet tomorrow" to "Хочешь встретиться завтра?",
            )
        ),
        Pattern(
            "Я… (обещания, решения)", "I'll {}.", listOf(
                "call you later" to "Я позвоню тебе позже.",
                "be there in ten minutes" to "Буду через десять минут.",
                "think about it" to "Я подумаю.",
                "take it" to "Я это возьму.",
                "help you" to "Я тебе помогу.",
                "do it tomorrow" to "Сделаю это завтра.",
                "be right back" to "Сейчас вернусь.",
                "text you" to "Я тебе напишу.",
                "pay" to "Я заплачу.",
                "wait for you" to "Я тебя подожду.",
                "check" to "Я проверю.",
                "let you know" to "Я дам тебе знать.",
            )
        ),
        Pattern(
            "Не… (просьбы)", "Don't {}.", listOf(
                "worry" to "Не волнуйся.",
                "be late" to "Не опаздывай.",
                "forget" to "Не забудь.",
                "tell anyone" to "Никому не говори.",
                "touch it" to "Не трогай.",
                "go there" to "Не ходи туда.",
                "call me after ten" to "Не звони мне после десяти.",
                "drive so fast" to "Не гони так.",
                "say that" to "Не говори так.",
                "give up" to "Не сдавайся.",
                "be sad" to "Не грусти.",
                "wait for me" to "Не жди меня.",
            )
        ),
        Pattern(
            "Не могли бы вы…? (очень вежливо)", "Could you {}?", listOf(
                "help me, please" to "Не могли бы вы мне помочь?",
                "repeat that" to "Не могли бы вы повторить?",
                "speak a little slower" to "Не могли бы вы говорить чуть медленнее?",
                "tell me the way to the station" to "Не подскажете, как пройти к вокзалу?",
                "take a photo of me" to "Не могли бы вы меня сфотографировать?",
                "call a taxi for me" to "Не могли бы вы вызвать мне такси?",
                "show me on the map" to "Не могли бы вы показать на карте?",
                "wait a second" to "Подождите секундочку, пожалуйста.",
                "bring the menu" to "Принесите, пожалуйста, меню.",
                "spell that" to "Не могли бы вы сказать по буквам?",
            )
        ),
        Pattern(
            "Ничего, если я…?", "Is it okay if I {}?", listOf(
                "sit here" to "Ничего, если я здесь сяду?",
                "open the window" to "Ничего, если я открою окно?",
                "come later" to "Ничего, если я приду позже?",
                "leave early" to "Ничего, если я уйду пораньше?",
                "take a photo" to "Ничего, если я сфотографирую?",
                "use your charger" to "Ничего, если я возьму твою зарядку?",
                "call you tonight" to "Ничего, если я позвоню тебе вечером?",
                "bring a friend" to "Ничего, если я приведу друга?",
                "park here" to "Ничего, если я здесь припаркуюсь?",
                "ask a question" to "Ничего, если я задам вопрос?",
            )
        ),
        Pattern(
            "Что ты…? (вопросы)", "What do you {}?", listOf(
                "do" to "Чем ты занимаешься?",
                "think" to "Что ты думаешь?",
                "mean" to "Что ты имеешь в виду?",
                "want for dinner" to "Что ты хочешь на ужин?",
                "do on weekends" to "Что ты делаешь по выходным?",
                "need" to "Что тебе нужно?",
                "usually eat for breakfast" to "Что ты обычно ешь на завтрак?",
                "like to do" to "Что ты любишь делать?",
                "recommend" to "Что посоветуете?",
                "call this in English" to "Как это называется по-английски?",
            )
        ),
        Pattern(
            "Я только что…", "I've just {}.", listOf(
                "arrived" to "Я только что приехал.",
                "woken up" to "Я только что проснулся.",
                "finished work" to "Я только что закончил работу.",
                "seen your message" to "Я только что увидел твоё сообщение.",
                "eaten" to "Я только что поел.",
                "called him" to "Я только что ему звонил.",
                "got home" to "Я только что пришёл домой.",
                "heard the news" to "Я только что услышал новости.",
                "bought a new phone" to "Я только что купил новый телефон.",
                "sent it" to "Я только что это отправил.",
            )
        ),
        Pattern(
            "Вчера я… (прошлое)", "Yesterday I {}.", listOf(
                "worked late" to "Вчера я поздно работал.",
                "stayed at home" to "Вчера я сидел дома.",
                "went to the shop" to "Вчера я ходил в магазин.",
                "saw my friend" to "Вчера я видел друга.",
                "bought a jacket" to "Вчера я купил куртку.",
                "watched a movie" to "Вчера я смотрел фильм.",
                "fixed my car" to "Вчера я чинил машину.",
                "cooked dinner" to "Вчера я готовил ужин.",
                "slept all day" to "Вчера я спал весь день.",
                "forgot my phone" to "Вчера я забыл телефон.",
                "called my mom" to "Вчера я звонил маме.",
                "didn't go out" to "Вчера я никуда не ходил.",
            )
        ),
        Pattern(
            "Ты когда-нибудь…?", "Have you ever {}?", listOf(
                "been to London" to "Ты когда-нибудь был в Лондоне?",
                "seen the sea" to "Ты когда-нибудь видел море?",
                "tried sushi" to "Ты когда-нибудь пробовал суши?",
                "driven a truck" to "Ты когда-нибудь водил грузовик?",
                "lost your phone" to "Ты когда-нибудь терял телефон?",
                "been on TV" to "Ты когда-нибудь был на телевидении?",
                "met a famous person" to "Ты когда-нибудь встречал знаменитость?",
                "flown on a plane" to "Ты когда-нибудь летал на самолёте?",
                "broken a bone" to "Ты когда-нибудь ломал кость?",
                "worked at night" to "Ты когда-нибудь работал ночью?",
            )
        ),
        Pattern(
            "Сколько времени…?", "How long {}?", listOf(
                "does it take" to "Сколько это займёт времени?",
                "will you stay" to "Как долго ты пробудешь?",
                "have you lived here" to "Сколько ты здесь живёшь?",
                "do I have to wait" to "Сколько мне ждать?",
                "is the flight" to "Сколько длится полёт?",
                "have you been learning English" to "Сколько ты учишь английский?",
                "does it take to get there" to "Сколько туда добираться?",
                "is the movie" to "Сколько длится фильм?",
                "have you known him" to "Как давно ты его знаешь?",
                "will it take to fix" to "Сколько времени займёт ремонт?",
            )
        ),
        Pattern(
            "Раньше я…", "I used to {}.", listOf(
                "smoke" to "Раньше я курил.",
                "live in Moscow" to "Раньше я жил в Москве.",
                "play football" to "Раньше я играл в футбол.",
                "work at night" to "Раньше я работал по ночам.",
                "have a dog" to "Раньше у меня была собака.",
                "get up late" to "Раньше я вставал поздно.",
                "drive a lot" to "Раньше я много ездил.",
                "hate vegetables" to "Раньше я ненавидел овощи.",
                "read a lot" to "Раньше я много читал.",
                "be shy" to "Раньше я был стеснительным.",
            )
        ),
        Pattern(
            "Я пытаюсь…", "I'm trying to {}.", listOf(
                "learn English" to "Я пытаюсь выучить английский.",
                "find my keys" to "Я пытаюсь найти ключи.",
                "sleep" to "Я пытаюсь уснуть.",
                "understand" to "Я пытаюсь понять.",
                "save money" to "Я пытаюсь копить деньги.",
                "call you" to "Я пытаюсь до тебя дозвониться.",
                "fix it" to "Я пытаюсь это починить.",
                "remember his name" to "Я пытаюсь вспомнить его имя.",
                "be on time" to "Я стараюсь не опаздывать.",
                "help" to "Я пытаюсь помочь.",
            )
        ),
        Pattern(
            "Я забыл…", "I forgot {}.", listOf(
                "my phone" to "Я забыл телефон.",
                "my keys at home" to "Я забыл ключи дома.",
                "to call you" to "Я забыл тебе позвонить.",
                "your name" to "Я забыл, как тебя зовут.",
                "to buy bread" to "Я забыл купить хлеб.",
                "my password" to "Я забыл пароль.",
                "about the meeting" to "Я забыл про встречу.",
                "to tell you" to "Я забыл тебе сказать.",
                "my wallet" to "Я забыл кошелёк.",
                "what I wanted to say" to "Я забыл, что хотел сказать.",
            )
        ),
    )

    val items: List<Item> = patterns.flatMapIndexed { n, p ->
        val group = "${n + 1}. ${p.title}"
        p.fills.map { (fill, ru) ->
            val en = p.en.replace("{}", fill)
            Item("c:$en", en, ru, group)
        }
    }

    val patternCount: Int get() = patterns.size
}
