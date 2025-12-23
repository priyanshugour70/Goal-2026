package com.lssgoo.planner.features.reminders.models

import java.util.UUID
import com.lssgoo.planner.features.tasks.models.RepeatType

/**
 * 11-Level Priority System for all items
 */
enum class ItemPriority(val level: Int, val displayName: String, val color: Long) {
    P1(1, "Critical", 0xFFDC2626),      // Red
    P2(2, "Very High", 0xFFEA580C),     // Orange Red
    P3(3, "High", 0xFFF97316),          // Orange
    P4(4, "Medium High", 0xFFFB923C),   // Light Orange
    P5(5, "Medium", 0xFFFBBF24),        // Yellow
    P6(6, "Medium Low", 0xFFA3E635),    // Lime
    P7(7, "Low", 0xFF4ADE80),           // Green
    P8(8, "Very Low", 0xFF22D3EE),      // Cyan
    P9(9, "Minimal", 0xFF60A5FA),       // Blue
    P10(10, "Optional", 0xFFA78BFA),    // Purple
    P11(11, "Someday", 0xFF9CA3AF)      // Gray
}

/**
 * Represents a reminder in the app
 */
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val reminderTime: Long,
    val repeatType: RepeatType = RepeatType.NONE,
    val priority: ItemPriority = ItemPriority.P5,
    val isEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val linkedNoteId: String? = null,
    val linkedTaskId: String? = null,
    val linkedGoalId: String? = null,
    val color: Long = 0xFF6C63FF,
    val notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val updateHistory: List<UpdateRecord> = emptyList()
)

/**
 * Track update history for any item
 */
data class UpdateRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val fieldChanged: String,
    val oldValue: String,
    val newValue: String,
    val comment: String = ""
)

/**
 * Unified item for calendar display - represents any type of item on a specific date
 */
data class CalendarItem(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val type: CalendarItemType,
    val priority: ItemPriority,
    val color: Long,
    val isCompleted: Boolean = false,
    val linkedGoalId: String? = null,
    val linkedItemId: String? = null
)

/**
 * Types of items that can appear on calendar
 */
enum class CalendarItemType(val displayName: String, val iconName: String) {
    TASK("Task", "TaskAlt"),
    NOTE("Note", "StickyNote2"),
    REMINDER("Reminder", "Alarm"),
    EVENT("Event", "Event"),
    GOAL_MILESTONE("Milestone", "Flag"),
    HABIT("Habit", "CheckCircle"),
    JOURNAL("Journal", "HistoryEdu"),
    FINANCE("Finance", "Payments"),
    GOAL("Goal", "EmojiEvents")
}
