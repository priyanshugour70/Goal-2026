package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Habits feature
 */
class HabitsViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    init {
        loadHabits()
    }
    
    fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            _habits.value = storageManager.getHabits()
        }
    }
    
    fun toggleHabit(habitId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val habits = storageManager.getHabits().toMutableList()
            val index = habits.indexOfFirst { it.id == habitId }
            if (index != -1) {
                val habit = habits[index]
                // Logic to toggle habit for today
                // ...
                storageManager.saveHabits(habits)
                loadHabits()
            }
        }
    }
}
