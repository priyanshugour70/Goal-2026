package com.lssgoo.planner.features.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ExperimentalFoundationApi
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import kotlinx.datetime.*
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.features.tasks.components.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.components.EmptyState
import com.lssgoo.planner.ui.components.GradientFAB

/**
 * Tasks list screen - follows SRP and size constraints
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val goals by viewModel.goals.collectAsState()
    
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    
    val today = remember {
        KmpTimeUtils.getStartOfDay(Clock.System.now().toEpochMilliseconds())
    }
    val tomorrow = today + 24 * 60 * 60 * 1000
    val dayAfterTomorrow = tomorrow + 24 * 60 * 60 * 1000
    
    val filteredTasks = remember(tasks, selectedFilter, searchQuery) {
        tasks.filter { task ->
            val matchesFilter = when (selectedFilter) {
                TaskFilter.ALL -> true
                TaskFilter.TODAY -> task.dueDate?.let { d -> d in today until tomorrow } ?: false
                TaskFilter.UPCOMING -> !task.isCompleted && (task.dueDate == null || task.dueDate >= tomorrow)
                TaskFilter.COMPLETED -> task.isCompleted
                TaskFilter.OVERDUE -> !task.isCompleted && task.dueDate != null && task.dueDate < today
            }
            val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) || 
                              task.description.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    // Grouping logic for date-wise display
    val groupedTasks = remember(filteredTasks) {
        filteredTasks.sortedWith(
            compareBy<Task> { it.isCompleted } // Completed tasks last
                .thenBy { it.dueDate ?: Long.MAX_VALUE }
        ).groupBy { task ->
            val date = task.dueDate
            when {
                task.isCompleted -> "Completed ✅"
                date == null -> "No Deadline 📅"
                date < today -> "Overdue ⚠️"
                date < tomorrow -> "Today ⚡"
                date < dayAfterTomorrow -> "Tomorrow 🌅"
                else -> KmpDateFormatter.formatDate(date)
            }
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = if (isSearchActive) 4.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    if (isSearchActive) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { isSearchActive = false },
                            active = false,
                            onActiveChange = { },
                            placeholder = { Text("Search tasks...") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            trailingIcon = { 
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    isSearchActive = false 
                                }) { Icon(Icons.Default.Close, null) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(AppIcons.Task, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Tasks", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    }
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
                TaskStatsHeader(
                    tasks = tasks,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(TaskFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.displayName) },
                            leadingIcon = if (selectedFilter == filter) {{ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }} else null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
            
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyState(
                        title = if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\"" else "No tasks found",
                        description = "Try changing the filter or add a new task",
                        icon = AppIcons.Task,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            } else {
                groupedTasks.forEach { (header, tasksInGroup) ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.labelLarge,
                                color = when {
                                    header.contains("Overdue") -> MaterialTheme.colorScheme.error
                                    header.contains("Today") -> MaterialTheme.colorScheme.primary
                                    header.contains("Completed") -> MaterialTheme.colorScheme.outline
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                    
                    items(tasksInGroup, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            goals = goals,
                            onToggle = { viewModel.toggleTaskCompletion(task.id) },
                            onClick = { editingTask = task },
                            onDelete = { taskToDelete = task },
                            modifier = Modifier.animateItem().padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
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
            onDelete = { taskId -> 
                taskToDelete = tasks.find { it.id == taskId }
                editingTask = null 
            }
        )
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task?") },
            text = { Text("Are you sure you want to delete \"${taskToDelete?.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.id?.let { viewModel.deleteTask(it) }
                        taskToDelete = null
                        showAddTaskSheet = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

enum class TaskFilter(val displayName: String) {
    ALL("All Tasks"), 
    TODAY("Today"), 
    UPCOMING("Upcoming"), 
    COMPLETED("Done"), 
    OVERDUE("Overdue")
}
