package ru.my.englishdaily

/** Уровни сложности. Выбираются при первом запуске, меняются в настройках. */
enum class Level(val emoji: String, val title: String, val hint: String, val cefr: String) {
    BEGINNER("🌱", "Начинающий", "Знаю немного слов, но не могу связать их в фразы", "A1–A2"),
    INTERMEDIATE("🌿", "Средний", "Понимаю простое, но говорю медленно и с ошибками", "B1"),
    ADVANCED("🌳", "Продвинутый", "Хорошо знаю язык, но мало практики разговора", "B2+"),
}

/** Текущий уровень — нужен там, где нет доступа к настройкам (таймеры и т. п.). */
object CurrentLevel {
    var value: Level = Level.BEGINNER

    /** Во сколько раз короче таймеры: продвинутым — меньше времени на раздумья. */
    val timerFactor: Float
        get() = when (value) {
            Level.BEGINNER -> 1f
            Level.INTERMEDIATE -> 0.8f
            Level.ADVANCED -> 0.6f
        }
}

/** Фразовые глаголы и идиомы — для среднего и продвинутого уровня. */
object Idioms {
    val items: List<Item> = listOf(
        "I'll figure it out." to "Я разберусь.",
        "Let's get down to business." to "Давай перейдём к делу.",
        "I ran into an old friend." to "Я случайно встретил старого друга.",
        "Don't give up." to "Не сдавайся.",
        "We need to come up with a plan." to "Нам нужно придумать план.",
        "I'm looking forward to it." to "Жду с нетерпением.",
        "Can you look into it?" to "Можешь разобраться с этим?",
        "The meeting was called off." to "Встречу отменили.",
        "I can't put up with this noise." to "Я не могу терпеть этот шум.",
        "She turned down the offer." to "Она отказалась от предложения.",
        "Let's catch up soon." to "Давай скоро встретимся и поболтаем.",
        "I'm running out of time." to "У меня заканчивается время.",
        "He takes after his father." to "Он похож на отца.",
        "Hang in there." to "Держись.",
        "It's not my cup of tea." to "Это не моё.",
        "It's a piece of cake." to "Это проще простого.",
        "Break a leg!" to "Ни пуха ни пера!",
        "I'm on the fence." to "Я ещё не решил, сомневаюсь.",
        "Let's call it a day." to "На сегодня хватит.",
        "It costs an arm and a leg." to "Это стоит целое состояние.",
        "I'm feeling under the weather." to "Мне нездоровится.",
        "That rings a bell." to "Что-то знакомое.",
        "Let's play it by ear." to "Посмотрим по ситуации.",
        "Keep me posted." to "Держи меня в курсе.",
        "I'll get back to you." to "Я тебе отвечу позже.",
        "It's up in the air." to "Это ещё не решено.",
        "You hit the nail on the head." to "Ты попал в точку.",
        "I'm all ears." to "Я весь внимание.",
        "Better late than never." to "Лучше поздно, чем никогда.",
        "Let's split the bill." to "Давай заплатим пополам.",
        "No hard feelings." to "Без обид.",
        "It's a no-brainer." to "Тут и думать нечего.",
        "I'm swamped at work." to "Я завален работой.",
        "He's a pain in the neck." to "Он ужасно раздражает.",
        "Let's touch base next week." to "Давай созвонимся на следующей неделе.",
        "I'll sleep on it." to "Я подумаю до завтра.",
        "Don't beat around the bush." to "Не ходи вокруг да около.",
        "That's the last straw." to "Это последняя капля.",
        "I'm broke." to "Я на мели.",
        "Fair enough." to "Справедливо. Логично.",
        "I couldn't care less." to "Мне совершенно всё равно.",
        "It's not a big deal." to "Ничего страшного.",
        "I'm into photography." to "Я увлекаюсь фотографией.",
        "She showed up late." to "Она пришла поздно.",
        "Let's work it out." to "Давай всё уладим.",
        "I'll pick you up at eight." to "Заеду за тобой в восемь.",
        "Can you fill in this form?" to "Можете заполнить эту форму?",
        "We should get rid of it." to "Нам стоит от этого избавиться.",
        "I'm getting used to it." to "Я к этому привыкаю.",
        "It turned out to be true." to "Это оказалось правдой.",
    ).map { (en, ru) -> Item("i:$en", en, ru) }
}

