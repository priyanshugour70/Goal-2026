package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Root ViewModel for global app state
 */
class PlannerViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    private val syncRepository = com.lssgoo.planner.data.repository.SyncRepository(application)
    
    private val _settings = MutableStateFlow(storageManager.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val _userProfile = MutableStateFlow(storageManager.getUserProfile() ?: UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isOnboardingComplete = MutableStateFlow(storageManager.isOnboardingComplete())
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    private val _isCheckingSync = MutableStateFlow(false)
    val isCheckingSync: StateFlow<Boolean> = _isCheckingSync.asStateFlow()

    private val financeRepository = com.lssgoo.planner.data.repository.FinanceRepository(storageManager)
    
    // Feature data states
    val goals = MutableStateFlow<List<Goal>>(emptyList())
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    val notes = MutableStateFlow<List<Note>>(emptyList())
    val reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val habits = MutableStateFlow<List<Habit>>(emptyList())
    val transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val financeLogs = MutableStateFlow<List<FinanceLog>>(emptyList())
    val budgets = MutableStateFlow<List<Budget>>(emptyList())
    val dashboardStats = MutableStateFlow(DashboardStats())
    val financeStats = MutableStateFlow(FinanceStats())
    val analyticsData = MutableStateFlow<AnalyticsData?>(null)
    val searchQuery = MutableStateFlow("")
    val searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val recentSearches = MutableStateFlow<List<String>>(emptyList())
    val searchFilters = MutableStateFlow(SearchFilters())
    val selectedDate = MutableStateFlow(System.currentTimeMillis())
    val events = MutableStateFlow<List<CalendarEvent>>(emptyList())

    init {
        checkCloudBackup()
        loadFinanceData()
    }

    private fun checkCloudBackup() {
        if (!_isOnboardingComplete.value) {
            viewModelScope.launch {
                _isCheckingSync.value = true
                val restored = syncRepository.checkAndDownloadBackup()
                if (restored) {
                    _userProfile.value = storageManager.getUserProfile() ?: UserProfile()
                    _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                    _settings.value = storageManager.getSettings()
                    showSnackbar("Data restored from cloud!")
                }
                _isCheckingSync.value = false
            }
        }
    }
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
            // Sync to cloud when profile is saved or onboarding complete
            syncToCloud()
        }
    }
    
    fun setOnboardingComplete(v: Boolean = true) {
        viewModelScope.launch {
            storageManager.setOnboardingComplete()
            _isOnboardingComplete.value = true
            syncToCloud()
        }
    }
    
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            storageManager.saveSettings(newSettings)
            _settings.value = newSettings
            syncToCloud()
        }
    }
    
    fun updateUserProfile(profile: UserProfile) {
        saveUserProfile(profile)
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            storageManager.clearAllData()
            _userProfile.value = UserProfile()
            _isOnboardingComplete.value = false
        }
    }
    
    val lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    
    fun syncToCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            val (success, error) = syncRepository.syncToCloud()
            _isSyncing.value = false
            if (success) {
                lastSyncTime.value = System.currentTimeMillis()
            } else if (error != null) {
                showSnackbar("Cloud sync failed: $error")
            }
        }
    }
    
    fun syncFromCloud() {
        viewModelScope.launch {
            _isCheckingSync.value = true
            val restored = syncRepository.checkAndDownloadBackup()
            if (restored) {
                _userProfile.value = storageManager.getUserProfile() ?: UserProfile()
                _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                _settings.value = storageManager.getSettings()
                showSnackbar("Data restored!")
            } else {
                showSnackbar("No cloud backup found or restored")
            }
            _isCheckingSync.value = false
        }
    }
    fun exportDataToFile(context: Context): android.net.Uri? = null
    fun importData(json: String): Boolean = true
    fun initializeAutoSync() {}

    private fun loadFinanceData() {
        viewModelScope.launch {
            transactions.value = financeRepository.getTransactions()
            budgets.value = financeRepository.getBudgets()
            financeLogs.value = financeRepository.getLogs()
            financeStats.value = financeRepository.getFinanceStats()
        }
    }

    // Feature methods
    fun refreshAnalytics() {}
    fun updateSearchQuery(q: String) {}
    fun updateSearchFilters(f: SearchFilters) {}
    fun clearSearch() {}
    fun setSelectedDate(t: Long) {}
    fun addEvent(e: CalendarEvent) {}
    fun deleteEvent(id: String) {}
    fun toggleMilestone(g: String, m: String) {}
    fun toggleTaskCompletion(t: String) {}
    fun toggleReminderEnabled(id: String) {}
    fun deleteTask(t: String) {}
    fun addTask(t: Task) {}
    fun updateTask(t: Task) {}
    fun addReminder(r: Reminder) {}
    fun updateReminder(r: Reminder) {}
    fun deleteReminder(id: String) {}
    fun addJournalEntry(e: JournalEntry) {
        viewModelScope.launch {
            storageManager.addJournalEntry(e)
            syncToCloud()
        }
    }
    fun updateTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.updateTransaction(tr)
            loadFinanceData()
            syncToCloud()
        }
    }
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(id)
            loadFinanceData()
            syncToCloud()
        }
    }
    fun addTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.addTransaction(tr)
            loadFinanceData()
            syncToCloud()
        }
    }
    fun addBudget(b: Budget) {
        viewModelScope.launch {
            financeRepository.addBudget(b)
            loadFinanceData()
            syncToCloud()
        }
    }
    fun removeBudget(id: String) {
        viewModelScope.launch {
            financeRepository.removeBudget(id)
            loadFinanceData()
            syncToCloud()
        }
    }

    fun settleDebt(id: String) {
        viewModelScope.launch {
            financeRepository.settleDebt(id)
            loadFinanceData()
            syncToCloud()
        }
    }

    fun exportFinanceCSV(): String = financeRepository.generateTransactionsCSV()
    fun addHabit(h: Habit) {
        viewModelScope.launch {
            storageManager.addHabit(h)
            syncToCloud()
        }
    }
    fun getHabitStats(id: String): HabitStats = HabitStats(id)
    fun getHabitEntriesForDate(d: Long): List<HabitEntry> = emptyList()
    fun toggleHabitEntry(id: String, d: Long) {}
    fun addNote(n: Note) {
        viewModelScope.launch {
            storageManager.addNote(n)
            syncToCloud()
        }
    }
    fun updateNote(n: Note) {}
    fun deleteNote(id: String) {}
    fun toggleNotePin(id: String) {}
    fun getUserGreeting(): String = "Hello!"
    fun getUpcomingTasks(): List<Task> = emptyList()
    fun getJournalEntryForDate(d: Long): JournalEntry? = null
    fun getGoalById(id: String): Goal? = null
    fun getAllItemsForDate(d: Long): List<CalendarItem> = emptyList()
}
