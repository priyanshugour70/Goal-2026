package com.lssgoo.goal2026.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lssgoo.goal2026.data.model.*

/**
 * LocalStorageManager handles all data persistence using SharedPreferences
 * This ensures data is saved on the device and persists across app restarts
 */
class LocalStorageManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "goal2026_storage"
        private const val KEY_GOALS = "goals"
        private const val KEY_NOTES = "notes"
        private const val KEY_TASKS = "tasks"
        private const val KEY_EVENTS = "events"
        private const val KEY_HABITS = "habit_entries"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_SYNC = "last_sync"
    }
    
    // ======================== GOALS ========================
    
    fun saveGoals(goals: List<Goal>) {
        val json = gson.toJson(goals)
        prefs.edit().putString(KEY_GOALS, json).apply()
    }
    
    fun getGoals(): List<Goal> {
        val json = prefs.getString(KEY_GOALS, null) ?: return emptyList()
        val type = object : TypeToken<List<Goal>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun updateGoal(goal: Goal) {
        val goals = getGoals().toMutableList()
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            goals[index] = goal.copy(updatedAt = System.currentTimeMillis())
        } else {
            goals.add(goal)
        }
        saveGoals(goals)
    }
    
    fun deleteGoal(goalId: String) {
        val goals = getGoals().filter { it.id != goalId }
        saveGoals(goals)
    }
    
    // ======================== NOTES ========================
    
    fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        prefs.edit().putString(KEY_NOTES, json).apply()
    }
    
    fun getNotes(): List<Note> {
        val json = prefs.getString(KEY_NOTES, null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addNote(note: Note) {
        val notes = getNotes().toMutableList()
        notes.add(0, note)
        saveNotes(notes)
    }
    
    fun updateNote(note: Note) {
        val notes = getNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note.copy(updatedAt = System.currentTimeMillis())
            saveNotes(notes)
        }
    }
    
    fun deleteNote(noteId: String) {
        val notes = getNotes().filter { it.id != noteId }
        saveNotes(notes)
    }
    
    // ======================== TASKS ========================
    
    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }
    
    fun getTasks(): List<Task> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Task>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addTask(task: Task) {
        val tasks = getTasks().toMutableList()
        tasks.add(0, task)
        saveTasks(tasks)
    }
    
    fun updateTask(task: Task) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(updatedAt = System.currentTimeMillis())
            saveTasks(tasks)
        }
    }
    
    fun deleteTask(taskId: String) {
        val tasks = getTasks().filter { it.id != taskId }
        saveTasks(tasks)
    }
    
    fun toggleTaskCompletion(taskId: String) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null,
                updatedAt = System.currentTimeMillis()
            )
            saveTasks(tasks)
        }
    }
    
    // ======================== CALENDAR EVENTS ========================
    
    fun saveEvents(events: List<CalendarEvent>) {
        val json = gson.toJson(events)
        prefs.edit().putString(KEY_EVENTS, json).apply()
    }
    
    fun getEvents(): List<CalendarEvent> {
        val json = prefs.getString(KEY_EVENTS, null) ?: return emptyList()
        val type = object : TypeToken<List<CalendarEvent>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addEvent(event: CalendarEvent) {
        val events = getEvents().toMutableList()
        events.add(event)
        saveEvents(events)
    }
    
    fun updateEvent(event: CalendarEvent) {
        val events = getEvents().toMutableList()
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1) {
            events[index] = event
            saveEvents(events)
        }
    }
    
    fun deleteEvent(eventId: String) {
        val events = getEvents().filter { it.id != eventId }
        saveEvents(events)
    }
    
    fun getEventsForDate(date: Long): List<CalendarEvent> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return getEvents().filter { it.date in dayStart..dayEnd }
    }
    
    // ======================== HABIT ENTRIES ========================
    
    fun saveHabitEntries(entries: List<HabitEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }
    
    fun getHabitEntries(): List<HabitEntry> {
        val json = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<HabitEntry>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addHabitEntry(entry: HabitEntry) {
        val entries = getHabitEntries().toMutableList()
        // Remove existing entry for same date and goal
        entries.removeAll { 
            getStartOfDay(it.date) == getStartOfDay(entry.date) && it.goalId == entry.goalId 
        }
        entries.add(entry)
        saveHabitEntries(entries)
    }
    
    // ======================== SETTINGS ========================
    
    fun saveSettings(settings: AppSettings) {
        val json = gson.toJson(settings)
        prefs.edit().putString(KEY_SETTINGS, json).apply()
    }
    
    fun getSettings(): AppSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return AppSettings()
        return try {
            gson.fromJson(json, AppSettings::class.java)
        } catch (e: Exception) {
            AppSettings()
        }
    }
    
    // ======================== FIRST LAUNCH ========================
    
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    // ======================== BACKUP & RESTORE ========================
    
    /**
     * Export all data as JSON string for backup
     */
    fun exportAllData(): String {
        val appData = AppData(
            version = 1,
            exportedAt = System.currentTimeMillis(),
            goals = getGoals(),
            notes = getNotes(),
            tasks = getTasks(),
            events = getEvents(),
            habitEntries = getHabitEntries(),
            settings = getSettings()
        )
        return gson.toJson(appData)
    }
    
    /**
     * Import data from JSON string backup
     * @return true if successful, false otherwise
     */
    fun importAllData(jsonData: String): Boolean {
        return try {
            val appData = gson.fromJson(jsonData, AppData::class.java)
            saveGoals(appData.goals)
            saveNotes(appData.notes)
            saveTasks(appData.tasks)
            saveEvents(appData.events)
            saveHabitEntries(appData.habitEntries)
            saveSettings(appData.settings)
            prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Clear all data - use with caution!
     */
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
    
    // ======================== STATS ========================
    
    fun getDashboardStats(): DashboardStats {
        val goals = getGoals()
        val tasks = getTasks()
        val today = getStartOfDay(System.currentTimeMillis())
        val todayEnd = today + 24 * 60 * 60 * 1000 - 1
        
        val totalMilestones = goals.sumOf { it.milestones.size }
        val completedMilestones = goals.sumOf { goal -> 
            goal.milestones.count { it.isCompleted } 
        }
        
        val todayTasks = tasks.filter { task ->
            task.dueDate?.let { it in today..todayEnd } ?: false
        }
        
        val overallProgress = if (totalMilestones > 0) {
            completedMilestones.toFloat() / totalMilestones.toFloat()
        } else 0f
        
        return DashboardStats(
            totalGoals = goals.size,
            completedMilestones = completedMilestones,
            totalMilestones = totalMilestones,
            tasksCompletedToday = todayTasks.count { it.isCompleted },
            totalTasksToday = todayTasks.size,
            currentStreak = calculateCurrentStreak(),
            longestStreak = calculateLongestStreak(),
            overallProgress = overallProgress
        )
    }
    
    private fun calculateCurrentStreak(): Int {
        val entries = getHabitEntries().filter { it.isCompleted }
        if (entries.isEmpty()) return 0
        
        val sortedDates = entries.map { getStartOfDay(it.date) }.distinct().sortedDescending()
        val today = getStartOfDay(System.currentTimeMillis())
        
        var streak = 0
        var expectedDate = today
        
        for (date in sortedDates) {
            if (date == expectedDate || date == expectedDate - 24 * 60 * 60 * 1000) {
                streak++
                expectedDate = date - 24 * 60 * 60 * 1000
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun calculateLongestStreak(): Int {
        val entries = getHabitEntries().filter { it.isCompleted }
        if (entries.isEmpty()) return 0
        
        val sortedDates = entries.map { getStartOfDay(it.date) }.distinct().sorted()
        
        var longestStreak = 1
        var currentStreak = 1
        
        for (i in 1 until sortedDates.size) {
            val diff = sortedDates[i] - sortedDates[i - 1]
            if (diff == 24 * 60 * 60 * 1000L) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }
        
        return longestStreak
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
