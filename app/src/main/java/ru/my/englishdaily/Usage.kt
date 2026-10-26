package ru.my.englishdaily

/** Пример употребления слова: фраза, перевод и где так говорят. */
data class Usage(val en: String, val ru: String, val tag: String)

private const val TALK = "💬 в разговоре"
private const val WORK = "💼 на работе"
private const val LAW = "⚖️ юридическое"
private const val OFFICIAL = "📄 официально"
private const val SHOP = "🛒 покупки"
private const val MED = "🩺 здоровье"
private const val ROAD = "🚗 дорога"
private const val MONEY = "🏦 финансы"
private const val TECH = "📱 техника"
private const val HOME = "🏠 дома"
private const val SPORT = "⚽ спорт"

private fun u(en: String, ru: String, tag: String = TALK) = Usage(en, ru, tag)

object Usages {

    /** Многозначные слова: одно слово — разные смыслы в разных ситуациях. */
    private val multi: Map<String, Pair<String, List<Usage>>> = mapOf(
        "case" to ("случай; дело; чехол" to listOf(
            u("In that case, let's meet tomorrow.", "В таком случае давай встретимся завтра."),
            u("I need a new case for my phone.", "Мне нужен новый чехол для телефона.", SHOP),
            u("The lawyer took on the case.", "Адвокат взялся за это дело.", LAW),
        )),
        "charge" to ("заряжать; цена; обвинение" to listOf(
            u("I need to charge my phone.", "Мне нужно зарядить телефон.", TECH),
            u("There's no extra charge for delivery.", "За доставку дополнительно не берут.", SHOP),
            u("He was charged with theft.", "Ему предъявили обвинение в краже.", LAW),
            u("Who's in charge here?", "Кто здесь главный?", WORK),
        )),
        "fine" to ("хорошо; штраф" to listOf(
            u("I'm fine, thanks.", "У меня всё хорошо, спасибо."),
            u("I got a fine for speeding.", "Мне выписали штраф за превышение скорости.", ROAD),
            u("The company was fined two million.", "Компанию оштрафовали на два миллиона.", LAW),
        )),
        "sentence" to ("предложение; приговор" to listOf(
            u("Write a sentence with this word.", "Напиши предложение с этим словом."),
            u("He got a five-year sentence.", "Он получил пять лет (приговор).", LAW),
        )),
        "bill" to ("счёт; законопроект" to listOf(
            u("Can we have the bill, please?", "Можно счёт, пожалуйста?", SHOP),
            u("I forgot to pay the electricity bill.", "Я забыл оплатить счёт за свет.", HOME),
            u("Parliament passed the new bill.", "Парламент принял новый законопроект.", OFFICIAL),
        )),
        "trial" to ("пробный период; суд" to listOf(
            u("The app has a free seven-day trial.", "У приложения бесплатный пробный период на семь дней.", TECH),
            u("The trial starts next week.", "Судебный процесс начнётся на следующей неделе.", LAW),
        )),
        "claim" to ("утверждать; заявка; претензия" to listOf(
            u("He claims he's never been late.", "Он утверждает, что никогда не опаздывал."),
            u("Please collect your luggage at baggage claim.", "Заберите багаж в зоне выдачи.", ROAD),
            u("I made an insurance claim after the accident.", "После аварии я подал заявление на страховку.", MONEY),
            u("The court rejected the claim.", "Суд отклонил иск.", LAW),
        )),
        "party" to ("вечеринка; сторона договора" to listOf(
            u("Are you coming to the party on Friday?", "Придёшь на вечеринку в пятницу?"),
            u("Both parties signed the agreement.", "Обе стороны подписали соглашение.", LAW),
        )),
        "appeal" to ("привлекать; обжаловать" to listOf(
            u("The idea doesn't really appeal to me.", "Эта идея меня не особо привлекает."),
            u("They're going to appeal against the decision.", "Они собираются обжаловать решение.", LAW),
        )),
        "deal" to ("договорились; сделка" to listOf(
            u("Deal! See you at six.", "Договорились! До встречи в шесть."),
            u("It's no big deal.", "Ничего страшного."),
            u("We closed the deal yesterday.", "Мы вчера заключили сделку.", WORK),
        )),
        "issue" to ("проблема; выпуск; выдавать" to listOf(
            u("What's the issue?", "В чём проблема?"),
            u("The new issue of the magazine is out.", "Вышел новый номер журнала."),
            u("Passports are issued within a month.", "Паспорта выдаются в течение месяца.", OFFICIAL),
        )),
        "notice" to ("заметить; уведомление" to listOf(
            u("Did you notice her new haircut?", "Ты заметил её новую стрижку?"),
            u("I handed in my notice today.", "Я сегодня подал заявление об уходе.", WORK),
            u("The landlord gave us a month's notice.", "Хозяин предупредил нас за месяц.", LAW),
        )),
        "account" to ("аккаунт; счёт; учитывать" to listOf(
            u("I can't log in to my account.", "Не могу войти в свой аккаунт.", TECH),
            u("I'll transfer the money to your account.", "Я переведу деньги тебе на счёт.", MONEY),
            u("Take into account that it's a holiday.", "Учти, что это праздник."),
        )),
        "interest" to ("интерес; проценты" to listOf(
            u("I have no interest in football.", "Футбол меня не интересует."),
            u("The interest rate on the loan is ten percent.", "Процентная ставка по кредиту — десять процентов.", MONEY),
        )),
        "term" to ("семестр; срок; условия" to listOf(
            u("The new term starts in September.", "Новый семестр начинается в сентябре."),
            u("Read the terms and conditions before you sign.", "Прочитай условия перед тем, как подписать.", LAW),
            u("We're on good terms with our neighbours.", "Мы в хороших отношениях с соседями."),
        )),
        "point" to ("смысл; довод; балл" to listOf(
            u("What's the point of waiting?", "Какой смысл ждать?"),
            u("That's a good point.", "Хорошее замечание."),
            u("Our team won by two points.", "Наша команда выиграла с перевесом в два очка.", SPORT),
        )),
        "figure" to ("фигура; цифра; понять" to listOf(
            u("I can't figure out how this works.", "Не могу понять, как это работает."),
            u("The sales figures are better this month.", "Цифры продаж в этом месяце лучше.", WORK),
        )),
        "cover" to ("покрывать; прикрыть; обложка" to listOf(
            u("Can you cover for me tomorrow?", "Можешь подменить меня завтра?", WORK),
            u("Does the insurance cover this?", "Страховка это покрывает?", MONEY),
            u("Don't judge a book by its cover.", "Не суди книгу по обложке."),
        )),
        "run" to ("бежать; управлять; работать" to listOf(
            u("I run every morning.", "Я бегаю каждое утро.", SPORT),
            u("She runs a small cafe.", "Она управляет небольшим кафе.", WORK),
            u("The app runs slowly on my phone.", "Приложение медленно работает на моём телефоне.", TECH),
            u("We've run out of milk.", "У нас закончилось молоко.", HOME),
        )),
        "address" to ("адрес; обращаться к, решать" to listOf(
            u("What's your address?", "Какой у тебя адрес?"),
            u("We need to address this problem.", "Нам нужно заняться этой проблемой.", WORK),
        )),
        "draw" to ("рисовать; ничья; тянуть" to listOf(
            u("My son loves to draw.", "Мой сын любит рисовать."),
            u("The match ended in a draw.", "Матч закончился вничью.", SPORT),
            u("I'll draw up the contract tomorrow.", "Завтра я составлю договор.", LAW),
        )),
        "miss" to ("скучать; пропустить; промахнуться" to listOf(
            u("I miss you.", "Я по тебе скучаю."),
            u("Hurry up, or we'll miss the train.", "Поторопись, а то опоздаем на поезд.", ROAD),
            u("You missed a call from your mum.", "У тебя пропущенный от мамы.", TECH),
        )),
        "save" to ("спасти; сэкономить; сохранить" to listOf(
            u("You saved my life!", "Ты спас мне жизнь!"),
            u("I'm saving for a new car.", "Я коплю на новую машину.", MONEY),
            u("Don't forget to save the file.", "Не забудь сохранить файл.", TECH),
        )),
        "sound" to ("звучать; звук; здравый" to listOf(
            u("That sounds great!", "Звучит отлично!"),
            u("Turn the sound down, please.", "Сделай звук потише, пожалуйста.", TECH),
            u("That's sound advice.", "Это здравый совет.", OFFICIAL),
        )),
        "pick up" to ("поднять; забрать; подхватить" to listOf(
            u("Can you pick me up at eight?", "Можешь заехать за мной в восемь?", ROAD),
            u("I picked up a few words on holiday.", "Я нахватался немного слов в отпуске."),
            u("Pick up the phone!", "Возьми трубку!", TECH),
        )),
        "get" to ("получить; понять; добраться" to listOf(
            u("I got your message.", "Я получил твоё сообщение."),
            u("Sorry, I don't get it.", "Извини, не понимаю."),
            u("How do I get to the station?", "Как добраться до вокзала?", ROAD),
            u("It's getting cold.", "Становится холодно."),
        )),
    )

