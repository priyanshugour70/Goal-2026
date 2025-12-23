package com.lssgoo.planner.features.habits.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.features.habits.models.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitHeatmap(
    heatmapData: Map<Long, Int>,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Generate last 100 days
    val days = remember {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        repeat(98) { // 14 weeks * 7
            list.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        list.reversed()
    }

    Column(modifier = modifier) {
        Text("Contribution Graph", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(14),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false,
            modifier = Modifier.height(140.dp) // Approx height
        ) {
            items(days) { date ->
                val level = heatmapData[date] ?: 0
                val alpha = when (level) {
                    0 -> 0.1f
                    1 -> 0.3f
                    2 -> 0.5f
                    3 -> 0.7f
                    else -> 1.0f
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color.copy(alpha = alpha))
                )
            }
        }
    }
}

@Composable
fun WeeklySuccessRings(
    last7Days: List<Boolean>, // Mon, Tue... Sun (or last 7 relative days)
    color: Color,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        last7Days.zip(days).forEach { (completed, day) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(32.dp),
                        color = if (completed) color else color.copy(alpha = 0.2f),
                        trackColor = Color.Transparent,
                        strokeWidth = 3.dp
                    )
                    if (completed) {
                        Text("✓", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HabitDetailedItem(
    habit: Habit,
    stats: HabitStats,
    isCompletedToday: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(habit.iconColor)
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(habit.icon, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (habit.targetValue > 1 || habit.type == HabitType.TIMER) {
                         Text(
                             "${habit.targetValue.toInt()} ${habit.unit ?: ""}", 
                             style = MaterialTheme.typography.labelSmall, 
                             color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                    } else {
                        Text(
                            habit.timeOfDay.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Toggle Button
                FilledIconButton(
                    onClick = onToggle,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCompletedToday) color else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isCompletedToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    if (isCompletedToday) {
                        Text("✓", fontWeight = FontWeight.Bold)
                    } else {
                        // Show input icon for quant habits
                        val icon = when(habit.type) {
                            HabitType.YES_NO -> "○"
                            HabitType.QUANTITATIVE -> "+"
                            HabitType.TIMER -> "▶"
                        }
                        Text(icon, fontSize = 18.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCompact("Streak", "${stats.currentStreak} days", "🔥")
                StatCompact("Rate", "${(stats.completionRate * 100).toInt()}%", "📊")
                StatCompact("Total", "${stats.totalCompletions}", "✅")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weekly Rings
            if (stats.last7Days.isNotEmpty()) {
                WeeklySuccessRings(stats.last7Days, color)
            }
        }
    }
}

@Composable
fun StatCompact(label: String, value: String, emoji: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitInteractionDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onConfirm: (Float, HabitMood?) -> Unit
) {
    var value by remember { mutableFloatStateOf(0f) }
    var selectedMood by remember { mutableStateOf<HabitMood?>(null) }
    
    // Timer state logic would go here for TIMER type
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(habit.icon)
                Spacer(modifier = Modifier.width(8.dp))
                Text(habit.title)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (habit.type == HabitType.QUANTITATIVE) {
                    Text("How much did you do today?")
                    OutlinedTextField(
                        value = if(value == 0f) "" else value.toString(),
                        onValueChange = { value = it.toFloatOrNull() ?: 0f },
                        label = { Text("Value (${habit.unit ?: "units"})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (habit.type == HabitType.TIMER) {
                    Text("Did you complete ${habit.targetValue.toInt()} mins?")
                    // Simplified for now
                } else {
                    Text("Completed!")
                }
                
                Text("How do you feel?", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HabitMood.entries.forEach { mood ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedMood = mood }
                                .background(if (selectedMood == mood) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .padding(8.dp)
                        ) {
                            Text(mood.emoji, fontSize = 24.sp)
                            Text(mood.label, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val finalVal = if(habit.type == HabitType.YES_NO) 1f else value
                    onConfirm(finalVal, selectedMood) 
                }
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
