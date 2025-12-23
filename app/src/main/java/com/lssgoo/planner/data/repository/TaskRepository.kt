package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Task
import com.lssgoo.planner.data.model.TaskPriority
import java.util.Calendar

/**
 * Repository for managing Tasks - part of the backend layer
 */
class TaskRepository(private val storage: LocalStorageManager) {

    fun getTasks(): List<Task> {
        val tasks = storage.getTasks()
        if (tasks.isEmpty()) {
            return seedDefaultTasks()
        }
        return tasks
    }

    private fun seedDefaultTasks(): List<Task> {
        val today = getStartOfDay(System.currentTimeMillis())
        val defaults = listOf(
            Task(
                title = "Plan my 2026 goals",
                description = "Outline the key achievements for this year",
                priority = TaskPriority.HIGH,
                dueDate = today + 9 * 60 * 60 * 1000,
                tags = listOf(com.lssgoo.planner.features.tasks.models.TaskTags.PERSONAL)
            ),
            Task(
                title = "Check weekly grocery",
                description = "Milk, Fruits, and Vegetables",
                priority = TaskPriority.MEDIUM,
                dueDate = today + 18 * 60 * 60 * 1000,
                tags = listOf(com.lssgoo.planner.features.tasks.models.TaskTags.SHOPPING)
            ),
            Task(
                title = "Morning focused work block",
                description = "Complete the most important task of the day",
                priority = TaskPriority.URGENT,
                dueDate = today + 10 * 60 * 60 * 1000,
                tags = listOf(com.lssgoo.planner.features.tasks.models.TaskTags.OFFICE)
            ),
            Task(
                title = "Evening walk or exercise",
                description = "Stay healthy and active",
                priority = TaskPriority.LOW,
                dueDate = today + 19 * 60 * 60 * 1000,
                tags = listOf(com.lssgoo.planner.features.tasks.models.TaskTags.HEALTH)
            )
        )
        storage.saveTasks(defaults)
        return defaults
    }

    fun saveTask(task: Task) {
        val tasks = storage.getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(updatedAt = System.currentTimeMillis())
        } else {
            tasks.add(0, task)
        }
        storage.saveTasks(tasks)
    }

    fun deleteTask(taskId: String) {
        val tasks = storage.getTasks().filter { it.id != taskId }
        storage.saveTasks(tasks)
    }

    fun toggleTaskCompletion(taskId: String) {
        val tasks = storage.getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null,
                updatedAt = System.currentTimeMillis()
            )
            storage.saveTasks(tasks)
        }
    }

    fun getTasksForDate(date: Long): List<Task> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        return storage.getTasks().filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }
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
