package ru.my.englishdaily

/**
 * Грамматика по шагам — по методу учебника Н. Бонк «Английский шаг за шагом»:
 * короткое объяснение по-русски → таблица → примеры → много упражнений
 * на трансформацию и перевод. Все тексты и упражнения — свои.
 *
 * В ответах можно указать несколько правильных вариантов через « | ».
 */
data class Example(val en: String, val ru: String)

sealed interface Task {
    /** Перестрой фразу: из утверждения — вопрос, отрицание и т. п. */
    data class Transform(val from: String, val task: String, val answer: String) : Task

    /** Переведи с русского. */
    data class Translate(val ru: String, val answer: String) : Task

    /** Вставь пропуск: «___» в предложении. */
    data class Choice(val sentence: String, val options: List<String>, val correct: Int, val ru: String) : Task
}

data class GrammarLesson(
    val id: String,
    val emoji: String,
    val title: String,
    val level: Int,
    val explain: String,
    val table: List<Pair<String, String>>,
    val examples: List<Example>,
    val tasks: List<Task>,
)

private fun ex(en: String, ru: String) = Example(en, ru)
private fun tr(from: String, task: String, answer: String) = Task.Transform(from, task, answer)
private fun tl(ru: String, answer: String) = Task.Translate(ru, answer)
private fun ch(sentence: String, ru: String, correct: Int, vararg options: String) =
    Task.Choice(sentence, options.toList(), correct, ru)

private const val NEG = "Сделай отрицание"
private const val Q = "Задай общий вопрос"

object Grammar {
    /** Все уроки: базовые + сложные времена. */
    val all: List<GrammarLesson> by lazy { basic + TenseLessons.lessons }

