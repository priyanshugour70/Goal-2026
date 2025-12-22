package com.lssgoo.goal2026.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.goal2026.data.model.*
import com.lssgoo.goal2026.ui.theme.*
import com.lssgoo.goal2026.ui.viewmodel.Goal2026ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: Goal2026ViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showExportSuccessDialog by remember { mutableStateOf(false) }
    var showImportSuccessDialog by remember { mutableStateOf(false) }
    var importError by remember { mutableStateOf<String?>(null) }
    
    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonData = reader.readText()
                    val success = viewModel.importData(jsonData)
                    if (success) {
                        showImportSuccessDialog = true
                    } else {
                        importError = "Invalid backup file format"
                    }
                }
            } catch (e: Exception) {
                importError = "Failed to read backup file: ${e.message}"
            }
        }
    }
    
    // Share launcher for export
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile card
            item {
                ProfileCard(
                    userName = settings.userName.ifBlank { "Goal Achiever" },
                    stats = stats
                )
            }
            
            // Data Management Section
            item {
                SettingsSection(title = "📦 Data Management") {
                    SettingsItem(
                        icon = Icons.Outlined.CloudUpload,
                        title = "Export Backup",
                        subtitle = "Save your data to a file",
                        onClick = {
                            val uri = viewModel.exportDataToFile(context)
                            if (uri != null) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                shareLauncher.launch(Intent.createChooser(shareIntent, "Save Backup"))
                                showExportSuccessDialog = true
                            }
                        },
                        iconColor = GoalColors.career
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Outlined.CloudDownload,
                        title = "Import Backup",
                        subtitle = "Restore data from a backup file",
                        onClick = {
                            importLauncher.launch("application/json")
                        },
                        iconColor = GoalColors.health
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Outlined.DeleteForever,
                        title = "Clear All Data",
                        subtitle = "Delete all data and start fresh",
                        onClick = { showClearDataDialog = true },
                        iconColor = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // App Info Section
            item {
                SettingsSection(title = "ℹ️ About") {
                    SettingsItem(
                        icon = Icons.Outlined.Info,
                        title = "App Version",
                        subtitle = "1.0.0",
                        onClick = { },
                        iconColor = GoalColors.learning
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Outlined.Code,
                        title = "Developer",
                        subtitle = "Built with ❤️ for your 2026 goals",
                        onClick = { },
                        iconColor = GoalColors.startup
                    )
                }
            }
            
            // Quick Stats
            item {
                SettingsSection(title = "📊 Statistics") {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatRow("Total Goals", stats.totalGoals.toString())
                        StatRow("Milestones Completed", "${stats.completedMilestones}/${stats.totalMilestones}")
                        StatRow("Tasks Completed Today", "${stats.tasksCompletedToday}/${stats.totalTasksToday}")
                        StatRow("Current Streak", "${stats.currentStreak} days")
                        StatRow("Longest Streak", "${stats.longestStreak} days")
                        StatRow("Overall Progress", "${(stats.overallProgress * 100).toInt()}%")
                    }
                }
            }
            
            // Tips Section
            item {
                TipsCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // Clear Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Clear All Data?") },
            text = { 
                Text(
                    "This will delete all your goals progress, notes, tasks, and calendar events. " +
                    "This action cannot be undone. Consider exporting a backup first."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Export Success Dialog
    if (showExportSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showExportSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = completedColor
                )
            },
            title = { Text("Backup Ready!") },
            text = { 
                Text(
                    "Your backup file is ready to be saved or shared. " +
                    "Keep it safe to restore your data later!"
                ) 
            },
            confirmButton = {
                Button(onClick = { showExportSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Import Success Dialog
    if (showImportSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showImportSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = completedColor
                )
            },
            title = { Text("Data Restored!") },
            text = { 
                Text("Your data has been successfully restored from the backup file.") 
            },
            confirmButton = {
                Button(onClick = { showImportSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Import Error Dialog
    if (importError != null) {
        AlertDialog(
            onDismissRequest = { importError = null },
            icon = {
                Icon(
                    Icons.Filled.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Import Failed") },
            text = { Text(importError!!) },
            confirmButton = {
                Button(onClick = { importError = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ProfileCard(
    userName: String,
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(GradientColors.purpleBlue)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.first().uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "2026 Goal Crusher 🚀",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(
                        value = "${(stats.overallProgress * 100).toInt()}%",
                        label = "Progress"
                    )
                    ProfileStat(
                        value = "${stats.currentStreak}",
                        label = "Streak 🔥"
                    )
                    ProfileStat(
                        value = "${stats.completedMilestones}",
                        label = "Milestones"
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        trailing?.invoke() ?: Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TipsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💡 Pro Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "📤 Regularly backup your data to avoid losing progress",
                "📧 Share backup file to your email for safe storage",
                "☁️ Save backup to Google Drive or other cloud storage",
                "📱 Before changing phones, export your data first"
            )
            
            tips.forEach { tip ->
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
