package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Трек «Переезд в США»: английский для конкретных ситуаций — от собеседования в посольстве
 * до первой работы. Американский вариант языка.
 */
object Usa {
    private fun g(group: String, vararg list: Pair<String, String>) =
        list.map { (en, ru) -> Item("usa:$en", en, ru, group) }

    val phrases: List<Item> =
        g(
            "🏛 Собеседование в посольстве",
            "Good morning. Here are my documents." to "Доброе утро. Вот мои документы.",
            "I'm applying for an immigrant visa through the Diversity Visa program." to "Я подаю на иммиграционную визу по программе Diversity Visa.",
            "I'm married. My wife is applying with me." to "Я женат. Жена подаёт вместе со мной.",
            "I've finished high school." to "Я окончил среднюю школу.",
            "I own a small cafe. I've been running it for several years." to "У меня небольшое кафе. Я веду его несколько лет.",
            "We plan to live in New York at first." to "Сначала мы планируем жить в Нью-Йорке.",
            "A friend of ours has agreed to help us at first." to "Наш друг согласился помочь нам на первых порах.",
            "Sorry, could you repeat the question, please?" to "Извините, не могли бы вы повторить вопрос?",
            "Could you speak a little more slowly, please?" to "Не могли бы вы говорить чуть медленнее?",
            "I'd prefer to answer with an interpreter, if possible." to "Я бы предпочёл отвечать с переводчиком, если можно.",
            "Yes, that's correct." to "Да, всё верно.",
            "No, I've never been to the United States." to "Нет, я никогда не был в США.",
        ) + g(
            "🛬 Аэропорт и паспортный контроль",
            "This is my first time entering the U.S. as an immigrant." to "Я впервые въезжаю в США как иммигрант.",
            "Here's my passport and the sealed envelope from the embassy." to "Вот мой паспорт и запечатанный конверт из посольства.",
            "We're going to stay with friends in Brooklyn." to "Мы будем жить у друзей в Бруклине.",
            "I have nothing to declare." to "Мне нечего декларировать.",
            "Where is the baggage claim?" to "Где выдача багажа?",
            "One of our bags is missing." to "Одна наша сумка потерялась.",
            "How do I get to the city center from here?" to "Как отсюда добраться до центра?",
            "Is there a shuttle to the hotel?" to "Есть шаттл до отеля?",
        ) + g(
            "🪪 Документы, SSN и банк",
            "I'd like to apply for a Social Security number." to "Я хочу оформить номер социального страхования (SSN).",
            "When will I receive my card by mail?" to "Когда мне придёт карта по почте?",
            "I'd like to open a checking account." to "Я хочу открыть текущий счёт.",
            "What documents do I need to open an account?" to "Какие документы нужны, чтобы открыть счёт?",
            "Is there a monthly fee?" to "Есть ежемесячная плата?",
            "I'd like to order a debit card." to "Я хочу заказать дебетовую карту.",
            "How can I build my credit history?" to "Как мне создать кредитную историю?",
            "I need to send money abroad." to "Мне нужно отправить деньги за границу.",
            "Can you help me fill out this form?" to "Можете помочь мне заполнить эту форму?",
            "What does this word mean on the form?" to "Что значит это слово в форме?",
        ) + g(
            "🏠 Жильё",
            "I'm calling about the apartment for rent." to "Я звоню по поводу квартиры в аренду.",
            "Is it still available?" to "Она ещё свободна?",
            "How much is the rent per month?" to "Сколько стоит аренда в месяц?",
            "Are utilities included?" to "Коммунальные услуги включены?",
            "How much is the security deposit?" to "Какой залог?",
            "We don't have a credit history yet. Can we pay a bigger deposit?" to "У нас пока нет кредитной истории. Можно заплатить залог побольше?",
            "Is there laundry in the building?" to "В доме есть прачечная?",
            "Are pets allowed?" to "С животными можно?",
            "When can we move in?" to "Когда можно въехать?",
            "The heating isn't working. Could you send someone?" to "Не работает отопление. Можете кого-нибудь прислать?",
            "The sink is leaking." to "Раковина протекает.",
        ) + g(
            "🚗 Права и машина (DMV)",
            "I'd like to get a driver's license." to "Я хочу получить водительские права.",
            "Can I make an appointment for the road test?" to "Можно записаться на экзамен по вождению?",
            "Where can I take the written test?" to "Где можно сдать теоретический экзамен?",
            "I have a foreign driver's license. Can I exchange it?" to "У меня иностранные права. Их можно обменять?",
            "I'd like to buy a used car." to "Я хочу купить подержанную машину.",
            "How many miles does it have?" to "Какой у неё пробег в милях?",
            "I need car insurance." to "Мне нужна автостраховка.",
            "Officer, here's my license and registration." to "Офицер, вот мои права и документы на машину.",
            "Fill it up with regular, please." to "Полный бак обычного, пожалуйста.",
        ) + g(
            "🩺 Врач и страховка",
            "I'd like to make an appointment with a doctor." to "Я хочу записаться к врачу.",
            "Do you accept my insurance?" to "Вы принимаете мою страховку?",
            "I don't have insurance yet. How much will it cost?" to "У меня пока нет страховки. Сколько это будет стоить?",
            "I've had a fever since yesterday." to "У меня температура со вчерашнего дня.",
            "I'm allergic to penicillin." to "У меня аллергия на пенициллин.",
            "Where is the nearest urgent care?" to "Где ближайший пункт неотложной помощи?",
            "Can I pick up my prescription here?" to "Можно здесь получить лекарство по рецепту?",
            "Call 911! It's an emergency!" to "Звоните 911! Это срочно!",
        ) + g(
            "🛒 Быт и магазины",
            "Do you have this in a different size?" to "Есть это другого размера?",
            "Can I return this? I have the receipt." to "Можно это вернуть? У меня есть чек.",
            "Is tax included in the price?" to "Налог входит в цену?",
            "Paper or plastic? — Paper, please." to "Бумажный или пластиковый пакет? — Бумажный, пожалуйста.",
            "Debit or credit? — Debit." to "Дебетовая или кредитная? — Дебетовая.",
            "I'd like to get a phone plan." to "Я хочу подключить тарифный план.",
            "How much is unlimited data?" to "Сколько стоит безлимитный интернет?",
            "Can I get a SIM card for this phone?" to "Можно сим-карту для этого телефона?",
        ) + g(
            "🍔 Работа в общепите",
            "I'm looking for a job in the kitchen." to "Я ищу работу на кухне.",
            "I have five years of experience in the food business." to "У меня пять лет опыта в общепите.",
            "I can start right away." to "Я могу начать сразу.",
            "Hi! What can I get for you?" to "Привет! Что вам предложить?",
            "Would you like that for here or to go?" to "Здесь или с собой?",
            "Do you want fries with that?" to "Картошку фри к этому?",
            "It'll be ready in five minutes." to "Будет готово через пять минут.",
            "Sorry, we're out of chicken today." to "Извините, курица сегодня закончилась.",
            "Are you allergic to anything?" to "У вас есть аллергия на что-нибудь?",
            "Order up! Table six." to "Заказ готов! Шестой столик.",
            "Behind you!" to "Осторожно, сзади! (на кухне)",
            "Wash your hands before you start." to "Помой руки перед началом.",
            "Keep raw meat away from vegetables." to "Держи сырое мясо отдельно от овощей.",
            "I'd like to open my own food truck." to "Я хочу открыть свой фудтрак.",
            "What licenses do I need to open a restaurant?" to "Какие лицензии нужны, чтобы открыть ресторан?",
        ) + g(
            "🚚 Водитель и доставка",
            "Hi, I'm outside with your order." to "Здравствуйте, я у входа с вашим заказом.",
            "I can't find the entrance. Could you come down?" to "Не могу найти вход. Можете спуститься?",
            "Which building is it?" to "Какое это здание?",
            "There's a lot of traffic. I'll be there in about ten minutes." to "Большие пробки. Буду минут через десять.",
            "I'll leave it at the door." to "Я оставлю у двери.",
            "Could you sign here, please?" to "Распишитесь здесь, пожалуйста.",
            "Take the next exit." to "Съезжай на следующем съезде.",
            "I got a flat tire on the highway." to "У меня спустило колесо на трассе.",
            "Where can I park the truck?" to "Где можно поставить грузовик?",
        ) + g(
            "🏗 Стройка и ремонт",
            "Where do you want me to start?" to "С чего мне начать?",
            "Hand me the hammer, please." to "Подай молоток, пожалуйста.",
            "We need more nails and screws." to "Нужно ещё гвоздей и шурупов.",
            "Measure it twice, cut it once." to "Семь раз отмерь — один раз отрежь.",
            "Put on your hard hat and safety glasses." to "Надень каску и защитные очки.",
            "Watch your step!" to "Смотри под ноги!",
            "Is the power off?" to "Электричество отключено?",
            "What time do we start tomorrow?" to "Во сколько завтра начинаем?",
            "How much do you pay per hour?" to "Сколько платите в час?",
            "When is payday?" to "Когда день зарплаты?",
        ) + g(
            "🇺🇸 Американские привычки",
            "How's it going? — Good, how about you?" to "Как дела? — Хорошо, а у тебя? (просто приветствие)",
            "Have a good one!" to "Хорошего дня!",
            "No worries!" to "Без проблем!",
            "I appreciate it." to "Спасибо, ценю.",
            "Excuse me, coming through." to "Простите, разрешите пройти.",
            "It's about seventy degrees outside." to "На улице около 70 по Фаренгейту (≈21 °C).",
            "It's about five miles from here." to "Это примерно в пяти милях (≈8 км) отсюда.",
            "Can I get a check, please?" to "Можно счёт? (в ресторане — check)",
            "Where is the restroom?" to "Где туалет? (вежливо — restroom)",
            "Let's grab a bite." to "Давай перекусим.",
        )

