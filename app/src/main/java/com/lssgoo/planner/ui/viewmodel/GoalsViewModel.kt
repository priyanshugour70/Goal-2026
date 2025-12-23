package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.data.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Goals feature
 */
class GoalsViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val goalRepository = GoalRepository(storageManager)
    
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()
    
    init {
        loadGoals()
    }
    
    fun loadGoals() {
        viewModelScope.launch(Dispatchers.IO) {
            _goals.value = goalRepository.getGoals()
        }
    }
    
    fun updateGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.updateGoal(goal)
            loadGoals()
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.saveGoal(goal)
            loadGoals()
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.deleteGoal(goalId)
            loadGoals()
        }
    }
    
    fun toggleMilestone(goalId: String, milestoneId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.toggleMilestone(goalId, milestoneId)
            loadGoals()
        }
    }

    fun updateMilestone(goalId: String, milestone: com.lssgoo.planner.features.goals.models.Milestone) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.updateMilestone(goalId, milestone)
            loadGoals()
        }
    }

    fun deleteMilestone(goalId: String, milestoneId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = getGoalById(goalId)
            if (goal != null) {
                val updatedMilestones = goal.milestones.filter { it.id != milestoneId }
                val total = updatedMilestones.size
                val completed = updatedMilestones.count { it.isCompleted }
                val progress = if (total > 0) completed.toFloat() / total else 0f
                
                goalRepository.updateGoal(goal.copy(
                    milestones = updatedMilestones,
                    progress = progress
                ))
                loadGoals()
            }
        }
    }

    fun getGoalsSortedByDate(): List<Goal> {
        return _goals.value.sortedBy { it.targetDate ?: Long.MAX_VALUE }
    }
    
    fun getGoalById(goalId: String): Goal? {
        return _goals.value.find { it.id == goalId }
    }
}
