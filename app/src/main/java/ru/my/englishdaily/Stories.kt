package ru.my.englishdaily

data class Sentence(val en: String, val ru: String)

/**
 * Вопрос по истории. answer — образец ответа.
 * keys — ключевые слова: если хоть одно прозвучало, ответ засчитан.
 */
data class Question(val q: String, val answer: String, val ru: String, val keys: List<String>)

data class Story(
    val id: String,
    val emoji: String,
    val title: String,
    val level: Int,
    val sentences: List<Sentence>,
    val glossary: Map<String, String>,
    val questions: List<Question>,
)

private fun s(en: String, ru: String) = Sentence(en, ru)
private fun q(q: String, a: String, ru: String, vararg keys: String) = Question(q, a, ru, keys.toList())

object Stories {
    val levelNames = mapOf(1 to "Уровень 1 · Начальный", 2 to "Уровень 2 · Базовый", 3 to "Уровень 3 · Средний")

    val all: List<Story> = listOf(
        Story(
            "morning", "☀️", "Утро Майка", 1,
            listOf(
                s("Mike gets up at seven.", "Майк встаёт в семь."),
                s("He is tired, but he is happy.", "Он устал, но он счастлив."),
                s("He makes coffee and eats bread with butter.", "Он варит кофе и ест хлеб с маслом."),
                s("Mike has a small black car.", "У Майка маленькая чёрная машина."),
                s("The car is old, but it works.", "Машина старая, но работает."),
                s("He drives to work.", "Он едет на работу."),
                s("His work starts at nine.", "Его работа начинается в девять."),
                s("Today the road is busy, and Mike is late.", "Сегодня на дороге пробки, и Майк опаздывает."),
                s("His boss is not happy.", "Его начальник недоволен."),
                s("Mike says: \"Sorry, I'm late. It won't happen again.\"", "Майк говорит: «Извините, я опоздал. Это больше не повторится»."),
            ),
            mapOf(
                "gets" to "получает; get up — вставать", "tired" to "уставший", "happy" to "счастливый, довольный",
                "makes" to "делает, готовит", "coffee" to "кофе", "eats" to "ест", "bread" to "хлеб",
                "butter" to "сливочное масло", "small" to "маленький", "black" to "чёрный", "car" to "машина",
                "works" to "работает", "drives" to "едет (за рулём)", "starts" to "начинается", "nine" to "девять",
                "seven" to "семь", "road" to "дорога", "busy" to "загруженный, занятой", "late" to "опоздавший, поздно",
                "boss" to "начальник", "sorry" to "извините", "happen" to "случаться", "again" to "снова",
            ),
            listOf(
                q("What time does Mike get up?", "He gets up at seven.", "Во сколько встаёт Майк?", "seven", "7"),
                q("Is Mike tired?", "Yes, he is.", "Майк устал?", "yes"),
                q("What does Mike drink?", "He drinks coffee.", "Что пьёт Майк?", "coffee"),
                q("Is his car new?", "No, it isn't. It's old.", "Его машина новая?", "no", "old"),
                q("When does his work start?", "It starts at nine.", "Когда начинается его работа?", "nine", "9"),
                q("Is his boss happy?", "No, he isn't.", "Начальник доволен?", "no"),
            )
        ),
        Story(
            "shop", "🛒", "В магазине", 1,
            listOf(
                s("Anna needs food for dinner.", "Анне нужна еда на ужин."),
                s("She goes to the shop near her house.", "Она идёт в магазин рядом с домом."),
                s("She buys bread, milk, cheese and apples.", "Она покупает хлеб, молоко, сыр и яблоки."),
                s("The apples are red and very sweet.", "Яблоки красные и очень сладкие."),
                s("At the checkout, Anna wants to pay by card.", "На кассе Анна хочет заплатить картой."),
                s("But her card doesn't work!", "Но её карта не работает!"),
                s("She has no cash.", "У неё нет наличных."),
                s("A kind old man says: \"Don't worry, I'll pay.\"", "Добрый пожилой мужчина говорит: «Не волнуйтесь, я заплачу»."),
                s("Anna says: \"Thank you so much!\"", "Анна говорит: «Огромное спасибо!»"),
                s("The next day she brings him a cake.", "На следующий день она приносит ему торт."),
            ),
            mapOf(
                "needs" to "нуждается, нужно", "food" to "еда", "dinner" to "ужин", "shop" to "магазин",
                "near" to "рядом", "buys" to "покупает", "milk" to "молоко", "cheese" to "сыр", "apples" to "яблоки",
                "red" to "красный", "sweet" to "сладкий", "checkout" to "касса", "wants" to "хочет",
                "pay" to "платить", "card" to "карта", "work" to "работать", "cash" to "наличные",
                "kind" to "добрый", "worry" to "волноваться", "thank" to "благодарить", "next" to "следующий",
                "brings" to "приносит", "cake" to "торт",
            ),
            listOf(
                q("Where does Anna go?", "She goes to the shop.", "Куда идёт Анна?", "shop", "store"),
                q("What does she buy?", "She buys bread, milk, cheese and apples.", "Что она покупает?", "bread", "milk", "cheese", "apples", "apple"),
                q("Are the apples sweet?", "Yes, they are.", "Яблоки сладкие?", "yes"),
                q("Does her card work?", "No, it doesn't.", "Её карта работает?", "no"),
                q("Who pays for Anna?", "An old man pays.", "Кто платит за Анну?", "man"),
                q("What does Anna bring the next day?", "She brings a cake.", "Что Анна приносит на следующий день?", "cake"),
            )
        ),
        Story(
            "dog", "🐶", "Мой пёс Макс", 1,
            listOf(
                s("I have a dog. His name is Max.", "У меня есть собака. Его зовут Макс."),
                s("Max is big and brown.", "Макс большой и коричневый."),
                s("He likes to run in the park.", "Он любит бегать в парке."),
                s("Every morning we walk for one hour.", "Каждое утро мы гуляем час."),
                s("Max loves other dogs, but he doesn't like cats.", "Макс любит других собак, но не любит кошек."),
                s("In the evening he sleeps near the door.", "Вечером он спит у двери."),
                s("When it rains, Max doesn't want to go out.", "Когда идёт дождь, Макс не хочет выходить."),
                s("He looks at me and says nothing.", "Он смотрит на меня и ничего не говорит."),
                s("But I know what he thinks: \"No way!\"", "Но я знаю, что он думает: «Ни за что!»"),
            ),
            mapOf(
                "dog" to "собака", "dogs" to "собаки", "name" to "имя", "big" to "большой", "brown" to "коричневый",
                "likes" to "любит, нравится", "run" to "бегать", "park" to "парк", "every" to "каждый",
                "morning" to "утро", "walk" to "гулять", "hour" to "час", "loves" to "любит", "other" to "другой",
                "cats" to "кошки", "evening" to "вечер", "sleeps" to "спит", "near" to "рядом, у", "door" to "дверь",
                "rains" to "идёт дождь", "want" to "хотеть", "looks" to "смотрит", "nothing" to "ничего",
                "know" to "знаю", "thinks" to "думает", "way" to "путь; no way — ни за что",
            ),
            listOf(
                q("What is the dog's name?", "His name is Max.", "Как зовут собаку?", "max"),
                q("Is Max small?", "No, he's big.", "Макс маленький?", "no", "big"),
                q("Where does Max like to run?", "In the park.", "Где Макс любит бегать?", "park"),
                q("How long do they walk every morning?", "For one hour.", "Сколько они гуляют каждое утро?", "hour"),
                q("Does Max like cats?", "No, he doesn't.", "Макс любит кошек?", "no"),
                q("Where does Max sleep?", "Near the door.", "Где спит Макс?", "door"),
            )
        ),
        Story(
            "sea", "🏖", "Поездка на море", 2,
            listOf(
                s("Last summer Tom and his wife went to the sea.", "Прошлым летом Том с женой ездили на море."),
                s("They flew to Turkey and stayed in a small hotel.", "Они полетели в Турцию и жили в маленькой гостинице."),
                s("The weather was hot and sunny every day.", "Каждый день было жарко и солнечно."),
                s("In the mornings they swam, and in the afternoons they slept.", "По утрам они плавали, а днём спали."),
                s("One day Tom wanted to try diving.", "Однажды Том захотел попробовать дайвинг."),
                s("He was a bit nervous, but the instructor helped him.", "Он немного нервничал, но инструктор ему помог."),
                s("Under the water he saw beautiful fish.", "Под водой он увидел красивых рыб."),
                s("In the evening they ate fresh fish in a restaurant.", "Вечером они ели свежую рыбу в ресторане."),
                s("Tom said: \"This is the best holiday of my life!\"", "Том сказал: «Это лучший отпуск в моей жизни!»"),
                s("Next year they want to go again.", "В следующем году они хотят поехать снова."),
            ),
            mapOf(
                "last" to "прошлый, последний", "summer" to "лето", "wife" to "жена", "went" to "поехали, пошли",
                "sea" to "море", "flew" to "полетели", "turkey" to "Турция", "stayed" to "остановились, жили",
                "hotel" to "гостиница", "weather" to "погода", "hot" to "жаркий", "sunny" to "солнечный",
                "mornings" to "утра", "swam" to "плавали", "afternoons" to "дни (после полудня)", "slept" to "спали",
                "wanted" to "хотел", "try" to "попробовать", "diving" to "дайвинг, ныряние", "bit" to "немного (a bit)",
                "nervous" to "нервный, нервничал", "instructor" to "инструктор", "helped" to "помог", "under" to "под",
                "water" to "вода", "saw" to "увидел", "beautiful" to "красивый", "fish" to "рыба", "evening" to "вечер",
                "ate" to "ели", "fresh" to "свежий", "restaurant" to "ресторан", "best" to "лучший",
                "holiday" to "отпуск, праздник", "life" to "жизнь", "next" to "следующий", "year" to "год",
                "again" to "снова",
            ),
            listOf(
                q("Where did Tom and his wife go?", "They went to the sea.", "Куда ездили Том с женой?", "sea", "turkey"),
                q("How was the weather?", "It was hot and sunny.", "Какая была погода?", "hot", "sunny"),
                q("What did they do in the mornings?", "They swam.", "Что они делали по утрам?", "swam", "swim", "swimming"),
                q("What did Tom want to try?", "He wanted to try diving.", "Что захотел попробовать Том?", "diving", "dive"),
                q("What did he see under the water?", "He saw beautiful fish.", "Что он увидел под водой?", "fish"),
                q("Do they want to go again?", "Yes, they do.", "Они хотят поехать снова?", "yes"),
            )
        ),
        Story(
            "job", "💼", "Новая работа", 2,
            listOf(
                s("Kate started a new job on Monday.", "Кейт вышла на новую работу в понедельник."),
                s("She works in a big office in the city center.", "Она работает в большом офисе в центре города."),
                s("On the first day she was very nervous.", "В первый день она очень нервничала."),
                s("She didn't know anyone, and she got lost twice.", "Она никого не знала и дважды заблудилась."),
                s("At lunch a man named Sam sat next to her.", "В обед мужчина по имени Сэм сел рядом с ней."),
                s("\"Hi, are you new here?\" he asked.", "«Привет, ты здесь новенькая?» — спросил он."),
                s("Sam showed her the coffee machine and told her about the team.", "Сэм показал ей кофемашину и рассказал о команде."),
                s("After that, everything was easier.", "После этого всё стало проще."),
                s("Now Kate likes her job, and Sam is her best friend at work.", "Теперь Кейт нравится работа, а Сэм — её лучший друг на работе."),
            ),
            mapOf(
                "started" to "начала", "job" to "работа", "monday" to "понедельник", "works" to "работает",
                "office" to "офис", "city" to "город", "center" to "центр", "first" to "первый",
                "nervous" to "нервничала", "know" to "знать", "anyone" to "кого-либо, никого (с not)",
                "got" to "стала, получила; got lost — заблудилась", "lost" to "потерянный; get lost — заблудиться",
                "twice" to "дважды", "lunch" to "обед", "named" to "по имени", "sat" to "сел", "next" to "рядом (next to)",
                "asked" to "спросил", "showed" to "показал", "coffee" to "кофе", "machine" to "машина, аппарат",
                "told" to "рассказал", "team" to "команда", "everything" to "всё", "easier" to "проще, легче",
                "likes" to "нравится", "best" to "лучший", "friend" to "друг",
            ),
            listOf(
                q("When did Kate start her new job?", "On Monday.", "Когда Кейт вышла на новую работу?", "monday"),
                q("Where is her office?", "In the city center.", "Где её офис?", "center", "city"),
                q("How did she feel on the first day?", "She was nervous.", "Как она себя чувствовала в первый день?", "nervous"),
                q("Who sat next to her at lunch?", "Sam did.", "Кто сел рядом с ней в обед?", "sam"),
                q("What did Sam show her?", "The coffee machine.", "Что Сэм ей показал?", "coffee", "machine"),
                q("Does Kate like her job now?", "Yes, she does.", "Кейт теперь нравится работа?", "yes"),
            )
        ),
        Story(
            "phone", "📱", "Разбитый телефон", 2,
            listOf(
                s("Yesterday Alex dropped his phone on the street.", "Вчера Алекс уронил телефон на улице."),
                s("The screen broke into small pieces.", "Экран разбился на мелкие кусочки."),
                s("He couldn't call anyone or read his messages.", "Он не мог никому позвонить и прочитать сообщения."),
                s("He went to a repair shop near his house.", "Он пошёл в мастерскую рядом с домом."),
                s("The man there said: \"I can fix it, but it will take two days.\"", "Мастер сказал: «Я могу починить, но это займёт два дня»."),
                s("\"How much will it cost?\" asked Alex.", "«Сколько это будет стоить?» — спросил Алекс."),
                s("\"Fifty dollars,\" said the man.", "«Пятьдесят долларов», — сказал мастер."),
                s("Alex agreed. For two days he lived without a phone.", "Алекс согласился. Два дня он жил без телефона."),
                s("Surprisingly, he read a book and slept better.", "Удивительно, но он прочитал книгу и спал лучше."),
            ),
            mapOf(
                "yesterday" to "вчера", "dropped" to "уронил", "phone" to "телефон", "street" to "улица",
                "screen" to "экран", "broke" to "разбился, сломался", "pieces" to "кусочки", "small" to "маленький",
                "call" to "звонить", "anyone" to "кому-либо", "read" to "читать", "messages" to "сообщения",
                "went" to "пошёл", "repair" to "ремонт", "shop" to "магазин; repair shop — мастерская",
                "fix" to "починить", "take" to "занять (время)", "days" to "дни", "cost" to "стоить",
                "fifty" to "пятьдесят", "dollars" to "доллары", "agreed" to "согласился", "lived" to "жил",
                "without" to "без", "surprisingly" to "удивительно", "book" to "книга", "slept" to "спал",
                "better" to "лучше",
            ),
            listOf(
                q("What did Alex drop?", "He dropped his phone.", "Что уронил Алекс?", "phone"),
                q("What happened to the screen?", "It broke.", "Что случилось с экраном?", "broke", "broken", "break"),
                q("Where did he go?", "To a repair shop.", "Куда он пошёл?", "repair", "shop"),
                q("How long will the repair take?", "Two days.", "Сколько займёт ремонт?", "two", "2"),
                q("How much did it cost?", "Fifty dollars.", "Сколько это стоило?", "fifty", "50"),
                q("What did Alex read?", "He read a book.", "Что прочитал Алекс?", "book"),
            )
        ),
        Story(
            "bank", "☎️", "Трудный звонок", 3,
            listOf(
                s("Lisa had to call the bank because money disappeared from her account.", "Лизе пришлось позвонить в банк: со счёта пропали деньги."),
                s("First, she waited on the line for twenty minutes.", "Сначала она двадцать минут ждала на линии."),
                s("Then a polite woman answered and asked for her name and date of birth.", "Потом ответила вежливая женщина и спросила её имя и дату рождения."),
                s("Lisa explained that she hadn't bought anything online that week.", "Лиза объяснила, что ничего не покупала в интернете на той неделе."),
                s("The woman checked her account and found two strange payments.", "Женщина проверила её счёт и нашла два странных платежа."),
                s("\"Don't worry, we'll block your card and return the money,\" she said.", "«Не волнуйтесь, мы заблокируем карту и вернём деньги», — сказала она."),
                s("\"How long will it take?\" Lisa asked.", "«Сколько времени это займёт?» — спросила Лиза."),
                s("\"Usually three to five working days.\"", "«Обычно от трёх до пяти рабочих дней»."),
                s("A week later the money was back, and Lisa got a new card.", "Через неделю деньги вернулись, и Лиза получила новую карту."),
                s("Now she always checks her account on Sundays.", "Теперь она всегда проверяет счёт по воскресеньям."),
            ),
            mapOf(
                "had" to "имела; had to — пришлось", "bank" to "банк", "because" to "потому что", "money" to "деньги",
                "disappeared" to "исчезли, пропали", "account" to "счёт", "first" to "сначала", "waited" to "ждала",
                "line" to "линия", "twenty" to "двадцать", "minutes" to "минуты", "polite" to "вежливый",
                "answered" to "ответила", "asked" to "спросила", "date" to "дата", "birth" to "рождение",
                "explained" to "объяснила", "bought" to "купила", "anything" to "что-либо, ничего (с not)",
                "online" to "в интернете", "week" to "неделя", "checked" to "проверила", "checks" to "проверяет",
                "found" to "нашла", "strange" to "странный", "payments" to "платежи", "worry" to "волноваться",
                "block" to "заблокировать", "card" to "карта", "return" to "вернуть", "usually" to "обычно",
                "five" to "пять", "working" to "рабочий", "later" to "позже, спустя", "back" to "обратно",
                "got" to "получила", "always" to "всегда", "sundays" to "по воскресеньям",
            ),
            listOf(
                q("Why did Lisa call the bank?", "Money disappeared from her account.", "Почему Лиза позвонила в банк?", "money", "account"),
                q("How long did she wait on the line?", "Twenty minutes.", "Сколько она ждала на линии?", "twenty", "20"),
                q("What did the woman find?", "Two strange payments.", "Что нашла женщина?", "payments", "payment", "strange"),
                q("What will the bank do with her card?", "They will block it.", "Что банк сделает с её картой?", "block", "blocked"),
                q("When did the money come back?", "A week later.", "Когда вернулись деньги?", "week"),
                q("When does Lisa check her account now?", "On Sundays.", "Когда Лиза теперь проверяет счёт?", "sunday", "sundays"),
            )
        ),
        Story(
            "move", "🏙", "Переезд в новый город", 3,
            listOf(
                s("Two years ago Daniel moved to a new city for work.", "Два года назад Дэниел переехал в новый город по работе."),
                s("At first he felt lonely, because he didn't have any friends there.", "Сначала ему было одиноко: друзей там у него не было."),
                s("He spent his evenings at home, watching TV series.", "Вечера он проводил дома за сериалами."),
                s("One day he saw an ad for a running club in the park.", "Однажды он увидел объявление о беговом клубе в парке."),
                s("He decided to try it, even though he hated running.", "Он решил попробовать, хотя ненавидел бег."),
                s("The first run was terrible, but the people were friendly.", "Первая пробежка была ужасной, но люди оказались дружелюбными."),
                s("After the run they all went for coffee and talked for hours.", "После пробежки все пошли пить кофе и болтали часами."),
                s("Now Daniel runs three times a week and has a lot of friends.", "Теперь Дэниел бегает три раза в неделю, и у него много друзей."),
                s("He says: \"Moving was hard, but it was the best decision of my life.\"", "Он говорит: «Переезд был трудным, но это лучшее решение в моей жизни»."),
            ),
            mapOf(
                "years" to "годы", "ago" to "назад", "moved" to "переехал", "moving" to "переезд", "city" to "город",
                "first" to "первый; at first — сначала", "felt" to "чувствовал", "lonely" to "одинокий",
                "because" to "потому что", "friends" to "друзья", "spent" to "проводил", "evenings" to "вечера",
                "watching" to "смотря", "tv" to "телевизор", "series" to "сериал(ы)", "saw" to "увидел",
                "ad" to "объявление, реклама", "running" to "бег", "club" to "клуб", "park" to "парк",
                "decided" to "решил", "try" to "попробовать", "even" to "даже; even though — хотя",
                "though" to "хотя", "hated" to "ненавидел", "run" to "пробежка, бегать", "runs" to "бегает",
                "terrible" to "ужасный", "friendly" to "дружелюбный", "went" to "пошли", "coffee" to "кофе",
                "talked" to "разговаривали", "hours" to "часы", "times" to "раз(а)", "week" to "неделя",
                "lot" to "много (a lot of)", "hard" to "трудный", "best" to "лучший", "decision" to "решение",
                "life" to "жизнь",
            ),
            listOf(
                q("Why did Daniel move?", "For work.", "Почему Дэниел переехал?", "work", "job"),
                q("How did he feel at first?", "He felt lonely.", "Как он себя чувствовал сначала?", "lonely", "alone"),
                q("What did he see one day?", "An ad for a running club.", "Что он однажды увидел?", "running", "club", "ad"),
                q("Did he like running before?", "No, he hated it.", "Ему раньше нравился бег?", "no", "hated"),
                q("What did they do after the run?", "They went for coffee.", "Что они делали после пробежки?", "coffee"),
                q("How often does Daniel run now?", "Three times a week.", "Как часто Дэниел теперь бегает?", "three", "3"),
            )
        ),
    )
}
