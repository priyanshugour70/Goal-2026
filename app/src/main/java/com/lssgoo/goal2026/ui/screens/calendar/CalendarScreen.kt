package com.lssgoo.goal2026.ui.screens.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
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
fun CalendarScreen(
    viewModel: Goal2026ViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val events by viewModel.events.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📅 Calendar 2026",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { showAddEventDialog = true },
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
            // Month navigation
            item {
                MonthNavigator(
                    currentMonth = currentMonth,
                    monthFormat = monthFormat,
                    onPreviousMonth = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                    },
                    onNextMonth = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                    },
                    onToday = {
                        currentMonth = Calendar.getInstance()
                        viewModel.setSelectedDate(System.currentTimeMillis())
                    }
                )
            }
            
            // Calendar grid
            item {
                CalendarGrid(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    events = events,
                    tasks = tasks,
                    onDateSelected = { viewModel.setSelectedDate(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Selected date info
            item {
                SelectedDateInfo(
                    selectedDate = selectedDate,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // Events and tasks for selected date
            val selectedDateEvents = viewModel.getEventsForDate(selectedDate)
            val selectedDateTasks = viewModel.getTasksForDate(selectedDate)
            
            if (selectedDateEvents.isEmpty() && selectedDateTasks.isEmpty()) {
                item {
                    EmptyState(
                        title = "No events",
                        description = "No events or tasks scheduled for this day",
                        icon = Icons.Outlined.EventAvailable,
                        actionText = "Add Event",
                        onActionClick = { showAddEventDialog = true },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Events
                if (selectedDateEvents.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "📌 Events",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    items(selectedDateEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onDelete = { viewModel.deleteEvent(event.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
                
                // Tasks
                if (selectedDateTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "✅ Tasks",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    items(selectedDateTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task.id) },
                            onClick = { },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
    
    // Add Event Dialog
    if (showAddEventDialog) {
        AddEventDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddEventDialog = false },
            onAddEvent = { event ->
                viewModel.addEvent(event)
                showAddEventDialog = false
            }
        )
    }
}

@Composable
fun MonthNavigator(
    currentMonth: Calendar,
    monthFormat: SimpleDateFormat,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthFormat.format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onToday) {
                Text("Today", style = MaterialTheme.typography.labelMedium)
            }
        }
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: Long,
    events: List<CalendarEvent>,
    tasks: List<Task>,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val calendar = currentMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val today = Calendar.getInstance()
    val isCurrentMonth = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
    
    Column(modifier = modifier) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar days
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7
        
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDayOfWeek + 1
                    
                    if (day in 1..daysInMonth) {
                        val dayCalendar = (currentMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val dayTimestamp = dayCalendar.timeInMillis
                        
                        val isSelected = isSameDay(dayTimestamp, selectedDate)
                        val isToday = isCurrentMonth && day == today.get(Calendar.DAY_OF_MONTH)
                        val hasEvents = events.any { isSameDay(it.date, dayTimestamp) }
                        val hasTasks = tasks.any { it.dueDate?.let { d -> isSameDay(d, dayTimestamp) } ?: false }
                        
                        CalendarDay(
                            day = day,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasEvents = hasEvents,
                            hasTasks = hasTasks,
                            onClick = { onDateSelected(dayTimestamp) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvents: Boolean,
    hasTasks: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            // Indicator dots
            if (hasEvents || hasTasks) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (hasEvents) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White
                                    else MaterialTheme.colorScheme.secondary
                                )
                        )
                    }
                    if (hasTasks) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.tertiary
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedDateInfo(
    selectedDate: Long,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(selectedDate))
    
    val isToday = isSameDay(selectedDate, System.currentTimeMillis())
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isToday) "Today" else formattedDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (isToday) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isToday) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Today",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: CalendarEvent,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(event.color).copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(event.color))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (event.description.isNotEmpty()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete event",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    selectedDate: Long,
    onDismiss: () -> Unit,
    onAddEvent: (CalendarEvent) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF2196F3L) }
    
    val colors = listOf(
        0xFF2196F3, 0xFF4CAF50, 0xFFFF9800, 0xFFE91E63,
        0xFF9C27B0, 0xFF00BCD4, 0xFFFF5722, 0xFF3F51B5
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Event",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .then(
                                    if (selectedColor == color) {
                                        Modifier.border(3.dp, Color.White, CircleShape)
                                    } else Modifier
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddEvent(
                            CalendarEvent(
                                title = title,
                                description = description,
                                date = selectedDate,
                                color = selectedColor
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add Event")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun isSameDay(date1: Long, date2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
