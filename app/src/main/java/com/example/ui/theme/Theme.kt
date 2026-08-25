package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EmergencyLightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = PrimaryNavy,
    secondary = SecondarySlate,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = SecondarySlate,
    tertiary = InfoBlue,
    onTertiary = Color.White,
    tertiaryContainer = InfoBlueContainer,
    onTertiaryContainer = InfoBlueOnContainer,
    error = EmergencyRed,
    onError = Color.White,
    errorContainer = EmergencyRedContainer,
    onErrorContainer = EmergencyRedOnContainer,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun DisasterResponseTheme(
    darkTheme: Boolean = false, // Enforce crisp light theme for emergency readability
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EmergencyLightColorScheme,
        typography = Typography,
        content = content
    )
}
