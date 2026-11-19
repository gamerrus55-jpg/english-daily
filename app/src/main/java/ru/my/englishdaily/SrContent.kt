package ru.my.englishdaily

/**
 * Сербский (латиница, экавица). В Item поле `en` хранит сербский текст — так работают все режимы приложения.
 * Чтобы добавить своё — допиши строку "srpski" to "русский".
 * Тексты написаны без носителя языка: стоит показать их сербоговорящему знакомому.
 */
object SrContent {

    private class Pattern(val title: String, val sr: String, val fills: List<Pair<String, String>>)

    private val patterns = listOf(
        Pattern("Я хочу…", "Želim da {}.", listOf(
            "jedem" to "Я хочу есть.",
            "se odmorim" to "Я хочу отдохнуть.",
            "idem kući" to "Я хочу домой.",
            "razgovaram s tobom" to "Я хочу с тобой поговорить.",
            "probam" to "Я хочу попробовать.",
            "pogledam film" to "Я хочу посмотреть фильм.",
            "vidim more" to "Я хочу увидеть море.",
            "promenim posao" to "Я хочу сменить работу.",
            "putujem više" to "Я хочу больше путешествовать.",
            "kupim novi auto" to "Я хочу купить новую машину.",
            "razumem srpski" to "Я хочу понимать сербский.",
            "ostanem ovde" to "Я хочу остаться здесь.",
        )),
        Pattern("Я не хочу…", "Ne želim da {}.", listOf(
            "čekam" to "Я не хочу ждать.",
            "kasnim" to "Я не хочу опаздывать.",
            "radim danas" to "Я не хочу сегодня работать.",
            "pričam o tome" to "Я не хочу об этом говорить.",
            "ustajem rano" to "Я не хочу рано вставать.",
            "trošim novac" to "Я не хочу тратить деньги.",
            "se svađam" to "Я не хочу спорить.",
            "idem tamo sam" to "Я не хочу идти туда один.",
            "izgubim posao" to "Я не хочу потерять работу.",
            "vozim noću" to "Я не хочу ездить ночью.",
        )),
        Pattern("Мне нужно…", "Treba da {}.", listOf(
            "idem" to "Мне нужно идти.",
            "popravim auto" to "Мне нужно починить машину.",
            "odem kod lekara" to "Мне нужно сходить к врачу.",
            "se naspavam" to "Мне нужно выспаться.",
            "razmislim" to "Мне нужно подумать.",
            "platim račune" to "Мне нужно оплатить счета.",
            "nađem posao" to "Мне нужно найти работу.",
            "napunim telefon" to "Мне нужно зарядить телефон.",
            "kupim hranu" to "Мне нужно купить еды.",
            "pozovem prijatelja" to "Мне нужно позвонить другу.",
        )),
        Pattern("Мне надо…", "Moram da {}.", listOf(
            "idem" to "Мне надо идти.",
            "radim" to "Мне надо работать.",
            "ustanem rano" to "Мне надо рано встать.",
            "završim ovo" to "Мне надо это закончить.",
            "platim" to "Мне надо заплатить.",
            "se vratim" to "Мне надо вернуться.",
            "pozovem majku" to "Мне надо позвонить маме.",
            "kupim kartu" to "Мне надо купить билет.",
        )),
        Pattern("Можно мне…?", "Mogu li da {}?", listOf(
            "sednem" to "Можно мне сесть?",
            "uđem" to "Можно мне войти?",
            "pitam nešto" to "Можно мне кое-что спросить?",
            "platim karticom" to "Можно заплатить картой?",
            "upotrebim telefon" to "Можно воспользоваться телефоном?",
            "ostavim torbu ovde" to "Можно оставить сумку здесь?",
            "vam pomognem" to "Могу я вам помочь?",
            "dobijem račun" to "Можно мне счёт?",
        )),
        Pattern("Не могли бы вы…?", "Možete li da {}?", listOf(
            "ponovite" to "Не могли бы вы повторить?",
            "pričate sporije" to "Не могли бы вы говорить медленнее?",
            "mi pomognete" to "Не могли бы вы мне помочь?",
            "mi pokažete put" to "Не могли бы вы показать мне дорогу?",
            "napišete to" to "Не могли бы вы это написать?",
            "me sačekate" to "Не могли бы вы меня подождать?",
            "mi date račun" to "Не могли бы вы дать мне счёт?",
        )),
        Pattern("У меня есть…", "Imam {}.", listOf(
            "pitanje" to "У меня есть вопрос.",
            "sastanak u tri" to "У меня встреча в три.",
            "dva brata" to "У меня два брата.",
            "puno posla" to "У меня много работы.",
            "vremena" to "У меня есть время.",
            "glavobolju" to "У меня болит голова.",
            "rezervaciju" to "У меня есть бронь.",
            "problem" to "У меня проблема.",
            "novi telefon" to "У меня новый телефон.",
        )),
        Pattern("У меня нет…", "Nemam {}.", listOf(
            "vremena" to "У меня нет времени.",
            "novca" to "У меня нет денег.",
            "pojma" to "Понятия не имею.",
            "sitnine" to "У меня нет мелочи.",
            "interneta" to "У меня нет интернета.",
            "snage" to "У меня нет сил.",
            "baterije" to "У меня нет заряда.",
        )),
        Pattern("Где…?", "Gde je {}?", listOf(
            "toalet" to "Где туалет?",
            "autobuska stanica" to "Где автовокзал?",
            "apoteka" to "Где аптека?",
            "najbliži bankomat" to "Где ближайший банкомат?",
            "izlaz" to "Где выход?",
            "moj telefon" to "Где мой телефон?",
            "centar grada" to "Где центр города?",
            "restoran" to "Где ресторан?",
        )),
        Pattern("Сколько стоит…?", "Koliko košta {}?", listOf(
            "ovo" to "Сколько это стоит?",
            "karta do Beograda" to "Сколько стоит билет до Белграда?",
            "kafa" to "Сколько стоит кофе?",
            "soba za jednu noć" to "Сколько стоит номер на одну ночь?",
            "ovaj hleb" to "Сколько стоит этот хлеб?",
            "taksi do aerodroma" to "Сколько стоит такси до аэропорта?",
        )),
        Pattern("Я…", "Ja sam {}.", listOf(
            "iz Kazahstana" to "Я из Казахстана.",
            "ovde prvi put" to "Я здесь впервые.",
            "na putu" to "Я в пути.",
            "kod kuće" to "Я дома.",
            "na poslu" to "Я на работе.",
            "u gradu" to "Я в городе.",
            "tu" to "Я здесь.",
        )),
        Pattern("Я люблю…", "Volim {}.", listOf(
            "kafu" to "Я люблю кофе.",
            "da putujem" to "Я люблю путешествовать.",
            "more" to "Я люблю море.",
            "planine" to "Я люблю горы.",
            "da kuvam" to "Я люблю готовить.",
            "srpsku hranu" to "Я люблю сербскую еду.",
            "da šetam" to "Я люблю гулять.",
            "muziku" to "Я люблю музыку.",
        )),
        Pattern("Завтра я…", "Sutra ću {}.", listOf(
            "ići na posao" to "Завтра я пойду на работу.",
            "te pozvati" to "Завтра я тебе позвоню.",
            "doći ranije" to "Завтра я приду пораньше.",
            "kupiti kartu" to "Завтра я куплю билет.",
            "se odmarati" to "Завтра я буду отдыхать.",
            "biti kod kuće" to "Завтра я буду дома.",
            "završiti posao" to "Завтра я закончу работу.",
        )),
        Pattern("Давай…", "Hajde da {}.", listOf(
            "idemo" to "Давай пойдём.",
            "popijemo kafu" to "Давай выпьем кофе.",
            "se vidimo sutra" to "Давай увидимся завтра.",
            "pojedemo nešto" to "Давай что-нибудь съедим.",
            "probamo" to "Давай попробуем.",
            "počnemo" to "Давай начнём.",
            "prošetamo" to "Давай погуляем.",
        )),
    )

