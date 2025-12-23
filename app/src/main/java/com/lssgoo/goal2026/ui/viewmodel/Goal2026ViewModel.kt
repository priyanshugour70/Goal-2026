package com.lssgoo.goal2026.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lssgoo.goal2026.data.local.LocalStorageManager
import com.lssgoo.goal2026.data.model.*
import com.lssgoo.goal2026.data.remote.S3Manager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Goal2026ViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val s3Manager = S3Manager(application)
    
    // Goals State
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()
    
    // Notes State
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    // Tasks State
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    // Events State
    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()
    
    // Reminders State
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()
    
    // User Profile State
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    // Onboarding State
    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()
    
    // Dashboard Stats
    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()
    
    // Settings
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    // Selected date for calendar
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    
    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Snackbar messages
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    // S3 Sync state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()
    
    private val _lastSyncTime = MutableStateFlow<Long?>(null)
    val lastSyncTime: StateFlow<Long?> = _lastSyncTime.asStateFlow()
    
    // Habits State
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    // Journal State
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()
    
    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val _searchFilters = MutableStateFlow(SearchFilters())
    val searchFilters: StateFlow<SearchFilters> = _searchFilters.asStateFlow()
    
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()
    // Analytics State
    private val _analyticsData = MutableStateFlow<AnalyticsData?>(null)
    val analyticsData: StateFlow<AnalyticsData?> = _analyticsData.asStateFlow()
    
    private val _analyticsDateRange = MutableStateFlow(
        Pair(
            System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, // 30 days ago
            System.currentTimeMillis()
        )
    )
    val analyticsDateRange: StateFlow<Pair<Long, Long>> = _analyticsDateRange.asStateFlow()
    
    // Finance State
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()
    
    private val _financeStats = MutableStateFlow(FinanceStats())
    val financeStats: StateFlow<FinanceStats> = _financeStats.asStateFlow()
    
    private val _financeLogs = MutableStateFlow<List<FinanceLog>>(emptyList())
    val financeLogs: StateFlow<List<FinanceLog>> = _financeLogs.asStateFlow()
    
    // Managers
    private val searchManager = com.lssgoo.goal2026.data.search.SearchManager(storageManager)
    private val analyticsManager = com.lssgoo.goal2026.data.analytics.AnalyticsManager(storageManager)
    
    init {
        loadAllData()
    }
    
    /**
     * Initialize auto-sync (call after ViewModel is created)
     */
    fun initializeAutoSync() {
        // Auto-sync on app start if online
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Wait 2 seconds after app start
            autoSync()
        }
    }
    
    private fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            withContext<Unit>(Dispatchers.IO) {
                // Load onboarding status first
                val onboardingComplete = storageManager.isOnboardingComplete()
                
                // If onboarding not complete, check S3 for existing data using IMEI
                if (!onboardingComplete) {
                    try {
                        val result = s3Manager.downloadFromS3()
                        val data = result.first
                        val error = result.second
                        
                        if (data != null && error == null) {
                            val success = storageManager.importAllData(data)
                            if (success) {
                                // Data recovered!
                                _snackbarMessage.value = "Welcome back! Your data has been recovered."
                            }
                        }
                    } catch (e: Exception) {
                        // Silent fail for auto-recovery
                    }
                }
                
                _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                _userProfile.value = storageManager.getUserProfile()
                
                // Check if first launch and initialize default goals
                if (storageManager.isFirstLaunch() && !storageManager.isOnboardingComplete()) {
                    storageManager.saveGoals(DefaultGoals.goals)
                    storageManager.setFirstLaunchComplete()
                }
                
                _goals.value = storageManager.getGoals()
                _notes.value = storageManager.getNotes()
                _tasks.value = storageManager.getTasks()
                _events.value = storageManager.getEvents()
                _reminders.value = storageManager.getReminders()
                _habits.value = storageManager.getHabits()
                _journalEntries.value = storageManager.getJournalEntries()
                _transactions.value = storageManager.getTransactions()
                _budgets.value = storageManager.getBudgets()
                _financeStats.value = storageManager.getFinanceStats()
                _financeLogs.value = storageManager.getFinanceLogs()
                _settings.value = storageManager.getSettings()
                _recentSearches.value = storageManager.getRecentSearches()
                _dashboardStats.value = storageManager.getDashboardStats()
                
            }
            _isLoading.value = false
        }
    }
    
    fun refreshData() {
        loadAllData()
    }
    
    // ======================== GOALS ========================
    
    fun updateGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateGoal(goal)
            _goals.value = storageManager.getGoals()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun toggleMilestone(goalId: String, milestoneId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goals = storageManager.getGoals().toMutableList()
            val goalIndex = goals.indexOfFirst { it.id == goalId }
            
            if (goalIndex != -1) {
                val goal = goals[goalIndex]
                val updatedMilestones = goal.milestones.map { milestone ->
                    if (milestone.id == milestoneId) {
                        milestone.copy(
                            isCompleted = !milestone.isCompleted,
                            completedAt = if (!milestone.isCompleted) System.currentTimeMillis() else null
                        )
                    } else milestone
                }
                
                val completedCount = updatedMilestones.count { it.isCompleted }
                val newProgress = if (updatedMilestones.isNotEmpty()) {
                    completedCount.toFloat() / updatedMilestones.size
                } else 0f
                
                goals[goalIndex] = goal.copy(
                    milestones = updatedMilestones,
                    progress = newProgress,
                    updatedAt = System.currentTimeMillis()
                )
                
                storageManager.saveGoals(goals)
                _goals.value = goals
                _dashboardStats.value = storageManager.getDashboardStats()
            }
        }
    }
    
    fun getGoalById(goalId: String): Goal? {
        return _goals.value.find { it.id == goalId }
    }
    
    // ======================== NOTES ========================
    
    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addNote(note)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note added successfully!")
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateNote(note)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note updated!")
    }
    
    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteNote(noteId)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note deleted")
    }
    
    fun toggleNotePin(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = storageManager.getNotes().toMutableList()
            val index = notes.indexOfFirst { it.id == noteId }
            if (index != -1) {
                notes[index] = notes[index].copy(isPinned = !notes[index].isPinned)
                storageManager.saveNotes(notes)
                _notes.value = notes.sortedByDescending { it.isPinned }
            }
        }
    }
    
    fun getNoteById(noteId: String): Note? {
        return _notes.value.find { it.id == noteId }
    }
    
    // ======================== TASKS ========================
    
    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addTask(task)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
        showSnackbar("Task added!")
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateTask(task)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteTask(taskId)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
        showSnackbar("Task deleted")
    }
    
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.toggleTaskCompletion(taskId)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun getTasksForDate(date: Long): List<Task> {
        val dayStart = this.getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        return _tasks.value.filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }
    }
    
    fun getTodayTasks(): List<Task> {
        return getTasksForDate(System.currentTimeMillis())
    }
    
    fun getUpcomingTasks(): List<Task> {
        val now = System.currentTimeMillis()
        return _tasks.value
            .filter { !it.isCompleted && (it.dueDate == null || it.dueDate >= now) }
            .sortedBy { it.dueDate ?: Long.MAX_VALUE }
            .take(5)
    }
    
    // ======================== EVENTS ========================
    
    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addEvent(event)
            _events.value = storageManager.getEvents()
        }
        showSnackbar("Event added to calendar!")
    }
    
    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateEvent(event)
            _events.value = storageManager.getEvents()
        }
    }
    
    fun deleteEvent(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteEvent(eventId)
            _events.value = storageManager.getEvents()
        }
        showSnackbar("Event deleted")
    }
    
    fun getEventsForDate(date: Long): List<CalendarEvent> {
        val dayStart = this.getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        return _events.value.filter { it.date in dayStart..dayEnd }
    }
    
    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }
    
    // ======================== SETTINGS ========================
    
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }
    
    // ======================== BACKUP & RESTORE ========================
    
    fun exportData(): String {
        return storageManager.exportAllData()
    }
    
    fun exportDataToFile(context: Context): Uri? {
        return try {
            val jsonData = storageManager.exportAllData()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
            val fileName = "Goal2026_Backup_${dateFormat.format(Date())}.json"
            
            val file = File(context.cacheDir, fileName)
            file.writeText(jsonData)
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun importData(jsonData: String): Boolean {
        val success = storageManager.importAllData(jsonData)
        if (success) {
            loadAllData()
            showSnackbar("Data restored successfully!")
        } else {
            showSnackbar("Failed to restore data. Invalid backup file.")
        }
        return success
    }
    
    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.clearAllData()
            // Reinitialize with default goals
            storageManager.saveGoals(DefaultGoals.goals)
            loadAllData()
        }
        showSnackbar("All data cleared. Default goals restored.")
    }
    
    // ======================== S3 CLOUD SYNC ========================
    
    /**
     * Upload local data to S3
     */
    fun syncToCloud() {
        viewModelScope.launch {
            if (_isSyncing.value) return@launch
            
            _isSyncing.value = true
            try {
                val jsonData = storageManager.exportAllData()
                val (success, error) = s3Manager.uploadToS3(jsonData)
                
                if (success) {
                    _lastSyncTime.value = System.currentTimeMillis()
                    showSnackbar("Data synced to cloud successfully!")
                } else {
                    showSnackbar("Sync failed: ${error ?: "Unknown error"}")
                }
            } catch (e: Exception) {
                showSnackbar("Sync error: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }
    
    /**
     * Download data from S3 and merge with local data
     */
    fun syncFromCloud() {
        viewModelScope.launch {
            if (_isSyncing.value) return@launch
            
            _isSyncing.value = true
            try {
                val (jsonData, error) = s3Manager.downloadFromS3()
                
                if (error != null) {
                    showSnackbar("Sync failed: $error")
                } else if (jsonData != null) {
                    val success = storageManager.importAllData(jsonData)
                    if (success) {
                        loadAllData()
                        _lastSyncTime.value = System.currentTimeMillis()
                        showSnackbar("Data synced from cloud successfully!")
                    } else {
                        showSnackbar("Failed to import cloud data")
                    }
                } else {
                    showSnackbar("No backup found in cloud")
                }
            } catch (e: Exception) {
                showSnackbar("Sync error: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }
    
    /**
     * Check if cloud backup exists
     */
    fun checkCloudBackup(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val (exists, error) = s3Manager.backupExists()
            if (error == null) {
                callback(exists)
            } else {
                callback(false)
            }
        }
    }
    
    /**
     * Auto-sync: Upload local data if online
     */
    fun autoSync() {
        if (s3Manager.isOnline()) {
            syncToCloud()
        }
    }
    
    // ======================== UTILITIES ========================
    
    private fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
    
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    // ======================== USER PROFILE ========================
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
        }
        showSnackbar("Profile saved!")
    }
    
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProfile = profile.copy(updatedAt = System.currentTimeMillis())
            storageManager.saveUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }
    
    fun setOnboardingComplete() {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.setOnboardingComplete()
            _isOnboardingComplete.value = true
        }
    }
    
    fun getUserGreeting(): String {
        return _userProfile.value?.greeting ?: run {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            when {
                hour < 12 -> "Good Morning!"
                hour < 17 -> "Good Afternoon!"
                else -> "Good Evening!"
            }
        }
    }
    
    // ======================== REMINDERS ========================
    
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addReminder(reminder)
            _reminders.value = storageManager.getReminders()
        }
        showSnackbar("Reminder set!")
    }
    
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateReminder(reminder)
            _reminders.value = storageManager.getReminders()
        }
    }
    
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteReminder(reminderId)
            _reminders.value = storageManager.getReminders()
        }
        showSnackbar("Reminder deleted")
    }
    
    fun toggleReminderEnabled(reminderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.toggleReminderEnabled(reminderId)
            _reminders.value = storageManager.getReminders()
        }
    }
    
    fun getRemindersForDate(date: Long): List<Reminder> {
        val dayStart = this.getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        return _reminders.value.filter { it.reminderTime in dayStart..dayEnd }
    }
    
    fun getUpcomingReminders(): List<Reminder> {
        val now = System.currentTimeMillis()
        return _reminders.value
            .filter { it.isEnabled && !it.isCompleted && it.reminderTime >= now }
            .sortedBy { it.reminderTime }
            .take(5)
    }
    
    // ======================== CALENDAR INTEGRATION ========================
    
    /**
     * Get all items for a specific date (tasks, events, reminders, notes with reminders)
     * This provides unified calendar view
     */
    fun getAllItemsForDate(date: Long): List<CalendarItem> {
        val dayStart = this.getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        
        val items = mutableListOf<CalendarItem>()
        
        // Add tasks
        _tasks.value.filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }.forEach { task ->
            items.add(
                CalendarItem(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    date = task.dueDate ?: date,
                    type = CalendarItemType.TASK,
                    priority = task.itemPriority,
                    color = task.priority.color,
                    isCompleted = task.isCompleted,
                    linkedGoalId = task.linkedGoalId
                )
            )
        }
        
        // Add events
        _events.value.filter { event ->
            event.date in dayStart..dayEnd
        }.forEach { event ->
            items.add(
                CalendarItem(
                    id = event.id,
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    type = CalendarItemType.EVENT,
                    priority = event.priority,
                    color = event.color,
                    linkedGoalId = event.linkedGoalId
                )
            )
        }
        
        // Add reminders
        _reminders.value.filter { reminder ->
            reminder.reminderTime in dayStart..dayEnd
        }.forEach { reminder ->
            items.add(
                CalendarItem(
                    id = reminder.id,
                    title = reminder.title,
                    description = reminder.description,
                    date = reminder.reminderTime,
                    type = CalendarItemType.REMINDER,
                    priority = reminder.priority,
                    color = reminder.color,
                    isCompleted = reminder.isCompleted,
                    linkedGoalId = reminder.linkedGoalId
                )
            )
        }
        
        // Add notes with reminders
        _notes.value.filter { note ->
            note.hasReminder && note.reminderTime?.let { it in dayStart..dayEnd } ?: false
        }.forEach { note ->
            items.add(
                CalendarItem(
                    id = note.id,
                    title = note.title,
                    description = note.content.take(100),
                    date = note.reminderTime ?: date,
                    type = CalendarItemType.NOTE,
                    priority = note.priority,
                    color = note.color,
                    linkedGoalId = note.linkedGoalId
                )
            )
        }
        
        return items.sortedBy { it.date }
    }
    
    // ======================== HABITS ========================
    
    fun addHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addHabit(habit)
            _habits.value = storageManager.getHabits()
        }
        showSnackbar("Habit added!")
    }
    
    fun updateHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateHabit(habit)
            _habits.value = storageManager.getHabits()
        }
        showSnackbar("Habit updated!")
    }
    
    fun deleteHabit(habitId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteHabit(habitId)
            _habits.value = storageManager.getHabits()
        }
        showSnackbar("Habit deleted")
    }
    
    fun toggleHabitEntry(habitId: String, date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val habit = _habits.value.find { it.id == habitId } ?: return@launch
            val targetDateStart = getStartOfDay(date)
            val getStartOfDayFunc: (Long) -> Long = ::getStartOfDay
            val existingEntry = storageManager.getHabitEntries()
                .firstOrNull { entry -> 
                    entry.goalId == habit.goalId && getStartOfDayFunc(entry.date) == targetDateStart 
                }
            
            if (existingEntry != null) {
                val updatedEntry = existingEntry.copy(isCompleted = !existingEntry.isCompleted)
                storageManager.addHabitEntry(updatedEntry)
            } else {
                val newEntry = HabitEntry(
                    date = date,
                    goalId = habit.goalId,
                    isCompleted = true
                )
                storageManager.addHabitEntry(newEntry)
            }
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun getHabitEntriesForDate(date: Long): List<HabitEntry> {
        val startOfDay = this.getStartOfDay(date)
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000L) - 1L
        return storageManager.getHabitEntries().filter { 
            it.date >= startOfDay && it.date <= endOfDay 
        }
    }
    
    fun getHabitStats(habitId: String): HabitStats {
        val habit = _habits.value.find { it.id == habitId } ?: return HabitStats(habitId)
        val entries = storageManager.getHabitEntriesForGoal(habit.goalId)
        val completedEntries = entries.filter { it.isCompleted }
        
        val sortedDates = completedEntries.map { this.getStartOfDay(it.date) }.distinct().sortedDescending()
        val today = this.getStartOfDay(System.currentTimeMillis())
        
        var currentStreak = 0
        var expectedDate = today
        for (date in sortedDates) {
            if (date == expectedDate || date == expectedDate - (24 * 60 * 60 * 1000L)) {
                currentStreak++
                expectedDate = date - (24 * 60 * 60 * 1000L)
            } else {
                break
            }
        }
        
        var longestStreak = 1
        var tempStreak = 1
        for (i in 1 until sortedDates.size) {
            val diff = sortedDates[i] - sortedDates[i - 1]
            if (diff == 24 * 60 * 60 * 1000L) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        
        val totalDays = maxOf(1, ((System.currentTimeMillis() - habit.createdAt) / (24 * 60 * 60 * 1000L)).toInt())
        val completionRate = if (totalDays > 0) completedEntries.size.toFloat() / totalDays.toFloat() else 0f
        
        return HabitStats(
            habitId = habitId,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            completionRate = completionRate,
            totalCompletions = completedEntries.size,
            totalDays = totalDays,
            lastCompletedDate = completedEntries.maxOfOrNull { it.date }
        )
    }
    
    fun getHabitCalendar(habitId: String, month: Int, year: Int): Map<Long, HabitEntry> {
        val habit = _habits.value.find { it.id == habitId } ?: return emptyMap()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        val startOfMonth = this.getStartOfDay(calendar.timeInMillis)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = this.getStartOfDay(calendar.timeInMillis) + (24 * 60 * 60 * 1000L) - 1L
        
        val entries = storageManager.getHabitEntriesForDateRange(startOfMonth, endOfMonth)
            .filter { it.goalId == habit.goalId }
        
        return entries.associateBy { this.getStartOfDay(it.date) }
    }
    
    // ======================== JOURNAL ========================
    
    fun addJournalEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addJournalEntry(entry)
            _journalEntries.value = storageManager.getJournalEntries()
        }
        showSnackbar("Journal entry saved!")
    }
    
    fun updateJournalEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateJournalEntry(entry)
            _journalEntries.value = storageManager.getJournalEntries()
        }
        showSnackbar("Journal entry updated!")
    }
    
    fun deleteJournalEntry(entryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteJournalEntry(entryId)
            _journalEntries.value = storageManager.getJournalEntries()
        }
        showSnackbar("Journal entry deleted")
    }
    
    fun getJournalEntryForDate(date: Long): JournalEntry? {
        return storageManager.getJournalEntryForDate(date)
    }
    
    fun getJournalEntriesForDateRange(startDate: Long, endDate: Long): List<JournalEntry> {
        return storageManager.getJournalEntriesForDateRange(startDate, endDate)
    }
    
    fun getJournalStats(): JournalStats {
        val entries = _journalEntries.value
        val now = System.currentTimeMillis()
        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val entriesThisMonth = entries.count { it.date >= monthStart }
        val avgMood = if (entries.isNotEmpty()) {
            entries.map { it.mood.ordinal.toFloat() }.average().toFloat()
        } else 0f
        
        // Calculate streak
        val sortedDates = entries.map { this.getStartOfDay(it.date) }.distinct().sortedDescending()
        val today = this.getStartOfDay(now)
        var currentStreak = 0
        var expectedDate = today
        
        for (date in sortedDates) {
            if (date == expectedDate || date == expectedDate - (24 * 60 * 60 * 1000L)) {
                currentStreak++
                expectedDate = date - (24 * 60 * 60 * 1000L)
            } else {
                break
            }
        }
        
        var longestStreak = 1
        var tempStreak = 1
        for (i in 1 until sortedDates.size) {
            val diff = sortedDates[i] - sortedDates[i - 1]
            if (diff == 24 * 60 * 60 * 1000L) {
                tempStreak++
                longestStreak = maxOf(longestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        
        val allTags = entries.flatMap { it.tags }
        val tagCounts = allTags.groupingBy { it }.eachCount()
        val mostUsedTags = tagCounts.toList().sortedByDescending { it.second }.take(5)
        
        return JournalStats(
            totalEntries = entries.size,
            entriesThisMonth = entriesThisMonth,
            averageMood = avgMood,
            longestStreak = longestStreak,
            currentStreak = currentStreak,
            mostUsedTags = mostUsedTags
        )
    }
    
    fun getRandomPrompt(category: PromptCategory? = null): JournalPrompt {
        val prompts = storageManager.getJournalPrompts()
        val filtered = if (category != null) {
            prompts.filter { it.category == category && !it.isUsed }
        } else {
            prompts.filter { !it.isUsed }
        }
        return if (filtered.isNotEmpty()) {
            filtered.random()
        } else {
            JournalPrompt(text = "How did today go?", category = PromptCategory.REFLECTION)
        }
    }
    
    // ======================== SEARCH ========================
    
    fun performSearch(query: String, filters: SearchFilters = SearchFilters()) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchQuery.value = query
            _searchFilters.value = filters
            val results = searchManager.search(
                query = query,
                goals = _goals.value,
                tasks = _tasks.value,
                notes = _notes.value,
                events = _events.value,
                reminders = _reminders.value,
                habits = _habits.value,
                journalEntries = _journalEntries.value,
                transactions = _transactions.value,
                filters = filters
            )
            _searchResults.value = results
            
            // Update recent searches state
            _recentSearches.value = storageManager.getRecentSearches()
            if (query.isNotBlank()) {
                storageManager.addRecentSearch(query)
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            performSearch(query, _searchFilters.value)
        } else {
            _searchResults.value = emptyList()
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _searchFilters.value = SearchFilters()
    }
    
    fun updateSearchFilters(filters: SearchFilters) {
        _searchFilters.value = filters
        if (_searchQuery.value.isNotBlank()) {
            performSearch(_searchQuery.value, filters)
        }
    }
    
    // ======================== ANALYTICS ========================
    
    fun loadAnalytics(startDate: Long, endDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _analyticsDateRange.value = Pair(startDate, endDate)
            val data = analyticsManager.generateAnalytics(startDate, endDate)
            _analyticsData.value = data
        }
    }
    
    fun refreshAnalytics() {
        val (start, end) = _analyticsDateRange.value
        loadAnalytics(start, end)
    }
    
    fun updateDateRange(startDate: Long, endDate: Long) {
        _analyticsDateRange.value = Pair(startDate, endDate)
        loadAnalytics(startDate, endDate)
    }

    // ======================== FINANCE ========================

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addTransaction(transaction)
            refreshFinanceData()
        }
        showSnackbar("Transaction added!")
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateTransaction(transaction)
            refreshFinanceData()
        }
        showSnackbar("Transaction updated!")
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteTransaction(id)
            refreshFinanceData()
        }
        showSnackbar("Transaction deleted")
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addBudget(budget)
            refreshFinanceData()
        }
        showSnackbar("Budget added!")
    }

    fun removeBudget(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.removeBudget(id)
            refreshFinanceData()
        }
        showSnackbar("Budget removed")
    }

    private suspend fun refreshFinanceData() {
        _transactions.value = storageManager.getTransactions()
        _budgets.value = storageManager.getBudgets()
        _financeStats.value = storageManager.getFinanceStats()
        _financeLogs.value = storageManager.getFinanceLogs()
    }
}
