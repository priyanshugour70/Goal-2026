package com.lssgoo.planner.features.tasks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Advanced Task item card component
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskItem(
    task: Task,
    goals: List<Goal>,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = Color(task.priority.color)
    val linkedGoal = task.linkedGoalId?.let { goalId -> goals.find { it.id == goalId } }
    
    val isOverdue = task.dueDate != null && task.dueDate < KmpTimeUtils.currentTimeMillis() && !task.isCompleted
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                isOverdue -> Color(0xFFEF4444).copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) Color(0xFF10B981) else priorityColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tags row
                    if (task.tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            task.tags.forEach { tag ->
                                val tagColor = Color(com.lssgoo.planner.features.tasks.models.TaskTags.getColorForTag(tag))
                                Surface(
                                    color = tagColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tagColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = priorityColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = task.priority.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            
            // Meta info row
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (task.dueDate != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = KmpDateFormatter.formatDateWithTime(task.dueDate),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    
                    if (linkedGoal != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                AppIcons.Target,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(linkedGoal.color)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = linkedGoal.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(linkedGoal.color),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 100.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = "Updated ${KmpDateFormatter.formatTime(task.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            }
        }
    }
}
