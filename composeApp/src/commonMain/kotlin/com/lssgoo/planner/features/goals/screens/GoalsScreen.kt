package com.lssgoo.planner.features.goals.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.GoalCategory
import com.lssgoo.planner.features.goals.components.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Goals list screen - follows SRP and size constraints
 */
enum class SortOrder {
    DEFAULT, DATE_ASC, NAME_ASC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: PlannerViewModel,
    onGoalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState()
    var selectedCategory by remember { mutableStateOf<GoalCategory?>(null) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.DEFAULT) }
    
    val filteredGoals = remember(goals, selectedCategory, sortOrder) {
        var result = if (selectedCategory != null) {
            goals.filter { it.category == selectedCategory }
        } else {
            goals
        }
        
        result = when(sortOrder) {
            SortOrder.DATE_ASC -> result.sortedBy { it.targetDate ?: Long.MAX_VALUE }
            SortOrder.NAME_ASC -> result.sortedBy { it.title }
            SortOrder.DEFAULT -> result.sortedBy { it.number }
        }
        result
    }
    
    val overallProgress = if (goals.isNotEmpty()) goals.sumOf { it.progress.toDouble() }.toFloat() / goals.size else 0f
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                AppIcons.Goal,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Goals",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        val haptic = LocalHapticFeedback.current
                        
                        
                        IconButton(onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            sortOrder = when(sortOrder) {
                                SortOrder.DEFAULT -> SortOrder.DATE_ASC
                                SortOrder.DATE_ASC -> SortOrder.NAME_ASC
                                SortOrder.NAME_ASC -> SortOrder.DEFAULT
                            }
                        }) {
                            Icon(
                                imageVector = when(sortOrder) {
                                    SortOrder.DATE_ASC -> Icons.Default.CalendarToday
                                    SortOrder.NAME_ASC -> Icons.Default.SortByAlpha
                                    SortOrder.DEFAULT -> Icons.Default.FilterList
                                },
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            val haptic = LocalHapticFeedback.current
            ExtendedFloatingActionButton(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showAddGoalDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Goal", fontWeight = FontWeight.Bold) }
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
            item {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                OverallProgressCard(
                    progress = overallProgress,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            items(filteredGoals, key = { it.id }) { goal ->
                GoalCard(
                    goal = goal,
                    onClick = { onGoalClick(goal.id) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null,
                        placementSpec = spring(stiffness = Spring.StiffnessLow)
                    ).padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    if (showAddGoalDialog) {
        AddEditGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { 
                viewModel.addGoal(it) 
                showAddGoalDialog = false
            }
        )
    }
}
