package com.lssgoo.planner.features.habits.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.ui.components.*
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: PlannerViewModel,
    onHabitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val goals by viewModel.goals.collectAsState()
    var showAddHabitDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Habits",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { showAddHabitDialog = true },
                icon = Icons.Filled.Add
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (habits.isEmpty()) {
            EmptyState(
                title = "No habits yet",
                description = "Create habits to track your daily progress",
                icon = Icons.Filled.CheckCircle,
                actionText = "Add Habit",
                onActionClick = { showAddHabitDialog = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    val goal = goals.find { it.id == habit.goalId }
                    val stats = viewModel.getHabitStats(habit.id)
                    val today = getStartOfDay(System.currentTimeMillis())
                    val todayEntry = viewModel.getHabitEntriesForDate(today)
                        .firstOrNull { it.goalId == habit.goalId }
                    
                    HabitCard(
                        habit = habit,
                        goal = goal,
                        stats = stats,
                        isCompletedToday = todayEntry?.isCompleted ?: false,
                        onToggle = { viewModel.toggleHabitEntry(habit.id, today) },
                        onClick = { onHabitClick(habit.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    if (showAddHabitDialog) {
        AddHabitDialog(
            goals = goals,
            onDismiss = { showAddHabitDialog = false },
            onAdd = { habit ->
                viewModel.addHabit(habit)
                showAddHabitDialog = false
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    goal: Goal?,
    stats: HabitStats,
    isCompletedToday: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    if (isCompletedToday) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle",
                    tint = if (isCompletedToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (goal != null) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥 ${stats.currentStreak} day streak",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(stats.completionRate * 100).toInt()}% complete",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    goals: List<Goal>,
    onDismiss: () -> Unit,
    onAdd: (Habit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Text("Link to Goal", style = MaterialTheme.typography.labelMedium)
                goals.forEach { goal ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedGoalId == goal.id,
                            onClick = { selectedGoalId = goal.id }
                        )
                        Text(goal.title)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedGoalId != null) {
                        onAdd(
                            Habit(
                                goalId = selectedGoalId!!,
                                title = title,
                                description = description
                            )
                        )
                    }
                },
                enabled = title.isNotBlank() && selectedGoalId != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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

