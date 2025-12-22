package com.lssgoo.goal2026.ui.screens.goals

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.goal2026.data.model.*
import com.lssgoo.goal2026.ui.components.*
import com.lssgoo.goal2026.ui.theme.*
import com.lssgoo.goal2026.ui.viewmodel.Goal2026ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: Goal2026ViewModel,
    onGoalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    
    var selectedCategory by remember { mutableStateOf<GoalCategory?>(null) }
    
    val filteredGoals = if (selectedCategory != null) {
        goals.filter { it.category == selectedCategory }
    } else {
        goals
    }
    
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Your Goals",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stats.completedMilestones}/${stats.totalMilestones} milestones completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            // Category filter chips
            item {
                CategoryFilterChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Overall progress card
            item {
                OverallProgressCard(
                    progress = stats.overallProgress,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Goals list
            items(filteredGoals, key = { it.id }) { goal ->
                GoalCard(
                    goal = goal,
                    onClick = { onGoalClick(goal.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterChips(
    selectedCategory: GoalCategory?,
    onCategorySelected: (GoalCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All chip
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedCategory == null) {
                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            } else null
        )
        
        // Category chips
        GoalCategory.entries.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { 
                    onCategorySelected(if (selectedCategory == category) null else category) 
                },
                label = { Text("${category.emoji} ${category.displayName}") },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
fun OverallProgressCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(GradientColors.purpleBlue)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Overall Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keep going! You're doing great 🚀",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Circular progress indicator
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White,
                        strokeWidth = 8.dp,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "🎯",
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}

// Goal Detail Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    viewModel: Goal2026ViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goal = viewModel.getGoalById(goalId)
    
    if (goal == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Goal not found")
        }
        return
    }
    
    val completedMilestones = goal.milestones.count { it.isCompleted }
    val totalMilestones = goal.milestones.size
    val progress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header with goal info
            item {
                GoalDetailHeader(
                    goal = goal,
                    progress = progress,
                    completedMilestones = completedMilestones,
                    totalMilestones = totalMilestones
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Milestones header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Milestones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completedMilestones / $totalMilestones",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(goal.color),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Milestones list
            items(goal.milestones, key = { it.id }) { milestone ->
                MilestoneItem(
                    milestone = milestone,
                    goalColor = Color(goal.color),
                    onToggle = { viewModel.toggleMilestone(goal.id, milestone.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GoalDetailHeader(
    goal: Goal,
    progress: Float,
    completedMilestones: Int,
    totalMilestones: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Category chip
        Surface(
            color = Color(goal.color).copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "${goal.category.emoji} ${goal.category.displayName}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color(goal.color),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Goal number and title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(goal.color), Color(goal.color).copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = goal.number.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = goal.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = goal.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Progress card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                            text = "Progress",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(goal.color)
                        )
                    }
                    
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(60.dp),
                        color = Color(goal.color),
                        strokeWidth = 6.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedProgressBar(
                    progress = progress,
                    gradientColors = listOf(Color(goal.color), Color(goal.color).copy(alpha = 0.6f)),
                    height = 12
                )
            }
        }
    }
}

@Composable
fun MilestoneItem(
    milestone: Milestone,
    goalColor: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted)
                completedColor.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (milestone.isCompleted) 
                        Icons.Filled.CheckCircle 
                    else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle milestone",
                    tint = if (milestone.isCompleted) completedColor else goalColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (milestone.isCompleted) 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough 
                    else null,
                    color = if (milestone.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                
                if (milestone.isCompleted && milestone.completedAt != null) {
                    val dateFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                    Text(
                        text = "Completed on ${dateFormat.format(java.util.Date(milestone.completedAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = completedColor
                    )
                }
            }
            
            if (milestone.isCompleted) {
                Text(
                    text = "✓",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = completedColor
                )
            }
        }
    }
}
