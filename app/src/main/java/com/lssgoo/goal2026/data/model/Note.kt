package com.lssgoo.goal2026.data.model

import java.util.UUID

/**
 * Represents a note in the app
 */
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Long = 0xFFFFFFFF,
    val isPinned: Boolean = false,
    val linkedGoalId: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Predefined note colors
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
}
