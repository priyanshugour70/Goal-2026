package com.lssgoo.goal2026.data.model

/**
 * Complete app data for backup/restore functionality
 * This wraps all user data for export/import
 */
data class AppData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val goals: List<Goal> = emptyList(),
    val notes: List<Note> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val events: List<CalendarEvent> = emptyList(),
    val habitEntries: List<HabitEntry> = emptyList(),
    val settings: AppSettings = AppSettings()
)

/**
 * App settings
 */
data class AppSettings(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "08:00",
    val weeklyReviewDay: Int = 0, // 0 = Sunday
    val userName: String = "",
    val profileImageUrl: String = ""
)

/**
 * Statistics for the dashboard
 */
data class DashboardStats(
    val totalGoals: Int = 0,
    val completedMilestones: Int = 0,
    val totalMilestones: Int = 0,
    val tasksCompletedToday: Int = 0,
    val totalTasksToday: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val overallProgress: Float = 0f
)