    private fun a(en: String, ru: String) = Line(0, en, ru)
    private fun b(en: String, ru: String) = Line(1, en, ru)

    val dialogues: List<Dialogue> = listOf(
        Dialogue(
            "usa_interview", "🏛", "Собеседование в посольстве", listOf(
                a("Good morning. Please raise your right hand. Do you swear that everything you've told us is true?", "Доброе утро. Поднимите правую руку. Клянётесь, что всё сказанное вами — правда?"),
                b("Yes, I do.", "Да, клянусь."),
                a("What's your highest level of education?", "Какое у вас самое высокое образование?"),
                b("I finished high school, and then I took a cooking course.", "Я окончил школу, а потом прошёл курсы повара."),
                a("What do you do for a living?", "Чем вы зарабатываете на жизнь?"),
                b("I own a small cafe. We make shawarma and other street food.", "У меня небольшое кафе. Мы делаем шаурму и другую уличную еду."),
                a("How long have you been married?", "Сколько вы женаты?"),
                b("We've been married for six years.", "Мы женаты шесть лет."),
                a("Where are you planning to live in the U.S.?", "Где вы планируете жить в США?"),
                b("At first in New York. We have friends there.", "Сначала в Нью-Йорке. У нас там друзья."),
                a("Thank you. Your visa has been approved.", "Спасибо. Ваша виза одобрена."),
            ), level = 2
        ),
        Dialogue(
            "usa_cbp", "🛬", "Паспортный контроль", listOf(
                a("Next, please. Passport and your visa envelope.", "Следующий. Паспорт и конверт с визой."),
                b("Here you go. This is our first time in the U.S.", "Вот, пожалуйста. Мы впервые в США."),
                a("Welcome. Is this your wife?", "Добро пожаловать. Это ваша жена?"),
                b("Yes, we're traveling together.", "Да, мы летим вместе."),
                a("What's your address here?", "Какой у вас здесь адрес?"),
                b("We'll stay with friends in Brooklyn. Here's the address.", "Мы остановимся у друзей в Бруклине. Вот адрес."),
                a("Do you have any food or more than ten thousand dollars in cash?", "Везёте еду или больше десяти тысяч долларов наличными?"),
                b("No, nothing like that.", "Нет, ничего такого."),
                a("Okay. Your green card will be mailed to that address. Welcome to the United States.", "Хорошо. Грин-карта придёт по почте на этот адрес. Добро пожаловать в США."),
            ), level = 2
        ),
        Dialogue(
            "usa_bank", "🏦", "Открываем счёт в банке", listOf(
                a("Hi there. How can I help you today?", "Здравствуйте. Чем могу помочь?"),
                b("Hi. We just moved here, and we'd like to open a checking account.", "Здравствуйте. Мы только что переехали и хотим открыть текущий счёт."),
                a("Sure. Do you have your passports and Social Security numbers?", "Конечно. У вас есть паспорта и номера SSN?"),
                b("We have our passports and green cards. The Social Security cards haven't arrived yet.", "Есть паспорта и грин-карты. Карточки SSN ещё не пришли."),
                a("That's okay. We can start with your passports and add the numbers later.", "Ничего. Можем начать с паспортов, а номера добавим позже."),
                b("Is there a monthly fee?", "Есть ежемесячная плата?"),
                a("It's twelve dollars, but we waive it if you have direct deposit.", "Двенадцать долларов, но её отменяют, если на счёт приходит зарплата."),
                b("Great. And can we get a credit card too?", "Отлично. А кредитную карту тоже можно?"),
                a("You could start with a secured card. It helps you build credit.", "Можно начать с обеспеченной карты. Она помогает создать кредитную историю."),
                b("Sounds good. Let's do that.", "Звучит хорошо. Давайте."),
            ), level = 2
        ),
        Dialogue(
            "usa_rent", "🏠", "Смотрим квартиру", listOf(
                a("So this is the living room, and the bedroom is through here.", "Вот гостиная, а спальня — вот здесь."),
                b("It's nice and bright. How much is the rent?", "Светло и приятно. Сколько аренда?"),
                a("Eighteen hundred a month, plus utilities.", "Тысяча восемьсот в месяц плюс коммуналка."),
                b("We just moved to the U.S., so we don't have a credit history yet.", "Мы только переехали в США, поэтому кредитной истории пока нет."),
                a("In that case, I'd need two months' rent as a deposit.", "Тогда мне нужен залог за два месяца."),
                b("That's fine. Is the lease for a year?", "Хорошо. Договор на год?"),
                a("Yes, twelve months. You can move in on the first.", "Да, двенадцать месяцев. Въехать можно с первого числа."),
                b("Great. Where do we sign?", "Отлично. Где подписать?"),
            ), level = 2
        ),
        Dialogue(
            "usa_job_kitchen", "🍔", "Собеседование в ресторан", listOf(
                a("Thanks for coming in. Tell me about your experience.", "Спасибо, что пришли. Расскажите о своём опыте."),
                b("I ran my own shawarma place for several years. I did everything — cooking, ordering supplies, managing staff.", "Я несколько лет вёл свою шаурмичную. Делал всё: готовил, заказывал продукты, руководил персоналом."),
                a("Impressive. Are you okay working weekends?", "Впечатляет. Вы готовы работать по выходным?"),
                b("Absolutely. I'm used to long shifts.", "Конечно. Я привык к длинным сменам."),
                a("Do you have a food handler's certificate?", "У вас есть сертификат для работы с едой?"),
                b("Not yet, but I can get one this week.", "Пока нет, но могу получить на этой неделе."),
                a("Great. We start at sixteen dollars an hour. Can you start Monday?", "Отлично. Начинаем с шестнадцати долларов в час. Можете выйти в понедельник?"),
                b("Yes, I can. Thank you for the opportunity!", "Да, могу. Спасибо за возможность!"),
            ), level = 2
        ),
        Dialogue(
            "usa_delivery", "🚚", "Звонок клиенту доставки", listOf(
                a("Hello?", "Алло?"),
                b("Hi, this is your delivery driver. I'm outside, but the gate is locked.", "Здравствуйте, это водитель доставки. Я у входа, но калитка закрыта."),
                a("Oh, sorry! The code is four-two-seven-one.", "Ой, извините! Код — четыре-два-семь-один."),
                b("Got it. Which building are you in?", "Понял. В каком вы корпусе?"),
                a("Building C, third floor, apartment three-twelve.", "Корпус C, третий этаж, квартира 312."),
                b("Okay, I'll be up in a minute.", "Хорошо, поднимусь через минуту."),
                a("Thanks so much!", "Большое спасибо!"),
                b("No problem. Enjoy your meal!", "Не за что. Приятного аппетита!"),
            ), level = 1
        ),
        Dialogue(
            "usa_site", "🏗", "Первый день на стройке", listOf(
                a("Morning! You're the new guy, right?", "Доброе утро! Ты новенький, да?"),
                b("Yeah, that's me. Where do you want me?", "Да, это я. Где мне работать?"),
                a("You'll help Carlos with the drywall today. First, grab a hard hat and some gloves.", "Сегодня поможешь Карлосу с гипсокартоном. Сначала возьми каску и перчатки."),
                b("Got it. Where are the tools?", "Понял. Где инструменты?"),
                a("In the trailer. And never go up the ladder without someone holding it.", "В вагончике. И никогда не лезь на стремянку, если её никто не держит."),
                b("Understood. What time is lunch?", "Ясно. Во сколько обед?"),
                a("Noon, thirty minutes. We wrap up at four thirty.", "В полдень, полчаса. Заканчиваем в полпятого."),
                b("Sounds good. Let's get to work.", "Хорошо. За работу."),
            ), level = 2
        ),
        Dialogue(
            "usa_dmv", "🚗", "В DMV за правами", listOf(
                a("Next! How can I help you?", "Следующий! Чем могу помочь?"),
                b("I'd like to get a driver's license. I have a license from Russia.", "Я хочу получить права. У меня российские права."),
                a("In this state, you'll need to take the written test and the road test.", "В этом штате нужно сдать теорию и вождение."),
                b("Can I take the written test today?", "Можно сдать теорию сегодня?"),
                a("Sure. It's available in several languages. Do you have proof of address?", "Конечно. Её можно сдать на нескольких языках. У вас есть подтверждение адреса?"),
                b("Yes, here's my lease and a bank statement.", "Да, вот договор аренды и выписка из банка."),
                a("Perfect. Please take a number and wait for the eye test.", "Отлично. Возьмите номерок и ждите проверку зрения."),
                b("Thank you.", "Спасибо."),
            ), level = 2
        ),
    )

