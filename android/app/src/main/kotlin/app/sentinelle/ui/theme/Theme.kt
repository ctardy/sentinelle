package app.sentinelle.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = SentinelleBlueStrong,
    onPrimary = Color.White,
    primaryContainer = SentinelleBlueContainer,
    onPrimaryContainer = SentinelleBlueDark,
    secondary = SentinelleInk700,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = SentinelleInk900,
    tertiary = SeverityImportant,
    onTertiary = SentinelleInk900,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = Color.White,
    onBackground = SentinelleInk900,
    surface = SentinelleInk50,
    onSurface = SentinelleInk900,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = SentinelleInk700,
    outline = Color(0xFFCBD5E1),
    error = SeverityCritical,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = SentinelleBlueLight,
    onPrimary = SentinelleInk900,
    primaryContainer = SentinelleBlueDark,
    onPrimaryContainer = SentinelleBlueContainer,
    secondary = SentinelleInk400,
    onSecondary = SentinelleInk900,
    secondaryContainer = SentinelleInk700,
    onSecondaryContainer = SentinelleInk100,
    tertiary = SeverityImportant,
    onTertiary = SentinelleInk900,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = SentinelleInk900,
    onBackground = SentinelleInk100,
    surface = SentinelleInk800,
    onSurface = SentinelleInk100,
    surfaceVariant = SentinelleInk700,
    onSurfaceVariant = SentinelleInk200,
    outline = SentinelleInk600,
    error = SeverityCritical,
    onError = SentinelleInk900,
)

@Composable
fun SentinelleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color désactivé par défaut : on tient la cohérence visuelle avec le site,
    // au prix de ne pas suivre le fond d'écran Material You sur Android 12+.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SentinelleTypography,
        content = content,
    )
}
