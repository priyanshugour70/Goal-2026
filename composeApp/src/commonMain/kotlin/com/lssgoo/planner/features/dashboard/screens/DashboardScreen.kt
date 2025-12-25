package com.lssgoo.planner.features.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.lssgoo.planner.ui.components.*
import com.lssgoo.planner.ui.theme.*
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import com.lssgoo.planner.features.dashboard.components.*
import com.lssgoo.planner.features.tasks.components.TaskItem
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PlannerViewModel,
    onGoalClick: (String) -> Unit,
    onViewAllGoals: () -> Unit,
    onViewAllTasks: () -> Unit,
    onViewAllHabits: () -> Unit,
    onViewAllFinance: () -> Unit,
    onViewAllJournal: () -> Unit,
    onViewAllNotes: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val financeStats by viewModel.financeStats.collectAsState()
    
    val greeting = remember(userProfile) {
        viewModel.getUserGreeting()
    }
    
    val currentDate = remember { KmpDateFormatter.formatFullDate(KmpTimeUtils.currentTimeMillis()) }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 120.dp
        )
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                // Search Bar
                Surface(
                    onClick = { onSearchClick() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Search your plan...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Target,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Let's crush your goals!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Day Summary Card
        item {
             DaySummaryCard(stats = stats, modifier = Modifier.padding(horizontal = 16.dp))
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
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
                        icon = AppIcons.TrendingUp,
                        gradientColors = GradientColors.purpleBlue,
                        onClick = onViewAllGoals,
                        modifier = Modifier.width(180.dp)
                    )
                }
                item {
                    StatsCard(
                        title = "Today's Tasks",
                        value = "${stats.tasksCompletedToday}/${stats.totalTasksToday}",
                        subtitle = "completed",
                        icon = AppIcons.Tasks,
                        gradientColors = GradientColors.cyanGreen,
                        onClick = onViewAllTasks,
                        modifier = Modifier.width(180.dp)
                    )
                }
                item {
                    StatsCard(
                        title = "Balance",
                        value = "₹${financeStats.currentBalance.roundToInt()}",
                        subtitle = "In: ₹${financeStats.totalIncome.roundToInt()}",
                        icon = Icons.Default.AccountBalanceWallet,
                        gradientColors = GradientColors.oceanBlue,
                        onClick = onViewAllFinance,
                        modifier = Modifier.width(180.dp)
                    )
                }
                item {
                    val activeHabits = habits.count { it.isActive }
                    StatsCard(
                        title = "Active Habits",
                        value = "$activeHabits",
                        subtitle = "Keep it up!",
                        icon = Icons.Default.Refresh,
                        gradientColors = GradientColors.orangePink,
                        onClick = onViewAllHabits,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Motivational Quote
        item {
            MotivationalQuoteCard(modifier = Modifier.padding(horizontal = 16.dp))
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // --- ALL TABS OVERVIEW SECTIONS ---

        // 1. Goals Overview
        item {
            SectionHeader(
                title = "Your Goals",
                icon = AppIcons.Target,
                action = "View All",
                onActionClick = onViewAllGoals,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals.take(5)) { goal ->
                    MiniGoalCard(
                        goal = goal,
                        onClick = { onGoalClick(goal.id) },
                        modifier = Modifier.width(260.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 2. Habits Overview
        item {
            SectionHeader(
                title = "Daily Habits",
                icon = Icons.Default.CheckCircle,
                action = "Track",
                onActionClick = onViewAllHabits,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits.take(7)) { habit ->
                    HabitOverviewItem(habit = habit)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 3. Journal Snapshot
        item {
            SectionHeader(
                title = "Latest Reflection",
                icon = AppIcons.MenuBook,
                action = "Journal",
                onActionClick = onViewAllJournal,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            val latestEntry = journalEntries.firstOrNull()
            if (latestEntry != null) {
                RecentJournalCard(
                    entry = latestEntry,
                    onClick = onViewAllJournal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                EmptyStateCard("No entries yet", "Capture your first thought today", Icons.Default.Edit, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 4. Notes Stream
        item {
            SectionHeader(
                title = "Recent Notes",
                icon = AppIcons.Notes,
                action = "Manage",
                onActionClick = onViewAllNotes,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (notes.isEmpty()) {
                EmptyStateCard("Empty library", "Keep your ideas safe", AppIcons.NoteAdd, modifier = Modifier.padding(horizontal = 16.dp))
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notes.take(5)) { note ->
                        NoteMiniCard(note = note, modifier = Modifier.width(180.dp))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 5. Tasks & Reminders
        item {
            SectionHeader(
                title = "Upcoming Tasks",
                icon = AppIcons.Tasks,
                action = "Schedule",
                onActionClick = onViewAllTasks,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        val upcomingTasks = viewModel.getUpcomingTasks()
        if (upcomingTasks.isEmpty()) {
            item {
                EmptyState(
                    title = "All caught up!",
                    description = "Add tasks to your workflow",
                    icon = Icons.Outlined.TaskAlt,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(upcomingTasks.take(3)) { task ->
                TaskItem(
                    task = task,
                    goals = goals,
                    onToggle = { viewModel.toggleTaskCompletion(task.id) },
                    onClick = { },
                    onDelete = { },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        // --- ANALYTICS SECTION WITH GRAPHS ---
        item {
            Text(
                text = "Analytics Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            DashboardGoalPieChart(
                goals = goals,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            DashboardFinanceLineChart(
                stats = financeStats,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        item {
            DashboardHabitBarChart(
                habits = habits,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Year Progress
        item {
            YearProgressCard(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun DaySummaryCard(
    stats: com.lssgoo.planner.features.settings.models.DashboardStats,
    modifier: Modifier = Modifier
) {
    val tasksLeft = stats.totalTasksToday - stats.tasksCompletedToday
    val habitsLeft = stats.totalHabitsToday - stats.habitsCompletedToday
    
    val message = when {
        tasksLeft <= 0 && habitsLeft <= 0 -> "You're all set for today!"
        tasksLeft <= 0 -> "Great job! Just $habitsLeft habits remaining."
        habitsLeft <= 0 -> "Habits done! Focus on your $tasksLeft tasks."
        else -> "Good Morning! You have $tasksLeft tasks and $habitsLeft habits left."
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (tasksLeft <= 0 && habitsLeft <= 0) Icons.Filled.Celebration else Icons.Filled.WbSunny,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Daily Summary",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
