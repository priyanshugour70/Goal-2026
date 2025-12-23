package com.lssgoo.goal2026.data.model

import java.util.UUID

/**
 * Habit model for tracking recurring activities
 */
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val goalId: String,
    val title: String,
    val description: String = "",
    val icon: String = "check_circle",
    val color: Long = 0xFF4DD0E1, // Light Blue
    val targetFrequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderTime: String? = null, // "08:00"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Extended HabitEntry with mood and value tracking
 */
data class HabitEntryExtended(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: Long,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val completionTime: Long? = null,
    val mood: HabitMood? = null,
    val value: Float? = null // For quantitative habits
)

enum class HabitMood(val emoji: String) {
    EXCELLENT("😄"),
    GOOD("🙂"),
    OKAY("😐"),
    BAD("😔"),
    TERRIBLE("😢")
}

enum class HabitFrequency(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    CUSTOM("Custom")
}

data class HabitStats(
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Float = 0f, // 0.0 to 1.0
    val totalCompletions: Int = 0,
    val totalDays: Int = 0,
    val lastCompletedDate: Long? = null
)

