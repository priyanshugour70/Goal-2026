package com.lssgoo.planner.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*

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
        private const val PREFS_NAME = "planner_storage"
        private const val KEY_GOALS = "goals"
        private const val KEY_NOTES = "notes"
        private const val KEY_TASKS = "tasks"
        private const val KEY_EVENTS = "events"
        private const val KEY_HABITS = "habit_entries"
        private const val KEY_HABITS_LIST = "habits_list"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_REMINDERS = "reminders"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_JOURNAL_ENTRIES = "journal_entries"
        private const val KEY_JOURNAL_PROMPTS = "journal_prompts"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val KEY_FINANCE_TRANSACTIONS = "finance_transactions"
        private const val KEY_FINANCE_BUDGETS = "finance_budgets"
        private const val KEY_FINANCE_LOGS = "finance_logs"
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
    
    // ======================== USER PROFILE ========================
    
    fun saveUserProfile(profile: UserProfile) {
        val json = gson.toJson(profile)
        prefs.edit().putString(KEY_USER_PROFILE, json).apply()
    }
    
    fun getUserProfile(): UserProfile? {
        val json = prefs.getString(KEY_USER_PROFILE, null) ?: return null
        return try {
            gson.fromJson(json, UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }
    
    fun setOnboardingComplete() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }
    
    // ======================== REMINDERS ========================
    
    fun saveReminders(reminders: List<Reminder>) {
        val json = gson.toJson(reminders)
        prefs.edit().putString(KEY_REMINDERS, json).apply()
    }
    
    fun getReminders(): List<Reminder> {
        val json = prefs.getString(KEY_REMINDERS, null) ?: return emptyList()
        val type = object : TypeToken<List<Reminder>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        reminders.add(0, reminder)
        saveReminders(reminders)
    }
    
    fun updateReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder.copy(updatedAt = System.currentTimeMillis())
            saveReminders(reminders)
        }
    }
    
    fun deleteReminder(reminderId: String) {
        val reminders = getReminders().filter { it.id != reminderId }
        saveReminders(reminders)
    }
    
    fun toggleReminderEnabled(reminderId: String) {
        val reminders = getReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index != -1) {
            val reminder = reminders[index]
            reminders[index] = reminder.copy(
                isEnabled = !reminder.isEnabled,
                updatedAt = System.currentTimeMillis()
            )
            saveReminders(reminders)
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
            version = 3,
            exportedAt = System.currentTimeMillis(),
            goals = getGoals(),
            notes = getNotes(),
            tasks = getTasks(),
            events = getEvents(),
            reminders = getReminders(),
            habitEntries = getHabitEntries(),
            habits = getHabits(),
            journalEntries = getJournalEntries(),
            transactions = getTransactions(),
            budgets = getBudgets(),
            logs = getFinanceLogs(),
            userProfile = getUserProfile(),
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
            if (appData.version >= 2) {
                appData.reminders?.let { saveReminders(it) }
                appData.habits?.let { saveHabits(it) }
                appData.journalEntries?.let { saveJournalEntries(it) }
            }
            if (appData.version >= 3) {
                appData.transactions?.let { saveTransactions(it) }
                appData.budgets?.let { saveBudgets(it) }
                appData.logs?.let { saveFinanceLogs(it) }
            }
            saveHabitEntries(appData.habitEntries)
            appData.userProfile?.let { saveUserProfile(it) }
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
        
        // HABIT STATS
        val allHabits = getHabits().filter { it.isActive }
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        // Convert Calendar day (Sun=1) to ISO (Mon=1)
        val todayDayOfWeek = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 + 1
        
        val todayHabits = allHabits.filter { it.frequency.contains(todayDayOfWeek) }
        val todayEntries = getHabitEntriesForDate(today).associateBy { it.habitId }
        
        val habitsCompleted = todayHabits.count { habit ->
            todayEntries[habit.id]?.isCompleted == true
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
            overallProgress = overallProgress,
            totalHabitsToday = todayHabits.size,
            habitsCompletedToday = habitsCompleted
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
    
    // ======================== HABITS ========================
    
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS_LIST, json).apply()
    }
    
    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS_LIST, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
        } else {
            habits.add(habit)
        }
        saveHabits(habits)
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)
    }
    
    fun getHabitEntriesForDateRange(startDate: Long, endDate: Long): List<HabitEntry> {
        return getHabitEntries().filter { 
             it.date >= startDate && it.date <= endDate 
        }
    }
    
    fun getHabitEntries(habitId: String): List<HabitEntry> {
        return getHabitEntries().filter { it.habitId == habitId }
    }
    
    fun getHabitEntriesForDate(date: Long): List<HabitEntry> {
        val start = getStartOfDay(date)
        return getHabitEntries().filter { getStartOfDay(it.date) == start }
    }
    
    fun deleteHabitEntry(entryId: String) {
        val entries = getHabitEntries().toMutableList()
        val entry = entries.find { it.id == entryId }
        if (entry != null) {
            entries.remove(entry)
            saveHabitEntries(entries)
        }
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
        // Remove existing entry for same date and habit
        entries.removeAll { 
            getStartOfDay(it.date) == getStartOfDay(entry.date) && it.habitId == entry.habitId 
        }
        entries.add(entry)
        saveHabitEntries(entries)
    }

    fun updateHabitEntry(entry: HabitEntry) {
        val entries = getHabitEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry
        } else {
            entries.add(entry)
        }
        saveHabitEntries(entries)
    }
    
    // ======================== JOURNAL ========================
    
    fun saveJournalEntries(entries: List<JournalEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString(KEY_JOURNAL_ENTRIES, json).apply()
    }
    
    fun getJournalEntries(): List<JournalEntry> {
        val json = prefs.getString(KEY_JOURNAL_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<JournalEntry>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addJournalEntry(entry: JournalEntry) {
        val entries = getJournalEntries().toMutableList()
        entries.add(0, entry)
        saveJournalEntries(entries)
    }
    
    fun updateJournalEntry(entry: JournalEntry) {
        val entries = getJournalEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry.copy(updatedAt = System.currentTimeMillis())
        } else {
            entries.add(entry)
        }
        saveJournalEntries(entries)
    }
    
    fun deleteJournalEntry(entryId: String) {
        val entries = getJournalEntries().filter { it.id != entryId }
        saveJournalEntries(entries)
    }
    
    fun getJournalEntryForDate(date: Long): JournalEntry? {
        val startOfDay = getStartOfDay(date)
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1
        return getJournalEntries().firstOrNull { 
            it.date >= startOfDay && it.date <= endOfDay 
        }
    }
    
    fun getJournalEntriesForDateRange(startDate: Long, endDate: Long): List<JournalEntry> {
        return getJournalEntries().filter { 
            it.date >= startDate && it.date <= endDate 
        }
    }
    
    fun saveJournalPrompts(prompts: List<JournalPrompt>) {
        val json = gson.toJson(prompts)
        prefs.edit().putString(KEY_JOURNAL_PROMPTS, json).apply()
    }
    
    fun getJournalPrompts(): List<JournalPrompt> {
        val json = prefs.getString(KEY_JOURNAL_PROMPTS, null) ?: return emptyList()
        val type = object : TypeToken<List<JournalPrompt>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // ======================== RECENT SEARCHES ========================
    
    fun saveRecentSearches(searches: List<String>) {
        val json = gson.toJson(searches)
        prefs.edit().putString(KEY_RECENT_SEARCHES, json).apply()
    }
    
    fun getRecentSearches(): List<String> {
        val json = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addRecentSearch(query: String) {
        val searches = getRecentSearches().toMutableList()
        searches.remove(query) // Remove if exists
        searches.add(0, query)
        saveRecentSearches(searches.take(10)) // Keep only last 10
    }

    // ======================== FINANCE ========================

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString(KEY_FINANCE_TRANSACTIONS, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = prefs.getString(KEY_FINANCE_TRANSACTIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTransaction(transaction: Transaction) {
        val list = getTransactions().toMutableList()
        list.add(0, transaction)
        saveTransactions(list)
        logFinanceAction("ADD", "TRANSACTION", transaction.id, "Added ${transaction.type} of ${transaction.amount} in ${transaction.category}")
        
        // Update budget spent amount if it's an expense
        if (transaction.type == TransactionType.EXPENSE) {
            updateBudgetSpending(transaction.category, transaction.amount)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        val list = getTransactions().toMutableList()
        val index = list.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            val old = list[index]
            list[index] = transaction
            saveTransactions(list)
            logFinanceAction("UPDATE", "TRANSACTION", transaction.id, "Updated transaction from ${old.amount} to ${transaction.amount}")
        }
    }

    fun deleteTransaction(id: String) {
        val list = getTransactions().toMutableList()
        val transaction = list.find { it.id == id }
        if (transaction != null) {
            list.remove(transaction)
            saveTransactions(list)
            logFinanceAction("REMOVE", "TRANSACTION", id, "Removed transaction of ${transaction.amount}")
            
            // Revert budget spending if it was an expense
            if (transaction.type == TransactionType.EXPENSE) {
                updateBudgetSpending(transaction.category, -transaction.amount)
            }
        }
    }

    // Budgets
    fun saveBudgets(budgets: List<Budget>) {
        val json = gson.toJson(budgets)
        prefs.edit().putString(KEY_FINANCE_BUDGETS, json).apply()
    }

    fun getBudgets(): List<Budget> {
        val json = prefs.getString(KEY_FINANCE_BUDGETS, null) ?: return emptyList()
        val type = object : TypeToken<List<Budget>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addBudget(budget: Budget) {
        val list = getBudgets().toMutableList()
        list.add(budget)
        saveBudgets(list)
        logFinanceAction("ADD", "BUDGET", budget.id, "Added budget for ${budget.category ?: "Overall"} with limit ${budget.limitAmount}")
    }
    
    fun removeBudget(id: String) {
        val list = getBudgets().toMutableList()
        val budget = list.find { it.id == id }
        if (budget != null) {
            list.remove(budget)
            saveBudgets(list)
            logFinanceAction("REMOVE", "BUDGET", id, "Removed budget for ${budget.category ?: "Overall"}")
        }
    }

    private fun updateBudgetSpending(category: TransactionCategory, amount: Double) {
        val budgets = getBudgets().toMutableList()
        var changed = false
        for (i in budgets.indices) {
            if (budgets[i].category == category || budgets[i].category == null) {
                budgets[i] = budgets[i].copy(spentAmount = budgets[i].spentAmount + amount)
                changed = true
            }
        }
        if (changed) saveBudgets(budgets)
    }

    // Logs
    fun saveFinanceLogs(logs: List<FinanceLog>) {
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_FINANCE_LOGS, json).apply()
    }

    fun getFinanceLogs(): List<FinanceLog> {
        val json = prefs.getString(KEY_FINANCE_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<FinanceLog>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun logFinanceAction(action: String, entityType: String, entityId: String, description: String) {
        val logs = getFinanceLogs().toMutableList()
        logs.add(0, FinanceLog(
            action = action,
            entityType = entityType,
            description = description
        ))
        saveFinanceLogs(logs.take(1000)) // Keep last 1000 logs
    }

    fun getFinanceStats(): FinanceStats {
        val transactions = getTransactions()
        val budgets = getBudgets()
        
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val borrowed = transactions.filter { it.type == TransactionType.BORROWED && !it.isSettled }.sumOf { it.amount }
        val lent = transactions.filter { it.type == TransactionType.LENT && !it.isSettled }.sumOf { it.amount }
        
        return FinanceStats(
            totalIncome = income,
            totalExpense = expense,
            currentBalance = income - expense + borrowed - lent,
            totalBorrowed = borrowed,
            totalLent = lent,
            budgetStatus = budgets,
            recentTransactions = transactions.take(10)
        )
    }
}
