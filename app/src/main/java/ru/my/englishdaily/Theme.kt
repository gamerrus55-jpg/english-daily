package ru.my.englishdaily

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Палитра и градиенты ----------

@Immutable
data class Grad(val a: Color, val b: Color) {
    val brush: Brush get() = Brush.linearGradient(listOf(a, b))
}

private fun grad(a: Long, b: Long) = Grad(Color(a), Color(b))

object Grads {
    val Hero = grad(0xFF4F46E5, 0xFF9333EA)
    val Recall = grad(0xFFF59E0B, 0xFFF97316)
    val Audio = grad(0xFF6366F1, 0xFF8B5CF6)
    val Build = grad(0xFF14B8A6, 0xFF10B981)
    val Speak = grad(0xFFEC4899, 0xFFF43F5E)
    val Repeat = grad(0xFF0EA5E9, 0xFF3B82F6)
    val Listen = grad(0xFF06B6D4, 0xFF6366F1)
    val Dictation = grad(0xFF8B5CF6, 0xFFD946EF)
    val Dialogs = grad(0xFFF43F5E, 0xFFFB923C)
    val Roleplay = grad(0xFF22C55E, 0xFF14B8A6)
    val Book = grad(0xFF64748B, 0xFF94A3B8)
}

val Good = Color(0xFF16A34A)
val Bad = Color(0xFFE11D48)

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondaryContainer = Color(0xFFEEF0FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    background = Color(0xFFF4F5FA),
    onBackground = Color(0xFF111327),
    surface = Color.White,
    onSurface = Color(0xFF111327),
    surfaceVariant = Color(0xFFECEEF6),
    onSurfaceVariant = Color(0xFF5B6077),
    outlineVariant = Color(0xFFE2E4EE),
    error = Bad,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9AA2FF),
    onPrimary = Color(0xFF14163A),
    primaryContainer = Color(0xFF2E3270),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondaryContainer = Color(0xFF262B4A),
    onSecondaryContainer = Color(0xFFE0E7FF),
    background = Color(0xFF0B0D18),
    onBackground = Color(0xFFF1F2FA),
    surface = Color(0xFF161929),
    onSurface = Color(0xFFF1F2FA),
    surfaceVariant = Color(0xFF22263A),
    onSurfaceVariant = Color(0xFFA3A8C3),
    outlineVariant = Color(0xFF2A2F47),
    error = Color(0xFFFB7185),
)

private val AppTypography: Typography
    get() {
        val t = Typography()
        return t.copy(
            displayLarge = t.displayLarge.copy(fontWeight = FontWeight.Black),
            headlineLarge = t.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
            headlineMedium = t.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
            headlineSmall = t.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            titleLarge = t.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 19.sp),
            titleMedium = t.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            labelLarge = t.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

// ---------- Компоненты ----------

/** Белая карточка с мягкой тенью. */
@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    radius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val base = modifier
        .clip(shape)
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    Column(
        (if (onClick != null) base.clickable(onClick = onClick) else base).padding(18.dp),
        content = content
    )
}

/** Цветная плитка с эмодзи — «иконка» режима. */
@Composable
fun IconTile(emoji: String, grad: Grad, size: Dp = 48.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(grad.brush),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size.value * 0.48f).sp)
    }
}

/** Главная кнопка: градиентная «таблетка». */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    grad: Grad = Grads.Hero,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .height(56.dp)
            .clip(shape)
            .background(if (enabled) grad.brush else Brush.linearGradient(listOf(Color(0xFFB8BCCB), Color(0xFFB8BCCB))))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

/** Второстепенная кнопка: спокойная «таблетка». */
@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 56.dp,
) {
    Box(
        modifier
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
    }
}

