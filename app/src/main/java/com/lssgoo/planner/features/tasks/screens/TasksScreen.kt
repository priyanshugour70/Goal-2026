package com.lssgoo.planner.features.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Task
import com.lssgoo.planner.features.tasks.components.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.components.EmptyState
import com.lssgoo.planner.ui.components.GradientFAB
import com.lssgoo.planner.ui.viewmodel.GoalsViewModel
import com.lssgoo.planner.ui.viewmodel.TasksViewModel
import java.util.*

/**
 * Tasks list screen - follows SRP and size constraints
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    goalsViewModel: GoalsViewModel, // Added to provide goals for task linking
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val goals by goalsViewModel.goals.collectAsState()
    
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayEnd = today + 24 * 60 * 60 * 1000 - 1
    
    val filteredTasks = when (selectedFilter) {
        TaskFilter.ALL -> tasks
        TaskFilter.TODAY -> tasks.filter { it.dueDate?.let { it in today..todayEnd } ?: false }
        TaskFilter.UPCOMING -> tasks.filter { !it.isCompleted && (it.dueDate == null || it.dueDate > todayEnd) }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        TaskFilter.OVERDUE -> tasks.filter { !it.isCompleted && it.dueDate != null && it.dueDate < today }
    }.sortedWith(
        compareBy<Task> { it.isCompleted }
            .thenBy { it.dueDate ?: Long.MAX_VALUE }
            .thenByDescending { it.priority.ordinal }
    )
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(AppIcons.Task, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Tasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        },
        floatingActionButton = {
            GradientFAB(onClick = { showAddTaskSheet = true }, icon = Icons.Filled.Add)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TaskFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.displayName) },
                            leadingIcon = if (selectedFilter == filter) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyState(
                        title = "No tasks found",
                        description = "Try changing the filter or add a new task",
                        icon = AppIcons.Task,
                        modifier = Modifier.padding(top = 80.dp)
                    )
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        goals = goals,
                        onToggle = { viewModel.toggleTaskCompletion(task.id) },
                        onClick = { editingTask = task },
                        onDelete = { viewModel.deleteTask(task.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
    
    if (showAddTaskSheet || editingTask != null) {
        TaskEditorSheet(
            task = editingTask,
            goals = goals,
            onDismiss = { showAddTaskSheet = false; editingTask = null },
            onSave = { task ->
                if (editingTask != null) viewModel.updateTask(task) else viewModel.addTask(task)
                showAddTaskSheet = false; editingTask = null
            },
            onDelete = { taskId -> viewModel.deleteTask(taskId); editingTask = null }
        )
    }
}

enum class TaskFilter(val displayName: String) {
    ALL("All"), TODAY("Today"), UPCOMING("Upcoming"), COMPLETED("Completed"), OVERDUE("Overdue")
}
