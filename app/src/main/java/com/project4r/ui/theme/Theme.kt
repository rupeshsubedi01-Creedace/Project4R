package com.project4r.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary        = Green600,
    onPrimary      = White,
    primaryContainer   = GreenCard,
    onPrimaryContainer = DarkGreen,
    secondary      = Green700,
    background     = GreenBg,
    surface        = White,
    onBackground   = DarkGreen,
    onSurface      = DarkGreen,
    outline        = GreenBorder,
    error          = RedAlert,
)

@Composable
fun Project4RTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography(),
        content     = content
    )
}
