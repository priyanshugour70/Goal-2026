package com.lssgoo.planner.features.journal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.features.journal.models.JournalEntry
import com.lssgoo.planner.features.journal.models.JournalMood
import com.lssgoo.planner.features.journal.models.JournalPrompt
import java.util.Calendar

@Composable
fun JournalPromptCard(
    prompt: JournalPrompt?,
    onAnswerClick: () -> Unit
) {
    if (prompt == null) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Daily Reflection",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = prompt.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAnswerClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Answer")
            }
        }
    }
}

@Composable
fun MoodCalendar(
    entries: List<JournalEntry>,
    currentMonth: Calendar = Calendar.getInstance()
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = currentMonth.clone() as Calendar
    firstDayOfWeek.set(Calendar.DAY_OF_MONTH, 1)
    val offset = firstDayOfWeek.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed offset (Sun=0)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Mood Calendar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Week headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { 
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid
        // Simple Grid implementation
        val totalCells = daysInMonth + offset
        val rows = (totalCells / 7) + if (totalCells % 7 > 0) 1 else 0
        
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (col in 0 until 7) {
                    val day = (row * 7 + col) - offset + 1
                    if (day in 1..daysInMonth) {
                        val entry = entries.find { 
                            val c = Calendar.getInstance()
                            c.timeInMillis = it.date
                            c.get(Calendar.DAY_OF_MONTH) == day && c.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    if (entry != null) Color(entry.mood.color).copy(alpha = 0.8f) 
                                    else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 12.sp,
                                color = if (entry != null) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MoodDistributionChart(entries: List<JournalEntry>) {
    val moodCounts = entries.groupingBy { it.mood }.eachCount()
    val total = entries.size
    
    if (total == 0) return

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mood Distribution", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp))) {
            JournalMood.entries.forEach { mood ->
                val count = moodCounts[mood] ?: 0
                if (count > 0) {
                    Box(
                         modifier = Modifier
                             .weight(count.toFloat())
                             .fillMaxHeight()
                             .background(Color(mood.color))
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Legend
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
             JournalMood.entries.forEach { mood ->
                 if ((moodCounts[mood] ?: 0) > 0) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Box(modifier = Modifier.size(8.dp).background(Color(mood.color), CircleShape))
                         Spacer(modifier = Modifier.width(4.dp))
                         Text(mood.emoji, fontSize = 12.sp)
                     }
                 }
             }
        }
    }
}

@Composable
fun MoodTimeline(entries: List<JournalEntry>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mood Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        // Simple vertical timeline
        entries.sortedByDescending { it.date }.take(10).forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                val dateFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                Text(
                    text = dateFormat.format(java.util.Date(entry.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(60.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Mood Emoji
                Text(text = entry.mood.emoji, fontSize = 20.sp)
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Title
                Text(
                    text = entry.title.ifBlank { "Journal Entry" },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
