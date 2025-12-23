package com.lssgoo.planner.features.notes.models

import java.util.UUID
import com.lssgoo.planner.features.reminders.models.ItemPriority
import com.lssgoo.planner.features.tasks.models.RepeatType

/**
 * Represents a note in the app with reminder capabilities
 */
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Long = 0xFFFFFFFF,
    val isPinned: Boolean = false,
    val linkedGoalId: String? = null,
    val linkedTaskId: String? = null,
    val linkedReminderId: String? = null,
    val tags: List<String> = emptyList(),
    val priority: ItemPriority = ItemPriority.P6,
    // Reminder features
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val reminderRepeatType: RepeatType = RepeatType.NONE,
    val isReminderEnabled: Boolean = true,
    val notificationId: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val isLocked: Boolean = false,
    val category: String = "General",
    val mood: String = "Neutral",
    val lastEditedBy: String = "User",
    // Recall features
    val recallIntervals: List<Long> = emptyList(), // Spaced repetition intervals
    val nextRecallDate: Long? = null,
    val recallCount: Int = 0,
    // Update history
    val updateHistory: List<NoteUpdateRecord> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Record of note updates
 */
data class NoteUpdateRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val fieldChanged: String,
    val summary: String = ""
)

/**
 * Predefined note colors (iOS-style palette)
 */
object NoteColors {
    val colors = listOf(
        0xFF4DD0E1, // Sparkle Blue
        0xFF81C784, // Sage Green
        0xFFFFD54F, // Vivid Amber
        0xFFFF8A65, // Coral Orange
        0xFFBA68C8, // Soft Amethyst
        0xFFF06292, // Rosy Pink
        0xFF4DB6AC, // Teal Mint
        0xFFAED581, // Lime Leaf
        0xFF7986CB, // Indigo Night
        0xFF90A4AE  // Steel Blue
    )
    
    val premiumColors = listOf(
        0xFFE57373, // Soft Red
        0xFFFFB74D, // Honey Orange
        0xFFFFF176, // Lemon Yellow
        0xFF64B5F6, // Sky Blue
        0xFF4FC3F7, // Ocean Cyan
        0xFF9575CD, // Deep Lavender
        0xFFD4E157, // Pear Green
        0xFFFF80AB  // Flamingo Pink
    )
    
    val allColors = colors + premiumColors
}

/**
 * Default recall intervals for spaced repetition (in milliseconds)
 */
object RecallIntervals {
    val DEFAULT = listOf(
        1L * 24 * 60 * 60 * 1000,    // 1 day
        3L * 24 * 60 * 60 * 1000,    // 3 days
        7L * 24 * 60 * 60 * 1000,    // 1 week
        14L * 24 * 60 * 60 * 1000,   // 2 weeks
        30L * 24 * 60 * 60 * 1000,   // 1 month
        60L * 24 * 60 * 60 * 1000,   // 2 months
        90L * 24 * 60 * 60 * 1000    // 3 months
    )
}
