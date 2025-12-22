package com.lssgoo.goal2026.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Dark Theme Colors
val primaryDark = Color(0xFF6C63FF)  // Vibrant Purple
val onPrimaryDark = Color(0xFFFFFFFF)
val primaryContainerDark = Color(0xFF3D3A8C)
val onPrimaryContainerDark = Color(0xFFE0DFFF)

val secondaryDark = Color(0xFF00D9FF)  // Cyan accent
val onSecondaryDark = Color(0xFF003544)
val secondaryContainerDark = Color(0xFF004D61)
val onSecondaryContainerDark = Color(0xFF97F0FF)

val tertiaryDark = Color(0xFFFF6B9D)  // Pink accent
val onTertiaryDark = Color(0xFF5D1037)
val tertiaryContainerDark = Color(0xFF7B294D)
val onTertiaryContainerDark = Color(0xFFFFD9E2)

val backgroundDark = Color(0xFF0D0D1A)  // Deep dark
val onBackgroundDark = Color(0xFFE6E1E5)
val surfaceDark = Color(0xFF1A1A2E)  // Slightly lighter
val onSurfaceDark = Color(0xFFE6E1E5)
val surfaceVariantDark = Color(0xFF2D2D44)
val onSurfaceVariantDark = Color(0xFFCAC4D0)

val outlineDark = Color(0xFF938F99)
val outlineVariantDark = Color(0xFF49454F)
val errorDark = Color(0xFFFF6B6B)
val successDark = Color(0xFF4ADE80)
val warningDark = Color(0xFFFBBF24)

// Premium Light Theme Colors
val primaryLight = Color(0xFF5046E5)  // Deep Purple
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFE0DFFF)
val onPrimaryContainerLight = Color(0xFF1A0063)

val secondaryLight = Color(0xFF0097B2)  // Teal
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFBDEBF5)
val onSecondaryContainerLight = Color(0xFF001F26)

val tertiaryLight = Color(0xFFDB2777)  // Pink
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD9E2)
val onTertiaryContainerLight = Color(0xFF3E0020)

val backgroundLight = Color(0xFFF8F9FC)  // Clean white
val onBackgroundLight = Color(0xFF1C1B1F)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF1C1B1F)
val surfaceVariantLight = Color(0xFFE7E0EC)
val onSurfaceVariantLight = Color(0xFF49454F)

val outlineLight = Color(0xFF79747E)
val outlineVariantLight = Color(0xFFCAC4D0)
val errorLight = Color(0xFFDC2626)
val successLight = Color(0xFF16A34A)
val warningLight = Color(0xFFD97706)

// Goal Category Colors
object GoalColors {
    val health = Color(0xFF4CAF50)
    val career = Color(0xFF2196F3)
    val learning = Color(0xFF9C27B0)
    val communication = Color(0xFFFF9800)
    val lifestyle = Color(0xFFE91E63)
    val discipline = Color(0xFF795548)
    val finance = Color(0xFF009688)
    val startup = Color(0xFF673AB7)
    val mindfulness = Color(0xFF607D8B)
}

// Gradient Colors for Cards
object GradientColors {
    val purpleBlue = listOf(Color(0xFF6C63FF), Color(0xFF00D9FF))
    val pinkPurple = listOf(Color(0xFFFF6B9D), Color(0xFF6C63FF))
    val cyanGreen = listOf(Color(0xFF00D9FF), Color(0xFF4ADE80))
    val orangePink = listOf(Color(0xFFFBBF24), Color(0xFFFF6B9D))
    val greenTeal = listOf(Color(0xFF4ADE80), Color(0xFF00D9FF))
    val darkOverlay = listOf(Color(0x00000000), Color(0x99000000))
}

// Status Colors
val completedColor = Color(0xFF4ADE80)
val pendingColor = Color(0xFFFBBF24)
val overdueColor = Color(0xFFFF6B6B)
val inProgressColor = Color(0xFF6C63FF)