    val items: List<Item> by lazy {
        phrases + dialogues.flatMap { d ->
            d.lines.mapIndexed { i, l -> Item("d:${d.id}:$i", l.en, l.ru, "${d.emoji} ${d.title}") }
        }
    }

    /** Американские мерки и привычки — шпаргалка. */
    val cheatSheet: List<Pair<String, List<Pair<String, String>>>> = listOf(
        "🌡 Температура (°F)" to listOf(
            "32 °F" to "0 °C — лёд", "50 °F" to "10 °C — прохладно", "68 °F" to "20 °C — комнатная",
            "86 °F" to "30 °C — жарко", "98.6 °F" to "37 °C — нормальная температура тела", "100 °F+" to "38 °C+ — жар",
            "Быстро в уме" to "°C ≈ (°F − 30) ÷ 2",
        ),
        "📏 Расстояние и рост" to listOf(
            "1 mile (миля)" to "1,6 км", "1 foot (фут)" to "30 см", "1 inch (дюйм)" to "2,5 см",
            "5′10″ (5 футов 10 дюймов)" to "≈ 178 см", "55 mph" to "≈ 90 км/ч",
        ),
        "⚖️ Вес и объём" to listOf(
            "1 pound (lb, фунт)" to "≈ 0,45 кг", "150 lbs" to "≈ 68 кг", "1 ounce (oz)" to "≈ 28 г",
            "1 gallon (галлон)" to "≈ 3,8 л — бензин продают галлонами", "1 cup" to "≈ 240 мл — в рецептах",
        ),
        "📅 Даты и время" to listOf(
            "10/27/2026" to "сначала МЕСЯЦ: 27 октября 2026", "7 AM / 7 PM" to "7 утра / 7 вечера",
            "noon / midnight" to "полдень / полночь",
        ),
        "💵 Деньги" to listOf(
            "Tax" to "налог прибавляют на кассе — цена на ценнике без него", "Tip 15–20%" to "чаевые в кафе, такси, доставке — норма",
            "a buck" to "доллар (разг.)", "a quarter / a dime" to "25 центов / 10 центов", "check" to "счёт в ресторане",
        ),
        "🗣 Американские слова" to listOf(
            "apartment" to "квартира (не flat)", "restroom" to "туалет", "gas" to "бензин", "fries" to "картошка фри",
            "line" to "очередь (не queue)", "cell phone" to "мобильный", "trash" to "мусор", "elevator" to "лифт",
            "sidewalk" to "тротуар", "vacation" to "отпуск", "zip code" to "почтовый индекс",
        ),
    )
}

