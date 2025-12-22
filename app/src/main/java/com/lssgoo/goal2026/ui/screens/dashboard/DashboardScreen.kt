package com.lssgoo.goal2026.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: Goal2026ViewModel,
    onGoalClick: (String) -> Unit,
    onViewAllGoals: () -> Unit,
    onViewAllTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()) }
    val currentDate = remember { dateFormat.format(Date()) }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header with gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp)
                    .padding(top = 60.dp, bottom = 20.dp)
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🎯 Let's crush your 2026 goals!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Quick Stats Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    StatsCard(
                        title = "Overall Progress",
                        value = "${(stats.overallProgress * 100).toInt()}%",
                        subtitle = "${stats.completedMilestones}/${stats.totalMilestones} milestones",
                        icon = Icons.Filled.TrendingUp,
                        gradientColors = GradientColors.purpleBlue,
                        modifier = Modifier.width(180.dp)
                    )
                }
                item {
                    StatsCard(
                        title = "Today's Tasks",
                        value = "${stats.tasksCompletedToday}/${stats.totalTasksToday}",
                        subtitle = "completed",
                        icon = Icons.Filled.TaskAlt,
                        gradientColors = GradientColors.cyanGreen,
                        modifier = Modifier.width(180.dp)
                    )
                }
                item {
                    StatsCard(
                        title = "Current Streak",
                        value = "${stats.currentStreak} 🔥",
                        subtitle = "Best: ${stats.longestStreak} days",
                        icon = Icons.Filled.LocalFireDepartment,
                        gradientColors = GradientColors.orangePink,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Motivational Quote Card
        item {
            MotivationalQuoteCard(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Today's Focus
        item {
            SectionHeader(
                title = "🎯 Your Goals",
                action = "View All",
                onActionClick = onViewAllGoals,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Goals horizontal scroll
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals.take(5)) { goal ->
                    MiniGoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal.id) },
                        modifier = Modifier.width(280.dp)
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Quick Tasks Section
        item {
            SectionHeader(
                title = "📋 Upcoming Tasks",
                action = "View All",
                onActionClick = onViewAllTasks,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Upcoming tasks
        val upcomingTasks = viewModel.getUpcomingTasks()
        if (upcomingTasks.isEmpty()) {
            item {
                EmptyState(
                    title = "No upcoming tasks",
                    description = "Add tasks to stay on track with your goals",
                    icon = Icons.Outlined.TaskAlt,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(upcomingTasks) { task ->
                TaskItem(
                    task = task,
                    onToggle = { viewModel.toggleTaskCompletion(task.id) },
                    onClick = { },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // 2026 Progress Overview
        item {
            YearProgressCard(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun MiniGoalCard(
    goal: Goal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedMilestones = goal.milestones.count { it.isCompleted }
    val totalMilestones = goal.milestones.size
    val progress = if (totalMilestones > 0) completedMilestones.toFloat() / totalMilestones else 0f
    
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(goal.color).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = goal.category.emoji,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedMilestones/$totalMilestones",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(goal.color)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AnimatedProgressBar(
                progress = progress,
                gradientColors = listOf(Color(goal.color), Color(goal.color).copy(alpha = 0.6f)),
                height = 6
            )
        }
    }
}

@Composable
fun MotivationalQuoteCard(modifier: Modifier = Modifier) {
    val quotes = listOf(
        "\"The only way to do great work is to love what you do.\" - Steve Jobs",
        "\"Success is not final, failure is not fatal: it is the courage to continue that counts.\" - Winston Churchill",
        "\"The future belongs to those who believe in the beauty of their dreams.\" - Eleanor Roosevelt",
        "\"It does not matter how slowly you go as long as you do not stop.\" - Confucius",
        "\"Believe you can and you're halfway there.\" - Theodore Roosevelt",
        "\"Your limitation—it's only your imagination.\"",
        "\"Push yourself, because no one else is going to do it for you.\"",
        "\"Great things never come from comfort zones.\"",
        "\"Dream it. Wish it. Do it.\"",
        "\"Success doesn't just find you. You have to go out and get it.\""
    )
    
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val quote = quotes[dayOfYear % quotes.size]
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "💡",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Daily Motivation",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = quote,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun YearProgressCard(modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val totalDays = if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) 366 else 365
    val progress = dayOfYear.toFloat() / totalDays
    val daysRemaining = totalDays - dayOfYear
    
    // Calculate progress for 2026 specifically
    val year = calendar.get(Calendar.YEAR)
    val is2026 = year == 2026
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        text = if (is2026) "2026 Progress" else "Year ${year} Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$daysRemaining days remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(GradientColors.purpleBlue)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📅",
                        fontSize = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Day $dayOfYear",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AnimatedProgressBar(
                progress = progress,
                gradientColors = GradientColors.purpleBlue,
                height = 10
            )
            
            if (!is2026) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your goals are set for 2026. Keep preparing!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}
