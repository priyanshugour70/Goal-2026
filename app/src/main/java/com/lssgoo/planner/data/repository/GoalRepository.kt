package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.data.model.DefaultGoals

/**
 * Repository for managing Goals - part of the backend layer
 */
class GoalRepository(private val storage: LocalStorageManager) {

    fun getGoals(): List<Goal> {
        val goals = storage.getGoals()
        // If empty, initialize with defaults
        if (goals.isEmpty() && storage.isFirstLaunch()) {
            val defaults = DefaultGoals.goals
            storage.saveGoals(defaults)
            storage.setFirstLaunchComplete()
            return defaults
        }
        return goals
    }

    fun getGoalById(id: String): Goal? {
        return storage.getGoals().find { it.id == id }
    }

    fun saveGoals(goals: List<Goal>) {
        storage.saveGoals(goals)
    }

    fun saveGoal(goal: Goal) {
        val goals = storage.getGoals().toMutableList()
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            goals[index] = goal.copy(updatedAt = System.currentTimeMillis())
        } else {
            goals.add(goal)
        }
        storage.saveGoals(goals)
    }

    fun updateGoal(goal: Goal) {
        saveGoal(goal)
    }

    fun deleteGoal(goalId: String) {
        val goals = storage.getGoals().filter { it.id != goalId }
        storage.saveGoals(goals)
    }

    fun updateMilestone(goalId: String, updatedMilestone: com.lssgoo.planner.features.goals.models.Milestone) {
        val goals = storage.getGoals().toMutableList()
        val goalIndex = goals.indexOfFirst { it.id == goalId }
        
        if (goalIndex != -1) {
            val goal = goals[goalIndex]
            val updatedMilestones = goal.milestones.map { milestone ->
                if (milestone.id == updatedMilestone.id) {
                    updatedMilestone
                } else milestone
            }
            
            val total = updatedMilestones.size
            val completed = updatedMilestones.count { it.isCompleted }
            val progress = if (total > 0) completed.toFloat() / total else 0f
            
            goals[goalIndex] = goal.copy(
                milestones = updatedMilestones,
                progress = progress,
                updatedAt = System.currentTimeMillis()
            )
            storage.saveGoals(goals)
        }
    }

    fun toggleMilestone(goalId: String, milestoneId: String) {
        val goals = storage.getGoals().toMutableList()
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
            
            storage.saveGoals(goals)
        }
    }

    fun getGoalsSortedByDate(): List<Goal> {
        return getGoals().sortedBy { it.targetDate ?: Long.MAX_VALUE }
    }
}
