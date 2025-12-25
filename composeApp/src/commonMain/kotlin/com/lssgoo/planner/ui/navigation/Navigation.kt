package com.lssgoo.planner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
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
    const val APPLOCK = "app_lock"
    
    // New Feature Routes
    const val HABITS = "habits"
    const val HABIT_DETAIL = "habit/{habitId}"
    const val SEARCH = "search"
    const val ANALYTICS = "analytics"
    const val JOURNAL = "journal"
    const val JOURNAL_ENTRY = "journal/{entryId}"
    const val FINANCE = "finance"
    
    // Settings Sub-pages
    const val ABOUT_DEVELOPER = "about_developer"
    const val VERSION_HISTORY = "version_history"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS_OF_SERVICE = "terms_of_service"
    const val DATA_USAGE = "data_usage"
    
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
    val unselectedIcon: ImageVector
) {
    DASHBOARD(
        route = Routes.DASHBOARD,
        label = "Home",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    GOALS(
        route = Routes.GOALS,
        label = "Goals",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents
    ),
    TASKS(
        route = Routes.TASKS,
        label = "Tasks",
        selectedIcon = Icons.Filled.TaskAlt,
        unselectedIcon = Icons.Outlined.TaskAlt
    ),
    FINANCE(
        route = Routes.FINANCE,
        label = "Finance",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    ),
    HABITS(
        route = Routes.HABITS,
        label = "Habits",
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle
    ),
    JOURNAL(
        route = Routes.JOURNAL,
        label = "Journal",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),
    NOTES(
        route = Routes.NOTES,
        label = "Notes",
        selectedIcon = Icons.AutoMirrored.Filled.StickyNote2,
        unselectedIcon = Icons.AutoMirrored.Outlined.StickyNote2
    ),
    CALENDAR(
        route = Routes.CALENDAR,
        label = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    SETTINGS(
        route = Routes.SETTINGS,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}
