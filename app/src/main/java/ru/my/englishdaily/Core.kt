package ru.my.englishdaily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 500+ самых частых слов английского — стартовый этап для начинающих.
 * Пока они не выучены, остальное приложение на уровне «Начинающий» закрыто.
 */
object Core {
    private fun g(group: String, list: String): List<Item> =
        list.trim().split("\n").map { it.trim() }.filter { it.isNotEmpty() }.map { line ->
            val (en, ru) = line.split("—", limit = 2).map { it.trim() }
            Item("core:$group:$en", en, ru, group)
        }

    val items: List<Item> by lazy {
        g("👤 Люди и местоимения", """
            I — я
            you — ты, вы
            he — он
            she — она
            it — оно, это
            we — мы
            they — они
            me — меня, мне
            my — мой
            your — твой, ваш
            his — его
            her — её
            our — наш
            their — их
            this — этот, это
            that — тот, то
            man — мужчина
            woman — женщина
            person — человек
            people — люди
            child — ребёнок
            children — дети
            kid — ребёнок (разг.)
            boy — мальчик
            girl — девочка
            guy — парень
            friend — друг
            family — семья
            mother — мама
            father — папа
            parents — родители
            brother — брат
            sister — сестра
            son — сын
            daughter — дочь
            husband — муж
            wife — жена
            baby — малыш
            neighbour — сосед
            boss — начальник
            name — имя
        """) + g("⚡ Главные глаголы", """
            be — быть
            have — иметь
            do — делать
            say — сказать
            go — идти, ехать
            get — получать
            make — делать, создавать
            know — знать
            think — думать
            take — брать
            see — видеть
            come — приходить
            want — хотеть
            look — смотреть
            use — использовать
            find — находить
            give — давать
            tell — рассказывать
            work — работать
            call — звонить, звать
            try — пытаться
            ask — спрашивать
            need — нуждаться
            feel — чувствовать
            become — становиться
            leave — уходить, оставлять
            put — класть
            mean — значить
            keep — хранить, продолжать
            let — позволять
            begin — начинать
            seem — казаться
            help — помогать
            talk — разговаривать
            turn — поворачивать
            start — начинать
            show — показывать
            hear — слышать
            play — играть
            run — бегать
            move — двигать, переезжать
            like — нравиться
            live — жить
            believe — верить
            hold — держать
            bring — приносить
            happen — случаться
            write — писать
            sit — сидеть
            stand — стоять
            lose — терять
            pay — платить
            meet — встречать
            learn — учить, узнавать
            change — менять
            understand — понимать
            watch — смотреть (наблюдать)
            follow — следовать
            stop — останавливать
            speak — говорить
            read — читать
            spend — тратить
            open — открывать
            close — закрывать
            walk — гулять, идти пешком
            win — выигрывать
            remember — помнить
            forget — забывать
            love — любить
            buy — покупать
            sell — продавать
            wait — ждать
            send — отправлять
            stay — оставаться
            fall — падать
            decide — решать
            carry — нести
            break — ломать
            drive — водить машину
        """) + g("🏠 Глаголы на каждый день", """
            eat — есть, кушать
            drink — пить
            sleep — спать
            cook — готовить
            wash — мыть
            clean — убирать
            wear — носить (одежду)
            sing — петь
            dance — танцевать
            swim — плавать
            fly — летать
            travel — путешествовать
            visit — посещать
            answer — отвечать
            explain — объяснять
            hope — надеяться
            worry — волноваться
            laugh — смеяться
            cry — плакать
            smile — улыбаться
            enjoy — наслаждаться
            hate — ненавидеть
            choose — выбирать
            agree — соглашаться
            finish — заканчивать
            catch — ловить
            throw — бросать
            fix — чинить
            check — проверять
            count — считать
            miss — скучать, пропускать
            return — возвращать(ся)
            arrive — прибывать
            wake up — просыпаться
            get up — вставать
            sit down — садиться
            come back — возвращаться
            put on — надевать
            take off — снимать
            turn on — включать
            turn off — выключать
            look for — искать
            find out — узнавать
            hurry — спешить
            rest — отдыхать
            study — учиться
            teach — учить (кого-то)
            draw — рисовать
            hide — прятать
            kiss — целовать
            hug — обнимать
            invite — приглашать
            order — заказывать
            share — делиться
            save — сохранять, экономить
            shout — кричать
            touch — трогать
            cost — стоить
            feed — кормить
            fight — драться
            lie — лежать
            climb — лезть, подниматься
            jump — прыгать
            knock — стучать
            park — парковать
            rent — снимать (жильё)
            push — толкать
            pull — тянуть
            build — строить
            grow — расти
        """) + g("⏰ Время", """
            time — время
            year — год
            month — месяц
            week — неделя
            day — день
            hour — час
            minute — минута
            morning — утро
            afternoon — день (после обеда)
            evening — вечер
            night — ночь
            today — сегодня
            tomorrow — завтра
            yesterday — вчера
            now — сейчас
            then — потом, тогда
            soon — скоро
            later — позже
            early — рано
            late — поздно
            weekend — выходные
            holiday — праздник, отпуск
            birthday — день рождения
            Monday — понедельник
            Friday — пятница
            Sunday — воскресенье
            summer — лето
            winter — зима
            spring — весна
            autumn — осень
        """) + g("🍎 Дом и еда", """
            house — дом (здание)
            home — дом (где живёшь)
            room — комната
            kitchen — кухня
            bathroom — ванная
            bedroom — спальня
            door — дверь
            window — окно
            table — стол
            chair — стул
            bed — кровать
            floor — пол, этаж
            wall — стена
            key — ключ
            light — свет
            garden — сад
            toilet — туалет
            shower — душ
            water — вода
            food — еда
            bread — хлеб
            milk — молоко
            coffee — кофе
            tea — чай
            meat — мясо
            fish — рыба
            chicken — курица
            egg — яйцо
            apple — яблоко
            fruit — фрукты
            vegetables — овощи
            potato — картошка
            cheese — сыр
            soup — суп
            sugar — сахар
            salt — соль
            breakfast — завтрак
            lunch — обед
            dinner — ужин
            glass — стакан
            cup — чашка
            plate — тарелка
            bottle — бутылка
        """) + g("🏙 Город и дорога", """
            city — город
            town — городок
            village — деревня
            country — страна
            world — мир
            place — место
            street — улица
            road — дорога
            way — путь, способ
            map — карта
            car — машина
            bus — автобус
            train — поезд
            plane — самолёт
            taxi — такси
            bike — велосипед
            ticket — билет
            station — вокзал, станция
            airport — аэропорт
            hotel — гостиница
            restaurant — ресторан
            cafe — кафе
            shop — магазин
            market — рынок
            bank — банк
            park — парк
            hospital — больница
            pharmacy — аптека
            school — школа
            office — офис
            address — адрес
            left — левый, налево
            right — правый, направо
            straight — прямо
            near — рядом, близко
            far — далеко
        """) + g("💼 Работа, деньги, вещи", """
            job — работа (должность)
            money — деньги
            price — цена
            cash — наличные
            card — карта
            bill — счёт
            teacher — учитель
            student — студент
            doctor — врач
            medicine — лекарство
            phone — телефон
            computer — компьютер
            internet — интернет
            message — сообщение
            email — электронное письмо
            letter — письмо, буква
            book — книга
            word — слово
            language — язык
            lesson — урок
            question — вопрос
            answer — ответ
            problem — проблема
            idea — идея
            thing — вещь
            plan — план
            mistake — ошибка
            meeting — встреча
            number — номер, число
            picture — картинка
            photo — фото
            bag — сумка
            box — коробка
            gift — подарок
            wallet — кошелёк
            umbrella — зонт
            clock — часы (настенные)
            watch — часы (наручные)
            clothes — одежда
            shirt — рубашка
            dress — платье
            shoes — обувь
            jacket — куртка
            hat — шапка, шляпа
        """) + g("🌳 Мир вокруг, тело, жизнь", """
            life — жизнь
            story — история
            news — новости
            music — музыка
            song — песня
            film — фильм
            game — игра
            sport — спорт
            team — команда
            ball — мяч
            party — вечеринка
            weather — погода
            rain — дождь
            snow — снег
            sun — солнце
            wind — ветер
            sky — небо
            sea — море
            river — река
            lake — озеро
            mountain — гора
            beach — пляж
            tree — дерево
            flower — цветок
            animal — животное
            dog — собака
            cat — кошка
            bird — птица
            head — голова
            face — лицо
            eye — глаз
            ear — ухо
            nose — нос
            mouth — рот
            tooth — зуб
            hair — волосы
            hand — рука (кисть)
            arm — рука
            leg — нога
            foot — ступня
            back — спина
            heart — сердце
            body — тело
            health — здоровье
            pain — боль
            love — любовь
            help — помощь
            colour — цвет
            size — размер
            part — часть
            side — сторона
            end — конец
            reason — причина
            group — группа
        """) + g("🎨 Прилагательные", """
            good — хороший
            bad — плохой
            big — большой
            small — маленький
            new — новый
            old — старый
            young — молодой
            long — длинный
            short — короткий
            high — высокий
            low — низкий
            great — отличный
            important — важный
            different — разный
            same — тот же самый
            easy — лёгкий
            difficult — трудный
            hard — трудный, твёрдый
            fast — быстрый
            slow — медленный
            hot — горячий, жаркий
            cold — холодный
            warm — тёплый
            cool — прохладный, классный
            happy — счастливый
            sad — грустный
            tired — уставший
            hungry — голодный
            thirsty — хочется пить
            angry — злой
            afraid — испуганный
            sick — больной
            beautiful — красивый
            pretty — симпатичный
            nice — приятный, милый
            kind — добрый
            funny — смешной
            interesting — интересный
            boring — скучный
            busy — занятой
            free — свободный, бесплатный
            cheap — дешёвый
            expensive — дорогой
            rich — богатый
            poor — бедный
            full — полный
            empty — пустой
            clean — чистый
            dirty — грязный
            ready — готовый
            sure — уверенный
            true — правдивый, верный
            real — настоящий
            quiet — тихий
            loud — громкий
            dark — тёмный
            heavy — тяжёлый
            strong — сильный
            weak — слабый
            safe — безопасный
            dangerous — опасный
            possible — возможный
            delicious — вкусный
            favourite — любимый
            wrong — неправильный
            last — последний
            next — следующий
            first — первый
            best — лучший
            better — лучше
            other — другой
            white — белый
            black — чёрный
            red — красный
            blue — синий
            green — зелёный
            yellow — жёлтый
        """) + g("🔗 Маленькие, но важные", """
            and — и
            but — но
            or — или
            because — потому что
            if — если
            so — поэтому, так
            with — с
            without — без
            for — для
            from — из, от
            to — к, в
            in — в
            on — на
            at — в, у
            under — под
            before — до, перед
            after — после
            about — о, про
            between — между
            here — здесь
            there — там
            always — всегда
            often — часто
            sometimes — иногда
            never — никогда
            usually — обычно
            again — снова
            already — уже
            still — всё ещё
            just — только что, просто
            very — очень
            really — действительно
            too — тоже, слишком
            also — также
            only — только
            maybe — может быть
            together — вместе
            almost — почти
            enough — достаточно
            more — больше
            less — меньше
            much — много (неисчисл.)
            many — много (исчисл.)
            a lot of — много
            some — немного, несколько
            any — любой, какой-нибудь
            all — все, всё
            every — каждый
            nothing — ничего
            something — что-то
            everything — всё
            nobody — никто
            somebody — кто-то
            everyone — все (люди)
            what — что
            who — кто
            where — где
            when — когда
            why — почему
            how — как
            which — который, какой
            yes — да
            no — нет
            please — пожалуйста
            thank you — спасибо
            sorry — извини
            hello — привет
            goodbye — до свидания
        """) + g("🔢 Числа", """
            one — один
            two — два
            three — три
            four — четыре
            five — пять
            six — шесть
            seven — семь
            eight — восемь
            nine — девять
            ten — десять
            eleven — одиннадцать
            twelve — двенадцать
            twenty — двадцать
            thirty — тридцать
            fifty — пятьдесят
            hundred — сто
            thousand — тысяча
            million — миллион
        """)
    }

