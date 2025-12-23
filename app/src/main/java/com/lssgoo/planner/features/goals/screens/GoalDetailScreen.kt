package com.lssgoo.planner.features.goals.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import com.lssgoo.planner.features.goals.components.*
import com.lssgoo.planner.features.goals.models.Milestone
import com.lssgoo.planner.ui.viewmodel.GoalsViewModel

/**
 * Goal Detail screen - follows SRP
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    viewModel: GoalsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState()
    val goal = goals.find { it.id == goalId }
    
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var milestoneToEdit by remember { mutableStateOf<Milestone?>(null) }
    var milestoneToComplete by remember { mutableStateOf<Milestone?>(null) }
    
    if (goal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Goal not found")
        }
        return
    }
    
    val completedMilestones = goal.milestones.count { it.isCompleted }
    val totalMilestones = goal.milestones.size
    val progress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = "Goal Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showEditGoalDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Goal")
                        }
                        IconButton(onClick = { 
                            viewModel.deleteGoal(goal.id)
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMilestoneDialog = true },
                containerColor = Color(goal.color),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Milestone")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
        ) {
            item {
                GoalDetailHeader(
                    goal = goal,
                    progress = progress,
                    completedMilestones = completedMilestones,
                    totalMilestones = totalMilestones
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Milestones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Surface(
                        color = Color(goal.color).copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "$completedMilestones / $totalMilestones",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(goal.color),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            items(goal.milestones, key = { it.id }) { milestone ->
                MilestoneItem(
                    milestone = milestone,
                    goalColor = Color(goal.color),
                    onToggle = { 
                        if (milestone.isCompleted) {
                            // Simple uncheck
                            viewModel.toggleMilestone(goal.id, milestone.id)
                        } else {
                            // Show completion dialog
                            milestoneToComplete = milestone
                        }
                    },
                    onEdit = { milestoneToEdit = milestone },
                    onDelete = { viewModel.deleteMilestone(goal.id, milestone.id) },
                    modifier = Modifier.animateItem().padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            
            if (goal.milestones.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(Modifier.height(16.dp))
                            Text("No milestones yet. Break down your goal!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }

    if (showEditGoalDialog) {
        AddEditGoalDialog(
            goal = goal,
            onDismiss = { showEditGoalDialog = false },
            onConfirm = { 
                viewModel.updateGoal(it)
                showEditGoalDialog = false
            }
        )
    }

    if (showAddMilestoneDialog) {
        AddEditMilestoneDialog(
            onDismiss = { showAddMilestoneDialog = false },
            onConfirm = { 
                val updatedMilestones = goal.milestones + it
                viewModel.updateGoal(goal.copy(milestones = updatedMilestones))
                showAddMilestoneDialog = false
            }
        )
    }

    if (milestoneToEdit != null) {
        val m = milestoneToEdit!!
        AddEditMilestoneDialog(
            milestone = m,
            onDismiss = { milestoneToEdit = null },
            onConfirm = { updated ->
                viewModel.updateMilestone(goal.id, updated)
                milestoneToEdit = null
            }
        )
    }

    if (milestoneToComplete != null) {
        val m = milestoneToComplete!!
        MilestoneCompletionDialog(
            milestone = m,
            onDismiss = { milestoneToComplete = null },
            onConfirm = { finishedMilestone ->
                viewModel.updateMilestone(goal.id, finishedMilestone)
                milestoneToComplete = null
            }
        )
    }
}
