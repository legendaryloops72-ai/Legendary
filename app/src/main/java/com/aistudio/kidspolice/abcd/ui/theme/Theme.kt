package com.aistudio.kidspolice.abcd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D47A1),
    secondary = Color(0xFF1E88E5),
    tertiary = Color(0xFFFFD700),
    background = Color(0xFFE1F5FE),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF0D47A1),
    onBackground = Color(0xFF0D47A1),
    onSurface = Color(0xFF0D47A1)
)

@Composable
fun KidsPoliceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

