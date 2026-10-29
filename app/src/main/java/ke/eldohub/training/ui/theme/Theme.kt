package ke.eldohub.training.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = EldoHubDarkBlue,
    onPrimary = White,
    primaryContainer = EldoHubLightTeal,
    onPrimaryContainer = EldoHubDarkBlue,
    secondary = MastercardOrange,
    onSecondary = White,
    secondaryContainer = MastercardOrangeLight,
    onSecondaryContainer = Color(0xFF5A1E00),
    tertiary = MastercardYellow,
    onTertiary = White,
    tertiaryContainer = MastercardYellowLight,
    onTertiaryContainer = Color(0xFF5A3800),
    error = MastercardRed,
    onError = White,
    background = Slate50,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200
)

private val DarkColors = darkColorScheme(
    primary = EldoHubMint,
    onPrimary = Color(0xFF003730),
    primaryContainer = EldoHubDarkBlue,
    onPrimaryContainer = EldoHubLightTeal,
    secondary = MastercardOrange,
    onSecondary = White,
    secondaryContainer = Color(0xFF4A1800),
    onSecondaryContainer = MastercardOrangeLight,
    tertiary = MastercardYellow,
    onTertiary = Black,
    tertiaryContainer = Color(0xFF452C00),
    onTertiaryContainer = MastercardYellowLight,
    error = Color(0xFFFF6B6B),
    onError = Black,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1E2D42)
)

private val HighContrastColors = darkColorScheme(
    primary = ContrastYellow,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = ContrastYellow,
    secondary = ContrastYellow,
    onSecondary = Color.Black,
    secondaryContainer = Color.Black,
    onSecondaryContainer = ContrastYellow,
    tertiary = ContrastYellow,
    onTertiary = Color.Black,
    tertiaryContainer = Color.Black,
    onTertiaryContainer = ContrastYellow,
    surface = Color.Black,
    onSurface = ContrastYellow,
    surfaceVariant = Color.Black,
    onSurfaceVariant = ContrastYellow,
    background = Color.Black,
    onBackground = ContrastYellow,
    outline = ContrastYellow,
    outlineVariant = ContrastYellow,
    error = ContrastYellow,
    onError = Color.Black
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun EldoHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> HighContrastColors
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EldoHubTypography,
        shapes = AppShapes,
        content = content
    )
}
