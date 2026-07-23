package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AppThemeConfig(
    val id: String,
    val name: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val isDark: Boolean
)

val AppThemes = listOf(
    AppThemeConfig(
        id = "navy_gold",
        name = "كحلي / ذهبي الملكي",
        primary = Color(0xFFEAB308), // Gold Yellow
        secondary = Color(0xFFFACC15),
        background = Color(0xFF0B1224), // Dark Navy
        surface = Color(0xFF172554), // Navy Card
        onPrimary = Color(0xFF030712),
        onSecondary = Color(0xFF030712),
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFF8FAFC),
        isDark = true
    ),
    AppThemeConfig(
        id = "midnight_dark",
        name = "داكن كلاسيكي فخم",
        primary = Color(0xFFF4F4F5), // Off-white
        secondary = Color(0xFFA1A1AA),
        background = Color(0xFF09090B), // Obsidian black
        surface = Color(0xFF18181B), // Zinc Card
        onPrimary = Color(0xFF09090B),
        onSecondary = Color(0xFF09090B),
        onBackground = Color(0xFFF4F4F5),
        onSurface = Color(0xFFE4E4E7),
        isDark = true
    ),
    AppThemeConfig(
        id = "light_minimal",
        name = "فاتح بسيط ناصع",
        primary = Color(0xFF0F172A), // Midnight blue text/accent
        secondary = Color(0xFF475569),
        background = Color(0xFFF8FAFC), // Slate 50
        surface = Color(0xFFFFFFFF), // Pure White
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF1E293B),
        isDark = false
    ),
    AppThemeConfig(
        id = "cyber_blue",
        name = "أزرق تقني متطور",
        primary = Color(0xFF22D3EE), // Cyan 400
        secondary = Color(0xFF3B82F6), // Blue 500
        background = Color(0xFF030712), // Gray 950
        surface = Color(0xFF111827), // Gray 900
        onPrimary = Color(0xFF030712),
        onSecondary = Color(0xFFFFFFFF),
        onBackground = Color(0xFFE5E7EB),
        onSurface = Color(0xFFF3F4F6),
        isDark = true
    ),
    AppThemeConfig(
        id = "modern_purple",
        name = "بنفسجي عصري أنيق",
        primary = Color(0xFFC084FC), // Purple 400
        secondary = Color(0xFFF472B6), // Pink 400
        background = Color(0xFF120320), // Purple-black
        surface = Color(0xFF210D35), // Dark purple card
        onPrimary = Color(0xFF120320),
        onSecondary = Color(0xFF120320),
        onBackground = Color(0xFFF3E8FF),
        onSurface = Color(0xFFFAE8FF),
        isDark = true
    )
)

@Composable
fun MyApplicationTheme(
    themeId: String = "navy_gold",
    content: @Composable () -> Unit
) {
    val themeConfig = AppThemes.find { it.id == themeId } ?: AppThemes[0]
    
    val colorScheme = if (themeConfig.isDark) {
        darkColorScheme(
            primary = themeConfig.primary,
            secondary = themeConfig.secondary,
            background = themeConfig.background,
            surface = themeConfig.surface,
            onPrimary = themeConfig.onPrimary,
            onSecondary = themeConfig.onSecondary,
            onBackground = themeConfig.onBackground,
            onSurface = themeConfig.onSurface
        )
    } else {
        lightColorScheme(
            primary = themeConfig.primary,
            secondary = themeConfig.secondary,
            background = themeConfig.background,
            surface = themeConfig.surface,
            onPrimary = themeConfig.onPrimary,
            onSecondary = themeConfig.onSecondary,
            onBackground = themeConfig.onBackground,
            onSurface = themeConfig.onSurface
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
