package com.lira.tracker.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lira.tracker.data.preferences.AppPreferences

// 1. Coral Dark
private val CoralDarkColors = darkColorScheme(
    primary = LiraCoralPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB82828),
    onPrimaryContainer = Color.White,
    secondary = LiraCoralSecondary,
    onSecondary = Color.Black,
    background = CoralDarkBackground,
    onBackground = TextPrimaryDark,
    surface = CoralDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CoralDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = CoralDarkBorder
)

// 2. Coral Light
private val CoralLightColors = lightColorScheme(
    primary = LiraCoralPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCDC),
    onPrimaryContainer = Color(0xFF8B1212),
    secondary = Color(0xFFFF6B6B),
    onSecondary = Color.White,
    background = CoralLightBackground,
    onBackground = TextPrimaryLight,
    surface = CoralLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = CoralLightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = CoralLightBorder
)

// 3. Wave Dark
private val WaveDarkColors = darkColorScheme(
    primary = WavePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5D24C6),
    onPrimaryContainer = Color.White,
    secondary = WaveSecondary,
    onSecondary = Color.Black,
    background = WaveDarkBackground,
    onBackground = TextPrimaryDark,
    surface = WaveDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = WaveDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = WaveDarkBorder
)

// 4. Wave Light
private val WaveLightColors = lightColorScheme(
    primary = Color(0xFF7028E4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9DCFF),
    onPrimaryContainer = Color(0xFF320078),
    secondary = Color(0xFF00A8A9),
    onSecondary = Color.White,
    background = WaveLightBackground,
    onBackground = TextPrimaryLight,
    surface = WaveLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = WaveLightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = WaveLightBorder
)

@Composable
fun LiraTheme(
    isDarkTheme: Boolean = true,
    appStyle: String = AppPreferences.STYLE_CORAL,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (appStyle) {
        AppPreferences.STYLE_NEON_WAVE -> {
            if (isDarkTheme) WaveDarkColors else WaveLightColors
        }
        else -> {
            // По умолчанию STYLE_CORAL
            if (isDarkTheme) CoralDarkColors else CoralLightColors
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
