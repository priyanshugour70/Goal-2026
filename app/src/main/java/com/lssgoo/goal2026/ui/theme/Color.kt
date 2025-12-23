package com.lssgoo.goal2026.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Black, White & Light Blue Theme - Dark Mode
val primaryDark = Color(0xFF4DD0E1)  // Light Blue
val onPrimaryDark = Color(0xFF000000)  // Black
val primaryContainerDark = Color(0xFF0097A7)  // Darker Blue
val onPrimaryContainerDark = Color(0xFFFFFFFF)

val secondaryDark = Color(0xFF80DEEA)  // Lighter Blue
val onSecondaryDark = Color(0xFF000000)
val secondaryContainerDark = Color(0xFF00838F)
val onSecondaryContainerDark = Color(0xFFFFFFFF)

val tertiaryDark = Color(0xFF4FC3F7)  // Sky Blue
val onTertiaryDark = Color(0xFF000000)
val tertiaryContainerDark = Color(0xFF0288D1)
val onTertiaryContainerDark = Color(0xFFFFFFFF)

val backgroundDark = Color(0xFF000000)  // Pure Black
val onBackgroundDark = Color(0xFFFFFFFF)  // White
val surfaceDark = Color(0xFF1A1A1A)  // Dark Gray
val onSurfaceDark = Color(0xFFFFFFFF)
val surfaceVariantDark = Color(0xFF2A2A2A)  // Slightly lighter gray
val onSurfaceVariantDark = Color(0xFFE0E0E0)

val outlineDark = Color(0xFF757575)
val outlineVariantDark = Color(0xFF424242)
val errorDark = Color(0xFFEF5350)
val successDark = Color(0xFF66BB6A)
val warningDark = Color(0xFFFFCA28)

// Modern Black, White & Light Blue Theme - Light Mode
val primaryLight = Color(0xFF0097A7)  // Teal Blue
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFB2EBF2)  // Light Blue Container
val onPrimaryContainerLight = Color(0xFF006064)

val secondaryLight = Color(0xFF00ACC1)  // Cyan Blue
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE0F7FA)
val onSecondaryContainerLight = Color(0xFF00838F)

val tertiaryLight = Color(0xFF0288D1)  // Bright Blue
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFE1F5FE)
val onTertiaryContainerLight = Color(0xFF01579B)

val backgroundLight = Color(0xFFFFFFFF)  // Pure White
val onBackgroundLight = Color(0xFF000000)  // Black
val surfaceLight = Color(0xFFFAFAFA)  // Off-white
val onSurfaceLight = Color(0xFF000000)
val surfaceVariantLight = Color(0xFFF5F5F5)  // Light Gray
val onSurfaceVariantLight = Color(0xFF424242)

val outlineLight = Color(0xFFBDBDBD)
val outlineVariantLight = Color(0xFFE0E0E0)
val errorLight = Color(0xFFD32F2F)
val successLight = Color(0xFF388E3C)
val warningLight = Color(0xFFF57C00)

// Goal Category Colors - Black, White, Light Blue variants
object GoalColors {
    val health = Color(0xFF26C6DA)  // Light Blue Cyan
    val career = Color(0xFF0097A7)  // Teal Blue
    val learning = Color(0xFF00ACC1)  // Cyan Blue
    val communication = Color(0xFF4DD0E1)  // Light Cyan
    val lifestyle = Color(0xFF80DEEA)  // Light Blue
    val discipline = Color(0xFF00838F)  // Dark Cyan
    val finance = Color(0xFF0288D1)  // Blue
    val startup = Color(0xFF039BE5)  // Bright Blue
    val mindfulness = Color(0xFF29B6F6)  // Sky Blue
}

// Gradient Colors for Cards - Black, White & Light Blue scheme
object GradientColors {
    val purpleBlue = listOf(Color(0xFF4DD0E1), Color(0xFF0097A7))  // Light Blue to Teal
    val pinkPurple = listOf(Color(0xFF80DEEA), Color(0xFF00ACC1))  // Light Blue variations
    val cyanGreen = listOf(Color(0xFF4DD0E1), Color(0xFF26C6DA))  // Blue gradient
    val orangePink = listOf(Color(0xFF80DEEA), Color(0xFF4FC3F7))  // Light blue gradient
    val greenTeal = listOf(Color(0xFF26C6DA), Color(0xFF0097A7))  // Cyan to teal
    val oceanBlue = listOf(Color(0xFF0288D1), Color(0xFF26C6DA))  // Blue gradient
    val royalBlue = listOf(Color(0xFF01579B), Color(0xFF0288D1))  // Deep Blue
    val darkOverlay = listOf(Color(0x00000000), Color(0x99000000))  // Black overlay
}

// Status Colors - Adapted for Black/White/Blue theme
val completedColor = Color(0xFF26C6DA)  // Light Blue
val pendingColor = Color(0xFF4DD0E1)  // Lighter Blue
val overdueColor = Color(0xFFEF5350)  // Red (error color)
val inProgressColor = Color(0xFF0097A7)  // Teal Blue