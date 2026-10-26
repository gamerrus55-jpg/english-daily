package ru.my.englishdaily

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/** Картинка-ассоциация к слову, как в детских карточках. */
object Pictures {
    private val map: Map<String, String> = mapOf(
        // Люди
        "i" to "🙋", "you" to "🫵", "he" to "👨", "she" to "👩", "we" to "👨‍👩‍👧", "they" to "👥", "me" to "🙋",
        "man" to "👨", "woman" to "👩", "person" to "🧍", "people" to "👥", "child" to "🧒", "children" to "👧🧒",
        "kid" to "🧒", "boy" to "👦", "girl" to "👧", "guy" to "🧔", "friend" to "🤝", "family" to "👨‍👩‍👧‍👦",
        "mother" to "👩‍🍼", "father" to "👨‍👧", "parents" to "👫", "brother" to "👦", "sister" to "👧", "son" to "👦",
        "daughter" to "👧", "husband" to "🤵", "wife" to "👰", "baby" to "👶", "neighbour" to "🏘️", "boss" to "👔",
        "name" to "🏷️", "teacher" to "👩‍🏫", "student" to "🧑‍🎓", "doctor" to "🧑‍⚕️",
        // Глаголы
        "be" to "🧘", "have" to "🤲", "do" to "🛠️", "say" to "🗣️", "go" to "🚶", "get" to "📥", "make" to "🔨",
        "know" to "🧠", "think" to "🤔", "take" to "✋", "see" to "👀", "come" to "🏃‍♂️", "want" to "🤩", "look" to "👀",
        "use" to "🔧", "find" to "🔍", "give" to "🎁", "tell" to "💬", "work" to "💼", "call" to "📞", "try" to "💪",
        "ask" to "❓", "need" to "🙏", "feel" to "💓", "leave" to "🚪", "put" to "📦", "help" to "🆘", "talk" to "🗨️",
        "turn" to "↪️", "start" to "🏁", "show" to "👉", "hear" to "👂", "play" to "🎮", "run" to "🏃", "move" to "🚚",
        "like" to "👍", "live" to "🏡", "hold" to "🫴", "bring" to "🛍️", "write" to "✍️", "sit" to "🪑", "stand" to "🧍",
        "lose" to "😩", "pay" to "💳", "meet" to "🤝", "learn" to "📚", "change" to "🔄", "understand" to "💡",
        "watch" to "📺", "stop" to "🛑", "speak" to "🗣️", "read" to "📖", "spend" to "💸", "open" to "🔓",
        "close" to "🔒", "walk" to "🚶", "win" to "🏆", "remember" to "🧠", "forget" to "🤷", "love" to "❤️",
        "buy" to "🛒", "sell" to "🏷️", "wait" to "⏳", "send" to "📤", "stay" to "🏠", "fall" to "🍂", "decide" to "⚖️",
        "carry" to "🎒", "break" to "💥", "drive" to "🚗", "eat" to "🍽️", "drink" to "🥤", "sleep" to "😴",
        "cook" to "👩‍🍳", "wash" to "🧼", "clean" to "🧹", "wear" to "👕", "sing" to "🎤", "dance" to "💃", "swim" to "🏊",
        "fly" to "✈️", "travel" to "🧳", "visit" to "🏛️", "answer" to "💬", "explain" to "👨‍🏫", "hope" to "🤞",
        "worry" to "😟", "laugh" to "😂", "cry" to "😢", "smile" to "😊", "enjoy" to "😋", "hate" to "😠",
        "choose" to "🤔", "agree" to "🤝", "finish" to "🏁", "catch" to "🎣", "throw" to "🤾", "fix" to "🔧",
        "check" to "✅", "count" to "🔢", "miss" to "💭", "return" to "🔙", "arrive" to "🛬", "wake up" to "⏰",
        "get up" to "🛏️", "sit down" to "🪑", "come back" to "🔙", "put on" to "🧥", "take off" to "👟",
        "turn on" to "💡", "turn off" to "🔌", "look for" to "🔎", "find out" to "🕵️", "hurry" to "🏃‍♀️", "rest" to "🛋️",
        "study" to "📝", "teach" to "👩‍🏫", "draw" to "🎨", "hide" to "🙈", "kiss" to "💋", "hug" to "🤗",
        "invite" to "💌", "order" to "🧾", "share" to "🫱", "save" to "💾", "shout" to "📢", "touch" to "👆",
        "cost" to "💰", "feed" to "🍼", "fight" to "🥊", "lie" to "🛌", "climb" to "🧗", "jump" to "🦘", "knock" to "🚪",
        "park" to "🅿️", "rent" to "🔑", "push" to "👐", "pull" to "🪢", "build" to "🏗️", "grow" to "🌱", "begin" to "🏁",
        // Время
        "time" to "⏰", "year" to "📅", "month" to "🗓️", "week" to "📆", "day" to "☀️", "hour" to "🕐",
        "minute" to "⏱️", "morning" to "🌅", "afternoon" to "🌤️", "evening" to "🌇", "night" to "🌙", "today" to "📍",
        "tomorrow" to "➡️", "yesterday" to "⬅️", "now" to "⏺️", "soon" to "⏳", "late" to "🐌", "early" to "🐓",
        "weekend" to "🎉", "holiday" to "🏖️", "birthday" to "🎂", "summer" to "☀️", "winter" to "❄️",
        "spring" to "🌸", "autumn" to "🍁",
        // Дом и еда
        "house" to "🏠", "home" to "🏡", "room" to "🚪", "kitchen" to "🍳", "bathroom" to "🛁", "bedroom" to "🛏️",
        "door" to "🚪", "window" to "🪟", "table" to "🍽️", "chair" to "🪑", "bed" to "🛏️", "floor" to "🟫", "wall" to "🧱",
        "key" to "🔑", "light" to "💡", "garden" to "🌷", "toilet" to "🚽", "shower" to "🚿", "water" to "💧",
        "food" to "🍲", "bread" to "🍞", "milk" to "🥛", "coffee" to "☕", "tea" to "🍵", "meat" to "🥩", "fish" to "🐟",
        "chicken" to "🍗", "egg" to "🥚", "apple" to "🍎", "fruit" to "🍇", "vegetables" to "🥕", "potato" to "🥔",
        "cheese" to "🧀", "soup" to "🍜", "sugar" to "🍬", "salt" to "🧂", "breakfast" to "🥞", "lunch" to "🥪",
        "dinner" to "🍝", "glass" to "🥛", "cup" to "☕", "plate" to "🍽️", "bottle" to "🍾",
        // Город
        "city" to "🏙️", "town" to "🏘️", "village" to "🛖", "country" to "🗺️", "world" to "🌍", "place" to "📍",
        "street" to "🛣️", "road" to "🛤️", "way" to "🧭", "map" to "🗺️", "car" to "🚗", "bus" to "🚌", "train" to "🚆",
        "plane" to "✈️", "taxi" to "🚕", "bike" to "🚲", "ticket" to "🎫", "station" to "🚉", "airport" to "🛫",
        "hotel" to "🏨", "restaurant" to "🍽️", "cafe" to "☕", "shop" to "🏪", "market" to "🧺", "bank" to "🏦",
        "hospital" to "🏥", "pharmacy" to "💊", "school" to "🏫", "office" to "🏢", "address" to "📮",
        "left" to "⬅️", "right" to "➡️", "straight" to "⬆️", "near" to "📍", "far" to "🔭",
        // Вещи, работа, деньги
        "job" to "💼", "money" to "💵", "price" to "🏷️", "cash" to "💵", "card" to "💳", "bill" to "🧾",
        "medicine" to "💊", "phone" to "📱", "computer" to "💻", "internet" to "🌐", "message" to "💬", "email" to "📧",
        "letter" to "✉️", "book" to "📕", "word" to "🔤", "language" to "🗣️", "lesson" to "📓", "question" to "❓",
        "problem" to "⚠️", "idea" to "💡", "thing" to "📦", "plan" to "📋", "mistake" to "❌", "meeting" to "🤝",
        "number" to "🔢", "picture" to "🖼️", "photo" to "📷", "bag" to "👜", "box" to "📦", "gift" to "🎁",
        "wallet" to "👛", "umbrella" to "☂️", "clock" to "🕰️", "clothes" to "👚", "shirt" to "👔", "dress" to "👗",
        "shoes" to "👟", "jacket" to "🧥", "hat" to "🎩",
        // Мир, тело
        "life" to "🌱", "story" to "📖", "news" to "📰", "music" to "🎵", "song" to "🎶", "film" to "🎬", "game" to "🎲",
        "sport" to "🏅", "team" to "👥", "ball" to "⚽", "party" to "🥳", "weather" to "🌦️", "rain" to "🌧️",
        "snow" to "❄️", "sun" to "☀️", "wind" to "🌬️", "sky" to "🌌", "sea" to "🌊", "river" to "🏞️", "lake" to "🛶",
        "mountain" to "⛰️", "beach" to "🏖️", "tree" to "🌳", "flower" to "🌸", "animal" to "🐾", "dog" to "🐶",
        "cat" to "🐱", "bird" to "🐦", "head" to "🗣️", "face" to "🙂", "eye" to "👁️", "ear" to "👂", "nose" to "👃",
        "mouth" to "👄", "tooth" to "🦷", "hair" to "💇", "hand" to "✋", "arm" to "💪", "leg" to "🦵", "foot" to "🦶",
        "back" to "🔙", "heart" to "❤️", "body" to "🧍", "health" to "🍏", "pain" to "🤕", "colour" to "🎨",
        "size" to "📏", "end" to "🔚",
        // Прилагательные
        "good" to "👍", "bad" to "👎", "big" to "🐘", "small" to "🐭", "new" to "✨", "old" to "👴", "young" to "🧒",
        "long" to "🐍", "short" to "📏", "high" to "🏔️", "low" to "⬇️", "great" to "🌟", "important" to "❗",
        "different" to "🔀", "same" to "🟰", "easy" to "😌", "difficult" to "🧗", "hard" to "🪨", "fast" to "🐆",
        "slow" to "🐢", "hot" to "🔥", "cold" to "🥶", "warm" to "🧣", "cool" to "😎", "happy" to "😄", "sad" to "😢",
        "tired" to "🥱", "hungry" to "🤤", "thirsty" to "🥵", "angry" to "😡", "afraid" to "😱", "sick" to "🤒",
        "beautiful" to "🌹", "pretty" to "💐", "nice" to "😊", "kind" to "🤗", "funny" to "🤣", "interesting" to "🧐",
        "boring" to "😐", "busy" to "📋", "free" to "🕊️", "cheap" to "🪙", "expensive" to "💎", "rich" to "🤑",
        "poor" to "🥺", "full" to "🈵", "empty" to "🫙", "dirty" to "💩", "ready" to "✅", "sure" to "👌",
        "true" to "✔️", "real" to "💯", "quiet" to "🤫", "loud" to "📣", "dark" to "🌑", "heavy" to "🏋️",
        "strong" to "💪", "weak" to "🥀", "safe" to "🛡️", "dangerous" to "☠️", "delicious" to "😋",
        "favourite" to "⭐", "wrong" to "❌", "last" to "🔚", "next" to "⏭️", "first" to "🥇", "best" to "🏆",
        "white" to "⚪", "black" to "⚫", "red" to "🔴", "blue" to "🔵", "green" to "🟢", "yellow" to "🟡",
        // Маленькие слова
        "yes" to "✅", "no" to "🚫", "please" to "🙏", "thank you" to "🙏", "sorry" to "😔", "hello" to "👋",
        "goodbye" to "👋", "with" to "🤝", "without" to "🚫", "under" to "⬇️", "between" to "↔️", "here" to "📍",
        "there" to "👉", "always" to "♾️", "never" to "🚫", "together" to "👫", "what" to "❓", "who" to "🧑❓",
        "where" to "📍❓", "when" to "🕐❓", "why" to "🤷", "how" to "🛠️❓",
        // Числа
        "one" to "1️⃣", "two" to "2️⃣", "three" to "3️⃣", "four" to "4️⃣", "five" to "5️⃣", "six" to "6️⃣",
        "seven" to "7️⃣", "eight" to "8️⃣", "nine" to "9️⃣", "ten" to "🔟", "hundred" to "💯",
    )