// ---------- Экраны ----------

@Composable
fun UsaHubScreen(
    store: ProgressStore,
    speaker: Speaker,
    onTrain: (Mode) -> Unit,
    onDialog: (Dialogue) -> Unit,
    onRoleplay: (Dialogue) -> Unit,
    onChat: () -> Unit,
    onBrowse: () -> Unit,
    onExit: () -> Unit,
) {
    var tab by remember { mutableIntStateOf(0) }
    val stats = remember { store.stats(Usa.phrases) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar("🇺🇸 Переезд в США", onExit)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Учить", { tab = 0 }, selected = tab == 0)
            Pill("Диалоги", { tab = 1 }, selected = tab == 1)
            Pill("Мерки и привычки", { tab = 2 }, selected = tab == 2)
        }

        when (tab) {
            0 -> {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Английский для переезда", style = MaterialTheme.typography.titleMedium)
                    Hint(
                        "${Usa.phrases.size} фраз для реальных ситуаций: собеседование в посольстве, паспортный контроль, SSN и банк, " +
                            "жильё, права, врач, магазины и первая работа — общепит, доставка, стройка. Американский вариант английского."
                    )
                    Spacer(Modifier.height(8.dp))
                    PillProgress(if (stats.total == 0) 0f else (stats.total - stats.fresh).toFloat() / stats.total, Modifier.fillMaxWidth(), Grads.Repeat)
                    Hint("Начато ${stats.total - stats.fresh} из ${stats.total} · выучено ${stats.learned}")
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GradientButton("⚡ Учить", { onTrain(Mode.RECALL) }, Modifier.weight(1f), grad = Grads.Repeat)
                    GradientButton("🎧 Аудио", { onTrain(Mode.AUDIO) }, Modifier.weight(1f), grad = Grads.Audio)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SoftButton("👂 На слух", { onTrain(Mode.LISTEN) }, Modifier.weight(1f), height = 48.dp)
                    SoftButton("🎙 Вслух", { onTrain(Mode.SPEAK) }, Modifier.weight(1f), height = 48.dp)
                }
                SoftButton("📖 Все фразы по ситуациям", onBrowse, Modifier.fillMaxWidth(), height = 48.dp)
                SoftCard(Modifier.fillMaxWidth(), onClick = onChat) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile("🤖", Grads.Hero, 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Репетиция с ИИ", style = MaterialTheme.typography.titleMedium)
                            Hint("Консул, паспортный контроль, арендодатель, менеджер ресторана, прораб — отвечай голосом")
                        }
                        Text("›", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Hint("Переезд не завтра — главное сейчас база: 500 слов и курс A1 → C1. Этот раздел — 10–15 минут сверху, чтобы к переезду говорить в нужных ситуациях уверенно.")
            }
            1 -> {
                Usa.dialogues.forEach { d ->
                    SoftCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconTile(d.emoji, Grads.Repeat, 44.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(d.title, style = MaterialTheme.typography.titleMedium)
                                Hint("${d.lines.size} реплик" + if (store.listened(d.id) > 0) " · прослушан" else "")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GradientButton("▶ Слушать", { onDialog(d) }, Modifier.weight(1f), grad = Grads.Dialogs)
                            SoftButton("🎭 Сыграть", { onRoleplay(d) }, Modifier.weight(1f))
                        }
                    }
                }
            }
            else -> {
                Usa.cheatSheet.forEach { (title, rows) ->
                    SoftCard(Modifier.fillMaxWidth()) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        rows.forEach { (en, ru) ->
                            Row(Modifier.padding(vertical = 3.dp)) {
                                Text(en, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(0.45f))
                                Spacer(Modifier.width(8.dp))
                                Text(ru, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.55f))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
