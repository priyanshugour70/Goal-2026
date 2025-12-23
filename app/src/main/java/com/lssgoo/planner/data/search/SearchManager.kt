package com.lssgoo.planner.data.search

import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*
import com.lssgoo.planner.ui.components.getIcon
import com.lssgoo.planner.features.reminders.models.ItemPriority

/**
 * Search manager for global search across all app content
 */
class SearchManager(
    private val storageManager: com.lssgoo.planner.data.local.LocalStorageManager
) {
    
    fun search(
        query: String,
        goals: List<Goal>,
        tasks: List<Task>,
        notes: List<Note>,
        events: List<CalendarEvent>,
        reminders: List<Reminder>,
        habits: List<Habit>,
        journalEntries: List<JournalEntry>,
        transactions: List<Transaction>,
        filters: SearchFilters = SearchFilters()
    ): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        
        val queryLower = query.lowercase()
        val results = mutableListOf<SearchResult>()
        
        // Search based on filters
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.GOAL)) {
            results.addAll(searchGoals(queryLower, goals))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.TASK)) {
            results.addAll(searchTasks(queryLower, tasks))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.NOTE)) {
            results.addAll(searchNotes(queryLower, notes))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.EVENT)) {
            results.addAll(searchEvents(queryLower, events))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.REMINDER)) {
            results.addAll(searchReminders(queryLower, reminders))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.MILESTONE)) {
            results.addAll(searchMilestones(queryLower, goals))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.HABIT)) {
            results.addAll(searchHabits(queryLower, habits))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.JOURNAL)) {
            results.addAll(searchJournal(queryLower, journalEntries))
        }
        if (filters.types.isEmpty() || filters.types.contains(SearchResultType.FINANCE)) {
            results.addAll(searchFinance(queryLower, transactions))
        }
        
        // Apply additional filters
        var filteredResults: List<SearchResult> = results
        
        if (filters.dateRange != null) {
            val (start, end) = filters.dateRange
            filteredResults = filteredResults.filter { result ->
                result.date?.let { date ->
                    (start == null || date >= start) && (end == null || date <= end)
                } ?: true // Keep if date is null? or filter out? Usually keep if it doesn't have a date filterable field
            }
        }
        
        if (filters.priority != null) {
            filteredResults = filteredResults.filter { it.priority == filters.priority }
        }
        
        if (filters.goalId != null) {
            filteredResults = filteredResults.filter { it.linkedGoalId == filters.goalId }
        }
        
        // Sort by relevance
        return filteredResults.sortedByDescending { calculateRelevance(it, queryLower) }
    }
    
    private fun searchGoals(query: String, goals: List<Goal>): List<SearchResult> {
        return goals.filter { goal ->
            goal.title.contains(query, ignoreCase = true) ||
            goal.description.contains(query, ignoreCase = true)
        }.map { goal ->
            SearchResult(
                id = goal.id,
                title = goal.title,
                description = goal.description,
                type = SearchResultType.GOAL,
                highlightText = highlightText(goal.title + " " + goal.description, query),
                linkedGoalId = goal.id,
                date = null,
                priority = null
            )
        }
    }
    
    private fun searchTasks(query: String, tasks: List<Task>): List<SearchResult> {
        return tasks.filter { task ->
            task.title.contains(query, ignoreCase = true) ||
            task.description.contains(query, ignoreCase = true)
        }.map { task ->
            SearchResult(
                id = task.id,
                title = task.title,
                description = task.description,
                type = SearchResultType.TASK,
                highlightText = highlightText(task.title + " " + task.description, query),
                linkedGoalId = task.linkedGoalId,
                date = task.dueDate,
                priority = task.itemPriority
            )
        }
    }
    
    private fun searchNotes(query: String, notes: List<Note>): List<SearchResult> {
        return notes.filter { note ->
            note.title.contains(query, ignoreCase = true) ||
            note.content.contains(query, ignoreCase = true) ||
            note.tags.any { it.contains(query, ignoreCase = true) }
        }.map { note ->
            SearchResult(
                id = note.id,
                title = note.title,
                description = note.content,
                type = SearchResultType.NOTE,
                highlightText = highlightText(note.title + " " + note.content, query),
                linkedGoalId = null,
                date = note.updatedAt,
                priority = null
            )
        }
    }
    
    private fun searchEvents(query: String, events: List<CalendarEvent>): List<SearchResult> {
        return events.filter { event ->
            event.title.contains(query, ignoreCase = true) ||
            event.description.contains(query, ignoreCase = true)
        }.map { event ->
            SearchResult(
                id = event.id,
                title = event.title,
                description = event.description,
                type = SearchResultType.EVENT,
                highlightText = highlightText(event.title + " " + event.description, query),
                linkedGoalId = event.linkedGoalId,
                date = event.date,
                priority = event.priority
            )
        }
    }
    
    private fun searchReminders(query: String, reminders: List<Reminder>): List<SearchResult> {
        return reminders.filter { reminder ->
            reminder.title.contains(query, ignoreCase = true) ||
            reminder.description.contains(query, ignoreCase = true)
        }.map { reminder ->
            SearchResult(
                id = reminder.id,
                title = reminder.title,
                description = reminder.description,
                type = SearchResultType.REMINDER,
                highlightText = highlightText(reminder.title + " " + reminder.description, query),
                linkedGoalId = reminder.linkedGoalId,
                date = reminder.reminderTime,
                priority = reminder.priority
            )
        }
    }
    
    private fun searchMilestones(query: String, goals: List<Goal>): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        
        goals.forEach { goal ->
            goal.milestones.filter { milestone ->
                milestone.title.contains(query, ignoreCase = true)
            }.forEach { milestone ->
                results.add(
                    SearchResult(
                        id = milestone.id,
                        title = milestone.title,
                        description = "Milestone for ${goal.title}",
                        type = SearchResultType.MILESTONE,
                        highlightText = highlightText(milestone.title, query),
                        linkedGoalId = goal.id,
                        date = milestone.completedAt,
                        priority = null
                    )
                )
            }
        }
        
        return results
    }
    
    private fun searchHabits(query: String, habits: List<Habit>): List<SearchResult> {
        return habits.filter { habit ->
            habit.title.contains(query, ignoreCase = true) ||
            habit.description.contains(query, ignoreCase = true)
        }.map { habit ->
            SearchResult(
                id = habit.id,
                title = habit.title,
                description = habit.description,
                type = SearchResultType.HABIT,
                highlightText = highlightText(habit.title + " " + habit.description, query),
                linkedGoalId = habit.goalId,
                date = habit.createdAt,
                priority = null
            )
        }
    }
    
    private fun searchJournal(query: String, entries: List<JournalEntry>): List<SearchResult> {
        return entries.filter { entry ->
            entry.title.contains(query, ignoreCase = true) ||
            entry.content.contains(query, ignoreCase = true) ||
            entry.tags.any { it.contains(query, ignoreCase = true) }
        }.map { entry ->
            SearchResult(
                id = entry.id,
                title = entry.title.ifBlank { "Untitled Entry" },
                description = entry.content,
                type = SearchResultType.JOURNAL,
                highlightText = highlightText(entry.title + " " + entry.content, query),
                linkedGoalId = null,
                date = entry.date,
                priority = null
            )
        }
    }

    private fun searchFinance(query: String, transactions: List<Transaction>): List<SearchResult> {
        return transactions.filter { tx ->
            tx.note.contains(query, ignoreCase = true) ||
            tx.category.name.contains(query, ignoreCase = true) ||
            (tx.personName?.contains(query, ignoreCase = true) ?: false)
        }.map { tx ->
            SearchResult(
                id = tx.id,
                title = "${tx.type}: ${tx.category.name} (₹${tx.amount})",
                description = tx.note + (tx.personName?.let { " - $it" } ?: ""),
                type = SearchResultType.FINANCE,
                highlightText = highlightText("${tx.category.name} ${tx.note} ${tx.personName ?: ""}", query),
                linkedGoalId = null,
                date = tx.date,
                priority = null
            )
        }
    }
    
    private fun highlightText(text: String, query: String): String {
        val regex = Regex(query, RegexOption.IGNORE_CASE)
        return regex.replace(text) { matchResult ->
            "**${matchResult.value}**"
        }
    }
    
    private fun calculateRelevance(result: SearchResult, query: String): Float {
        var score = 0f
        
        if (result.title.contains(query, ignoreCase = true)) {
            score += 10f
        }
        
        if (result.description.contains(query, ignoreCase = true)) {
            score += 5f
        }
        
        if (result.title.equals(query, ignoreCase = true)) {
            score += 20f
        }
        
        val p = result.priority
        p?.let { priority ->
            score += (priority.level / 10f)
        }
        
        return score
    }
}

