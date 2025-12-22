package com.lssgoo.goal2026.data.model

import java.util.UUID

/**
 * Represents a task in the app
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val linkedGoalId: String? = null,
    val category: String = "",
    val repeatType: RepeatType = RepeatType.NONE,
    val reminder: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
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
 * Represents a calendar event
 */
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val date: Long,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val color: Long = 0xFF2196F3,
    val linkedGoalId: String? = null,
    val linkedTaskId: String? = null,
    val isAllDay: Boolean = true,
    val reminder: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
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
