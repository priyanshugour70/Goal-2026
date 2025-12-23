package com.lssgoo.planner.data.model

import java.util.UUID

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
        0xFFFFFFFF, // White
        0xFFFFF9C4, // Light Yellow
        0xFFFFCCBC, // Light Orange
        0xFFE1BEE7, // Light Purple
        0xFFB3E5FC, // Light Blue
        0xFFC8E6C9, // Light Green
        0xFFFFE0B2, // Peach
        0xFFF8BBD0, // Light Pink
        0xFFD7CCC8, // Light Brown
        0xFFCFD8DC  // Blue Grey
    )
    
    // Additional premium colors
    val premiumColors = listOf(
        0xFFE8F5E9, // Mint
        0xFFFCE4EC, // Rose
        0xFFF3E5F5, // Lavender
        0xFFE0F7FA, // Cyan Light
        0xFFFFF3E0, // Amber Light
        0xFFEDE7F6, // Deep Purple Light
        0xFFE1F5FE, // Light Blue 50
        0xFFFBE9E7  // Deep Orange Light
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
