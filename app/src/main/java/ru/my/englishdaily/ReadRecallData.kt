package ru.my.englishdaily

/**
 * «Читай и вспоминай»: отрывок с выделенными словами. Разметка: [слово=перевод].
 * Слова не выбираются из вариантов — их нужно вспомнить и вписать.
 */
sealed interface RrSeg {
    data class Plain(val text: String) : RrSeg
    data class Target(val idx: Int, val word: String, val ru: String, val cardId: String) : RrSeg
}

class RrPassage(
    val id: String,
    val lang: Lang,
    val level: Int,
    val emoji: String,
    val title: String,
    markup: String,
) {
    val segments: List<RrSeg> = parseRr(markup, lang)
    val targets: List<RrSeg.Target> = segments.filterIsInstance<RrSeg.Target>()
    val plain: String = segments.joinToString("") { if (it is RrSeg.Plain) it.text else (it as RrSeg.Target).word }
}

private fun parseRr(markup: String, lang: Lang): List<RrSeg> {
    val out = mutableListOf<RrSeg>()
    var last = 0
    var n = 0
    for (m in Regex("\\[([^\\]=]+)=([^\\]]+)\\]").findAll(markup)) {
        if (m.range.first > last) out += RrSeg.Plain(markup.substring(last, m.range.first))
        val word = m.groupValues[1]
        out += RrSeg.Target(n++, word, m.groupValues[2], "rr:${lang.code}:${word.lowercase()}")
        last = m.range.last + 1
    }
    if (last < markup.length) out += RrSeg.Plain(markup.substring(last))
    return out
}

object ReadRecall {
    val levelNames = mapOf(1 to "🌱 Лёгкие (A1–A2)", 2 to "🌿 Средние (B1)", 3 to "🌳 Сложные (B2)")

