package com.example.arogyasahaya3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = HighContrastPrimaryDark,
    onPrimary = HighContrastOnPrimaryDark,
    secondary = HighContrastSecondaryDark,
    onSecondary = HighContrastOnSecondaryDark,
    error = HighContrastErrorDark,
    onError = HighContrastOnErrorDark,
    background = HighContrastBackgroundDark,
    onBackground = HighContrastOnBackgroundDark,
    surface = HighContrastSurfaceDark,
    onSurface = HighContrastOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = HighContrastPrimaryLight,
    onPrimary = HighContrastOnPrimaryLight,
    secondary = HighContrastSecondaryLight,
    onSecondary = HighContrastOnSecondaryLight,
    error = HighContrastErrorLight,
    onError = HighContrastOnErrorLight,
    background = HighContrastBackgroundLight,
    onBackground = HighContrastOnBackgroundLight,
    surface = HighContrastSurfaceLight,
    onSurface = HighContrastOnSurfaceLight
)

@Composable
fun ArogyaSahaya3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for explicit high contrast control
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
