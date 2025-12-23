package com.lssgoo.planner.features.journal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.features.journal.components.JournalPromptCard
import com.lssgoo.planner.features.journal.components.MoodCalendar
import com.lssgoo.planner.features.journal.components.MoodDistributionChart
import com.lssgoo.planner.features.journal.components.MoodTimeline
import com.lssgoo.planner.features.journal.models.JournalEntry
import com.lssgoo.planner.features.journal.models.JournalMood
import com.lssgoo.planner.ui.components.EmptyState
import com.lssgoo.planner.ui.components.GradientFAB
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: PlannerViewModel,
    onEntryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.journalEntries.collectAsState()
    val prompts by viewModel.journalPrompts.collectAsState() // Observe strictly if needed, but we use getDailyPrompt
    val allGoals by viewModel.goals.collectAsState()
    
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var activePromptQuestion by remember { mutableStateOf<String?>(null) }
    
    val today = System.currentTimeMillis()
    val dailyPrompt = viewModel.getDailyPrompt()
    
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
                        Icons.Filled.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Journal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        floatingActionButton = {
            GradientFAB(
                onClick = { 
                    activePromptQuestion = null
                    showAddEntryDialog = true 
                },
                icon = Icons.Filled.Add
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Dashboard Section
            if (entries.isNotEmpty()) {
                item {
                    Column {
                        MoodDistributionChart(entries)
                        MoodCalendar(entries)
                        MoodTimeline(entries)
                    }
                }
            }
            
            // 2. Daily Prompt
            item {
                JournalPromptCard(
                    prompt = dailyPrompt,
                    onAnswerClick = {
                        activePromptQuestion = dailyPrompt?.text
                        showAddEntryDialog = true
                    }
                )
            }
            
            // 3. Entries List
            if (entries.isEmpty()) {
                item {
                    EmptyState(
                        title = "No journal entries yet",
                        description = "Start specific journaling with guided prompts.",
                        icon = Icons.Filled.Book,
                        actionText = "Write First Entry",
                        onActionClick = { showAddEntryDialog = true }
                    )
                }
            } else {
                item {
                     Text(
                        "Your Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(entries.sortedByDescending { it.date }) { entry ->
                   JournalEntryCard(
                       entry = entry,
                       onClick = { onEntryClick(entry.id) },
                       modifier = Modifier.padding(horizontal = 16.dp)
                   )
                }
            }
        }
    }
    
    if (showAddEntryDialog) {
        AddJournalEntryDialog(
            initialPrompt = activePromptQuestion,
            availableGoals = allGoals,
            onDismiss = { showAddEntryDialog = false },
            onAdd = { entry ->
                viewModel.addJournalEntry(entry)
                showAddEntryDialog = false
            }
        )
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                     Text(
                        text = dateFormat.format(Date(entry.date)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = entry.title.ifBlank { "Daily Entry" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = entry.mood.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            if (entry.content.isNotEmpty()) {
                Text(
                    text = entry.content.take(150) + if (entry.content.length > 150) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Structured Data Preview (Gratitude / Wins)
            if (entry.gratitude.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grateful for ${entry.gratitude.size} things", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
             if (entry.achievements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Achieved ${entry.achievements.size} goals", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
            }
            
            // Photo Placeholder logic
            if (entry.photos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                // Visual placeholder for photo
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Photo, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Photo Memory", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalEntryDialog(
    initialPrompt: String? = null,
    availableGoals: List<Goal>,
    onDismiss: () -> Unit,
    onAdd: (JournalEntry) -> Unit
) {
    var title by remember { mutableStateOf(initialPrompt ?: "") }
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf(JournalMood.NEUTRAL) }
    
    // Structured inputs
    var gratitude1 by remember { mutableStateOf("") }
    var gratitude2 by remember { mutableStateOf("") }
    var win1 by remember { mutableStateOf("") }
    var challenge1 by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }
    
    // Linking
    var selectedGoalId by remember { mutableStateOf<String?>(null) }
    var hasPhoto by remember { mutableStateOf(false) } // Mock photo attachment
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f).fillMaxWidth(),
        title = { Text("New Entry") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. Basic Info
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title / Prompt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // 2. Mood
                item {
                    Text("How are you feeling?", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        JournalMood.entries.forEach { mood ->
                           Column(horizontalAlignment = Alignment.CenterHorizontally) {
                               IconButton(
                                   onClick = { selectedMood = mood },
                                   modifier = Modifier
                                       .size(40.dp)
                                       .background(
                                           if (selectedMood == mood) Color(mood.color).copy(alpha = 0.3f) else Color.Transparent, 
                                           CircleShape
                                       )
                               ) {
                                   Text(mood.emoji, fontSize = 24.sp)
                               }
                           }
                        }
                    }
                }
                
                // 3. Main Content
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Details & Reflection") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        maxLines = 10
                    )
                }
                
                // 4. Structured Sections
                item {
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Structured Reflection", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
                
                item {
                    OutlinedTextField(
                        value = gratitude1,
                        onValueChange = { gratitude1 = it },
                        label = { Text("I am grateful for...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Favorite, null) }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = win1,
                        onValueChange = { win1 = it },
                        label = { Text("Daily Win / Achievement") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.EmojiEvents, null) }
                    )
                }
                
                 item {
                    OutlinedTextField(
                        value = challenge1,
                        onValueChange = { challenge1 = it },
                        label = { Text("Challenge Overcome") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Terrain, null) }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Label, null) }
                    )
                }
                
                // 5. Attachments
                item {
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { hasPhoto = !hasPhoto }) {
                            Icon(Icons.Default.PhotoCamera, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (hasPhoto) "Photo Attached" else "Attach Photo")
                        }
                        
                         // Simple goal linker dropdown could go here, for now simpler UI
                    }
                }
                
                // 6. Goal Link
                if (availableGoals.isNotEmpty()) {
                    item {
                        Text("Link to Goal", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())) {
                            availableGoals.forEach { goal ->
                                FilterChip(
                                    selected = selectedGoalId == goal.id,
                                    onClick = { selectedGoalId = if (selectedGoalId == goal.id) null else goal.id },
                                    label = { Text(goal.title) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val gratitudes = listOfNotNull(
                        gratitude1.ifBlank { null },
                        gratitude2.ifBlank { null }
                    )
                    val wins = listOfNotNull(win1.ifBlank { null })
                    val challenges = listOfNotNull(challenge1.ifBlank { null })
                    val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val photos = if (hasPhoto) listOf("mock_photo_uri") else emptyList()
                    val linkedGoals = if (selectedGoalId != null) listOf(selectedGoalId!!) else emptyList()
                    
                    onAdd(
                        JournalEntry(
                            date = System.currentTimeMillis(),
                            title = title,
                            content = content,
                            mood = selectedMood,
                            gratitude = gratitudes,
                            achievements = wins,
                            challenges = challenges,
                            tags = tags,
                            photos = photos,
                            linkedGoalIds = linkedGoals
                        )
                    )
                }
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