/** Сложные диалоги: живая речь, длинные фразы. */
object AdvancedDialogues {
    private fun a(en: String, ru: String) = Line(0, en, ru)
    private fun b(en: String, ru: String) = Line(1, en, ru)

    val all: List<Dialogue> = listOf(
        Dialogue(
            "interview", "🧑‍💼", "Собеседование", listOf(
                a("Thanks for coming in. So, tell me a bit about yourself.", "Спасибо, что пришли. Расскажите немного о себе."),
                b("Sure. I've been working in logistics for about five years, mostly managing deliveries.", "Конечно. Я около пяти лет работаю в логистике, в основном управляю доставками."),
                a("What made you decide to apply for this position?", "Почему вы решили откликнуться на эту вакансию?"),
                b("I'm looking for a role where I can take on more responsibility and grow.", "Я ищу должность, где смогу взять больше ответственности и расти."),
                a("What would you say is your biggest weakness?", "Что бы вы назвали своей главной слабостью?"),
                b("I tend to take on too much myself, but I'm learning to delegate.", "Я склонен брать слишком много на себя, но учусь делегировать."),
                a("How do you usually handle stress?", "Как вы обычно справляетесь со стрессом?"),
                b("I try to break big problems down into smaller steps and deal with them one by one.", "Стараюсь разбить большую проблему на маленькие шаги и решать их по очереди."),
                a("Great. Do you have any questions for us?", "Отлично. У вас есть вопросы к нам?"),
                b("Yes, what does a typical day look like for this team?", "Да, как выглядит обычный день этой команды?"),
            )
        ),
        Dialogue(
            "complaint", "😤", "Жалоба в отеле", listOf(
                a("Good evening, how can I help you?", "Добрый вечер, чем могу помочь?"),
                b("Hi, I'm afraid there's a problem with my room. The air conditioning isn't working.", "Здравствуйте, боюсь, с моим номером проблема. Не работает кондиционер."),
                a("I'm so sorry about that. Have you tried the remote on the wall?", "Очень сожалею. Вы пробовали пульт на стене?"),
                b("I have, and it doesn't respond at all. It's really hot in there.", "Пробовал, он вообще не реагирует. Там очень жарко."),
                a("I'll send someone up to take a look right away.", "Я сейчас же пришлю кого-нибудь посмотреть."),
                b("To be honest, I'd rather change rooms if that's possible.", "Честно говоря, я бы предпочёл сменить номер, если можно."),
                a("Let me check what's available. I can offer you a room with a sea view.", "Сейчас проверю, что свободно. Могу предложить номер с видом на море."),
                b("That would be great, thank you. Is there any extra charge?", "Было бы здорово, спасибо. Это за дополнительную плату?"),
                a("No, of course not. It's the least we can do.", "Нет, конечно. Это меньшее, что мы можем сделать."),
                b("I really appreciate it.", "Я очень ценю это."),
            )
        ),
        Dialogue(
            "opinion", "🍿", "Обсуждаем фильм", listOf(
                a("So, what did you think of the movie?", "Ну, как тебе фильм?"),
                b("Honestly, I was a bit disappointed. The trailer looked way better.", "Честно, я немного разочарован. Трейлер выглядел намного лучше."),
                a("Really? I thought the acting was amazing, especially the main character.", "Правда? По-моему, актёры играли потрясающе, особенно главный герой."),
                b("The acting was fine, but the plot was so predictable. I guessed the ending halfway through.", "Игра нормальная, но сюжет такой предсказуемый. Я угадал концовку на середине."),
                a("Fair enough, but I don't think every movie needs a big twist.", "Справедливо, но, по-моему, не каждому фильму нужен неожиданный поворот."),
                b("True. I guess I just expected something different.", "Верно. Наверное, я просто ждал чего-то другого."),
                a("Would you watch the sequel?", "Ты бы посмотрел продолжение?"),
                b("Maybe, if it gets good reviews. What about you?", "Может быть, если будут хорошие отзывы. А ты?"),
                a("Definitely. I'm already hooked.", "Обязательно. Я уже подсел."),
            )
        ),
        Dialogue(
            "rent", "🏠", "Аренда квартиры", listOf(
                a("Hi, I'm calling about the flat you advertised. Is it still available?", "Здравствуйте, я звоню по поводу квартиры из объявления. Она ещё свободна?"),
                b("Yes, it is. Would you like to come and see it?", "Да. Хотите прийти посмотреть?"),
                a("I'd love to. But first, could you tell me if the bills are included in the rent?", "С удовольствием. Но сначала скажите, коммунальные платежи входят в аренду?"),
                b("Water and heating are included, but you'd pay for electricity separately.", "Вода и отопление входят, а электричество оплачивается отдельно."),
                a("I see. And how long is the minimum contract?", "Понятно. А какой минимальный срок договора?"),
                b("We usually ask for at least six months.", "Обычно мы просим минимум полгода."),
                a("Is there any flexibility on the price if I sign for a year?", "Можно ли немного уступить в цене, если я подпишу на год?"),
                b("I'd have to talk to the owner, but I think we could work something out.", "Мне нужно поговорить с владельцем, но думаю, договоримся."),
                a("Great. Can I come by tomorrow after work?", "Отлично. Можно я подъеду завтра после работы?"),
                b("Sure, how about six thirty?", "Конечно, как насчёт половины седьмого?"),
            )
        ),
    ).map { it.copy(level = 3) }

