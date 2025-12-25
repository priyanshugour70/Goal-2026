package com.lssgoo.planner.features.tasks.components

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Flag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.ui.theme.GradientColors

@Composable
fun TaskStatsHeader(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val total = tasks.size
    val completed = tasks.count { it.isCompleted }
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val pending = total - completed
    val highPriority = tasks.count { !it.isCompleted && it.priority == com.lssgoo.planner.features.tasks.models.TaskPriority.HIGH }
    val overdue = tasks.count { !it.isCompleted && it.dueDate != null && it.dueDate < com.lssgoo.planner.util.KmpTimeUtils.currentTimeMillis() }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7), Color(0xFFEC4899)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                ))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Task Progress",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (total == 0) "No tasks yet" else "$completed of $total completed",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White.copy(alpha = 0.15f),
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBadge(
                        text = "$pending Pending", 
                        icon = Icons.Default.PendingActions,
                        color = Color.White.copy(alpha = 0.15f)
                    )
                    if (overdue > 0) {
                        StatBadge(
                            text = "$overdue Overdue", 
                            icon = Icons.Default.Warning,
                            color = Color(0xFFFFE4E1).copy(alpha = 0.2f)
                        )
                    }
                    if (highPriority > 0) {
                        StatBadge(
                            text = "$highPriority High", 
                            icon = Icons.Default.Flag,
                            color = Color(0xFFFFB6C1).copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color.White)
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
