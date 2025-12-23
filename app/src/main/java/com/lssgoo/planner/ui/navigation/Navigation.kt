package com.lssgoo.planner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for the app
 */
object Routes {
    const val DASHBOARD = "dashboard"
    const val GOALS = "goals"
    const val GOAL_DETAIL = "goal/{goalId}"
    const val CALENDAR = "calendar"
    const val NOTES = "notes"
    const val NOTE_DETAIL = "note/{noteId}"
    const val NOTE_CREATE = "note/create"
    const val TASKS = "tasks"
    const val REMINDERS = "reminders"
    const val SETTINGS = "settings"
    const val BACKUP = "backup"
    const val ONBOARDING = "onboarding"
    const val PROFILE = "profile"
    
    // New Feature Routes
    const val HABITS = "habits"
    const val HABIT_DETAIL = "habit/{habitId}"
    const val SEARCH = "search"
    const val ANALYTICS = "analytics"
    const val JOURNAL = "journal"
    const val JOURNAL_ENTRY = "journal/{entryId}"
    const val FINANCE = "finance"
    
    fun goalDetail(goalId: String) = "goal/$goalId"
    fun noteDetail(noteId: String) = "note/$noteId"
    fun habitDetail(habitId: String) = "habit/$habitId"
    fun journalEntry(entryId: String) = "journal/$entryId"
}

/**
 * Bottom navigation destinations
 * Uses Material Icons as the primary visual element
 * Emoji field kept for backward compatibility but icons are preferred
 */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val emoji: String = "" // Deprecated: use icons instead
) {
    DASHBOARD(
        route = Routes.DASHBOARD,
        label = "Home",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        emoji = "🏠"
    ),
    GOALS(
        route = Routes.GOALS,
        label = "Goals",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
        emoji = "🎯"
    ),
    TASKS(
        route = Routes.TASKS,
        label = "Tasks",
        selectedIcon = Icons.Filled.TaskAlt,
        unselectedIcon = Icons.Outlined.TaskAlt,
        emoji = "✅"
    ),
    FINANCE(
        route = Routes.FINANCE,
        label = "Finance",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        emoji = "💰"
    ),
    HABITS(
        route = Routes.HABITS,
        label = "Habits",
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        emoji = "🔄"
    ),
    JOURNAL(
        route = Routes.JOURNAL,
        label = "Journal",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        emoji = "📔"
    ),
    NOTES(
        route = Routes.NOTES,
        label = "Notes",
        selectedIcon = Icons.Filled.StickyNote2,
        unselectedIcon = Icons.Outlined.StickyNote2,
        emoji = "📝"
    ),
    CALENDAR(
        route = Routes.CALENDAR,
        label = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        emoji = "📅"
    ),
    SETTINGS(
        route = Routes.SETTINGS,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        emoji = "⚙️"
    )
}

