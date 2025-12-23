package com.lssgoo.planner.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.reminders.models.CalendarItem
import com.lssgoo.planner.features.reminders.models.CalendarItemType
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.CalendarColors
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

@Composable
fun CalendarItemCard(
    item: CalendarItem,
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val icon = when(item.type) {
        CalendarItemType.TASK -> AppIcons.Tasks
        CalendarItemType.EVENT -> AppIcons.Event
        CalendarItemType.REMINDER -> AppIcons.Reminders
        CalendarItemType.NOTE -> AppIcons.Notes
        CalendarItemType.GOAL_MILESTONE -> AppIcons.Milestone
        CalendarItemType.FINANCE -> AppIcons.Finance
        CalendarItemType.HABIT -> AppIcons.Habit
        CalendarItemType.JOURNAL -> AppIcons.Journal
        CalendarItemType.GOAL -> AppIcons.Trophy
    }
    
    val color = Color(item.color)
    val isTaskOrReminder = item.type == CalendarItemType.TASK || item.type == CalendarItemType.REMINDER
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Checkbox for tasks/reminders
            if (isTaskOrReminder) {
                IconButton(
                    onClick = {
                        if (item.type == CalendarItemType.TASK) {
                            viewModel.toggleTaskCompletion(item.id)
                        } else if (item.type == CalendarItemType.REMINDER) {
                            viewModel.toggleReminderEnabled(item.id)
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (item.isCompleted) AppIcons.CheckCircle else AppIcons.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (item.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (isTaskOrReminder && item.isCompleted) 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    color = if (isTaskOrReminder && item.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                )
                
                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                
                // Priority Badge
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = item.priority.level.let { 
                            if (it <= 3) CalendarColors.urgent else MaterialTheme.colorScheme.onSurfaceVariant 
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.priority.displayName + " Priority",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Delete Action
            if (item.type == CalendarItemType.EVENT) {
                IconButton(onClick = { viewModel.deleteEvent(item.id) }) {
                    Icon(
                        AppIcons.DeleteOutlined,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
