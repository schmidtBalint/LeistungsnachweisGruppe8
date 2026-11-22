package de.hhn.umgebungsscanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B45B0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E0FF),
    onPrimaryContainer = Color(0xFF1E0E5E),
    secondaryContainer = Color(0xFFEDE6FA),
    onSecondaryContainer = Color(0xFF2B2140),
    background = Color(0xFFF7F3FC),
    onBackground = Color(0xFF1C1B20),
    surface = Color(0xFFF7F3FC),
    onSurface = Color(0xFF1C1B20),
    surfaceVariant = Color(0xFFE7E0F2),
    onSurfaceVariant = Color(0xFF4A4458),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCFAFF),
    surfaceContainer = Color(0xFFF3EEFB),
    outline = Color(0xFFC3B7E3),
    outlineVariant = Color(0xFFE0D8F0),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFBE4E2),
    onErrorContainer = Color(0xFF5F1410),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFCDBDFF),
    onPrimary = Color(0xFF2E1A80),
    primaryContainer = Color(0xFF443096),
    onPrimaryContainer = Color(0xFFE9E0FF),
    secondaryContainer = Color(0xFF3A3150),
    onSecondaryContainer = Color(0xFFEDE6FA),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0EC),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0EC),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D6),
    surfaceContainerLowest = Color(0xFF1D1A24),
    surfaceContainerLow = Color(0xFF211E28),
    surfaceContainer = Color(0xFF2A2633),
    outline = Color(0xFF7D7392),
    outlineVariant = Color(0xFF3F3A4B),
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF5C1E1A),
    onErrorContainer = Color(0xFFF9DEDC),
)

/** Farben, die Material 3 nicht mitbringt (Erfolg/Zielkreis). */
@Immutable
data class ExtraColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val targetFill: Color,
    val arenaFill: Color,
)

private val LightExtra = ExtraColors(
    success = Color(0xFF2E9B47),
    onSuccess = Color.White,
    successContainer = Color(0xFFE6F4E4),
    onSuccessContainer = Color(0xFF145A26),
    targetFill = Color(0xFFDDF0DA),
    arenaFill = Color(0xFFF1ECFA),
)

private val DarkExtra = ExtraColors(
    success = Color(0xFF6FD487),
    onSuccess = Color(0xFF00391A),
    successContainer = Color(0xFF173A22),
    onSuccessContainer = Color(0xFFBFF0C8),
    targetFill = Color(0xFF1F3D27),
    arenaFill = Color(0xFF221E2B),
)

val LocalExtraColors = staticCompositionLocalOf { LightExtra }

private val AppTypography = Typography().run {
    copy(
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
        bodyLarge = bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    )
}

val ValueTextStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp)

@Composable
fun UmgebungsscannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Bewusst kein Dynamic Color: Die App soll überall wie im Entwurf aussehen.
    CompositionLocalProvider(LocalExtraColors provides if (darkTheme) DarkExtra else LightExtra) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            content = content,
        )
    }
}

object AppTheme {
    val extraColors: ExtraColors
        @Composable get() = LocalExtraColors.current
}