/** Маленькая кнопка-пилюля (озвучка и т. п.). */
@Composable
fun Pill(text: String, onClick: () -> Unit, selected: Boolean = false, grad: Grad = Grads.Hero) {
    val shape = RoundedCornerShape(50)
    val mod = Modifier
        .clip(shape)
        .then(
            if (selected) Modifier.background(grad.brush)
            else Modifier
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
        )
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 10.dp)
    Box(mod) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Тонкая полоса прогресса с градиентом. */
@Composable
fun PillProgress(progress: Float, modifier: Modifier = Modifier, grad: Grad = Grads.Hero, height: Dp = 8.dp) {
    Box(
        modifier
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(grad.brush)
        )
    }
}

/** Кольцо прогресса (цель дня). */
@Composable
fun ProgressRing(progress: Float, size: Dp, stroke: Dp, track: Color, fill: Color, content: @Composable () -> Unit) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val s = stroke.toPx()
            val arc = Size(this.size.width - s, this.size.height - s)
            val tl = Offset(s / 2, s / 2)
            drawArc(track, -90f, 360f, false, tl, arc, style = Stroke(s, cap = StrokeCap.Round))
            drawArc(fill, -90f, 360f * progress.coerceIn(0f, 1f), false, tl, arc, style = Stroke(s, cap = StrokeCap.Round))
        }
        content()
    }
}

/** Круглая кнопка «закрыть/назад». */
@Composable
fun CircleButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Шапка экранов-списков. */
@Composable
fun TopBar(title: String, onExit: () -> Unit, right: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        CircleButton("←", onExit)
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), maxLines = 1)
        if (right != null) {
            Text(right, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Шапка занятия: крестик, полоса прогресса, счётчик. */
@Composable
fun SessionBar(onExit: () -> Unit, progress: Float, counter: String?, grad: Grad) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        CircleButton("✕", onExit)
        Spacer(Modifier.width(14.dp))
        PillProgress(progress, Modifier.weight(1f), grad, height = 10.dp)
        if (counter != null) {
            Spacer(Modifier.width(14.dp))
            Text(counter, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SpeakRow(text: String, speaker: Speaker) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Pill("🔊 Ещё раз", { speaker.say(text) })
        Pill("🐢 Медленно", { speaker.say(text, slow = true) })
    }
}

@Composable
fun WordChip(text: String, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(top = 14.dp, bottom = 2.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Плашка результата: зелёная или красная. */
@Composable
fun ResultBanner(ok: Boolean, title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit = {}) {
    val color = if (ok) Good else Bad
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = color)
        content()
    }
}

/** Крупный текст карточки по центру. */
@Composable
fun BigText(text: String, color: Color = Color.Unspecified, style: TextStyle = MaterialTheme.typography.headlineMedium) {
    Text(text, style = style, color = color, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

/** Подпись мелким серым. */
@Composable
fun Hint(text: String, center: Boolean = false) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        modifier = if (center) Modifier.fillMaxWidth() else Modifier
    )
}

/** Поле ввода с кнопкой «✕ очистить» — удобно, если распознавание ошиблось. */
@Composable
fun ClearableField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 3,
    onDone: (() -> Unit)? = null,
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(20.dp),
        enabled = enabled,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.titleMedium,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences,
            imeAction = if (onDone != null) androidx.compose.ui.text.input.ImeAction.Done else androidx.compose.ui.text.input.ImeAction.Default
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { onDone?.invoke() }),
        trailingIcon = if (value.isNotEmpty() && enabled) {
            {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onValue("") },
                    contentAlignment = Alignment.Center
                ) { Text("✕", style = MaterialTheme.typography.titleMedium) }
            }
        } else null,
        modifier = modifier
    )
}

/** Убирает последнее предложение — если микрофон добавил ерунду. */
fun dropLastSentence(text: String): String {
    val t = text.trimEnd()
    if (t.isEmpty()) return ""
    val body = t.trimEnd('.', '!', '?')
    val cut = maxOf(body.lastIndexOf(". "), body.lastIndexOf("! "), body.lastIndexOf("? "))
    return if (cut < 0) "" else t.substring(0, cut + 1)
}
