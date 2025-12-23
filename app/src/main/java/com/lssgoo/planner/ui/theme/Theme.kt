package com.lssgoo.planner.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lssgoo.planner.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    error = errorDark,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    error = errorLight,
    onError = Color.White
)

private val OceanColorScheme = lightColorScheme(
    primary = oceanPrimary,
    onPrimary = Color.White,
    secondary = oceanSecondary,
    tertiary = oceanTertiary,
    background = oceanBackground,
    surface = oceanSurface,
    onSurface = oceanOnSurface
)

private val SunsetColorScheme = lightColorScheme(
    primary = sunsetPrimary,
    onPrimary = Color.White,
    secondary = sunsetSecondary,
    tertiary = sunsetTertiary,
    background = sunsetBackground,
    surface = sunsetSurface,
    onSurface = sunsetOnSurface
)

private val ForestColorScheme = lightColorScheme(
    primary = forestPrimary,
    onPrimary = Color.White,
    secondary = forestSecondary,
    tertiary = forestTertiary,
    background = forestBackground,
    surface = forestSurface,
    onSurface = forestOnSurface
)

private val MidnightColorScheme = darkColorScheme(
    primary = midnightPrimary,
    onPrimary = Color.White,
    secondary = midnightSecondary,
    tertiary = midnightTertiary,
    background = midnightBackground,
    surface = midnightSurface,
    onSurface = midnightOnSurface
)

@Composable
fun PlannerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.OCEAN -> OceanColorScheme
        ThemeMode.SUNSET -> SunsetColorScheme
        ThemeMode.FOREST -> ForestColorScheme
        ThemeMode.MIDNIGHT -> MidnightColorScheme
        ThemeMode.SYSTEM -> if (systemDarkTheme) DarkColorScheme else LightColorScheme
    }
    
    val isDark = when (themeMode) {
        ThemeMode.DARK, ThemeMode.MIDNIGHT -> true
        ThemeMode.LIGHT, ThemeMode.OCEAN, ThemeMode.SUNSET, ThemeMode.FOREST -> false
        ThemeMode.SYSTEM -> systemDarkTheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar and navigation bar to match theme colors
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            // Light icons for dark theme, dark icons for light theme
            val isLightStatusBar = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightStatusBar
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isLightStatusBar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}