package com.lssgoo.planner.features.settings.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.ui.theme.GoalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreenShell(
    title: String,
    icon: ImageVector,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun AboutDeveloperScreen(onBack: () -> Unit) {
    InfoScreenShell(title = "About Developer", icon = Icons.Default.Code, onBack = onBack) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Built with ❤️ by",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "The LSSGOO Team",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "We are dedicated to crafting tools that help you master your time and reach your peak potential. Planner is our vision of a unified command center for a focused life.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Our Mission", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "To bridge the gap between dreaming and doing through elegant, intuitive technology.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

data class VersionInfo(val version: String, val date: String, val changes: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionHistoryScreen(onBack: () -> Unit) {
    val versions = listOf(
        VersionInfo("1.2.0", "Dec 24, 2025", listOf("Unified Calendar Activity View", "Enhanced Finance Tracking", "Daily Pulse Indicators")),
        VersionInfo("1.1.0", "Dec 15, 2025", listOf("Cloud Sync with AWS S3", "New Midnight Purple Theme", "Performance Improvements")),
        VersionInfo("1.0.0", "Nov 25, 2025", listOf("Initial Release", "Core Goal Management", "Smart Notes & Tasks"))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Version History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(versions) { ver ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("v${ver.version}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(ver.date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ver.changes.forEach { change ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(change, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    InfoScreenShell(title = "Privacy Policy", icon = Icons.Default.VerifiedUser, onBack = onBack) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Text(
                    "Your Privacy Matters",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "1. Data Ownership: All your data is stored locally on your device unless you explicitly enable Cloud Sync.\n\n" +
                    "2. Information We Collect: We do not sell your personal data. We only collect basic diagnostic information to improve app stability.\n\n" +
                    "3. Security: We use industry-standard encryption for your PIN and cloud backups.\n\n" +
                    "4. Your Rights: You have full control over your data, including the ability to export and clear all data at any time from the Settings menu.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    InfoScreenShell(title = "Terms of Service", icon = Icons.Default.Gavel, onBack = onBack) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Text(
                    "Terms of Use",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "By using Planner, you agree to the following terms:\n\n" +
                    "• The app is provided 'as is' without warranties of any kind.\n" +
                    "• You are responsible for maintaining the confidentiality of your PIN.\n" +
                    "• We are not liable for any data loss that occurs due to device failure or clearing local storage.\n" +
                    "• You agree not to use the app for any illegal activities.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp
                )
            }
        }
    }
}
