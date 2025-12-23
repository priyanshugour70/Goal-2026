package com.lssgoo.planner.features.settings.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.ui.theme.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PlannerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        AppIcons.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
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
                var showEditProfileDialog by remember { mutableStateOf(false) }
                
                ProfileCard(
                    userProfile = userProfile,
                    stats = stats,
                    onEditClick = { showEditProfileDialog = true }
                )
                
                // Edit Profile Dialog
                if (showEditProfileDialog) {
                    EditProfileDialog(
                        userProfile = userProfile,
                        onDismiss = { showEditProfileDialog = false },
                        onSave = { updatedProfile ->
                            viewModel.updateUserProfile(updatedProfile)
                            showEditProfileDialog = false
                        }
                    )
                }
            }
            
            // Appearance Section
            item {
                var showThemeDialog by remember { mutableStateOf(false) }
                
                SettingsSection(
                    title = "Appearance",
                    icon = Icons.Outlined.Palette
                ) {
                    SettingsItem(
                        icon = if (settings.themeMode == ThemeMode.DARK) Icons.Filled.DarkMode 
                               else if (settings.themeMode == ThemeMode.LIGHT) Icons.Filled.LightMode
                               else Icons.Filled.BrightnessAuto,
                        title = "Theme",
                        subtitle = when (settings.themeMode) {
                            ThemeMode.LIGHT -> "Classic Light"
                            ThemeMode.DARK -> "Classic Dark"
                            ThemeMode.SYSTEM -> "System Default"
                            ThemeMode.OCEAN -> "Deep Ocean"
                            ThemeMode.SUNSET -> "Sunset Glow"
                            ThemeMode.FOREST -> "Forest Green"
                            ThemeMode.MIDNIGHT -> "Midnight Purple"
                        },
                        onClick = { showThemeDialog = true },
                        iconColor = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Theme Selection Dialog
                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        icon = {
                            Icon(
                                Icons.Filled.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("Choose Your Vibe") },
                        text = {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.BrightnessAuto,
                                        title = "System Default",
                                        isSelected = settings.themeMode == ThemeMode.SYSTEM,
                                        colorPreview = MaterialTheme.colorScheme.outline,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.SYSTEM))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.LightMode,
                                        title = "Classic Light",
                                        isSelected = settings.themeMode == ThemeMode.LIGHT,
                                        colorPreview = Color.White,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.LIGHT))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.DarkMode,
                                        title = "Classic Dark",
                                        isSelected = settings.themeMode == ThemeMode.DARK,
                                        colorPreview = Color.Black,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.DARK))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.WaterDrop,
                                        title = "Deep Ocean",
                                        isSelected = settings.themeMode == ThemeMode.OCEAN,
                                        colorPreview = ThemePreviewColors.ocean,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.OCEAN))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.WbSunny,
                                        title = "Sunset Glow",
                                        isSelected = settings.themeMode == ThemeMode.SUNSET,
                                        colorPreview = ThemePreviewColors.sunset,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.SUNSET))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.Park,
                                        title = "Forest Green",
                                        isSelected = settings.themeMode == ThemeMode.FOREST,
                                        colorPreview = ThemePreviewColors.forest,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.FOREST))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                                item {
                                    ThemeOption(
                                        icon = Icons.Filled.NightsStay,
                                        title = "Midnight Purple",
                                        isSelected = settings.themeMode == ThemeMode.MIDNIGHT,
                                        colorPreview = ThemePreviewColors.midnight,
                                        onClick = {
                                            viewModel.updateSettings(settings.copy(themeMode = ThemeMode.MIDNIGHT))
                                            showThemeDialog = false
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            
            // Cloud Sync Section
            item {
                val isSyncing by viewModel.isSyncing.collectAsState()
                val lastSyncTime by viewModel.lastSyncTime.collectAsState()
                val lastSyncTimeValue = lastSyncTime // Store in local variable for smart cast
                
                SettingsSection(
                    title = "Cloud Sync (AWS S3)",
                    icon = Icons.Filled.CloudSync
                ) {
                    SettingsItem(
                        icon = Icons.Filled.CloudUpload,
                        title = "Sync to Cloud",
                        subtitle = if (lastSyncTimeValue != null) {
                            "Last synced: ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastSyncTimeValue))}"
                        } else {
                            "Upload your data to AWS S3"
                        },
                        onClick = {
                            if (!isSyncing) {
                                viewModel.syncToCloud()
                            }
                        },
                        iconColor = GoalColors.career,
                        trailing = {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    SettingsItem(
                        icon = Icons.Filled.CloudDownload,
                        title = "Sync from Cloud",
                        subtitle = "Download your data from AWS S3",
                        onClick = {
                            if (!isSyncing) {
                                viewModel.syncFromCloud()
                            }
                        },
                        iconColor = GoalColors.health,
                        trailing = {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )
                }
            }
            
            // Data Management Section
            item {
                SettingsSection(
                    title = "Data Management",
                    icon = AppIcons.SettingsBackupRestore
                ) {
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
                SettingsSection(
                    title = "About",
                    icon = AppIcons.Info
                ) {
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
                        subtitle = "Built with ❤️ for your goals",
                        onClick = { },
                        iconColor = GoalColors.startup
                    )
                }
            }
            
            // Quick Stats
            item {
                SettingsSection(
                    title = "Statistics",
                    icon = AppIcons.Assessment
                ) {
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
    userProfile: com.lssgoo.planner.data.model.UserProfile?,
    stats: DashboardStats,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userName = userProfile?.firstName ?: "Goal Achiever"
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with Outer Ring
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.first().uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = userProfile?.occupation?.uppercase() ?: "VISIONARY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Detailed Stats Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(
                        value = "${(stats.overallProgress * 100).toInt()}%",
                        label = "Goal Progress",
                        icon = Icons.Default.TrendingUp
                    )
                    VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    ProfileStat(
                        value = "${stats.currentStreak}",
                        label = "Day Streak",
                        icon = Icons.Default.LocalFireDepartment
                    )
                    VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    ProfileStat(
                        value = "${stats.completedMilestones}",
                        label = "Victories",
                        icon = Icons.Default.EmojiEvents
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
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 9.sp
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector? = null,
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
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
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
fun ThemeOption(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    colorPreview: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Theme Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title and Preview
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colorPreview)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSelected) "Active" else "Preview",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isSelected) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    userProfile: com.lssgoo.planner.data.model.UserProfile?,
    onDismiss: () -> Unit,
    onSave: (com.lssgoo.planner.data.model.UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(userProfile?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userProfile?.lastName ?: "") }
    var email by remember { mutableStateOf(userProfile?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(userProfile?.phoneNumber ?: "") }
    var occupation by remember { mutableStateOf(userProfile?.occupation ?: "") }
    var selectedGender by remember { mutableStateOf(userProfile?.gender ?: com.lssgoo.planner.data.model.Gender.PREFER_NOT_TO_SAY) }
    
    val dateFormat = remember { java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()) }
    val dateOfBirthDisplay = remember(userProfile?.dateOfBirth) {
        userProfile?.dateOfBirth?.let { dateFormat.format(java.util.Date(it)) } ?: "Not set"
    }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(userProfile?.dateOfBirth) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Name
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Last Name
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Phone Number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Occupation
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = { Text("Occupation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Date of Birth
                OutlinedTextField(
                    value = dateOfBirthDisplay,
                    onValueChange = { },
                    label = { Text("Date of Birth") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Select Date")
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Gender
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.lssgoo.planner.data.model.Gender.entries.forEach { gender ->
                        FilterChip(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            label = { Text(gender.displayName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedProfile = (userProfile ?: com.lssgoo.planner.data.model.UserProfile()).copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phoneNumber = phoneNumber,
                        occupation = occupation,
                        gender = selectedGender,
                        dateOfBirth = selectedDate,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(updatedProfile)
                },
                enabled = firstName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Date Picker (simplified - using a basic dialog)
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Date of Birth") },
            text = {
                Text("Date picker implementation - for now, you can manually enter dates in the future")
            },
            confirmButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    AppIcons.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pro Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "Regularly backup your data to avoid losing progress",
                "Share backup file to your email for safe storage",
                "Save backup to Google Drive or other cloud storage",
                "Before changing phones, export your data first"
            )
            
            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        AppIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