    val items: List<Item> = all.flatMap { d ->
        d.lines.mapIndexed { i, l -> Item("d:${d.id}:$i", l.en, l.ru, "${d.emoji} ${d.title}") }
    }
}

/** Темы для монолога: говоришь минуту без остановки. */
object Monologue {
    val topics: Map<Level, List<String>> = mapOf(
        Level.BEGINNER to listOf(
            "Tell me about your family.", "Describe your morning.", "What did you do yesterday?",
            "Talk about your favourite food.", "Describe your home.", "What are your plans for the weekend?",
            "Talk about your job or studies.", "Describe your best friend.",
        ),
        Level.INTERMEDIATE to listOf(
            "Describe a trip you remember well.", "What would you change in your city?",
            "Talk about a film or series you liked recently.", "Describe a typical working day.",
            "What are the pros and cons of living in a big city?", "Tell me about a difficult situation and how you solved it.",
            "What do you do to relax?", "Describe a person who inspires you.",
        ),
        Level.ADVANCED to listOf(
            "Is it better to work from home or in an office? Why?", "Should social media be banned for children?",
            "Describe a decision that changed your life.", "What makes a good leader?",
            "Will AI replace most jobs? Give your opinion.", "Is it possible to learn a language without living abroad?",
            "Tell a funny story that happened to you.", "What would you do if you didn't have to work?",
        ),
    )
}

/** Сложные истории для продвинутого уровня. */
object AdvancedStories {
    private fun s(en: String, ru: String) = Sentence(en, ru)
    private fun q(q: String, a: String, ru: String, vararg keys: String) = Question(q, a, ru, keys.toList())

