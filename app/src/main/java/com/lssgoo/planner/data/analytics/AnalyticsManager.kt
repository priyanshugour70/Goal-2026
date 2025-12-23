package com.lssgoo.planner.data.analytics

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*
import java.util.*

/**
 * Analytics manager for generating insights and reports
 */
class AnalyticsManager(
    private val storageManager: LocalStorageManager
) {
    
    fun generateComprehensiveReport(): AnalyticsData {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - (30L * 24 * 60 * 60 * 1000)
        return generateAnalytics(startDate, endDate)
    }
    
    fun generateAnalytics(
        startDate: Long,
        endDate: Long
    ): AnalyticsData {
        val goals = storageManager.getGoals()
        val tasks = storageManager.getTasks()
        val habits = storageManager.getHabits()
        val habitEntries = storageManager.getHabitEntries()
        
        val goalProgress = goals.map { goal ->
            val completed = goal.milestones.count { it.isCompleted }
            val total = goal.milestones.size
            val progress = if (total > 0) completed.toFloat() / total else 0f
            
            GoalProgressData(
                goalId = goal.id,
                goalTitle = goal.title,
                progress = progress,
                milestonesCompleted = completed,
                totalMilestones = total,
                trend = calculateTrend(goal.id, progress)
            )
        }
        
        val taskCompletion = calculateTaskCompletion(tasks, startDate, endDate)
        val habitStreaks = calculateHabitStreaks(habits, habitEntries)
        val timeSeries = generateTimeSeries(startDate, endDate)
        val categoryBreakdown = calculateCategoryBreakdown(goals)
        val insights = generateInsights(goals, tasks, habits, habitEntries)
        
        return AnalyticsData(
            goalProgress = goalProgress,
            taskCompletion = taskCompletion,
            habitStreaks = habitStreaks,
            timeSeries = timeSeries,
            categoryBreakdown = categoryBreakdown,
            insights = insights
        )
    }
    
    private fun calculateTrend(goalId: String, currentProgress: Float): TrendDirection {
        // Simplified - in real implementation, compare with previous period
        return when {
            currentProgress > 0.7f -> TrendDirection.IMPROVING
            currentProgress < 0.3f -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }
    
    private fun calculateTaskCompletion(
        tasks: List<Task>,
        startDate: Long,
        endDate: Long
    ): TaskCompletionData {
        val filteredTasks = tasks.filter { task ->
            task.createdAt >= startDate && task.createdAt <= endDate
        }
        
        val completed = filteredTasks.count { it.isCompleted }
        val total = filteredTasks.size
        val overdue = filteredTasks.count { task ->
            !task.isCompleted && task.dueDate != null && task.dueDate < System.currentTimeMillis()
        }
        
        val completionRate = if (total > 0) completed.toFloat() / total else 0f
        
        val byPriority = filteredTasks.groupBy { it.priority }
            .mapValues { it.value.count { t -> t.isCompleted } }
        
        val byCategory = filteredTasks.groupBy { it.tags.firstOrNull() ?: "Uncategorized" }
            .mapValues { it.value.count { t -> t.isCompleted } }
        
        // Calculate average completion time (simplified)
        val completedTasks = filteredTasks.filter { it.isCompleted && it.completedAt != null }
        val avgCompletionTime = if (completedTasks.isNotEmpty()) {
            val avgMillis = completedTasks.mapNotNull { task ->
                task.completedAt?.let { it - task.createdAt }?.toDouble()
            }.average()
            (avgMillis / (1000.0 * 60.0 * 60.0)).toLong() // Convert to hours
        } else 0L
        
        return TaskCompletionData(
            totalTasks = total,
            completedTasks = completed,
            overdueTasks = overdue,
            completionRate = completionRate,
            averageCompletionTime = avgCompletionTime,
            byPriority = byPriority,
            byCategory = byCategory
        )
    }
    
    private fun calculateHabitStreaks(
        habits: List<Habit>,
        entries: List<HabitEntry>
    ): List<HabitStreakData> {
        return habits.map { habit ->
            val habitEntries = entries.filter { it.habitId == habit.id && it.isCompleted }
            val currentStreak = calculateCurrentStreak(habitEntries)
            val longestStreak = calculateLongestStreak(habitEntries)
            val totalDays = getDaysSinceCreation(habit.createdAt)
            val completionRate = if (totalDays > 0) habitEntries.size.toFloat() / totalDays else 0f
            
            HabitStreakData(
                habitId = habit.id,
                habitTitle = habit.title,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                completionRate = completionRate
            )
        }
    }
    
    private fun calculateCurrentStreak(entries: List<HabitEntry>): Int {
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
    
    private fun calculateLongestStreak(entries: List<HabitEntry>): Int {
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
    
    private fun generateTimeSeries(startDate: Long, endDate: Long): List<TimeSeriesData> {
        val calendar = Calendar.getInstance()
        val data = mutableListOf<TimeSeriesData>()
        
        calendar.timeInMillis = startDate
        while (calendar.timeInMillis <= endDate) {
            val dayStart = getStartOfDay(calendar.timeInMillis)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
            
            val tasks = storageManager.getTasks().filter { task ->
                task.completedAt != null && task.completedAt!! >= dayStart && task.completedAt!! <= dayEnd
            }
            
            val habitEntries = storageManager.getHabitEntries().filter { entry ->
                entry.isCompleted && entry.date >= dayStart && entry.date <= dayEnd
            }
            
            val goals = storageManager.getGoals()
            val milestones = goals.flatMap { it.milestones }.filter { milestone ->
                milestone.completedAt != null && milestone.completedAt!! >= dayStart && milestone.completedAt!! <= dayEnd
            }
            
            data.add(
                TimeSeriesData(
                    date = dayStart,
                    tasksCompleted = tasks.size,
                    habitsCompleted = habitEntries.size,
                    milestonesCompleted = milestones.size
                )
            )
            
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return data
    }
    
    private fun calculateCategoryBreakdown(goals: List<Goal>): List<CategoryData> {
        return GoalCategory.entries.map { category ->
            val categoryGoals = goals.filter { it.category == category }
            val totalMilestones = categoryGoals.sumOf { it.milestones.size }
            val completedMilestones = categoryGoals.sumOf { goal ->
                goal.milestones.count { it.isCompleted }
            }
            val avgProgress = if (categoryGoals.isNotEmpty()) {
                categoryGoals.map { goal ->
                    val completed = goal.milestones.count { it.isCompleted }
                    val total = goal.milestones.size
                    if (total > 0) completed.toFloat() / total else 0f
                }.average().toFloat()
            } else 0f
            
            CategoryData(
                category = category,
                goalsCount = categoryGoals.size,
                averageProgress = avgProgress,
                totalMilestones = totalMilestones,
                completedMilestones = completedMilestones
            )
        }.filter { it.goalsCount > 0 }
    }
    
    private fun generateInsights(
        goals: List<Goal>,
        tasks: List<Task>,
        habits: List<Habit>,
        habitEntries: List<HabitEntry>
    ): List<Insight> {
        val insights = mutableListOf<Insight>()
        
        // Achievement insights
        val completedGoals = goals.count { goal ->
            goal.milestones.isNotEmpty() && goal.milestones.all { it.isCompleted }
        }
        if (completedGoals > 0) {
            insights.add(
                Insight(
                    type = InsightType.CELEBRATION,
                    title = "🎉 Amazing Progress!",
                    message = "You've completed $completedGoals goal${if (completedGoals > 1) "s" else ""}!",
                    priority = InsightPriority.HIGH
                )
            )
        }
        
        // Warning insights
        val overdueTasks = tasks.count { task ->
            !task.isCompleted && task.dueDate != null && task.dueDate!! < System.currentTimeMillis()
        }
        if (overdueTasks > 0) {
            insights.add(
                Insight(
                    type = InsightType.WARNING,
                    title = "⚠️ Overdue Tasks",
                    message = "You have $overdueTasks overdue task${if (overdueTasks > 1) "s" else ""}. Consider reviewing them.",
                    actionText = "View Tasks",
                    priority = InsightPriority.HIGH
                )
            )
        }
        
        // Suggestion insights
        val inactiveGoals = goals.count { goal ->
            goal.milestones.isNotEmpty() && goal.milestones.none { it.isCompleted }
        }
        if (inactiveGoals > 0) {
            insights.add(
                Insight(
                    type = InsightType.SUGGESTION,
                    title = "💡 Get Started",
                    message = "You have $inactiveGoals goal${if (inactiveGoals > 1) "s" else ""} with no progress yet. Start working on them!",
                    actionText = "View Goals",
                    priority = InsightPriority.MEDIUM
                )
            )
        }
        
        return insights
    }
    
    private fun getDaysSinceCreation(createdAt: Long): Int {
        val days = (System.currentTimeMillis() - createdAt) / (24 * 60 * 60 * 1000)
        return maxOf(1, days.toInt())
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
}

