package com.aistudio.kidspolice.abcd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PoliceGold,
    secondary = PoliceAccentCyan,
    tertiary = PoliceBlue,
    background = PoliceNavy,
    surface = PoliceCardBg,
    onPrimary = PoliceNavy,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun KidsPoliceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
