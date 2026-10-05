package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PrimeEsportsDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = DarkEsportsBg,
    primaryContainer = ElectricViolet,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricViolet,
    onSecondary = TextPrimary,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = CyberCyan,
    tertiary = EmeraldNeon,
    onTertiary = DarkEsportsBg,
    background = DarkEsportsBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = DarkSurfaceBorderGlowing,
    error = FlameCrimson,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for Prime Esports signature aesthetic
    dynamicColor: Boolean = false, // Keep consistent esports branding
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkEsportsBg.toArgb()
                window.navigationBarColor = DarkEsportsBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = PrimeEsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