    val all: List<Story> = listOf(
        Story(
            "startup", "🚀", "Стартап в гараже", 4,
            listOf(
                s("When Marco lost his job at the factory, he felt that his world had fallen apart.", "Когда Марко потерял работу на заводе, ему казалось, что его мир рухнул."),
                s("Instead of sending out hundreds of CVs, he decided to turn his hobby into a business.", "Вместо того чтобы рассылать сотни резюме, он решил превратить хобби в бизнес."),
                s("For years he had been repairing old bicycles in his garage just for fun.", "Годами он чинил старые велосипеды в гараже просто для удовольствия."),
                s("He started posting photos of the restored bikes online, and to his surprise, people wanted to buy them.", "Он начал выкладывать фото восстановленных велосипедов в сеть, и, к его удивлению, люди захотели их купить."),
                s("Within six months, he was getting more orders than he could handle.", "Через полгода у него было больше заказов, чем он успевал выполнять."),
                s("He had to hire two assistants and rent a proper workshop.", "Ему пришлось нанять двух помощников и арендовать настоящую мастерскую."),
                s("Of course, not everything went smoothly: one supplier let him down, and he nearly ran out of money.", "Конечно, не всё шло гладко: один поставщик подвёл, и деньги почти закончились."),
                s("But he refused to give up, and eventually the business took off.", "Но он отказался сдаваться, и в итоге бизнес пошёл в гору."),
                s("Looking back, Marco says losing his job was the best thing that ever happened to him.", "Оглядываясь назад, Марко говорит, что потеря работы — лучшее, что с ним случалось."),
            ),
            mapOf(
                "fallen" to "рухнул (fall apart — развалиться)", "apart" to "на части", "instead" to "вместо",
                "cvs" to "резюме", "hobby" to "хобби", "repairing" to "ремонтируя", "restored" to "восстановленные",
                "surprise" to "удивление", "within" to "в течение", "orders" to "заказы", "handle" to "справиться",
                "hire" to "нанять", "assistants" to "помощники", "proper" to "настоящий", "workshop" to "мастерская",
                "smoothly" to "гладко", "supplier" to "поставщик", "let" to "let down — подвести", "nearly" to "почти",
                "refused" to "отказался", "eventually" to "в конце концов", "took" to "take off — взлететь, пойти в гору",
                "looking" to "look back — оглядываться назад",
            ),
            listOf(
                q("Why did Marco start a business?", "Because he lost his job.", "Почему Марко начал бизнес?", "lost", "job"),
                q("What was his hobby?", "Repairing old bicycles.", "Какое у него было хобби?", "bicycles", "bikes", "bike", "bicycle"),
                q("How did people find out about his bikes?", "He posted photos online.", "Как люди узнали о его велосипедах?", "photos", "online", "internet"),
                q("What problems did he have?", "A supplier let him down and he nearly ran out of money.", "Какие у него были проблемы?", "supplier", "money"),
                q("Did he give up?", "No, he didn't.", "Он сдался?", "no"),
                q("What does he think about losing his job now?", "It was the best thing that happened to him.", "Что он теперь думает о потере работы?", "best"),
            )
        ),
        Story(
            "remote", "🏡", "Работа из дома", 4,
            listOf(
                s("When her company allowed everyone to work from home, Julia was thrilled.", "Когда компания разрешила всем работать из дома, Юлия была в восторге."),
                s("No more crowded buses, no more getting up at six — it sounded like a dream.", "Больше никаких переполненных автобусов и подъёмов в шесть — звучало как мечта."),
                s("However, after a couple of months, she noticed that something was wrong.", "Однако через пару месяцев она заметила, что что-то не так."),
                s("She was working longer hours than ever, and the line between work and rest had disappeared.", "Она работала дольше, чем когда-либо, а граница между работой и отдыхом исчезла."),
                s("She missed chatting with colleagues over coffee and felt increasingly isolated.", "Ей не хватало болтовни с коллегами за кофе, и она чувствовала себя всё более одинокой."),
                s("So she set some rules: she would start at nine, take a real lunch break and switch off her laptop at six.", "Тогда она установила правила: начинать в девять, делать настоящий обеденный перерыв и выключать ноутбук в шесть."),
                s("She also started going to a co-working space twice a week.", "Ещё она стала ходить в коворкинг два раза в неделю."),
                s("Now she believes that remote work can be great, but only if you're disciplined about it.", "Теперь она считает, что удалённая работа может быть отличной, но только при дисциплине."),
            ),
            mapOf(
                "allowed" to "разрешила", "thrilled" to "в восторге", "crowded" to "переполненные", "sounded" to "звучало",
                "however" to "однако", "noticed" to "заметила", "hours" to "часы", "ever" to "когда-либо", "line" to "граница, линия",
                "rest" to "отдых", "disappeared" to "исчезла", "missed" to "скучала", "chatting" to "болтовня",
                "colleagues" to "коллеги", "increasingly" to "всё более", "isolated" to "изолированной", "rules" to "правила",
                "break" to "перерыв", "switch" to "switch off — выключать", "laptop" to "ноутбук", "co" to "co-working — коворкинг",
                "space" to "пространство", "believes" to "считает", "remote" to "удалённый", "disciplined" to "дисциплинированный",
            ),
            listOf(
                q("How did Julia feel at first about working from home?", "She was thrilled.", "Как Юлия сначала отнеслась к работе из дома?", "thrilled", "happy", "excited", "glad"),
                q("What went wrong after a couple of months?", "She worked longer hours and felt isolated.", "Что пошло не так через пару месяцев?", "longer", "isolated", "lonely", "hours"),
                q("What did she miss?", "Chatting with colleagues.", "Чего ей не хватало?", "colleagues", "chatting"),
                q("What time does she switch off her laptop?", "At six.", "Во сколько она выключает ноутбук?", "six"),
                q("Where does she go twice a week?", "To a co-working space.", "Куда она ходит два раза в неделю?", "co", "working", "coworking"),
                q("What is her opinion about remote work now?", "It can be great if you're disciplined.", "Что она теперь думает об удалёнке?", "disciplined", "discipline", "great"),
            )
        ),
    )
}
