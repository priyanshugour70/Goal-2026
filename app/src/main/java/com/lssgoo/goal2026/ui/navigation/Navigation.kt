package com.lssgoo.goal2026.ui.navigation

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
    const val SETTINGS = "settings"
    const val BACKUP = "backup"
    
    fun goalDetail(goalId: String) = "goal/$goalId"
    fun noteDetail(noteId: String) = "note/$noteId"
}

/**
 * Bottom navigation destinations
 */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val emoji: String
) {
    DASHBOARD(
        route = Routes.DASHBOARD,
        label = "Dashboard",
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
    CALENDAR(
        route = Routes.CALENDAR,
        label = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        emoji = "📅"
    ),
    NOTES(
        route = Routes.NOTES,
        label = "Notes",
        selectedIcon = Icons.Filled.StickyNote2,
        unselectedIcon = Icons.Outlined.StickyNote2,
        emoji = "📝"
    ),
    TASKS(
        route = Routes.TASKS,
        label = "Tasks",
        selectedIcon = Icons.Filled.TaskAlt,
        unselectedIcon = Icons.Outlined.TaskAlt,
        emoji = "✅"
    )
}
