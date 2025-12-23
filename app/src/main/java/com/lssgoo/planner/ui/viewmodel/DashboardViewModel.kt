package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard - aggregates data from multiple repositories
 */
class DashboardViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val goalRepo = GoalRepository(storageManager)
    private val taskRepo = TaskRepository(storageManager)
    private val noteRepo = NoteRepository(storageManager)
    private val financeRepo = FinanceRepository(storageManager)
    
    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()
    
    private val _recentGoals = MutableStateFlow<List<Goal>>(emptyList())
    val recentGoals: StateFlow<List<Goal>> = _recentGoals.asStateFlow()
    
    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch(Dispatchers.IO) {
            val goals = goalRepo.getGoals()
            val tasks = taskRepo.getTasks()
            
            _recentGoals.value = goals.take(5)
            _todayTasks.value = tasks.filter { isToday(it.dueDate) }.take(5)
            
            // Calculate stats
            _stats.value = DashboardStats(
                totalGoals = goals.size,
                completedMilestones = goals.sumOf { it.milestones.count { m -> m.isCompleted } },
                totalMilestones = goals.sumOf { it.milestones.size },
                tasksCompletedToday = tasks.count { it.isCompleted && isToday(it.completedAt) },
                totalTasksToday = tasks.count { isToday(it.dueDate) }
            )
            
            _userProfile.value = storageManager.getUserProfile() ?: UserProfile()
        }
    }
    
    private fun isToday(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val cal = java.util.Calendar.getInstance()
        val today = cal.get(java.util.Calendar.DAY_OF_YEAR)
        val year = cal.get(java.util.Calendar.YEAR)
        cal.timeInMillis = timestamp
        return cal.get(java.util.Calendar.DAY_OF_YEAR) == today && cal.get(java.util.Calendar.YEAR) == year
    }
}
