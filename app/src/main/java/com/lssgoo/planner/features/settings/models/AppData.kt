package com.lssgoo.planner.features.settings.models

import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*

/**
 * Complete app data for backup/restore functionality
 * This wraps all user data for export/import
 */
data class AppData(
    val version: Int = 3,
    val exportedAt: Long = System.currentTimeMillis(),
    val goals: List<Goal> = emptyList(),
    val notes: List<Note> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val events: List<CalendarEvent> = emptyList(),
    val reminders: List<Reminder>? = null, 
    val habitEntries: List<HabitEntry> = emptyList(),
    val habits: List<Habit>? = null, 
    val journalEntries: List<JournalEntry>? = null,
    val transactions: List<Transaction>? = null,
    val budgets: List<Budget>? = null,
    val logs: List<FinanceLog>? = null,
    val userProfile: UserProfile? = null,
    val settings: AppSettings = AppSettings()
)

/**
 * Theme mode enum
 * Using string values for Gson serialization compatibility
 */
enum class ThemeMode(val value: String) {
    LIGHT("LIGHT"),
    DARK("DARK"),
    SYSTEM("SYSTEM"),
    OCEAN("OCEAN"),
    SUNSET("SUNSET"),
    FOREST("FOREST"),
    MIDNIGHT("MIDNIGHT");
    
    companion object {
        fun fromString(value: String?): ThemeMode {
            return when (value) {
                "LIGHT" -> LIGHT
                "DARK" -> DARK
                "SYSTEM" -> SYSTEM
                "OCEAN" -> OCEAN
                "SUNSET" -> SUNSET
                "FOREST" -> FOREST
                "MIDNIGHT" -> MIDNIGHT
                else -> SYSTEM // Default to SYSTEM
            }
        }
    }
}

/**
 * App settings
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "08:00",
    val weeklyReviewDay: Int = 0, // 0 = Sunday
    val userName: String = "",
    val profileImageUrl: String = "",
    val pinCode: String? = null // Encrypted or plain PIN for App Lock
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
    val overallProgress: Float = 0f,
    val totalHabitsToday: Int = 0,
    val habitsCompletedToday: Int = 0
)
