package com.lssgoo.planner.features.habits.models

import java.util.UUID

enum class HabitType {
    YES_NO,       // Simple checkbox
    QUANTITATIVE, // Count (e.g., 5 glasses of water)
    TIMER         // Duration (e.g., 10 mins meditation)
}

enum class HabitTimeOfDay(val displayName: String) {
    ANY_TIME("Any Time"),
    MORNING("Morning Routine"),
    AFTERNOON("Afternoon Grind"),
    EVENING("Evening Wind-down")
}

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val goalId: String?, // Nullable if not linked to a specific goal
    val title: String,
    val description: String = "",
    val icon: String = "✨",
    val iconColor: Long = 0xFF4DD0E1, // Hex color
    val type: HabitType = HabitType.YES_NO,
    val targetValue: Float = 1f, // e.g., 10 (pages), 15 (mins)
    val unit: String? = null,    // e.g., "pages", "mins", "glasses"
    val frequency: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // Days of week (1=Mon, 7=Sun)
    val timeOfDay: HabitTimeOfDay = HabitTimeOfDay.ANY_TIME,
    val reminderTime: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class HabitEntry(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: Long, // Processed to start of day
    val value: Float = 0f, // For quant/timer (current progress)
    val isCompleted: Boolean = false,
    val mood: HabitMood? = null,
    val notes: String = ""
)

enum class HabitMood(val emoji: String, val label: String) {
    EXCELLENT("🤩", "Excellent"),
    GOOD("🙂", "Good"),
    NEUTRAL("😐", "Neutral"),
    TIRED("😴", "Tired"),
    STRESSED("😫", "Stressed")
}

data class HabitStats(
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val completionRate: Float = 0f,
    val consistencyScore: Int = 0, // 0-100 score
    val last7Days: List<Boolean> = emptyList(), // For rings/mini-charts
    val dayPerformance: Map<Int, Float> = emptyMap(), // DayOfWeek (1-7) -> Rate (0.0-1.0)
    val heatmapData: Map<Long, Int> = emptyMap() // Date -> Level (0-4) for GitHub style graph
)
