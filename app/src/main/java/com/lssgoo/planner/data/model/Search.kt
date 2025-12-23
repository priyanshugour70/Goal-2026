package com.lssgoo.planner.data.model

/**
 * Search result model for global search
 */
data class SearchResult(
    val id: String,
    val title: String,
    val description: String,
    val type: SearchResultType,
    val highlightText: String, // Matched text with highlights
    val linkedGoalId: String? = null,
    val date: Long? = null,
    val priority: ItemPriority? = null
)

enum class SearchResultType(val displayName: String, val icon: String) {
    GOAL("Goal", "flag"),
    TASK("Task", "task"),
    NOTE("Note", "note"),
    EVENT("Event", "event"),
    REMINDER("Reminder", "alarm"),
    MILESTONE("Milestone", "milestone"),
    HABIT("Habit", "check_circle"),
    JOURNAL("Journal", "book"),
    FINANCE("Finance", "payments")
}

data class SearchFilters(
    val types: Set<SearchResultType> = emptySet(),
    val dateRange: Pair<Long?, Long?>? = null,
    val priority: ItemPriority? = null,
    val goalId: String? = null
)

