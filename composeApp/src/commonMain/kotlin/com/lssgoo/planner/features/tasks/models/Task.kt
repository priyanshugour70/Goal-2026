package com.lssgoo.planner.features.tasks.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.util.KmpTimeUtils
import com.lssgoo.planner.features.reminders.models.ItemPriority

/**
 * Represents a task in the app with full linking and history tracking
 */
@Serializable
data class Task(
    val id: String = KmpIdGenerator.generateId(),
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
    val notificationId: Int = (KmpTimeUtils.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val subtasks: List<Subtask> = emptyList(),
    val updateHistory: List<TaskUpdateRecord> = emptyList(),
    val completedAt: Long? = null,
    val createdAt: Long = KmpTimeUtils.currentTimeMillis(),
    val updatedAt: Long = KmpTimeUtils.currentTimeMillis(),
    val isDeleted: Boolean = false
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
    const val WORK = "Work"
    const val STUDY = "Study"
    const val FINANCE = "Finance"
    const val URGENT = "Urgent"
    const val OTHER = "Other"

    val ALL = listOf(PERSONAL, OFFICE, HEALTH, SHOPPING, TRAVEL, WORK, STUDY, FINANCE, URGENT, OTHER)
    
    fun getColorForTag(tag: String): Long = when(tag) {
        PERSONAL -> 0xFF2196F3 // Blue
        OFFICE -> 0xFFFF9800   // Orange
        HEALTH -> 0xFF4CAF50   // Green
        SHOPPING -> 0xFFE91E63 // Pink
        TRAVEL -> 0xFF9C27B0   // Purple
        WORK -> 0xFF795548     // Brown
        STUDY -> 0xFF673AB7    // Deep Purple
        FINANCE -> 0xFF009688  // Teal
        URGENT -> 0xFFF44336   // Red
        else -> 0xFF607D8B     // Grey
    }
}

/**
 * Subtask for breaking down tasks
 */
@Serializable
data class Subtask(
    val id: String = KmpIdGenerator.generateId(),
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

/**
 * Record of task updates
 */
@Serializable
data class TaskUpdateRecord(
    val id: String = KmpIdGenerator.generateId(),
    val timestamp: Long = KmpTimeUtils.currentTimeMillis(),
    val fieldChanged: String,
    val oldValue: String = "",
    val newValue: String = "",
    val summary: String = ""
)

@Serializable
enum class TaskPriority(val displayName: String, val color: Long) {
    LOW("Low", 0xFF4CAF50),      // Green
    MEDIUM("Medium", 0xFFFF9800), // Orange
    HIGH("High", 0xFFE91E63),     // Pink
    URGENT("Urgent", 0xFFF44336)  // Red
}

@Serializable
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
@Serializable
data class CalendarEvent(
    val id: String = KmpIdGenerator.generateId(),
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
    val notificationId: Int = (KmpTimeUtils.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val updateHistory: List<EventUpdateRecord> = emptyList(),
    val createdAt: Long = KmpTimeUtils.currentTimeMillis(),
    val updatedAt: Long = KmpTimeUtils.currentTimeMillis(),
    val isDeleted: Boolean = false
)

/**
 * Record of event updates
 */
@Serializable
data class EventUpdateRecord(
    val id: String = KmpIdGenerator.generateId(),
    val timestamp: Long = KmpTimeUtils.currentTimeMillis(),
    val fieldChanged: String,
    val summary: String = ""
)

/**
 * Daily habit tracker
 */
@Serializable
data class HabitEntry(
    val id: String = KmpIdGenerator.generateId(),
    val date: Long,
    val goalId: String,
    val isCompleted: Boolean = false,
    val notes: String = ""
)
