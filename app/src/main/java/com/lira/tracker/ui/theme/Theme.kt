package com.lira.tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LiraHeartCoral,
    onPrimary = Color.White,
    primaryContainer = LiraHeartCoralDark,
    onPrimaryContainer = Color.White,
    secondary = LiraHeartCoralLight,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun LiraTheme(
    darkTheme: Boolean = true, // По умолчанию элегантный темный стиль в духе LU
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