    val all: List<RrPassage> = listOf(
        // ---------- Английский, уровень 1 ----------
        RrPassage("en1", Lang.EN, 1, "☕", "A morning at home",
            "Anna wakes up at seven. She goes to the [kitchen=кухня] and makes [coffee=кофе]. " +
                "Then she eats [breakfast=завтрак] with her brother. At eight she takes her [bag=сумка] and leaves for work."),
        RrPassage("en2", Lang.EN, 1, "🛒", "At the shop",
            "Tom needs [milk=молоко] and [bread=хлеб]. The shop is [near=рядом] his house. " +
                "He puts everything in a [basket=корзина] and pays the [cashier=кассир]."),
        RrPassage("en3", Lang.EN, 1, "🌧", "A rainy day",
            "Today it is [raining=идёт дождь]. Mia takes her [umbrella=зонт] and her [coat=пальто]. " +
                "The streets are [wet=мокрые] and the bus is late. She waits ten [minutes=минут]."),
        RrPassage("en4", Lang.EN, 1, "👨‍👩‍👧", "My family",
            "I have a small [family=семья]. My [mother=мама] is a teacher. My [father=папа] drives a bus. " +
                "On [Sundays=по воскресеньям] we eat together."),

        // ---------- Английский, уровень 2 ----------
        RrPassage("en5", Lang.EN, 2, "📱", "A lost phone",
            "Last night Ben left his phone on the [train=поезд]. He [realized=понял] it ten minutes later. " +
                "He called the station, but nobody had [found=нашёл] it yet. " +
                "Luckily, a kind woman [handed it in=сдала его] the next morning."),
        RrPassage("en6", Lang.EN, 2, "💼", "Job interview",
            "Sara had an [interview=собеседование] for a new job. She was [nervous=нервничала], but she had " +
                "[prepared=подготовилась] well. The manager asked about her [experience=опыт], and she answered calmly. " +
                "A week later they [offered=предложили] her the position."),
        RrPassage("en7", Lang.EN, 2, "✈️", "The delayed flight",
            "The flight to Rome was [delayed=задержан] by three hours. Passengers waited at the [gate=выход на посадку] " +
                "and drank too much coffee. At last the [announcement=объявление] came, and everyone [rushed=бросился] to board."),
        RrPassage("en8", Lang.EN, 2, "🏠", "A new neighbour",
            "A new [neighbour=сосед] moved in next door last week. He [introduced=представился] himself and brought a cake. " +
                "We talked for an hour and found out we both [enjoy=любим] cycling. " +
                "Now we plan to ride together on [weekends=выходных]."),

        // ---------- Английский, уровень 3 ----------
        RrPassage("en9", Lang.EN, 3, "💻", "Working from home",
            "Working from home sounds [convenient=удобно], but it has its [drawbacks=недостатки]. " +
                "Without a clear [routine=распорядок], days blend together. Many people struggle to [switch off=отключиться] " +
                "after work. Some try to [set boundaries=установить границы] by using a separate room."),
        RrPassage("en10", Lang.EN, 3, "🏙", "The housing market",
            "Rents in the city have [soared=резко выросли] over the past decade. Young professionals often [settle for=соглашаются на] " +
                "tiny flats far from the centre. Landlords [take advantage=пользуются] of the shortage by demanding large deposits. " +
                "Some people [cut back on=урезают] other expenses just to keep a roof over their heads."),
        RrPassage("en11", Lang.EN, 3, "🔄", "A change of career",
            "After ten years in banking, Lena decided to [retrain=переучиться] as a nurse. Her friends thought she was " +
                "[reckless=безрассудна], but she had [weighed up=взвесила] the risks carefully. " +
                "The first year was [gruelling=изматывающим], yet she never [regretted=жалела] it."),
        RrPassage("en12", Lang.EN, 3, "⛈", "A stormy trip",
            "The ferry [set off=отплыл] despite the [forecast=прогноз]. Halfway across, the waves grew so [fierce=яростными] " +
                "that several passengers felt [seasick=дурно от качки]. The captain stayed [composed=хладнокровным] " +
                "and steered the ship safely back to the [harbour=гавань]."),

        // ---------- Сербский, уровень 1 ----------
        RrPassage("sr1", Lang.SR, 1, "☕", "Jutro",
            "Ana se budi u sedam. Ide u [kuhinju=кухню] i pravi [kafu=кофе]. Zatim doručkuje sa [bratom=братом]. " +
                "U osam uzima [torbu=сумку] i ide na posao."),
        RrPassage("sr2", Lang.SR, 1, "🛒", "U prodavnici",
            "Marko treba [mleko=молоко] i [hleb=хлеб]. [Prodavnica=магазин] je blizu njegove kuće. " +
                "Stavlja sve u [korpu=корзину] i plaća na [kasi=кассе]."),
        RrPassage("sr3", Lang.SR, 1, "🌧", "Kiša",
            "Danas [pada kiša=идёт дождь]. Mia uzima [kišobran=зонт] i [jaknu=куртку]. " +
                "[Ulice=улицы] su mokre, a autobus kasni. Čeka deset [minuta=минут]."),

        // ---------- Сербский, уровень 2 ----------
        RrPassage("sr4", Lang.SR, 2, "📱", "Izgubljen telefon",
            "Sinoć je Petar ostavio telefon u [vozu=поезде]. [Shvatio=Понял] je to tek posle deset minuta. " +
                "Pozvao je stanicu, ali [niko=никто] ga nije našao. Srećom, ljubazna žena ga je [predala=сдала] sledećeg jutra."),
        RrPassage("sr5", Lang.SR, 2, "💼", "Razgovor za posao",
            "Sara je imala [razgovor za posao=собеседование] u novoj firmi. Bila je [nervozna=нервная], ali se dobro " +
                "[pripremila=подготовилась]. Direktor ju je pitao o [iskustvu=опыте], a ona je mirno odgovarala. " +
                "Nedelju dana kasnije su joj [ponudili=предложили] posao."),
        RrPassage("sr6", Lang.SR, 2, "✈️", "Kašnjenje leta",
            "Let za Rim je [kasnio=задерживался] tri sata. Putnici su čekali na [izlazu=выходе] i pili previše kafe. " +
                "Konačno je stiglo [obaveštenje=объявление], pa su svi [požurili=поспешили] na ukrcavanje."),

        // ---------- Сербский, уровень 3 ----------
        RrPassage("sr7", Lang.SR, 3, "💻", "Rad od kuće",
            "Rad od kuće zvuči [zgodno=удобно], ali ima i [mane=недостатки]. Bez jasnog [rasporeda=распорядка] dani se mešaju. " +
                "Mnogima je teško da se [isključe=отключиться] posle posla. " +
                "Neki pokušavaju da [postave granice=установить границы] tako što rade u posebnoj sobi."),
        RrPassage("sr8", Lang.SR, 3, "🏙", "Tržište stanova",
            "[Kirije=Аренда] u gradu su [skočile=подскочили] u poslednjih deset godina. Mladi često [pristaju=соглашаются] " +
                "na sitne stanove daleko od centra. [Stanodavci=Арендодатели] koriste nestašicu i traže velike kaucije. " +
                "Neki ljudi [štede=экономят] na svemu drugom da bi imali krov nad glavom."),
        RrPassage("sr9", Lang.SR, 3, "🔄", "Promena karijere",
            "Posle deset godina u banci, Lena je odlučila da se [prekvalifikuje=переучилась] za medicinsku sestru. " +
                "Prijatelji su mislili da je [nepromišljena=опрометчива], ali je pažljivo [odmerila=взвесила] rizike. " +
                "Prva godina je bila [iscrpljujuća=изматывающая], ali nikada nije [požalila=пожалела]."),
    )

    fun forLang(l: Lang): List<RrPassage> = all.filter { it.lang == l }
}
