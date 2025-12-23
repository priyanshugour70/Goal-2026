package com.lssgoo.planner.features.habits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.components.*
import com.lssgoo.planner.features.habits.models.*
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0=All, 1=Morning, 2=Afternoon, 3=Evening
    
    // Interaction State
    var habitToInteract by remember { mutableStateOf<Habit?>(null) }
    
    // Analytics Data
    val globalHeatmap = remember(habits) { viewModel.getGlobalHeatmap() }
    
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
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Habits & Routines",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Analytics Dashboard (Collapsible or just placed at top)
            if (habits.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                         HabitHeatmap(globalHeatmap, MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Tabs
            val tabs = listOf("All", "Morning", "Afternoon", "Evening")
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
            
            // Filter Logic
            val filteredHabits = habits.filter {
                when (selectedTab) {
                    1 -> it.timeOfDay == HabitTimeOfDay.MORNING
                    2 -> it.timeOfDay == HabitTimeOfDay.AFTERNOON
                    3 -> it.timeOfDay == HabitTimeOfDay.EVENING
                    else -> true
                }
            }
            
            if (filteredHabits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
                    Text("No habits found for this time.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredHabits) { habit ->
                        val stats = viewModel.getHabitStats(habit.id)
                        val today = getStartOfDay(System.currentTimeMillis())
                        val todayEntry = viewModel.getHabitEntriesForDate(today)
                            .firstOrNull { it.habitId == habit.id }
                        
                        HabitDetailedItem(
                           habit = habit,
                           stats = stats,
                           isCompletedToday = todayEntry?.isCompleted ?: false,
                           onToggle = { 
                               if (habit.type == HabitType.YES_NO) {
                                   viewModel.toggleHabitEntry(habit.id, today)
                               } else {
                                   habitToInteract = habit
                               }
                           },
                           onClick = { onHabitClick(habit.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Dialogs
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
    
    if (habitToInteract != null) {
        HabitInteractionDialog(
            habit = habitToInteract!!,
            onDismiss = { habitToInteract = null },
            onConfirm = { value, mood ->
                val today = getStartOfDay(System.currentTimeMillis())
                viewModel.toggleHabitEntry(habitToInteract!!.id, today, value, mood)
                habitToInteract = null
            }
        )
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
    var selectedType by remember { mutableStateOf(HabitType.YES_NO) }
    var selectedTime by remember { mutableStateOf(HabitTimeOfDay.ANY_TIME) }
    var targetValue by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(0xFF4DD0E1) }
    var selectedIcon by remember { mutableStateOf("✨") }

    val icons = listOf("✨", "💧", "📚", "💪", "🧘", "💊", "💰", "🏃", "🍳", "💤")
    val colors = listOf(0xFFF44336, 0xFFE91E63, 0xFF9C27B0, 0xFF2196F3, 0xFF4CAF50, 0xFFFFC107, 0xFFFF5722)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Habit") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title, 
                    onValueChange = { title = it }, 
                    label = { Text("Title (e.g., Read Book)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Type Selector
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name.replace("_", " ")) }
                        )
                    }
                }
                
                if (selectedType != HabitType.YES_NO) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetValue,
                            onValueChange = { if(it.all { c -> c.isDigit() }) targetValue = it },
                            label = { Text("Target") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit (e.g. pages)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Time of Day
                Text("Time of Day", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitTimeOfDay.entries.forEach { time ->
                        FilterChip(
                           selected = selectedTime == time,
                           onClick = { selectedTime = time },
                           label = { Text(time.displayName.take(7) + "..") } // Compact
                        )
                    }
                }
                
                // Icon Picker
                Text("Icon", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(icons) { icon ->
                        FilterChip(selected = selectedIcon == icon, onClick = { selectedIcon = icon }, label = { Text(icon) })
                    }
                }
                
                // Color Picker - Simple Circles
                Text("Color", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(colors) { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .clickable { selectedColor = color }
                        ) {
                            if (selectedColor == color) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
                
                // Goal Link
                Text("Link to Goal (Optional)", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(goals) { goal ->
                        FilterChip(
                            selected = selectedGoalId == goal.id,
                            onClick = { selectedGoalId = if (selectedGoalId == goal.id) null else goal.id },
                            label = { Text(goal.title) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(Habit(
                        title = title,
                        goalId = selectedGoalId,
                        type = selectedType,
                        targetValue = targetValue.toFloatOrNull() ?: 1f,
                        unit = unit.ifBlank { null },
                        timeOfDay = selectedTime,
                        icon = selectedIcon,
                        iconColor = selectedColor
                    ))
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Create Habit")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
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