    /** Примеры для слов B1. */
    private val b1: Map<String, List<Usage>> = mapOf(
        "achieve" to listOf(u("She achieved her goal of running a marathon.", "Она добилась своей цели — пробежала марафон."), u("We achieved great results this quarter.", "В этом квартале мы добились отличных результатов.", WORK)),
        "afford" to listOf(u("I can't afford a new car right now.", "Я сейчас не могу позволить себе новую машину.", MONEY)),
        "apologize" to listOf(u("You should apologize to her.", "Тебе стоит перед ней извиниться."), u("We apologize for the delay.", "Приносим извинения за задержку.", OFFICIAL)),
        "argue" to listOf(u("They argue about money all the time.", "Они постоянно спорят из-за денег."), u("The lawyer argued that he was innocent.", "Адвокат утверждал, что он невиновен.", LAW)),
        "arrange" to listOf(u("Let's arrange to meet next week.", "Давай договоримся встретиться на следующей неделе."), u("I'll arrange a meeting with the client.", "Я организую встречу с клиентом.", WORK)),
        "avoid" to listOf(u("I try to avoid sugar.", "Я стараюсь избегать сахара.", MED), u("Leave early to avoid the traffic.", "Выезжай пораньше, чтобы не попасть в пробки.", ROAD)),
        "behave" to listOf(u("The kids behaved really well today.", "Дети сегодня вели себя очень хорошо.")),
        "borrow" to listOf(u("Can I borrow your charger?", "Можно взять твою зарядку?"), u("We borrowed money from the bank.", "Мы взяли кредит в банке.", MONEY)),
        "lend" to listOf(u("Could you lend me some money till Friday?", "Можешь одолжить мне денег до пятницы?", MONEY)),
        "complain" to listOf(u("He always complains about the weather.", "Он вечно жалуется на погоду."), u("I complained to the manager.", "Я пожаловался менеджеру.", SHOP)),
        "consider" to listOf(u("I'm considering changing jobs.", "Я подумываю сменить работу."), u("The court will consider the case in May.", "Суд рассмотрит дело в мае.", LAW)),
        "deal with" to listOf(u("I'll deal with it tomorrow.", "Я разберусь с этим завтра."), u("She deals with difficult customers every day.", "Она каждый день имеет дело с трудными клиентами.", WORK)),
        "deliver" to listOf(u("They deliver pizza until midnight.", "Пиццу доставляют до полуночи.", SHOP), u("He delivered a great speech.", "Он выступил с отличной речью.", WORK)),
        "depend on" to listOf(u("It depends on the weather.", "Это зависит от погоды.")),
        "deserve" to listOf(u("You deserve a break.", "Ты заслужил отдых.")),
        "disappoint" to listOf(u("The film really disappointed me.", "Фильм меня очень разочаровал.")),
        "encourage" to listOf(u("My parents always encouraged me.", "Родители всегда меня поддерживали.")),
        "expect" to listOf(u("I didn't expect to see you here!", "Не ожидал тебя здесь увидеть!"), u("We expect the parcel on Monday.", "Мы ожидаем посылку в понедельник.", SHOP)),
        "improve" to listOf(u("My English is improving.", "Мой английский становится лучше.")),
        "include" to listOf(u("Is breakfast included?", "Завтрак включён?", SHOP)),
        "increase" to listOf(u("Prices have increased again.", "Цены опять выросли.", MONEY)),
        "manage" to listOf(u("Don't worry, I'll manage.", "Не волнуйся, я справлюсь."), u("She manages a team of ten people.", "Она руководит командой из десяти человек.", WORK)),
        "mention" to listOf(u("He didn't mention it.", "Он об этом не упоминал.")),
        "notice" to listOf(u("I didn't notice you come in.", "Я не заметил, как ты вошёл.")),
        "offer" to listOf(u("Can I offer you a coffee?", "Могу предложить тебе кофе?"), u("They offered me the job!", "Мне предложили эту работу!", WORK)),
        "prefer" to listOf(u("I prefer tea to coffee.", "Я предпочитаю чай кофе.")),
        "pretend" to listOf(u("He pretended he didn't hear me.", "Он сделал вид, что не слышит меня.")),
        "prevent" to listOf(u("Wash your hands to prevent infection.", "Мойте руки, чтобы предотвратить инфекцию.", MED)),
        "protect" to listOf(u("This case protects your phone.", "Этот чехол защищает телефон.", TECH)),
        "provide" to listOf(u("The hotel provides free Wi-Fi.", "Отель предоставляет бесплатный вайфай."), u("The landlord shall provide hot water.", "Арендодатель обязан обеспечить горячую воду.", LAW)),
        "realize" to listOf(u("I didn't realize it was so late.", "Я не заметил, что уже так поздно.")),
        "recommend" to listOf(u("Can you recommend a good restaurant?", "Можешь посоветовать хороший ресторан?")),
        "reduce" to listOf(u("I'm trying to reduce my screen time.", "Я стараюсь меньше сидеть в телефоне."), u("We need to reduce costs.", "Нам нужно сократить расходы.", WORK)),
        "refuse" to listOf(u("She refused to answer.", "Она отказалась отвечать.")),
        "remind" to listOf(u("Remind me to call mum.", "Напомни мне позвонить маме."), u("You remind me of my brother.", "Ты напоминаешь мне моего брата.")),
        "replace" to listOf(u("I need to replace the battery.", "Мне нужно заменить аккумулятор.", TECH)),
        "respect" to listOf(u("I respect your decision.", "Я уважаю твоё решение.")),
        "share" to listOf(u("Let's share a pizza.", "Давай возьмём одну пиццу на двоих."), u("Can you share your screen?", "Можешь показать свой экран?", WORK)),
        "succeed" to listOf(u("If you keep trying, you'll succeed.", "Если продолжишь стараться, у тебя получится.")),
        "suggest" to listOf(u("I suggest we take a taxi.", "Предлагаю взять такси.")),
        "suppose" to listOf(u("I suppose you're right.", "Полагаю, ты прав."), u("You're supposed to be at work!", "Ты вообще-то должен быть на работе!")),
        "survive" to listOf(u("I survived my first week at the new job.", "Я пережил первую неделю на новой работе.")),
        "waste" to listOf(u("Don't waste your time.", "Не трать время зря.")),
        "persuade" to listOf(u("She persuaded me to come.", "Она уговорила меня прийти.")),
        "admit" to listOf(u("I admit I was wrong.", "Признаю, я был неправ."), u("He was admitted to hospital last night.", "Вчера ночью его положили в больницу.", MED)),
        "advantage" to listOf(u("The big advantage is the location.", "Большое преимущество — расположение."), u("Don't let them take advantage of you.", "Не позволяй им тобой пользоваться.")),
        "disadvantage" to listOf(u("The only disadvantage is the price.", "Единственный минус — цена.")),
        "advice" to listOf(u("Can I give you some advice?", "Можно дам тебе совет?")),
        "attention" to listOf(u("Pay attention to the road!", "Следи за дорогой!", ROAD), u("Attention, please! The train is delayed.", "Внимание! Поезд задерживается.", OFFICIAL)),
        "choice" to listOf(u("I had no choice.", "У меня не было выбора.")),
        "decision" to listOf(u("It's a tough decision.", "Это трудное решение.")),
        "experience" to listOf(u("Do you have any experience in sales?", "У вас есть опыт в продажах?", WORK), u("It was an amazing experience.", "Это было потрясающе (впечатление).")),
        "habit" to listOf(u("Smoking is a bad habit.", "Курение — плохая привычка.")),
        "opportunity" to listOf(u("It's a great opportunity to practise English.", "Это отличная возможность попрактиковать английский.")),
        "purpose" to listOf(u("What's the purpose of your visit?", "Какова цель вашего визита?", OFFICIAL), u("Sorry, I didn't do it on purpose.", "Извини, я не специально.")),
        "reason" to listOf(u("There's no reason to worry.", "Нет причин волноваться.")),
        "result" to listOf(u("I got my test results today.", "Сегодня я получил результаты анализов.", MED)),
        "salary" to listOf(u("The salary is good, but the hours are long.", "Зарплата хорошая, но рабочий день длинный.", WORK)),
        "skill" to listOf(u("Communication skills are important in this job.", "В этой работе важны навыки общения.", WORK)),
        "success" to listOf(u("The party was a big success.", "Вечеринка удалась на славу.")),
        "trouble" to listOf(u("I had trouble finding your house.", "Я с трудом нашёл твой дом."), u("Sorry to trouble you.", "Извините за беспокойство.", OFFICIAL)),
        "environment" to listOf(u("I work in a friendly environment.", "У меня на работе дружеская атмосфера.", WORK), u("We need to protect the environment.", "Нам нужно беречь природу.")),
        "government" to listOf(u("The government raised taxes again.", "Правительство снова подняло налоги.", OFFICIAL)),
        "knowledge" to listOf(u("To my knowledge, he's still in Moscow.", "Насколько я знаю, он всё ещё в Москве.")),
        "behaviour" to listOf(u("His behaviour at the party was awful.", "Его поведение на вечеринке было ужасным.")),
        "goal" to listOf(u("My goal is to speak English fluently.", "Моя цель — свободно говорить по-английски."), u("What a goal!", "Какой гол!", SPORT)),
        "deadline" to listOf(u("The deadline is Friday.", "Крайний срок — пятница.", WORK)),
        "crowd" to listOf(u("There was a huge crowd at the concert.", "На концерте была огромная толпа.")),
        "neighbour" to listOf(u("Our neighbours are really friendly.", "Наши соседи очень дружелюбные.", HOME)),
        "relationship" to listOf(u("They've been in a relationship for two years.", "Они встречаются уже два года."), u("We have a good relationship with our clients.", "У нас хорошие отношения с клиентами.", WORK)),
        "competition" to listOf(u("She won a photo competition.", "Она выиграла фотоконкурс."), u("There's a lot of competition in this market.", "На этом рынке большая конкуренция.", WORK)),
        "rent" to listOf(u("The rent is due on the first.", "Аренду нужно платить первого числа.", HOME), u("We rented a car for the weekend.", "Мы взяли машину напрокат на выходные.", ROAD)),
        "appointment" to listOf(u("I have a dentist appointment at three.", "У меня в три запись к стоматологу.", MED), u("Can I make an appointment for Monday?", "Можно записаться на понедельник?", OFFICIAL)),
        "complaint" to listOf(u("I'd like to make a complaint.", "Я хотел бы подать жалобу.", SHOP), u("The neighbours filed a complaint with the police.", "Соседи написали заявление в полицию.", LAW)),
        "argument" to listOf(u("We had an argument about the holiday.", "Мы поссорились из-за отпуска."), u("That's a strong argument.", "Это сильный довод.", WORK)),
        "available" to listOf(u("Are you available on Tuesday?", "Ты свободен во вторник?", WORK), u("This size isn't available.", "Этого размера нет в наличии.", SHOP)),
        "aware" to listOf(u("Are you aware of the risks?", "Ты знаешь о рисках?")),
        "brave" to listOf(u("That was really brave of you.", "Это было очень смело с твоей стороны.")),
        "careful" to listOf(u("Be careful, the floor is wet.", "Осторожно, пол мокрый.")),
        "certain" to listOf(u("Are you certain about that?", "Ты в этом уверен?")),
        "confident" to listOf(u("She's very confident at work.", "Она очень уверена в себе на работе.", WORK)),
        "curious" to listOf(u("I'm curious — how did you do it?", "Мне любопытно — как ты это сделал?")),
        "embarrassed" to listOf(u("I was so embarrassed when I fell.", "Мне было так неловко, когда я упал.")),
        "essential" to listOf(u("A good sleep is essential.", "Хороший сон необходим.", MED)),
        "familiar" to listOf(u("Your face looks familiar.", "Твоё лицо кажется знакомым.")),
        "generous" to listOf(u("That's very generous of you.", "Это очень щедро с твоей стороны.")),
        "honest" to listOf(u("To be honest, I don't like it.", "Честно говоря, мне не нравится.")),
        "independent" to listOf(u("She's very independent.", "Она очень самостоятельная.")),
        "jealous" to listOf(u("Are you jealous?", "Ты ревнуешь?"), u("I'm so jealous of your holiday!", "Я так завидую твоему отпуску!")),
        "lazy" to listOf(u("I feel lazy today.", "Мне сегодня лень.")),
        "likely" to listOf(u("It's likely to rain later.", "Скорее всего, позже будет дождь.")),
        "obvious" to listOf(u("It's obvious that he likes her.", "Очевидно, что она ему нравится.")),
        "patient" to listOf(u("Be patient, it takes time.", "Наберись терпения, это требует времени."), u("The doctor has a lot of patients.", "У врача много пациентов.", MED)),
        "proud" to listOf(u("I'm so proud of you!", "Я так тобой горжусь!")),
        "reliable" to listOf(u("My car is old but reliable.", "Моя машина старая, но надёжная.", ROAD)),
        "rude" to listOf(u("Don't be rude.", "Не груби.")),
        "similar" to listOf(u("Our jobs are quite similar.", "Наши работы довольно похожи.")),
        "strict" to listOf(u("My parents were very strict.", "Мои родители были очень строгими.")),
        "upset" to listOf(u("She's upset because she failed the test.", "Она расстроена, потому что завалила тест."), u("I've got an upset stomach.", "У меня расстройство желудка.", MED)),
        "worried" to listOf(u("I'm worried about you.", "Я за тебя переживаю.")),
        "convenient" to listOf(u("Is ten o'clock convenient for you?", "Вам удобно в десять?", WORK)),
        "anxious" to listOf(u("I always get anxious before flights.", "Я всегда нервничаю перед полётами.")),
        "annoyed" to listOf(u("I'm a bit annoyed with him.", "Я немного на него злюсь.")),
        "actually" to listOf(u("Actually, I've changed my mind.", "Вообще-то, я передумал.")),
        "anyway" to listOf(u("Anyway, let's talk about something else.", "В общем, давай о чём-нибудь другом.")),
        "eventually" to listOf(u("We eventually found the hotel.", "В конце концов мы нашли отель.")),
        "hardly" to listOf(u("I can hardly hear you.", "Я тебя почти не слышу.")),
        "instead" to listOf(u("Let's stay in instead.", "Давай лучше останемся дома.")),
        "otherwise" to listOf(u("Hurry up, otherwise we'll be late.", "Поторопись, иначе опоздаем.")),
        "recently" to listOf(u("Have you seen any good films recently?", "Смотрел недавно что-нибудь хорошее?")),
        "suddenly" to listOf(u("Suddenly, the lights went out.", "Вдруг погас свет.")),
        "therefore" to listOf(u("The flight was cancelled; therefore, we stayed another night.", "Рейс отменили, поэтому мы остались ещё на ночь.", OFFICIAL)),
        "unfortunately" to listOf(u("Unfortunately, I can't come.", "К сожалению, я не смогу прийти.")),
        "although" to listOf(u("Although it was raining, we went for a walk.", "Хотя шёл дождь, мы пошли гулять.")),
        "whether" to listOf(u("I don't know whether he's coming.", "Не знаю, придёт ли он.")),
        "despite" to listOf(u("Despite the traffic, we arrived on time.", "Несмотря на пробки, мы приехали вовремя.")),
        "however" to listOf(u("The flat is small. However, it's very cheap.", "Квартира маленькая. Однако она очень дешёвая.")),
        "apparently" to listOf(u("Apparently, they're getting married.", "Говорят, они собираются пожениться.")),
    )

