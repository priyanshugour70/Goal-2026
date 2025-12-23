package com.lssgoo.planner.features.calendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.calendar.components.*
import com.lssgoo.planner.ui.components.*
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    // We collect individual lists to update the monthly dots indicators
    val events by viewModel.events.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    
    // Force refresh when these change
    val itemsTrigger = events.size + tasks.size + reminders.size
    
    // Get unified items for selected date
    val calendarItems = remember(selectedDate, itemsTrigger) {
        viewModel.getAllItemsForDate(selectedDate)
    }
    
    // Monthly activity counts for indicators
    val activityCounts = remember(currentMonth, itemsTrigger) {
        viewModel.getMonthActivityCounts(currentMonth)
    }
    
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    
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
                        AppIcons.Calendar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { showAddEventDialog = true },
                icon = AppIcons.Add
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
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
                    activityCounts = activityCounts,
                    onDateSelected = { viewModel.setSelectedDate(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Selected date info
            item {
                SelectedDateInfo(
                    selectedDate = selectedDate,
                    count = calendarItems.size,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // Unified items list
            if (calendarItems.isEmpty()) {
                item {
                    EmptyState(
                        title = "Nothing Scheduled",
                        description = "No tasks, events, or reminders for this day",
                        icon = AppIcons.Calendar,
                        actionText = "Add Event",
                        onActionClick = { showAddEventDialog = true },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(calendarItems, key = { it.id }) { item ->
                    CalendarItemCard(
                        item = item,
                        viewModel = viewModel,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
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
