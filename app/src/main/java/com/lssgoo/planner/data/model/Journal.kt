package com.lssgoo.planner.data.model

import java.util.UUID

/**
 * Journal entry for daily reflection and tracking
 */
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val title: String = "",
    val content: String,
    val mood: JournalMood = JournalMood.NEUTRAL,
    val tags: List<String> = emptyList(),
    val linkedGoalIds: List<String> = emptyList(),
    val linkedTaskIds: List<String> = emptyList(),
    val photos: List<String> = emptyList(), // URIs
    val gratitude: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val challenges: List<String> = emptyList(),
    val reflection: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class JournalMood(val emoji: String, val color: Long) {
    EXCELLENT("😄", 0xFF26C6DA),  // Light Blue
    GOOD("🙂", 0xFF4DD0E1),      // Light Cyan
    NEUTRAL("😐", 0xFF0097A7),    // Teal Blue
    BAD("😔", 0xFF00ACC1),       // Cyan Blue
    TERRIBLE("😢", 0xFF0288D1)   // Blue
}

data class JournalPrompt(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val category: PromptCategory,
    val isUsed: Boolean = false
)

enum class PromptCategory {
    GRATITUDE,
    REFLECTION,
    GOAL_REVIEW,
    SELF_IMPROVEMENT,
    MOTIVATION
}

data class JournalStats(
    val totalEntries: Int = 0,
    val entriesThisMonth: Int = 0,
    val averageMood: Float = 0f,
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    val mostUsedTags: List<Pair<String, Int>> = emptyList()
)