    val course: List<Item> = patterns.flatMapIndexed { n, p ->
        val group = "${n + 1}. ${p.title}"
        p.fills.map { (fill, ru) ->
            val sr = p.sr.replace("{}", fill)
            Item("sr:c:$sr", sr, ru, group)
        }
    }

    val patternCount: Int get() = patterns.size

    val phrases: List<Item> = listOf(
        // Приветствия
        "Dobro jutro!" to "Доброе утро!",
        "Dobar dan!" to "Добрый день!",
        "Dobro veče!" to "Добрый вечер!",
        "Laku noć!" to "Спокойной ночи!",
        "Zdravo!" to "Привет!",
        "Ćao!" to "Привет! / Пока!",
        "Kako si?" to "Как дела? (на «ты»)",
        "Kako ste?" to "Как поживаете? (на «вы»)",
        "Dobro sam, hvala." to "Хорошо, спасибо.",
        "A ti?" to "А ты?",
        "Drago mi je." to "Приятно познакомиться.",
        "Kako se zoveš?" to "Как тебя зовут?",
        "Odakle si?" to "Откуда ты?",
        "Iz Kazahstana sam." to "Я из Казахстана.",
        "Hvala puno." to "Большое спасибо.",
        "Molim." to "Пожалуйста.",
        "Izvinite." to "Извините.",
        "Žao mi je." to "Мне жаль.",
        "Nema problema." to "Без проблем.",
        "Vidimo se sutra." to "Увидимся завтра.",
        "Doviđenja!" to "До свидания!",
        "Srećno!" to "Удачи!",
        "Prijatno!" to "Приятного аппетита!",
        "Živeli!" to "За здоровье!",
        "Dobro došli!" to "Добро пожаловать!",

        // Не понимаю
        "Ne razumem." to "Я не понимаю.",
        "Razumem." to "Я понимаю.",
        "Ne znam." to "Я не знаю.",
        "Možete li da ponovite, molim vas?" to "Повторите, пожалуйста.",
        "Govorite sporije, molim vas." to "Говорите медленнее, пожалуйста.",
        "Govorim malo srpski." to "Я немного говорю по-сербски.",
        "Šta to znači?" to "Что это значит?",
        "Kako se kaže ovo na srpskom?" to "Как это сказать по-сербски?",
        "Govorite li ruski?" to "Вы говорите по-русски?",
        "Govorite li engleski?" to "Вы говорите по-английски?",

        // Кафе и еда
        "Jednu kafu, molim." to "Один кофе, пожалуйста.",
        "Jednu vodu, molim." to "Одну воду, пожалуйста.",
        "Imate li meni?" to "У вас есть меню?",
        "Želim čorbu." to "Я хочу суп.",
        "Bez mesa, molim." to "Без мяса, пожалуйста.",
        "Da li je ovo ljuto?" to "Это острое?",
        "Vrlo je ukusno." to "Очень вкусно.",
        "Račun, molim." to "Счёт, пожалуйста.",
        "Može li kartica?" to "Можно картой?",
        "Mogu li da sednem ovde?" to "Можно здесь сесть?",

        // Магазин
        "Koliko košta ovo?" to "Сколько это стоит?",
        "To je skupo." to "Это дорого.",
        "Imate li nešto jeftinije?" to "У вас есть что-нибудь подешевле?",
        "Uzeću ovo." to "Я возьму это.",
        "Samo gledam, hvala." to "Я только смотрю, спасибо.",
        "Gde mogu da platim?" to "Где можно заплатить?",
        "Treba mi kesa." to "Мне нужен пакет.",
        "Imate li veću veličinu?" to "У вас есть размер побольше?",

        // Дорога и транспорт
        "Gde je autobuska stanica?" to "Где автовокзал?",
        "Kako da stignem do centra?" to "Как добраться до центра?",
        "Idite pravo." to "Идите прямо.",
        "Skrenite levo." to "Поверните налево.",
        "Skrenite desno." to "Поверните направо.",
        "Jednu kartu do Novog Sada, molim." to "Один билет до Нови-Сада, пожалуйста.",
        "Kada polazi sledeći autobus?" to "Когда отправляется следующий автобус?",
        "Sa kog perona polazi voz?" to "С какой платформы отправляется поезд?",
        "Pozovite taksi, molim vas." to "Вызовите такси, пожалуйста.",
        "Stanite ovde, molim." to "Остановитесь здесь, пожалуйста.",

        // Жильё
        "Imam rezervaciju." to "У меня есть бронь.",
        "Koliko košta soba za jednu noć?" to "Сколько стоит номер на одну ночь?",
        "Da li je doručak uključen?" to "Завтрак включён?",
        "Koja je lozinka za Wi-Fi?" to "Какой пароль от Wi-Fi?",
        "Gde je toalet?" to "Где туалет?",

        // Здоровье и помощь
        "Treba mi lekar." to "Мне нужен врач.",
        "Boli me glava." to "У меня болит голова.",
        "Boli me stomak." to "У меня болит живот.",
        "Gde je apoteka?" to "Где аптека?",
        "Pozovite hitnu pomoć!" to "Вызовите скорую!",
        "Ukraden mi je novčanik." to "У меня украли кошелёк.",
        "Pomozite mi, molim vas!" to "Помогите мне, пожалуйста!",

        // Разговор
        "Koliko je sati?" to "Который час?",
        "Šta ima?" to "Как дела? (разговорное)",
        "Kasnim deset minuta." to "Я опаздываю на десять минут.",
        "Vidimo se u pet." to "Увидимся в пять.",
        "Danas je lep dan." to "Сегодня хороший день.",
        "Hladno je." to "Холодно.",
        "Vruće je." to "Жарко.",
        "Naravno." to "Конечно.",
        "Možda." to "Может быть.",
        "Nema veze." to "Ничего страшного.",
        "U redu." to "Хорошо. / Ладно.",
        "Polako!" to "Потихоньку! / Не спеши!",
        "Hvala, isto." to "Спасибо, вам тоже.",
    ).map { (sr, ru) -> Item("sr:p:$sr", sr, ru) }