    fun forWord(en: String): String? = map[en.lowercase().trim()]
}

/** Подсказка-ассоциация от ИИ (созвучие или яркая картинка в голове). */
object Mnemonics {
    private val cache = mutableMapOf<String, String>()

    private val system = """
You help a Russian speaker memorize an English word with a vivid association, like in children's flashcards.
Write ONE short, funny, memorable association in Russian (1–2 sentences):
use a Russian word or phrase that SOUNDS like the English word and connect it to the meaning in a vivid little picture.
Example for "beach" (пляж): «Бич» — на пляже загорает бич (бродяга) в шляпе.
Only the association, nothing else.
""".trimIndent()

    fun cached(word: String) = cache[word.lowercase()]

    suspend fun make(store: ProgressStore, en: String, ru: String): Result<String> {
        cached(en)?.let { return Result.success(it) }
        return Ai.ask(store, system, listOf(Ai.Msg("user", "$en ($ru)")), maxTokens = 150)
            .map { it.trim().trim('"', '«', '»').also { t -> cache[en.lowercase()] = t } }
    }
}

/** Большая картинка слова для карточки + кнопка ассоциации. */
@Composable
fun PictureCard(item: Item, store: ProgressStore, showMnemonic: Boolean = true, showPicture: Boolean = true) {
    val pic = remember(item.id) { Pictures.forWord(item.en) }
    val scope = rememberCoroutineScope()
    var mnemonic by remember(item.id) { mutableStateOf(Mnemonics.cached(item.en)) }
    var loading by remember(item.id) { mutableStateOf(false) }
    val isWord = item.en.split(" ").size <= 2

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (pic != null && showPicture) {
            Box(
                Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(pic, fontSize = 60.sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(10.dp))
        }
        if (showMnemonic && isWord) {
            val m = mnemonic
            when {
                m != null -> Text("💡 $m", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                store.aiReady -> Pill(if (loading) "Придумываю…" else "💡 Ассоциация, чтобы запомнить", {
                    if (loading) return@Pill
                    loading = true
                    scope.launch {
                        Mnemonics.make(store, item.en, item.ru).onSuccess { mnemonic = it }
                        loading = false
                    }
                })
            }
        }
    }
}
