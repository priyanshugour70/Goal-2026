package com.lssgoo.planner.features.tasks.models

import java.util.UUID
import com.lssgoo.planner.features.reminders.models.ItemPriority

/**
 * Represents a task in the app with full linking and history tracking
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val itemPriority: ItemPriority = ItemPriority.P5, // 11-level priority
    val dueDate: Long? = null,
    val linkedGoalId: String? = null,
    val linkedNoteId: String? = null,
    val linkedReminderId: String? = null,
    val tags: List<String> = emptyList(),
    val repeatType: RepeatType = RepeatType.NONE,
    val reminder: Long? = null,
    val reminderEnabled: Boolean = true,
    val notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val subtasks: List<Subtask> = emptyList(),
    val updateHistory: List<TaskUpdateRecord> = emptyList(),
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Common Task Tags
 */
object TaskTags {
    const val PERSONAL = "Personal"
    const val OFFICE = "Office"
    const val HEALTH = "Health"
    const val SHOPPING = "Shopping"
    const val TRAVEL = "Travel"
    const val OTHER = "Other"

    val ALL = listOf(PERSONAL, OFFICE, HEALTH, SHOPPING, TRAVEL, OTHER)
    
    fun getColorForTag(tag: String): Long = when(tag) {
        PERSONAL -> 0xFF2196F3 // Blue
        OFFICE -> 0xFFFF9800   // Orange
        HEALTH -> 0xFF4CAF50   // Green
        SHOPPING -> 0xFFE91E63 // Pink
        TRAVEL -> 0xFF9C27B0   // Purple
        else -> 0xFF607D8B     // Grey
    }
}

/**
 * Subtask for breaking down tasks
 */
data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

/**
 * Record of task updates
 */
data class TaskUpdateRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val fieldChanged: String,
    val oldValue: String = "",
    val newValue: String = "",
    val summary: String = ""
)

enum class TaskPriority(val displayName: String, val color: Long) {
    LOW("Low", 0xFF4CAF50),      // Green
    MEDIUM("Medium", 0xFFFF9800), // Orange
    HIGH("High", 0xFFE91E63),     // Pink
    URGENT("Urgent", 0xFFF44336)  // Red
}

enum class RepeatType(val displayName: String) {
    NONE("No Repeat"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

/**
 * Represents a calendar event with full linking
 */
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val date: Long,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val color: Long = 0xFF2196F3,
    val priority: ItemPriority = ItemPriority.P5,
    val linkedGoalId: String? = null,
    val linkedTaskId: String? = null,
    val linkedNoteId: String? = null,
    val linkedReminderId: String? = null,
    val isAllDay: Boolean = true,
    val reminder: Long? = null,
    val reminderEnabled: Boolean = true,
    val notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val updateHistory: List<EventUpdateRecord> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Record of event updates
 */
data class EventUpdateRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val fieldChanged: String,
    val summary: String = ""
)

/**
 * Daily habit tracker
 */
data class HabitEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val goalId: String,
    val isCompleted: Boolean = false,
    val notes: String = ""
)