    /** Сколько нужно выучить, чтобы открылось остальное (90%). */
    val goal: Int get() = (items.size * 0.9).toInt()

    fun learned(store: ProgressStore): Int = items.count { store.box(it.id) >= 1 }

    /** Для начинающих приложение открывается после базовых слов. */
    fun locked(store: ProgressStore): Boolean =
        store.level == Level.BEGINNER && !store.coreSkip && learned(store) < goal
}

/** Главный экран начинающего, пока не выучены базовые слова. */
@Composable
fun CoreStage(
    store: ProgressStore,
    onTrain: (Mode) -> Unit,
    onGame: (Game) -> Unit,
    onBrowse: () -> Unit,
) {
    val learned = remember { Core.learned(store) }
    val total = Core.items.size
    val goal = Core.goal
    val groups = remember { Core.items.groupBy { it.group } }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Grads.Build.brush)
            .padding(20.dp)
    ) {
        Text("Этап 0 · Базовые слова", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.labelLarge)
        Text("$total самых нужных слов", color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(6.dp))
        Text(
            "С ними уже можно объясниться — пусть не идеально, но понятно. Выучи $goal из $total, и откроется всё остальное.",
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(14.dp))
        PillProgress(learned.toFloat() / goal, Modifier.fillMaxWidth(), Grads.Book, height = 10.dp)
        Spacer(Modifier.height(6.dp))
        Text("Выучено: $learned из $goal", color = Color.White, style = MaterialTheme.typography.titleMedium)
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GradientButton("⚡ Учить", { onTrain(Mode.RECALL) }, Modifier.weight(1f), grad = Grads.Build)
        GradientButton("🎧 Аудио", { onTrain(Mode.AUDIO) }, Modifier.weight(1f), grad = Grads.Audio)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SoftButton("👂 На слух", { onTrain(Mode.LISTEN) }, Modifier.weight(1f), height = 48.dp)
        SoftButton("🎙 Вслух", { onTrain(Mode.SPEAK) }, Modifier.weight(1f), height = 48.dp)
    }
    Hint("Совет: 15–20 минут в день. Знакомые слова отмечай «✓ Уже знаю» — так быстрее дойдёшь до нового.")

    SectionTitle("🎮 Закрепи на скорость", null)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Game.entries.forEach { game ->
            SoftCard(Modifier.weight(1f).height(110.dp), onClick = { onGame(game) }, radius = 22.dp) {
                IconTile(game.emoji, game.grad, 36.dp)
                Spacer(Modifier.height(8.dp))
                Text(game.title, style = MaterialTheme.typography.labelLarge, maxLines = 2)
            }
        }
    }

    SectionTitle("Темы", null)
    groups.forEach { (group, list) ->
        val done = list.count { store.box(it.id) >= 1 }
        SoftCard(Modifier.fillMaxWidth(), onClick = onBrowse) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text("$done/${list.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(6.dp))
            PillProgress(done.toFloat() / list.size, Modifier.fillMaxWidth(), Grads.Build, height = 6.dp)
        }
    }

    SectionTitle("🔒 Откроется после базовых слов", null)
    listOf(
        "🗺 Курс A1 → C1 · 80 уроков", "📐 Грамматика шаг за шагом", "🎬 Диалоги и ролевые игры",
        "📚 Истории", "🤖 Собеседник-ИИ", "🎤 Монолог",
    ).forEach { t ->
        SoftCard(Modifier.fillMaxWidth().alpha(0.5f)) {
            Text(t, style = MaterialTheme.typography.titleMedium)
        }
    }
}
