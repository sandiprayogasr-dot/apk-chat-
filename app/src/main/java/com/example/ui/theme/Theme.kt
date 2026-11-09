package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AccentTheme
import com.example.model.ThemeMode

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    accentTheme: AccentTheme = AccentTheme.BLUE,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val accent = if (accentTheme == AccentTheme.BLUE) VerbaPrimary else Color(accentTheme.hex)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = accent.copy(alpha = 0.2f),
            onPrimaryContainer = Color(0xFFD4E4FF),
            secondary = VerbaTextSecondary,
            onSecondary = VerbaDarkBackground,
            background = VerbaDarkBackground,
            onBackground = VerbaTextPrimary,
            surface = VerbaCard,
            onSurface = VerbaTextPrimary,
            surfaceVariant = VerbaSidebar,
            onSurfaceVariant = VerbaTextSecondary,
            outline = VerbaBorder,
            error = VerbaDanger,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = accent.copy(alpha = 0.15f),
            onPrimaryContainer = Color(0xFF003882),
            secondary = Color(0xFF5A5A5A),
            onSecondary = Color.White,
            background = Color(0xFFF4F6F9),
            onBackground = Color(0xFF14171E),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF14171E),
            surfaceVariant = Color(0xFFE9EDF2),
            onSurfaceVariant = Color(0xFF656B75),
            outline = Color(0x18000000),
            error = VerbaDanger,
            onError = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
