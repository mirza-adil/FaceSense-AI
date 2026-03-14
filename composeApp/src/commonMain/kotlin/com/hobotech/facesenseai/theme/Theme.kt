package com.hobotech.facesenseai.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FaceSenseDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF00391A),
    primaryContainer = Color(0xFF005228),
    onPrimaryContainer = Color(0xFF84FB9E),
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF003547),
    secondaryContainer = Color(0xFF004D65),
    onSecondaryContainer = Color(0xFFB8EAFF),
    tertiary = Color(0xFFB388FF),
    onTertiary = Color(0xFF1F0052),
    tertiaryContainer = Color(0xFF3700B3),
    onTertiaryContainer = Color(0xFFE1BEE7),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF30363D),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF5C1010),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun FaceSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FaceSenseDarkColorScheme,
        content = content
    )
}
