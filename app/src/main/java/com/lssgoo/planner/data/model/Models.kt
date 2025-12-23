/**
 * Re-exports all model types for central access
 * 
 * This file acts as a facade, re-exporting models from feature modules
 * so existing code using `com.lssgoo.planner.data.model.*` continues to work.
 */
@file:JvmName("Models")
package com.lssgoo.planner.data.model

// Re-export Goals models
typealias Goal = com.lssgoo.planner.features.goals.models.Goal
typealias GoalCategory = com.lssgoo.planner.features.goals.models.GoalCategory
typealias Milestone = com.lssgoo.planner.features.goals.models.Milestone
typealias DefaultGoals = com.lssgoo.planner.features.goals.models.DefaultGoals

// Re-export Tasks models
typealias Task = com.lssgoo.planner.features.tasks.models.Task
typealias TaskPriority = com.lssgoo.planner.features.tasks.models.TaskPriority
typealias CalendarEvent = com.lssgoo.planner.features.tasks.models.CalendarEvent

// Re-export Notes models
typealias Note = com.lssgoo.planner.features.notes.models.Note

// Re-export Habits models
// Removed to avoid ambiguity with main package imports

// Re-export Journal models  
typealias JournalEntry = com.lssgoo.planner.features.journal.models.JournalEntry
typealias JournalMood = com.lssgoo.planner.features.journal.models.JournalMood
typealias JournalPrompt = com.lssgoo.planner.features.journal.models.JournalPrompt
typealias PromptCategory = com.lssgoo.planner.features.journal.models.PromptCategory
typealias JournalStats = com.lssgoo.planner.features.journal.models.JournalStats

// Re-export Reminders models
typealias Reminder = com.lssgoo.planner.features.reminders.models.Reminder
typealias CalendarItem = com.lssgoo.planner.features.reminders.models.CalendarItem
typealias CalendarItemType = com.lssgoo.planner.features.reminders.models.CalendarItemType
typealias ItemPriority = com.lssgoo.planner.features.reminders.models.ItemPriority

// Re-export Finance models
typealias Transaction = com.lssgoo.planner.features.finance.models.Transaction
typealias TransactionType = com.lssgoo.planner.features.finance.models.TransactionType
typealias TransactionCategory = com.lssgoo.planner.features.finance.models.TransactionCategory
typealias Budget = com.lssgoo.planner.features.finance.models.Budget
typealias BudgetPeriod = com.lssgoo.planner.features.finance.models.BudgetPeriod
typealias FinanceLog = com.lssgoo.planner.features.finance.models.FinanceLog
typealias FinanceStats = com.lssgoo.planner.features.finance.models.FinanceStats

// Re-export Settings models
typealias AppSettings = com.lssgoo.planner.features.settings.models.AppSettings
typealias ThemeMode = com.lssgoo.planner.features.settings.models.ThemeMode

// Re-export Search models
typealias SearchResult = com.lssgoo.planner.features.search.models.SearchResult
typealias SearchResultType = com.lssgoo.planner.features.search.models.SearchResultType
typealias SearchFilters = com.lssgoo.planner.features.search.models.SearchFilters

// Re-export User Profile model
typealias UserProfile = com.lssgoo.planner.features.settings.models.UserProfile
typealias Gender = com.lssgoo.planner.features.settings.models.Gender

// Dashboard Stats model
typealias DashboardStats = com.lssgoo.planner.features.settings.models.DashboardStats

// App Data for backup/restore
typealias AppData = com.lssgoo.planner.features.settings.models.AppData

// Analytics data model
data class AnalyticsData(
    val goalProgress: List<GoalProgressData> = emptyList(),
    val taskCompletion: TaskCompletionData = TaskCompletionData(),
    val habitStreaks: List<HabitStreakData> = emptyList(),
    val timeSeries: List<TimeSeriesData> = emptyList(),
    val categoryBreakdown: List<CategoryData> = emptyList(),
    val insights: List<Insight> = emptyList()
)

data class GoalProgressData(
    val goalId: String,
    val goalTitle: String,
    val progress: Float = 0f,
    val milestonesCompleted: Int = 0,
    val totalMilestones: Int = 0,
    val trend: TrendDirection = TrendDirection.STABLE
)

data class TaskCompletionData(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Float = 0f,
    val averageCompletionTime: Long = 0L,
    val byPriority: Map<com.lssgoo.planner.features.tasks.models.TaskPriority, Int> = emptyMap(),
    val byCategory: Map<String, Int> = emptyMap()
)

data class HabitStreakData(
    val habitId: String = "",
    val habitTitle: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Float = 0f,
    val habitPerformance: Map<String, Float> = emptyMap()
)

data class TimeSeriesData(
    val date: Long,
    val tasksCompleted: Int = 0,
    val habitsCompleted: Int = 0,
    val milestonesCompleted: Int = 0
)

data class CategoryData(
    val category: com.lssgoo.planner.features.goals.models.GoalCategory,
    val goalsCount: Int = 0,
    val averageProgress: Float = 0f,
    val totalMilestones: Int = 0,
    val completedMilestones: Int = 0
)

data class Insight(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: InsightType = InsightType.INFO,
    val title: String,
    val message: String,
    val actionText: String? = null,
    val priority: InsightPriority = InsightPriority.LOW
)

enum class InsightType {
    CELEBRATION, WARNING, SUGGESTION, INFO
}

enum class InsightPriority {
    HIGH, MEDIUM, LOW
}

enum class TrendDirection {
    UP, DOWN, STABLE, IMPROVING, DECLINING
}

data class MoodTrendData(
    val date: Long,
    val averageMood: Float,
    val entriesCount: Int
)