    val words: List<Item> = listOf(
        // Числа
        "jedan" to "один", "dva" to "два", "tri" to "три", "četiri" to "четыре", "pet" to "пять",
        "šest" to "шесть", "sedam" to "семь", "osam" to "восемь", "devet" to "девять", "deset" to "десять",
        "sto" to "сто", "hiljada" to "тысяча",
        // Дни и время
        "ponedeljak" to "понедельник", "utorak" to "вторник", "sreda" to "среда", "četvrtak" to "четверг",
        "petak" to "пятница", "subota" to "суббота", "nedelja" to "воскресенье",
        "danas" to "сегодня", "sutra" to "завтра", "juče" to "вчера", "sada" to "сейчас",
        "uvek" to "всегда", "nikad" to "никогда", "ujutru" to "утром", "uveče" to "вечером",
        // Основа
        "da" to "да", "ne" to "нет", "molim" to "пожалуйста", "hvala" to "спасибо",
        "ko" to "кто", "šta" to "что", "gde" to "где", "kada" to "когда", "zašto" to "почему",
        "kako" to "как", "koliko" to "сколько", "koji" to "какой",
        // Семья
        "majka" to "мама", "otac" to "отец", "brat" to "брат", "sestra" to "сестра", "dete" to "ребёнок",
        "prijatelj" to "друг", "porodica" to "семья", "muž" to "муж", "žena" to "жена; женщина",
        // Еда
        "hleb" to "хлеб", "voda" to "вода", "mleko" to "молоко", "kafa" to "кофе", "čaj" to "чай",
        "sir" to "сыр", "meso" to "мясо", "riba" to "рыба", "jaje" to "яйцо", "jabuka" to "яблоко",
        "krompir" to "картофель", "paradajz" to "помидор", "sok" to "сок", "pivo" to "пиво",
        "rakija" to "ракия (фруктовая водка)", "čorba" to "суп", "salata" to "салат",
        "sladoled" to "мороженое", "so" to "соль", "šećer" to "сахар",
        // Места
        "kuća" to "дом", "stan" to "квартира", "grad" to "город", "ulica" to "улица", "škola" to "школа",
        "posao" to "работа", "prodavnica" to "магазин", "pijaca" to "рынок", "restoran" to "ресторан",
        "bolnica" to "больница", "apoteka" to "аптека", "aerodrom" to "аэропорт", "stanica" to "станция; остановка",
        "most" to "мост", "reka" to "река", "more" to "море", "planina" to "гора", "park" to "парк",
        "banka" to "банк", "pošta" to "почта",
        // Глаголы
        "ići" to "идти", "doći" to "прийти", "videti" to "видеть", "znati" to "знать", "hteti" to "хотеть",
        "moći" to "мочь", "raditi" to "работать; делать", "govoriti" to "говорить", "jesti" to "есть (кушать)",
        "piti" to "пить", "spavati" to "спать", "kupiti" to "купить", "platiti" to "заплатить",
        "čekati" to "ждать", "voleti" to "любить", "živeti" to "жить", "razumeti" to "понимать",
        "učiti" to "учить; учиться", "pitati" to "спросить", "dati" to "дать", "uzeti" to "взять",
        "otvoriti" to "открыть", "zatvoriti" to "закрыть", "naći" to "найти", "pisati" to "писать",
        "čitati" to "читать",
        // Прилагательные
        "dobar" to "хороший", "loš" to "плохой", "lep" to "красивый", "skup" to "дорогой", "jeftin" to "дешёвый",
        "brz" to "быстрый", "spor" to "медленный", "topao" to "тёплый", "hladan" to "холодный",
        "nov" to "новый", "star" to "старый", "lak" to "лёгкий", "težak" to "тяжёлый",
        "ukusan" to "вкусный", "veliki" to "большой", "mali" to "маленький",
    ).map { (sr, ru) -> Item("sr:w:$sr", sr, ru) }
}
