package com.lssgoo.goal2026.ui.screens.tasks

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lssgoo.goal2026.data.model.*
import com.lssgoo.goal2026.ui.components.*
import com.lssgoo.goal2026.ui.theme.*
import com.lssgoo.goal2026.ui.viewmodel.Goal2026ViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class TaskFilter(val displayName: String) {
    ALL("All"),
    TODAY("Today"),
    UPCOMING("Upcoming"),
    COMPLETED("Completed"),
    OVERDUE("Overdue")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: Goal2026ViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val goals by viewModel.goals.collectAsState()
    
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    
    val today = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val todayEnd = today + 24 * 60 * 60 * 1000 - 1
    
    val filteredTasks = when (selectedFilter) {
        TaskFilter.ALL -> tasks
        TaskFilter.TODAY -> tasks.filter { task ->
            task.dueDate?.let { it in today..todayEnd } ?: false
        }
        TaskFilter.UPCOMING -> tasks.filter { task ->
            !task.isCompleted && (task.dueDate == null || task.dueDate > today)
        }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        TaskFilter.OVERDUE -> tasks.filter { task ->
            !task.isCompleted && task.dueDate != null && task.dueDate < today
        }
    }.sortedWith(
        compareBy<Task> { it.isCompleted }
            .thenBy { it.dueDate ?: Long.MAX_VALUE }
            .thenByDescending { it.priority.ordinal }
    )
    
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "✅ Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedCount of $totalCount completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { showAddTaskSheet = true },
                icon = Icons.Filled.Add
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Filter chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TaskFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.displayName) },
                            leadingIcon = if (selectedFilter == filter) {
                                { Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Progress card for today
            if (selectedFilter == TaskFilter.TODAY || selectedFilter == TaskFilter.ALL) {
                item {
                    TodayProgressCard(
                        tasks = tasks.filter { task ->
                            task.dueDate?.let { it in today..todayEnd } ?: false
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Tasks list
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyState(
                        title = when (selectedFilter) {
                            TaskFilter.ALL -> "No tasks yet"
                            TaskFilter.TODAY -> "No tasks for today"
                            TaskFilter.UPCOMING -> "No upcoming tasks"
                            TaskFilter.COMPLETED -> "No completed tasks"
                            TaskFilter.OVERDUE -> "No overdue tasks"
                        },
                        description = when (selectedFilter) {
                            TaskFilter.ALL -> "Add your first task to get started"
                            TaskFilter.TODAY -> "Enjoy your free day! 🎉"
                            TaskFilter.UPCOMING -> "Great job staying on top of things"
                            TaskFilter.COMPLETED -> "Complete some tasks to see them here"
                            TaskFilter.OVERDUE -> "You're all caught up! 👍"
                        },
                        icon = when (selectedFilter) {
                            TaskFilter.COMPLETED -> Icons.Outlined.CheckCircle
                            TaskFilter.OVERDUE -> Icons.Outlined.Warning
                            else -> Icons.Outlined.TaskAlt
                        },
                        actionText = if (selectedFilter == TaskFilter.ALL) "Add Task" else null,
                        onActionClick = if (selectedFilter == TaskFilter.ALL) {{ showAddTaskSheet = true }} else null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskItemExpanded(
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
    
    // Add/Edit Task Sheet
    if (showAddTaskSheet || editingTask != null) {
        TaskEditorSheet(
            task = editingTask,
            goals = goals,
            onDismiss = {
                showAddTaskSheet = false
                editingTask = null
            },
            onSave = { task ->
                if (editingTask != null) {
                    viewModel.updateTask(task)
                } else {
                    viewModel.addTask(task)
                }
                showAddTaskSheet = false
                editingTask = null
            },
            onDelete = { taskId ->
                viewModel.deleteTask(taskId)
                editingTask = null
            }
        )
    }
}

@Composable
fun TodayProgressCard(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val completed = tasks.count { it.isCompleted }
    val total = tasks.size
    val progress = if (total > 0) completed.toFloat() / total else 0f
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (total == 0) "No tasks today" else "$completed of $total tasks done",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (total > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedProgressBar(
                    progress = progress,
                    gradientColors = GradientColors.cyanGreen,
                    height = 8
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItemExpanded(
    task: Task,
    goals: List<Goal>,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = Color(task.priority.color)
    val linkedGoal = task.linkedGoalId?.let { goalId ->
        goals.find { it.id == goalId }
    }
    
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    val isOverdue = task.dueDate != null && 
            task.dueDate < System.currentTimeMillis() && 
            !task.isCompleted
    
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                isOverdue -> overdueColor.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) 
                            Icons.Filled.CheckCircle 
                        else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = "Toggle task",
                        tint = if (task.isCompleted) completedColor else priorityColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) 
                            MaterialTheme.colorScheme.onSurfaceVariant 
                        else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Priority indicator
                Surface(
                    color = priorityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = task.priority.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onClick()
                            },
                            leadingIcon = { Icon(Icons.Outlined.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { 
                                Icon(
                                    Icons.Outlined.Delete, 
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                ) 
                            }
                        )
                    }
                }
            }
            
            // Footer with due date and linked goal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Due date
                if (task.dueDate != null) {
                    Surface(
                        color = if (isOverdue) overdueColor.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Event,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isOverdue) overdueColor 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateFormat.format(Date(task.dueDate)),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) overdueColor 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Linked goal
                if (linkedGoal != null) {
                    Surface(
                        color = Color(linkedGoal.color).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = linkedGoal.category.emoji,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = linkedGoal.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(linkedGoal.color),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                // Repeat indicator
                if (task.repeatType != RepeatType.NONE) {
                    Icon(
                        Icons.Outlined.Repeat,
                        contentDescription = "Repeating",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorSheet(
    task: Task?,
    goals: List<Goal>,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf(task?.dueDate) }
    var linkedGoalId by remember { mutableStateOf(task?.linkedGoalId) }
    var repeatType by remember { mutableStateOf(task?.repeatType ?: RepeatType.NONE) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (task != null) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                if (task != null) {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Priority
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(p.color).copy(alpha = 0.2f),
                            selectedLabelColor = Color(p.color)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dueDate?.let {
                                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
                            } ?: "Select Date"
                        )
                    }
                    
                    if (dueDate != null) {
                        IconButton(onClick = { dueDate = null }) {
                            Icon(Icons.Filled.Close, "Clear date")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Link to Goal
            Text(
                text = "Link to Goal (optional)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = linkedGoalId == null,
                        onClick = { linkedGoalId = null },
                        label = { Text("None") }
                    )
                }
                items(goals) { goal ->
                    FilterChip(
                        selected = linkedGoalId == goal.id,
                        onClick = { linkedGoalId = goal.id },
                        label = { Text("${goal.category.emoji} ${goal.title}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(goal.color).copy(alpha = 0.2f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Repeat
            Text(
                text = "Repeat",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RepeatType.entries) { type ->
                    FilterChip(
                        selected = repeatType == type,
                        onClick = { repeatType = type },
                        label = { Text(type.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save button
            Button(
                onClick = {
                    onSave(
                        Task(
                            id = task?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate,
                            linkedGoalId = linkedGoalId,
                            repeatType = repeatType,
                            isCompleted = task?.isCompleted ?: false,
                            completedAt = task?.completedAt,
                            createdAt = task?.createdAt ?: System.currentTimeMillis()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank()
            ) {
                Icon(Icons.Filled.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Task")
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete confirmation
    if (showDeleteConfirm && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(task.id)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