    /** Примеры для живых слов B2. */
    private val b2: Map<String, List<Usage>> = mapOf(
        "chores" to listOf(u("I spent the whole Saturday doing chores.", "Я всю субботу занимался домашними делами.", HOME)),
        "run errands" to listOf(u("I need to run a few errands before work.", "Мне нужно сделать пару дел перед работой.")),
        "leftovers" to listOf(u("We had leftovers for lunch.", "На обед мы доели вчерашнее.", HOME)),
        "do the laundry" to listOf(u("Whose turn is it to do the laundry?", "Чья очередь стирать?", HOME)),
        "iron" to listOf(u("I need to iron my shirt.", "Мне нужно погладить рубашку.", HOME)),
        "vacuum" to listOf(u("Can you vacuum the living room?", "Можешь пропылесосить в гостиной?", HOME)),
        "tidy up" to listOf(u("Tidy up your room, please!", "Прибери, пожалуйста, в комнате!", HOME)),
        "clutter" to listOf(u("I need to get rid of all this clutter.", "Мне надо избавиться от всего этого хлама.", HOME)),
        "landlord" to listOf(u("I'll call the landlord about the heating.", "Я позвоню хозяину насчёт отопления.", HOME)),
        "utilities" to listOf(u("Is the rent including utilities?", "Аренда с коммуналкой?", HOME)),
        "leak" to listOf(u("There's a leak under the sink.", "Под раковиной течёт.", HOME), u("The information was leaked to the press.", "Информация утекла в прессу.", OFFICIAL)),
        "plumber" to listOf(u("We need to call a plumber.", "Надо вызвать сантехника.", HOME)),
        "socket" to listOf(u("Is there a socket near the bed?", "Возле кровати есть розетка?", HOME)),
        "light bulb" to listOf(u("The light bulb in the kitchen has gone.", "На кухне перегорела лампочка.", HOME)),
        "drawer" to listOf(u("The keys are in the top drawer.", "Ключи в верхнем ящике.", HOME)),
        "mortgage" to listOf(u("We took out a mortgage to buy the flat.", "Мы взяли ипотеку, чтобы купить квартиру.", MONEY)),
        "neighbourhood" to listOf(u("It's a quiet neighbourhood.", "Это тихий район.")),
        "bin" to listOf(u("Throw it in the bin.", "Выбрось в мусорку.", HOME)),
        "take out the rubbish" to listOf(u("Can you take out the rubbish on your way?", "Вынесешь мусор по дороге?", HOME)),
        "spill" to listOf(u("Oops, I spilled coffee on my shirt.", "Ой, я пролил кофе на рубашку.")),
        "stain" to listOf(u("This stain won't come out.", "Это пятно не отстирывается.", HOME)),
        "receipt" to listOf(u("Do you need the receipt?", "Вам нужен чек?", SHOP), u("Keep the receipt for your tax return.", "Сохраните квитанцию для налоговой.", OFFICIAL)),
        "refund" to listOf(u("Can I get a refund?", "Можно вернуть деньги?", SHOP)),
        "bargain" to listOf(u("This jacket was a real bargain!", "Эта куртка была очень выгодной покупкой!", SHOP)),
        "out of stock" to listOf(u("Sorry, that model is out of stock.", "Извините, этой модели нет в наличии.", SHOP)),
        "queue" to listOf(u("The queue was really long.", "Очередь была очень длинной.", SHOP)),
        "tip" to listOf(u("Did you leave a tip?", "Ты оставил чаевые?", SHOP), u("Here's a tip: book early.", "Совет: бронируй заранее.")),
        "change" to listOf(u("Keep the change.", "Сдачи не надо.", SHOP), u("I need to change trains in Moscow.", "Мне нужно сделать пересадку в Москве.", ROAD)),
        "in instalments" to listOf(u("You can pay in instalments.", "Можно платить в рассрочку.", SHOP)),
        "savings" to listOf(u("We spent all our savings on the trip.", "Мы потратили все сбережения на поездку.", MONEY)),
        "debt" to listOf(u("He's in debt again.", "Он опять в долгах.", MONEY)),
        "owe" to listOf(u("How much do I owe you?", "Сколько я тебе должен?", MONEY), u("I owe you one!", "С меня причитается!")),
        "subscription" to listOf(u("I cancelled my subscription.", "Я отменил подписку.", TECH)),
        "rip-off" to listOf(u("Twenty dollars for a coffee? What a rip-off!", "Двадцать долларов за кофе? Вот обдираловка!", SHOP)),
        "splurge" to listOf(u("I splurged on a new phone.", "Я шиканул и купил новый телефон.", SHOP)),
        "cash back" to listOf(u("This card gives five percent cash back.", "По этой карте кэшбэк пять процентов.", MONEY)),
        "price tag" to listOf(u("I forgot to take off the price tag!", "Я забыл снять ценник!", SHOP)),
        "be broke" to listOf(u("I can't go out tonight, I'm broke.", "Не могу пойти сегодня, я на мели.")),
        "split the bill" to listOf(u("Shall we split the bill?", "Разделим счёт?", SHOP)),
        "commute" to listOf(u("My commute takes an hour each way.", "Дорога на работу занимает час в одну сторону.", WORK)),
        "overtime" to listOf(u("I worked overtime all week.", "Я всю неделю работал сверхурочно.", WORK)),
        "day off" to listOf(u("Tomorrow's my day off.", "Завтра у меня выходной.", WORK)),
        "sick leave" to listOf(u("She's on sick leave this week.", "Она на этой неделе на больничном.", WORK)),
        "shift" to listOf(u("I'm on the night shift.", "Я в ночную смену.", WORK), u("Can you shift a bit to the left?", "Можешь подвинуться чуть влево?")),
        "promotion" to listOf(u("He got a promotion last month.", "В прошлом месяце его повысили.", WORK), u("There's a promotion on coffee this week.", "На этой неделе акция на кофе.", SHOP)),
        "quit" to listOf(u("I quit my job.", "Я уволился."), u("I quit smoking two years ago.", "Я бросил курить два года назад.", MED)),
        "get fired" to listOf(u("He got fired for being late.", "Его уволили за опоздания.", WORK)),
        "hire" to listOf(u("We're hiring new staff.", "Мы набираем новых сотрудников.", WORK), u("Let's hire a car.", "Давай возьмём машину напрокат.", ROAD)),
        "paperwork" to listOf(u("There's so much paperwork in this job.", "На этой работе столько бумажек.", WORK)),
        "workload" to listOf(u("My workload is crazy this month.", "У меня в этом месяце безумная нагрузка.", WORK)),
        "reschedule" to listOf(u("Can we reschedule the meeting?", "Можем перенести встречу?", WORK)),
        "take over" to listOf(u("Can you take over while I'm away?", "Можешь подменить меня, пока меня нет?", WORK)),
        "team lead" to listOf(u("Ask the team lead about it.", "Спроси об этом у тимлида.", WORK)),
        "pay rise" to listOf(u("I'm going to ask for a pay rise.", "Я собираюсь попросить прибавку.", WORK)),
        "work from home" to listOf(u("I work from home on Fridays.", "По пятницам я работаю из дома.", WORK)),
        "burnout" to listOf(u("I think I'm close to burnout.", "Кажется, я на грани выгорания.", WORK)),
        "catch up on emails" to listOf(u("I spent the morning catching up on emails.", "Всё утро разбирал почту.", WORK)),
        "traffic jam" to listOf(u("Sorry, I'm stuck in a traffic jam.", "Извини, я стою в пробке.", ROAD)),
        "rush hour" to listOf(u("Avoid the city centre during rush hour.", "Избегай центра в час пик.", ROAD)),
        "flat tyre" to listOf(u("I've got a flat tyre.", "У меня спустило колесо.", ROAD)),
        "fuel" to listOf(u("We're running low on fuel.", "У нас заканчивается бензин.", ROAD)),
        "speed bump" to listOf(u("Slow down, there's a speed bump.", "Притормози, там лежачий полицейский.", ROAD)),
        "fine" to listOf(u("I got a fine for parking.", "Мне выписали штраф за парковку.", ROAD), u("Don't worry, I'm fine.", "Не волнуйся, я в порядке.")),
        "parking ticket" to listOf(u("I found a parking ticket on my windscreen.", "Я нашёл штраф за парковку на лобовом стекле.", ROAD)),
        "pedestrian" to listOf(u("Watch out for pedestrians.", "Осторожно, пешеходы.", ROAD)),
        "roundabout" to listOf(u("Take the second exit at the roundabout.", "На круговом движении — второй съезд.", ROAD)),
        "overtake" to listOf(u("Don't overtake on a bend.", "Не обгоняй на повороте.", ROAD)),
        "brake" to listOf(u("I had to brake suddenly.", "Мне пришлось резко затормозить.", ROAD)),
        "steering wheel" to listOf(u("Keep both hands on the steering wheel.", "Держи обе руки на руле.", ROAD)),
        "windscreen" to listOf(u("A stone cracked my windscreen.", "Камень разбил мне лобовое стекло.", ROAD)),
        "tow truck" to listOf(u("We had to call a tow truck.", "Пришлось вызвать эвакуатор.", ROAD)),
        "detour" to listOf(u("There's a detour because of roadworks.", "Из-за ремонта дороги объезд.", ROAD)),
        "pull over" to listOf(u("Pull over here, please.", "Остановитесь здесь, пожалуйста.", ROAD)),
        "give someone a lift" to listOf(u("Can you give me a lift to the station?", "Подвезёшь меня до вокзала?", ROAD)),
        "runny nose" to listOf(u("I've got a runny nose.", "У меня насморк.", MED)),
        "cough" to listOf(u("I can't get rid of this cough.", "Никак не избавлюсь от кашля.", MED)),
        "rash" to listOf(u("I've got a rash on my arm.", "У меня сыпь на руке.", MED)),
        "sprain" to listOf(u("I sprained my ankle.", "Я подвернул лодыжку.", MED)),
        "bruise" to listOf(u("Where did you get that bruise?", "Откуда у тебя этот синяк?", MED)),
        "prescription" to listOf(u("You need a prescription for this medicine.", "На это лекарство нужен рецепт.", MED)),
        "painkiller" to listOf(u("Take a painkiller and lie down.", "Выпей обезболивающее и приляг.", MED)),
        "check-up" to listOf(u("I go for a check-up once a year.", "Я раз в год прохожу обследование.", MED)),
        "dizzy" to listOf(u("I feel a bit dizzy.", "У меня немного кружится голова.", MED)),
        "exhausted" to listOf(u("I'm absolutely exhausted.", "Я совершенно вымотан.")),
        "get over" to listOf(u("It took me a week to get over the flu.", "Я неделю отходил от гриппа.", MED), u("She still hasn't got over the break-up.", "Она всё ещё не пришла в себя после расставания.")),
        "throw up" to listOf(u("I think I'm going to throw up.", "Кажется, меня сейчас стошнит.", MED)),
        "stuffy nose" to listOf(u("I can't sleep with this stuffy nose.", "Не могу уснуть с заложенным носом.", MED)),
        "pull a muscle" to listOf(u("I pulled a muscle at the gym.", "Я потянул мышцу в зале.", SPORT)),
        "grumpy" to listOf(u("He's always grumpy in the morning.", "Он всегда ворчит по утрам.")),
        "stubborn" to listOf(u("Don't be so stubborn!", "Не будь таким упрямым!")),
        "moody" to listOf(u("Teenagers can be really moody.", "У подростков бывают резкие перепады настроения.")),
        "clumsy" to listOf(u("Sorry, I'm so clumsy.", "Извини, я такой неуклюжий.")),
        "picky" to listOf(u("My son is a picky eater.", "Мой сын привередлив в еде.")),
        "nosy" to listOf(u("Our neighbour is so nosy.", "Наша соседка такая любопытная — везде суёт нос.")),
        "easy-going" to listOf(u("My boss is really easy-going.", "Мой начальник очень лёгкий в общении.", WORK)),
        "awkward" to listOf(u("There was an awkward silence.", "Повисла неловкая пауза.")),
        "relieved" to listOf(u("I was so relieved when he called.", "У меня отлегло, когда он позвонил.")),
        "thrilled" to listOf(u("She was thrilled with the present.", "Она была в восторге от подарка.")),
        "fed up" to listOf(u("I'm fed up with this rain.", "Мне надоел этот дождь.")),
        "overwhelmed" to listOf(u("I feel a bit overwhelmed at work.", "На работе я немного на пределе.", WORK)),
        "homesick" to listOf(u("I got homesick after a month abroad.", "Через месяц за границей я затосковал по дому.")),
        "ashamed" to listOf(u("You should be ashamed of yourself!", "Как тебе не стыдно!")),
        "grateful" to listOf(u("I'm really grateful for your help.", "Я очень благодарен за помощь.")),
        "show off" to listOf(u("Stop showing off!", "Хватит выпендриваться!")),
        "freak out" to listOf(u("Don't freak out, but I lost your keys.", "Только не психуй, но я потерял твои ключи.")),
        "mess up" to listOf(u("I totally messed up the interview.", "Я полностью завалил собеседование.", WORK)),
        "blame" to listOf(u("Don't blame me!", "Не вини меня!")),
        "tease" to listOf(u("Stop teasing your sister.", "Перестань дразнить сестру.")),
        "gossip" to listOf(u("I don't like gossip at work.", "Не люблю сплетни на работе.", WORK)),
        "charger" to listOf(u("Have you got a phone charger?", "У тебя есть зарядка для телефона?", TECH)),
        "my battery is dead" to listOf(u("Sorry I didn't call — my battery was dead.", "Извини, что не позвонил, — телефон сел.", TECH)),
        "screenshot" to listOf(u("Send me a screenshot.", "Скинь мне скриншот.", TECH)),
        "settings" to listOf(u("Go to settings and turn on Bluetooth.", "Зайди в настройки и включи блютус.", TECH)),
        "notification" to listOf(u("I turned off all notifications.", "Я отключил все уведомления.", TECH)),
        "no signal" to listOf(u("There's no signal in the basement.", "В подвале нет связи.", TECH)),
        "back up" to listOf(u("Back up your photos before the update.", "Сделай копию фото перед обновлением.", TECH), u("Back me up in the meeting, okay?", "Поддержи меня на встрече, ладно?", WORK)),
        "log in" to listOf(u("I can't log in.", "Не могу войти.", TECH)),
        "reset" to listOf(u("Try to reset your password.", "Попробуй сбросить пароль.", TECH)),
        "it froze" to listOf(u("My laptop froze again.", "Мой ноутбук опять завис.", TECH)),
        "voice message" to listOf(u("I'll send you a voice message.", "Я пришлю тебе голосовое.", TECH)),
        "unsubscribe" to listOf(u("How do I unsubscribe from these emails?", "Как отписаться от этих писем?", TECH)),
        "basically" to listOf(u("Basically, we need more time.", "Короче, нам нужно больше времени.")),
        "literally" to listOf(u("I literally just got home.", "Я буквально только что пришёл домой.")),
        "obviously" to listOf(u("Obviously, I said yes!", "Конечно же, я согласился!")),
        "hopefully" to listOf(u("Hopefully, it won't rain.", "Надеюсь, дождя не будет.")),
        "luckily" to listOf(u("Luckily, nobody was hurt.", "К счастью, никто не пострадал.")),
        "meanwhile" to listOf(u("Meanwhile, I'll make some tea.", "А я пока сделаю чай.")),
        "by the way" to listOf(u("By the way, have you seen Anna?", "Кстати, ты видел Анну?")),
        "no matter what" to listOf(u("I'll be there no matter what.", "Я приду, что бы ни случилось.")),
        "as long as" to listOf(u("You can borrow it as long as you bring it back.", "Можешь взять, если вернёшь.")),
        "in case" to listOf(u("Take an umbrella in case it rains.", "Возьми зонт на случай дождя.")),
        "unless" to listOf(u("I won't go unless you come too.", "Я не пойду, если ты не пойдёшь.")),
        "even though" to listOf(u("He went to work even though he was ill.", "Он пошёл на работу, хотя был болен.")),
        "at least" to listOf(u("At least it's not raining.", "По крайней мере, дождя нет.")),
        "so far" to listOf(u("So far, so good.", "Пока всё хорошо.")),
        "kind of" to listOf(u("I'm kind of tired.", "Я вроде как устал.")),
    )