    private val basic: List<GrammarLesson> = listOf(
        GrammarLesson(
            "be", "🧱", "Глагол to be: am, is, are", 1,
            "В русском мы говорим «Я врач», «Он дома» — без глагола. В английском так нельзя: нужен глагол to be («быть»). " +
                "Его форма зависит от того, кто: I am, he/she/it is, we/you/they are. " +
                "Вопрос — ставим am/is/are в начало. Отрицание — добавляем not после него.",
            listOf("I am (I'm)" to "я есть", "he / she / it is (he's)" to "он / она / оно", "we / you / they are (we're)" to "мы / вы / они", "Are you…? — Yes, I am." to "вопрос и короткий ответ", "I'm not / he isn't / they aren't" to "отрицание"),
            listOf(
                ex("I'm a driver.", "Я водитель."), ex("She is at home.", "Она дома."), ex("We are tired.", "Мы устали."),
                ex("Is he your brother?", "Он твой брат?"), ex("They aren't ready.", "Они не готовы."), ex("It's cold today.", "Сегодня холодно."),
            ),
            listOf(
                tr("She is at work.", Q, "Is she at work?"),
                tr("They are friends.", NEG, "They are not friends. | They aren't friends. | They're not friends."),
                tr("You are tired.", Q, "Are you tired?"),
                tr("I am hungry.", NEG, "I am not hungry. | I'm not hungry."),
                tl("Он дома.", "He is at home. | He's at home."),
                tl("Ты готов?", "Are you ready?"),
                tl("Мы не заняты.", "We are not busy. | We aren't busy. | We're not busy."),
                ch("My car ___ old.", "Моя машина старая.", 1, "am", "is", "are"),
                ch("___ you from Russia?", "Ты из России?", 2, "Am", "Is", "Are"),
                ch("I ___ not a teacher.", "Я не учитель.", 0, "am", "is", "are"),
                ch("The shops ___ closed.", "Магазины закрыты.", 2, "am", "is", "are"),
            )
        ),
        GrammarLesson(
            "ps1", "🔁", "Present Simple: что я делаю обычно", 1,
            "Present Simple — время для привычек, фактов и расписаний: «я работаю», «он живёт в Омске». " +
                "Глагол без изменений: I work, they live. Но с he / she / it добавляем -s: he works, she lives. " +
                "Слова-подсказки: every day, usually, often, always, never.",
            listOf("I / you / we / they work" to "без окончания", "he / she / it works" to "+ s", "go → goes, do → does" to "+ es", "have → has" to "особый случай", "study → studies" to "y → ies"),
            listOf(
                ex("I work every day.", "Я работаю каждый день."), ex("He lives in Moscow.", "Он живёт в Москве."),
                ex("She usually drinks tea.", "Она обычно пьёт чай."), ex("We often go to the park.", "Мы часто ходим в парк."),
                ex("My brother has a car.", "У моего брата есть машина."), ex("The shop opens at nine.", "Магазин открывается в девять."),
            ),
            listOf(
                tr("I work in an office.", "Начни с «He»", "He works in an office."),
                tr("They watch TV in the evening.", "Начни с «She»", "She watches TV in the evening."),
                tr("We have a dog.", "Начни с «My sister»", "My sister has a dog."),
                tr("I go to bed at eleven.", "Начни с «He»", "He goes to bed at eleven."),
                tl("Она работает в больнице.", "She works in a hospital. | She works at a hospital."),
                tl("Я обычно встаю в семь.", "I usually get up at seven."),
                tl("Мой папа любит футбол.", "My dad likes football. | My father likes football. | My dad loves football. | My father loves football."),
                ch("He ___ coffee every morning.", "Он пьёт кофе каждое утро.", 1, "drink", "drinks", "drinking"),
                ch("They ___ in a big city.", "Они живут в большом городе.", 0, "live", "lives", "living"),
                ch("My mom ___ dinner at six.", "Мама готовит ужин в шесть.", 1, "cook", "cooks", "cooking"),
                ch("She ___ two children.", "У неё двое детей.", 2, "have", "haves", "has"),
            )
        ),
        GrammarLesson(
            "ps2", "❓", "Present Simple: вопросы и отрицания", 1,
            "Чтобы задать вопрос в Present Simple, нужен помощник do (или does для he / she / it). Он встаёт в начало: Do you work? Does he work? " +
                "Важно: если есть does, у основного глагола -s уже НЕ ставим: Does he work? (не works). " +
                "Отрицание: don't / doesn't перед глаголом: I don't know, she doesn't like it.",
            listOf("Do you / they work?" to "вопрос", "Does he / she work?" to "вопрос (он, она)", "I don't work" to "отрицание", "He doesn't work" to "без -s у глагола!", "Where do you live?" to "вопрос со словом"),
            listOf(
                ex("Do you speak English?", "Ты говоришь по-английски?"), ex("Does she like coffee?", "Ей нравится кофе?"),
                ex("I don't understand.", "Я не понимаю."), ex("He doesn't drive.", "Он не водит машину."),
                ex("Where do you work?", "Где ты работаешь?"), ex("What time does the bus come?", "Во сколько приходит автобус?"),
            ),
            listOf(
                tr("You like pizza.", Q, "Do you like pizza?"),
                tr("He works on Sundays.", Q, "Does he work on Sundays?"),
                tr("She drives a car.", NEG, "She does not drive a car. | She doesn't drive a car."),
                tr("They live here.", NEG, "They do not live here. | They don't live here."),
                tl("Ты знаешь его?", "Do you know him?"),
                tl("Он не пьёт кофе.", "He does not drink coffee. | He doesn't drink coffee."),
                tl("Где она живёт?", "Where does she live?"),
                ch("___ your brother play football?", "Твой брат играет в футбол?", 1, "Do", "Does", "Is"),
                ch("I ___ like this song.", "Мне не нравится эта песня.", 0, "don't", "doesn't", "not"),
                ch("Does he ___ English?", "Он говорит по-английски?", 0, "speak", "speaks", "speaking"),
                ch("Where ___ you work?", "Где ты работаешь?", 0, "do", "does", "are"),
            )
        ),
        GrammarLesson(
            "there", "📍", "There is / there are: есть, находится", 1,
            "Когда мы говорим, что где-то что-то есть, используем there is (для одного) и there are (для многих): " +
                "There is a cafe near my house — «Рядом с моим домом есть кафе». " +
                "Вопрос: Is there…? Are there…? Отрицание: There isn't… / There aren't any…",
            listOf("There is a…" to "есть один", "There are some…" to "есть несколько", "Is there a…?" to "есть ли?", "There isn't a…" to "нет (одного)", "There aren't any…" to "нет (ни одного)"),
            listOf(
                ex("There is a pharmacy near here.", "Здесь рядом есть аптека."), ex("There are two bedrooms in my flat.", "В моей квартире две спальни."),
                ex("Is there a bus stop near here?", "Здесь рядом есть остановка?"), ex("There isn't any milk.", "Молока нет."),
                ex("Are there any questions?", "Есть вопросы?"), ex("There are a lot of people.", "Там много людей."),
            ),
            listOf(
                tr("There is a bank near here.", Q, "Is there a bank near here?"),
                tr("There are some apples.", NEG, "There are not any apples. | There aren't any apples."),
                tr("There is a park in our city.", Q, "Is there a park in our city?"),
                tr("There is a problem.", NEG, "There is not a problem. | There isn't a problem. | There is no problem. | There's no problem."),
                tl("В комнате есть окно.", "There is a window in the room."),
                tl("Здесь рядом есть магазин?", "Is there a shop near here? | Is there a store near here?"),
                tl("В парке много людей.", "There are a lot of people in the park. | There are many people in the park."),
                ch("There ___ three cats in the garden.", "В саду три кошки.", 1, "is", "are", "be"),
                ch("___ there a toilet here?", "Здесь есть туалет?", 0, "Is", "Are", "Do"),
                ch("There aren't ___ eggs.", "Яиц нет.", 2, "some", "a", "any"),
                ch("There ___ a good restaurant on this street.", "На этой улице есть хороший ресторан.", 0, "is", "are", "have"),
            )
        ),
        GrammarLesson(
            "can", "💪", "Can: могу, умею, можно", 1,
            "Can — «могу», «умею», «можно». После can глагол всегда без to и без -s: He can swim (не can swims). " +
                "Вопрос: Can you…? Отрицание: can't (cannot). " +
                "Can I…? — вежливо попросить разрешения, Can you…? — попросить о помощи.",
            listOf("I / he / they can swim" to "одинаково для всех", "Can you help me?" to "просьба", "Can I sit here?" to "разрешение", "I can't (cannot)" to "отрицание", "could" to "мог (в прошлом) / вежливо"),
            listOf(
                ex("I can drive.", "Я умею водить."), ex("She can't swim.", "Она не умеет плавать."),
                ex("Can you help me?", "Можешь мне помочь?"), ex("Can I pay by card?", "Можно оплатить картой?"),
                ex("He can speak three languages.", "Он говорит на трёх языках."), ex("We can't come tomorrow.", "Мы не сможем прийти завтра."),
            ),
            listOf(
                tr("You can cook.", Q, "Can you cook?"),
                tr("He can play the guitar.", NEG, "He cannot play the guitar. | He can't play the guitar."),
                tr("I can open the window.", "Спроси разрешения", "Can I open the window?"),
                tr("She can come.", NEG, "She cannot come. | She can't come."),
                tl("Я не умею плавать.", "I cannot swim. | I can't swim."),
                tl("Можно войти?", "Can I come in?"),
                tl("Ты можешь мне позвонить?", "Can you call me?"),
                ch("He can ___ very fast.", "Он умеет бегать очень быстро.", 0, "run", "runs", "to run"),
                ch("___ I ask a question?", "Можно задать вопрос?", 1, "Do", "Can", "Am"),
                ch("Sorry, I ___ hear you.", "Извини, я тебя не слышу.", 2, "don't can", "not can", "can't"),
                ch("Can you ___ me the salt?", "Можешь передать соль?", 0, "pass", "passing", "to pass"),
            )
        ),
        GrammarLesson(
            "pc", "⏳", "Present Continuous: прямо сейчас", 1,
            "Present Continuous — то, что происходит прямо сейчас: «я сейчас читаю». " +
                "Формула: am / is / are + глагол с -ing. I am reading. She is cooking. " +
                "Ещё так говорят о планах на ближайшее будущее: I'm meeting him tomorrow. " +
                "Подсказки: now, right now, at the moment, look!",
            listOf("I am working" to "я работаю (сейчас)", "he is reading" to "он читает", "they are sleeping" to "они спят", "Are you listening?" to "вопрос", "I'm not working" to "отрицание"),
            listOf(
                ex("I'm watching TV right now.", "Я сейчас смотрю телевизор."), ex("She is talking on the phone.", "Она говорит по телефону."),
                ex("What are you doing?", "Что ты делаешь?"), ex("It's raining.", "Идёт дождь."),
                ex("We aren't sleeping.", "Мы не спим."), ex("I'm meeting my friend tonight.", "Сегодня вечером я встречаюсь с другом."),
            ),
            listOf(
                tr("I read a book every day.", "Скажи, что делаешь это прямо сейчас", "I am reading a book now. | I'm reading a book now. | I am reading a book. | I'm reading a book. | I'm reading a book right now. | I am reading a book right now."),
                tr("She is cooking dinner.", Q, "Is she cooking dinner?"),
                tr("They are playing football.", NEG, "They are not playing football. | They aren't playing football. | They're not playing football."),
                tr("He is working.", Q, "Is he working?"),
                tl("Что ты делаешь?", "What are you doing?"),
                tl("Я жду автобус.", "I am waiting for the bus. | I'm waiting for the bus. | I'm waiting for a bus. | I am waiting for a bus."),
                tl("Они сейчас не работают.", "They are not working now. | They aren't working now. | They're not working now. | They aren't working right now. | They're not working right now."),
                ch("Look! The baby ___.", "Смотри! Малыш спит.", 1, "sleeps", "is sleeping", "sleep"),
                ch("I ___ to music right now.", "Я сейчас слушаю музыку.", 2, "listen", "listens", "am listening"),
                ch("___ you working now?", "Ты сейчас работаешь?", 1, "Do", "Are", "Is"),
                ch("She usually ___ tea, but now she's drinking coffee.", "Обычно она пьёт чай, но сейчас пьёт кофе.", 0, "drinks", "is drinking", "drinking"),
            )
        ),
        GrammarLesson(
            "past1", "⏮", "Past Simple: что было вчера", 1,
            "Past Simple — прошедшее время: «я работал», «она пришла». " +
                "У правильных глаголов добавляем -ed: work → worked, play → played. " +
                "У неправильных — своя форма, её надо запомнить: go → went, see → saw, buy → bought, have → had. " +
                "Форма одна для всех: I went, he went, they went. Подсказки: yesterday, last week, ago.",
            listOf("work → worked" to "правильный", "go → went" to "неправильный", "see → saw, have → had" to "неправильные", "buy → bought, get → got" to "неправильные", "was / were" to "был / были"),
            listOf(
                ex("I worked late yesterday.", "Вчера я работал допоздна."), ex("We went to the cinema.", "Мы ходили в кино."),
                ex("She bought a new phone.", "Она купила новый телефон."), ex("I saw him last week.", "Я видел его на прошлой неделе."),
                ex("They were at home.", "Они были дома."), ex("He came two hours ago.", "Он пришёл два часа назад."),
            ),
            listOf(
                tr("I watch TV.", "Скажи в прошедшем времени", "I watched TV."),
                tr("She goes to work.", "Скажи в прошедшем времени", "She went to work."),
                tr("We have lunch.", "Скажи в прошедшем времени", "We had lunch."),
                tr("He buys bread.", "Скажи в прошедшем времени", "He bought bread."),
                tl("Вчера я был дома.", "I was at home yesterday. | Yesterday I was at home."),
                tl("Мы видели этот фильм.", "We saw this film. | We saw this movie. | We saw that movie. | We saw that film."),
                tl("Она пришла в восемь.", "She came at eight."),
                ch("Yesterday I ___ to the shop.", "Вчера я ходил в магазин.", 1, "go", "went", "goed"),
                ch("They ___ very tired last night.", "Вчера вечером они очень устали.", 2, "was", "are", "were"),
                ch("He ___ me a message.", "Он прислал мне сообщение.", 0, "sent", "sended", "send"),
                ch("We ___ a lot of photos.", "Мы сделали много фото.", 1, "taked", "took", "take"),
            )
        ),
        GrammarLesson(
            "past2", "🔎", "Past Simple: вопросы и отрицания", 1,
            "Вопросы и отрицания в прошедшем времени строятся с помощником did — он один для всех. " +
                "Главное правило: после did глагол возвращается в начальную форму. Did you see? (не saw). I didn't go (не went). " +
                "Исключение — глагол to be: Were you at home? I wasn't there.",
            listOf("Did you see him?" to "вопрос", "Where did you go?" to "вопрос со словом", "I didn't see" to "отрицание", "Was he there? Were you…?" to "с to be", "I wasn't / we weren't" to "отрицание с to be"),
            listOf(
                ex("Did you sleep well?", "Ты хорошо спал?"), ex("Where did you go yesterday?", "Куда ты вчера ходил?"),
                ex("I didn't know.", "Я не знал."), ex("She didn't call me.", "Она мне не позвонила."),
                ex("Were you at work?", "Ты был на работе?"), ex("It wasn't my fault.", "Это была не моя вина."),
            ),
            listOf(
                tr("You saw the news.", Q, "Did you see the news?"),
                tr("He went home.", NEG, "He did not go home. | He didn't go home."),
                tr("They were happy.", Q, "Were they happy?"),
                tr("She bought a dress.", NEG, "She did not buy a dress. | She didn't buy a dress."),
                tl("Ты вчера работал?", "Did you work yesterday?"),
                tl("Я не видел его.", "I did not see him. | I didn't see him."),
                tl("Что ты купил?", "What did you buy?"),
                ch("Did you ___ my message?", "Ты получил моё сообщение?", 0, "get", "got", "gets"),
                ch("I ___ go out last night.", "Вчера вечером я никуда не ходил.", 1, "don't", "didn't", "wasn't"),
                ch("___ you at home yesterday?", "Ты был вчера дома?", 2, "Did", "Was", "Were"),
                ch("Where did he ___?", "Куда он пошёл?", 1, "went", "go", "goes"),
            )
        ),
        GrammarLesson(
            "future", "🔮", "Будущее: will и going to", 2,
            "Will — решение, которое принимаешь прямо сейчас, обещание или прогноз: I'll call you. It will rain. " +
                "Going to — то, что уже запланировал заранее: I'm going to visit my parents. " +
                "Отрицание: won't (will not) и I'm not going to. В речи will почти всегда сокращают: I'll, you'll, we'll.",
            listOf("I'll help you" to "решил сейчас / обещание", "It will be cold" to "прогноз", "I'm going to buy a car" to "план", "Will you…?" to "вопрос / просьба", "I won't (will not)" to "отрицание"),
            listOf(
                ex("I'll call you tomorrow.", "Я позвоню тебе завтра."), ex("I think it will rain.", "Думаю, будет дождь."),
                ex("We're going to move in summer.", "Летом мы переезжаем."), ex("Will you help me?", "Поможешь мне?"),
                ex("I won't tell anyone.", "Я никому не скажу."), ex("What are you going to do?", "Что ты собираешься делать?"),
            ),
            listOf(
                tr("I will be late.", NEG, "I will not be late. | I won't be late."),
                tr("She is going to study English.", Q, "Is she going to study English?"),
                tr("You will come.", Q, "Will you come?"),
                tr("We are going to sell the car.", NEG, "We are not going to sell the car. | We aren't going to sell the car. | We're not going to sell the car."),
                tl("Я помогу тебе.", "I will help you. | I'll help you."),
                tl("Я собираюсь купить новый телефон.", "I am going to buy a new phone. | I'm going to buy a new phone."),
                tl("Завтра будет холодно.", "It will be cold tomorrow. | Tomorrow it will be cold. | It'll be cold tomorrow."),
                ch("The phone is ringing. — I ___ answer it.", "Телефон звонит. — Я отвечу.", 0, "'ll", "am going to", "will to"),
                ch("I've decided. I ___ learn to drive.", "Я решил. Я собираюсь научиться водить.", 1, "will", "am going to", "going"),
                ch("Don't worry, I ___ tell anyone.", "Не волнуйся, я никому не скажу.", 2, "don't", "not will", "won't"),
                ch("___ you open the door, please?", "Откроешь дверь, пожалуйста?", 0, "Will", "Are", "Do"),
            )
        ),
        GrammarLesson(
            "pp", "✅", "Present Perfect: уже, ещё не, когда-нибудь", 2,
            "Present Perfect связывает прошлое с настоящим: важен результат сейчас, а не когда это было. " +
                "Формула: have / has + третья форма глагола: I have finished. She has lost her keys. " +
                "Подсказки: already (уже), yet (ещё / уже — в вопросах), just (только что), ever (когда-нибудь), never (никогда). " +
                "Если называем точное время в прошлом (yesterday, in 2020) — нужен Past Simple.",
            listOf("I have done (I've done)" to "сделал (результат есть)", "she has gone (she's gone)" to "ушла (её нет)", "Have you ever been…?" to "бывал ли когда-нибудь", "I haven't finished yet" to "ещё не закончил", "I've just arrived" to "только что приехал"),
            listOf(
                ex("I've lost my keys.", "Я потерял ключи."), ex("She has already left.", "Она уже ушла."),
                ex("Have you ever been to London?", "Ты когда-нибудь был в Лондоне?"), ex("I haven't eaten yet.", "Я ещё не ел."),
                ex("We've just arrived.", "Мы только что приехали."), ex("He has never seen the sea.", "Он никогда не видел моря."),
            ),
            listOf(
                tr("You have finished.", Q, "Have you finished?"),
                tr("She has called him.", NEG, "She has not called him. | She hasn't called him."),
                tr("I eat sushi.", "Скажи «я когда-нибудь…» — вопрос «ты когда-нибудь ел суши?»", "Have you ever eaten sushi?"),
                tr("They have seen this film.", NEG, "They have not seen this film. | They haven't seen this film."),
                tl("Я уже сделал это.", "I have already done it. | I've already done it. | I have already done this. | I've already done this."),
                tl("Ты когда-нибудь был в Турции?", "Have you ever been to Turkey?"),
                tl("Он ещё не пришёл.", "He has not come yet. | He hasn't come yet. | He has not arrived yet. | He hasn't arrived yet."),
                ch("I ___ my homework already.", "Я уже сделал домашку.", 1, "did", "have done", "has done"),
                ch("She ___ to Paris three times.", "Она была в Париже три раза.", 2, "have been", "was been", "has been"),
                ch("I saw him ___.", "Я видел его вчера.", 0, "yesterday", "already", "ever"),
                ch("Have you finished ___?", "Ты уже закончил?", 1, "already", "yet", "ever"),
            )
        ),
        GrammarLesson(
            "compare", "📏", "Сравнение: bigger, more interesting", 2,
            "Чтобы сравнить, к коротким прилагательным добавляем -er (больше) и -est (самый): big → bigger → the biggest. " +
                "К длинным — more и the most: interesting → more interesting → the most interesting. " +
                "После сравнения — than: My car is older than yours. " +
                "Особые формы: good → better → the best, bad → worse → the worst.",
            listOf("cheap → cheaper → the cheapest" to "короткие", "big → bigger" to "удвоение буквы", "easy → easier" to "y → ier", "expensive → more expensive" to "длинные", "good → better → best" to "исключения"),
            listOf(
                ex("This phone is cheaper.", "Этот телефон дешевле."), ex("Moscow is bigger than Omsk.", "Москва больше Омска."),
                ex("English is easier than Chinese.", "Английский легче китайского."), ex("This is the best day of my life.", "Это лучший день в моей жизни."),
                ex("Today is worse than yesterday.", "Сегодня хуже, чем вчера."), ex("It's the most expensive car here.", "Это самая дорогая машина здесь."),
            ),
            listOf(
                tr("My flat is small.", "Сравни с «yours» (твоя меньше? нет — моя меньше)", "My flat is smaller than yours."),
                tr("This film is interesting.", "Скажи «интереснее, чем тот»", "This film is more interesting than that one. | This film is more interesting than that film."),
                tr("He is a good driver.", "Скажи «лучший водитель»", "He is the best driver. | He's the best driver."),
                tr("Today is cold.", "Скажи «холоднее, чем вчера»", "Today is colder than yesterday. | It's colder than yesterday. | It is colder than yesterday."),
                tl("Моя машина старше твоей.", "My car is older than yours. | My car is older than your car."),
                tl("Это самый дешёвый магазин.", "This is the cheapest shop. | It's the cheapest shop. | This is the cheapest store. | It is the cheapest shop."),
                tl("Летом лучше, чем зимой.", "Summer is better than winter. | It's better in summer than in winter."),
                ch("This bag is ___ than that one.", "Эта сумка тяжелее той.", 1, "heavy", "heavier", "more heavy"),
                ch("It's the ___ city in the world.", "Это самый красивый город в мире.", 2, "beautifulest", "more beautiful", "most beautiful"),
                ch("My English is ___ than last year.", "Мой английский лучше, чем в прошлом году.", 0, "better", "gooder", "best"),
                ch("He is older ___ me.", "Он старше меня.", 1, "that", "than", "then"),
            )
        ),
        GrammarLesson(
            "modal", "⚖️", "Must, have to, should: надо и стоит", 2,
            "Must — «должен», когда это моё решение или строгое правило: I must stop smoking. " +
                "Have to — «приходится» из-за обстоятельств: I have to work on Saturday. В прошлом — had to. " +
                "Should — «стоит, следует» — совет: You should see a doctor. " +
                "Внимание: mustn't — «нельзя», а don't have to — «не обязательно».",
            listOf("I must go" to "должен (сам решил)", "I have to work" to "приходится", "You should rest" to "совет", "You mustn't smoke here" to "нельзя", "You don't have to come" to "не обязательно"),
            listOf(
                ex("I have to get up early.", "Мне приходится рано вставать."), ex("You should try it.", "Тебе стоит попробовать."),
                ex("You mustn't park here.", "Здесь нельзя парковаться."), ex("You don't have to pay.", "Платить не нужно."),
                ex("I had to wait an hour.", "Мне пришлось ждать час."), ex("Should I call him?", "Мне стоит ему позвонить?"),
            ),
            listOf(
                tr("You should go.", NEG, "You should not go. | You shouldn't go."),
                tr("I have to work today.", "Скажи в прошедшем: «вчера пришлось»", "I had to work yesterday."),
                tr("She has to wait.", Q, "Does she have to wait?"),
                tr("We should buy it.", Q, "Should we buy it?"),
                tl("Тебе стоит отдохнуть.", "You should rest. | You should have a rest. | You should relax."),
                tl("Мне приходится работать в субботу.", "I have to work on Saturday. | I have to work on Saturdays."),
                tl("Здесь нельзя курить.", "You must not smoke here. | You mustn't smoke here. | You can't smoke here."),
                ch("You look tired. You ___ go to bed.", "Ты выглядишь уставшим. Тебе стоит лечь спать.", 1, "have", "should", "must to"),
                ch("It's free. You ___ pay.", "Это бесплатно. Платить не нужно.", 2, "mustn't", "shouldn't", "don't have to"),
                ch("Yesterday I ___ work late.", "Вчера мне пришлось работать допоздна.", 0, "had to", "must", "have to"),
                ch("You ___ drive after drinking!", "Нельзя садиться за руль после выпивки!", 1, "don't have to", "mustn't", "not must"),
            )
        ),
        GrammarLesson(
            "articles", "🅰️", "Артикли a, an, the", 2,
            "A / an — «какой-то один», когда называем предмет впервые: I have a car. An — перед гласным звуком: an apple, an hour. " +
                "The — «тот самый», когда понятно, о чём речь: The car is old (та машина, о которой говорили). " +
                "Артикля нет: с именами, городами, странами (in Moscow), с множественным числом и неисчисляемыми в общем смысле: I like coffee.",
            listOf("a car, a job" to "один, любой", "an apple, an hour" to "перед гласным звуком", "the car (which we know)" to "конкретный", "the sun, the sea" to "единственный такой", "I like music" to "без артикля — в общем"),
            listOf(
                ex("I need a doctor.", "Мне нужен врач."), ex("She is an engineer.", "Она инженер."),
                ex("Can you close the door?", "Можешь закрыть дверь?"), ex("I love coffee.", "Я люблю кофе."),
                ex("I bought a jacket. The jacket is blue.", "Я купил куртку. Куртка синяя."), ex("Let's go to the sea.", "Поехали на море."),
            ),
            listOf(
                tr("I have car.", "Исправь ошибку", "I have a car."),
                tr("She is a actress.", "Исправь ошибку", "She is an actress."),
                tr("Can you open window?", "Исправь ошибку (окно в этой комнате)", "Can you open the window?"),
                tr("I like the music.", "Исправь: любишь музыку вообще", "I like music."),
                tl("Он водитель.", "He is a driver. | He's a driver."),
                tl("Солнце очень яркое.", "The sun is very bright."),
                tl("Мне нужен час.", "I need an hour."),
                ch("I'm reading ___ interesting book.", "Я читаю интересную книгу.", 1, "a", "an", "the"),
                ch("Where are ___ keys? I put them here.", "Где ключи? Я положил их сюда.", 2, "a", "-", "the"),
                ch("She lives in ___ London.", "Она живёт в Лондоне.", 0, "-", "the", "a"),
                ch("It's ___ best pizza in town.", "Это лучшая пицца в городе.", 2, "a", "-", "the"),
            )
        ),
        GrammarLesson(
            "some", "🧺", "Some / any, much / many", 2,
            "Some — «немного, несколько» в утверждениях: I have some money. " +
                "Any — в вопросах и отрицаниях: Do you have any questions? I don't have any time. " +
                "Many — со счётными (many people), much — с неисчисляемыми (much time), чаще в вопросах и отрицаниях. " +
                "A lot of подходит всегда: a lot of people, a lot of time.",
            listOf("some milk / some eggs" to "немного (утверждение)", "any questions?" to "вопрос", "not any / no" to "нет ни одного", "many friends" to "много (счётное)", "much money" to "много (несчётное)"),
            listOf(
                ex("I need some water.", "Мне нужно немного воды."), ex("Do you have any brothers?", "У тебя есть братья?"),
                ex("There isn't any bread.", "Хлеба нет."), ex("How many people were there?", "Сколько там было людей?"),
                ex("I don't have much time.", "У меня мало времени."), ex("She has a lot of friends.", "У неё много друзей."),
            ),
            listOf(
                tr("I have some money.", NEG, "I do not have any money. | I don't have any money. | I have no money."),
                tr("There are some chairs.", Q, "Are there any chairs?"),
                tr("We have some time.", NEG, "We do not have any time. | We don't have any time. | We have no time."),
                tr("He has many books.", "Спроси «сколько?»", "How many books does he have?"),
                tl("Хочешь немного чая?", "Do you want some tea? | Would you like some tea?"),
                tl("Сколько это стоит?", "How much is it? | How much does it cost? | How much is this?"),
                tl("У меня нет вопросов.", "I do not have any questions. | I don't have any questions. | I have no questions."),
                ch("How ___ money do you need?", "Сколько денег тебе нужно?", 0, "much", "many", "lot"),
                ch("I bought ___ apples.", "Я купил немного яблок.", 1, "any", "some", "much"),
                ch("Are there ___ messages for me?", "Мне есть сообщения?", 2, "some", "much", "any"),
                ch("There were too ___ people.", "Там было слишком много людей.", 1, "much", "many", "any"),
            )
        ),
        GrammarLesson(
            "prep", "🗓", "Предлоги in, on, at: время и место", 2,
            "Время: at — точное время (at 7 o'clock, at night), on — дни и даты (on Monday, on May 5), in — месяцы, годы, части дня (in July, in 2020, in the morning). " +
                "Место: at — точка (at work, at home, at the station), in — внутри (in the car, in Moscow), on — на поверхности (on the table, on the wall).",
            listOf("at 7, at night, at the weekend" to "точное время", "on Monday, on my birthday" to "день", "in May, in 2024, in the morning" to "месяц, год, часть дня", "at home, at work" to "место-точка", "in the room, on the table" to "внутри / на"),
            listOf(
                ex("The meeting is at three.", "Встреча в три."), ex("I was born in 1995.", "Я родился в 1995 году."),
                ex("See you on Friday.", "Увидимся в пятницу."), ex("He's at work.", "Он на работе."),
                ex("Your keys are on the table.", "Твои ключи на столе."), ex("I live in Omsk.", "Я живу в Омске."),
            ),
            listOf(
                tr("I get up in seven.", "Исправь ошибку", "I get up at seven."),
                tr("We met in Monday.", "Исправь ошибку", "We met on Monday."),
                tr("She lives at Moscow.", "Исправь ошибку", "She lives in Moscow."),
                tr("The phone is in the table.", "Исправь ошибку", "The phone is on the table."),
                tl("Увидимся в субботу.", "See you on Saturday."),
                tl("Я дома.", "I am at home. | I'm at home."),
                tl("Летом я в отпуске.", "I am on holiday in summer. | I'm on vacation in summer. | In summer I'm on holiday. | I'm on holiday in the summer."),
                ch("My birthday is ___ June.", "Мой день рождения в июне.", 0, "in", "on", "at"),
                ch("I'll call you ___ the evening.", "Я позвоню тебе вечером.", 2, "at", "on", "in"),
                ch("The bus leaves ___ 6:30.", "Автобус уходит в 6:30.", 1, "in", "at", "on"),
                ch("There's a picture ___ the wall.", "На стене картина.", 1, "in", "on", "at"),
            )
        ),
        GrammarLesson(
            "cond", "🔀", "Условия: if I had… I would…", 3,
            "Реальное условие (First conditional): If it rains, I'll stay at home — «Если пойдёт дождь, останусь дома». После if — настоящее время, во второй части — will. " +
                "Нереальное (Second conditional): If I had money, I would buy a house — «Если бы у меня были деньги, я бы купил дом». После if — прошедшее, во второй части — would. " +
                "Обрати внимание: «если бы я был» — If I were (для всех лиц).",
            listOf("If it rains, I'll stay home" to "реально", "If I had time, I'd travel" to "нереально сейчас", "If I were you, I'd…" to "на твоём месте", "I'd = I would" to "сокращение", "What would you do if…?" to "вопрос"),
            listOf(
                ex("If you're tired, we'll go home.", "Если ты устал, пойдём домой."), ex("If I had more time, I'd learn Spanish.", "Будь у меня больше времени, я бы выучил испанский."),
                ex("If I were you, I'd call her.", "На твоём месте я бы ей позвонил."), ex("What would you do if you won a million?", "Что бы ты сделал, если бы выиграл миллион?"),
                ex("If it's sunny tomorrow, let's go to the beach.", "Если завтра будет солнечно, пойдём на пляж."), ex("I wouldn't do that if I were you.", "Я бы на твоём месте так не делал."),
            ),
            listOf(
                tr("I don't have a car, so I don't drive to work.", "Скажи через «If I had…»", "If I had a car, I would drive to work. | If I had a car, I'd drive to work."),
                tr("It may rain. Then we'll stay at home.", "Скажи через «If…»", "If it rains, we will stay at home. | If it rains, we'll stay at home. | If it rains, we'll stay home. | If it rains, we will stay home."),
                tr("I'm not you. I would say no.", "Скажи «На твоём месте…»", "If I were you, I would say no. | If I were you, I'd say no."),
                tr("You would help me.", Q, "Would you help me?"),
                tl("Если я опоздаю, я тебе позвоню.", "If I am late, I will call you. | If I'm late, I'll call you. | If I'm late, I will call you."),
                tl("Если бы я был богат, я бы путешествовал.", "If I were rich, I would travel. | If I were rich, I'd travel. | If I was rich, I'd travel."),
                tl("Что бы ты сделал на моём месте?", "What would you do if you were me? | What would you do in my place?"),
                ch("If I ___ more money, I'd buy a new car.", "Будь у меня больше денег, я бы купил новую машину.", 1, "have", "had", "will have"),
                ch("If you heat ice, it ___.", "Если нагреть лёд, он тает.", 0, "melts", "would melt", "melted"),
                ch("If I ___ you, I'd take the job.", "На твоём месте я бы согласился на работу.", 2, "am", "be", "were"),
                ch("If she calls, I ___ tell her.", "Если она позвонит, я ей скажу.", 1, "would", "will", "had"),
            )
        ),
        GrammarLesson(
            "passive", "🏗", "Страдательный залог: it was built", 3,
            "Passive voice — когда важно, что сделали, а не кто: The house was built in 1990 — «Дом построен в 1990». " +
                "Формула: to be в нужном времени + третья форма глагола. " +
                "Сейчас: is made, are sold. Прошлое: was built, were stolen. Будущее: will be done. Present Perfect: has been fixed. " +
                "Если важно, кто сделал, — by: The film was directed by…",
            listOf("It is made in China" to "настоящее", "It was built in 1990" to "прошедшее", "It will be done tomorrow" to "будущее", "My car has been repaired" to "уже сделано", "…by Tolstoy" to "кем"),
            listOf(
                ex("English is spoken all over the world.", "На английском говорят по всему миру."), ex("My phone was stolen.", "У меня украли телефон."),
                ex("The order will be delivered tomorrow.", "Заказ доставят завтра."), ex("The road has been closed.", "Дорогу закрыли."),
                ex("This song was written in 1975.", "Эта песня написана в 1975 году."), ex("We were invited to the party.", "Нас пригласили на вечеринку."),
            ),
            listOf(
                tr("Someone stole my bike.", "Скажи через passive: «My bike…»", "My bike was stolen."),
                tr("They will fix the car tomorrow.", "Скажи через passive: «The car…»", "The car will be fixed tomorrow."),
                tr("People speak English here.", "Скажи через passive: «English…»", "English is spoken here."),
                tr("Someone has opened the window.", "Скажи через passive: «The window…»", "The window has been opened."),
                tl("Этот дом построили в 2010 году.", "This house was built in 2010."),
                tl("Меня пригласили на собеседование.", "I was invited to an interview. | I was invited to a job interview. | I've been invited to an interview."),
                tl("Ужин будет готов через час.", "Dinner will be ready in an hour."),
                ch("The letter ___ yesterday.", "Письмо отправили вчера.", 1, "sent", "was sent", "is sent"),
                ch("These phones ___ in Korea.", "Эти телефоны производят в Корее.", 0, "are made", "make", "were make"),
                ch("Your car ___ already been washed.", "Твою машину уже помыли.", 2, "is", "was", "has"),
                ch("The meeting will ___ tomorrow.", "Встречу проведут завтра.", 1, "held", "be held", "being held"),
            )
        ),
        GrammarLesson(
            "reported", "🗣", "Косвенная речь: he said that…", 3,
            "Когда пересказываем чужие слова, время «сдвигается» на шаг в прошлое: " +
                "«I'm tired» → He said (that) he was tired. «I will call» → She said she would call. «I can't come» → He said he couldn't come. " +
                "Вопросы пересказываем без инверсии: «Where do you live?» → She asked where I lived. «Are you OK?» → He asked if I was OK. " +
                "Просьбы — через to: «Please wait» → He asked me to wait.",
            listOf("am / is → was" to "сдвиг времени", "will → would, can → could" to "модальные", "did / have done → had done" to "прошлое", "asked if…" to "общий вопрос", "told me to…" to "просьба / приказ"),
            listOf(
                ex("She said she was busy.", "Она сказала, что занята."), ex("He told me he would be late.", "Он сказал мне, что опоздает."),
                ex("They asked where we lived.", "Они спросили, где мы живём."), ex("I asked if he could help.", "Я спросил, может ли он помочь."),
                ex("The doctor told me to rest.", "Врач велел мне отдыхать."), ex("She said she had lost her keys.", "Она сказала, что потеряла ключи."),
            ),
            listOf(
                tr("\"I am hungry,\" he said.", "Перескажи: He said…", "He said he was hungry. | He said that he was hungry."),
                tr("\"I will call you,\" she said.", "Перескажи: She said…", "She said she would call me. | She said that she would call me."),
                tr("\"Do you like coffee?\" he asked.", "Перескажи: He asked…", "He asked if I liked coffee. | He asked whether I liked coffee."),
                tr("\"Please close the door,\" she said to me.", "Перескажи: She asked me…", "She asked me to close the door."),
                tl("Он сказал, что не может прийти.", "He said he could not come. | He said he couldn't come. | He said that he couldn't come."),
                tl("Она спросила, где я работаю.", "She asked where I worked. | She asked me where I worked."),
                tl("Начальник велел мне подождать.", "The boss told me to wait. | My boss told me to wait."),
                ch("He said he ___ tired.", "Он сказал, что устал.", 1, "is", "was", "be"),
                ch("She asked me where I ___.", "Она спросила, где я живу.", 0, "lived", "do live", "did live"),
                ch("They told us ___ quiet.", "Они велели нам вести себя тихо.", 2, "be", "being", "to be"),
                ch("I asked ___ he was OK.", "Я спросил, всё ли у него в порядке.", 1, "that", "if", "what"),
            )
        ),
    )

    /** Подходит ли ответ: сравниваем слова без регистра, знаков и с раскрытием сокращений. */
    fun accepts(answer: String, expected: String): Boolean {
        val given = canonWords(answer)
        if (given.isEmpty()) return false
        return expected.split("|").any { canonWords(it.trim()) == given }
    }

    fun firstAnswer(expected: String): String = expected.split("|").first().trim()
}
