package com.eve.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EveDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = SpaceBlack,
    primaryContainer = NebulaDark,
    onPrimaryContainer = CyanBright,
    secondary = CyberViolet,
    onSecondary = SpaceBlack,
    secondaryContainer = DeepViolet,
    onSecondaryContainer = Ultraviolet,
    tertiary = NeonAzure,
    onTertiary = SpaceBlack,
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    error = StateError,
    onError = SpaceBlack
)

@Composable
fun EVETheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