    /** Колода «Одно слово — разные смыслы». */
    val multiDeck: List<Item> = multi.map { (word, pair) -> Item("mm:$word", word, pair.first) }

    /** Пары «предложение» из всех колод — для автоматических примеров. */
    private val sentences: List<Item> by lazy {
        (Course.items + Content.phrases + Dialogues.items + Complex.items + Vocab.phrases + Vocab.phrasal + Idioms.items + Slang.items)
            .distinctBy { it.en }
    }

    private val cache = mutableMapOf<String, List<Usage>>()
    private val extra = mutableMapOf<String, Usage>()

    /** Пример из урока курса (сгенерирован ИИ). */
    fun register(item: Item, usage: Usage) {
        val key = item.en.lowercase().trim()
        if (extra[key] != usage) {
            extra[key] = usage
            cache.remove(key)
        }
    }

    /** Примеры для слова: сначала написанные вручную, иначе — фразы из приложения с этим словом. */
    fun forItem(item: Item): List<Usage> {
        val key = item.en.lowercase().trim()
        cache[key]?.let { return it }
        val manual = multi[key]?.second ?: b2[key] ?: b1[key] ?: extra[key]?.let { listOf(it) }
        val result = manual ?: run {
            // Ищем слово целиком во фразах приложения (не для длинных фраз)
            if (key.split(" ").size > 3) return@run emptyList()
            val re = Regex("(?i)\\b" + Regex.escape(key) + "\\b")
            sentences.asSequence()
                .filter { it.en.lowercase() != key && re.containsMatchIn(it.en) }
                .take(2)
                .map { Usage(it.en, it.ru, "💬 из приложения") }
                .toList()
        }
        cache[key] = result
        return result
    }
}
