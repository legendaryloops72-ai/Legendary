package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PoliceBlueDark,
    secondary = TurquoiseDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = BackgroundDark,
    onSecondary = BackgroundDark,
    onBackground = LightBackground,
    onSurface = LightBackground
)

private val LightColorScheme = lightColorScheme(
    primary = PoliceBlue,
    secondary = Turquoise,
    background = LightBackground,
    surface = SurfaceCard,
    onPrimary = SurfaceCard,
    onSecondary = SurfaceCard,
    onBackground = SurfaceDark,
    onSurface = SurfaceDark,
    primaryContainer = SoftBlueContainer,
    secondaryContainer = SoftTealContainer,
    tertiaryContainer = SoftPeachContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
