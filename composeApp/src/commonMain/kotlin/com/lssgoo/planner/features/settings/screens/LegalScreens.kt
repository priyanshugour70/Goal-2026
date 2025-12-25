package com.lssgoo.planner.features.settings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreenShell(
    title: String,
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
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(16.dp)
        ) {
            content()
        }
    }
}


@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    LegalScreenShell(title = "Privacy Policy", onBack = onBack) {
        LegalHeader(icon = Icons.Outlined.Security, text = "Your Trust Is Our Priority")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "1. Introduction",
            body = "Welcome to Planner. We are committed to protecting your personal information and your right to privacy. If you have any questions or concerns about this privacy notice, or our practices with regards to your personal information, please contact us."
        )
        LegalSection(
            title = "2. Information We Collect",
            body = "We collect personal information that you voluntarily provide to us when you register on the App, expressed an interest in obtaining information about us or our products and Services, when you participate in activities on the App or otherwise when you contact us.\n\nThe personal information that we collect depends on the context of your interactions with us and the App, the choices you make and the products and features you use."
        )
        LegalSection(
            title = "3. How We Use Your Information",
            body = "We use personal information collected via our App for a variety of business purposes described below. We process your personal information for these purposes in reliance on our legitimate business interests, in order to enter into or perform a contract with you, with your consent, and/or for compliance with our legal obligations.\n\n- To facilitate account creation and logon process.\n- To post testimonials.\n- Request feedback.\n- To enable user-to-user communications."
        )
        LegalSection(
            title = "4. Will Your Information Be Shared With Anyone?",
            body = "We only share information with your consent, to comply with laws, to provide you with services, to protect your rights, or to fulfill business obligations."
        )
        LegalSection(
            title = "5. How Long Do We Keep Your Information?",
            body = "We keep your information for as long as necessary to fulfill the purposes outlined in this privacy notice unless otherwise required by law."
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    LegalScreenShell(title = "Terms of Service", onBack = onBack) {
        LegalHeader(icon = Icons.Outlined.Gavel, text = "Agreements for Usage")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "1. Agreement to Terms",
            body = "These Terms of Use constitute a legally binding agreement made between you, whether personally or on behalf of an entity (“you”) and LssGoo (“Company”, “we”, “us”, or “our”), concerning your access to and use of the Planner application as well as any other related media form, media channel, mobile website or mobile application related, linked, or otherwise connected thereto (collectively, the “Site”)."
        )
        LegalSection(
            title = "2. Intellectual Property Rights",
            body = "Unless otherwise indicated, the Site is our proprietary property and all source code, databases, functionality, software, website designs, audio, video, text, photographs, and graphics on the Site (collectively, the “Content”) and the trademarks, service marks, and logos contained therein (the “Marks”) are owned or controlled by us or licensed to us, and are protected by copyright and trademark laws."
        )
        LegalSection(
            title = "3. User Representations",
            body = "By using the Site, you represent and warrant that:\n(1) all registration information you submit will be true, accurate, current, and complete.\n(2) you will maintain the accuracy of such information and promptly update such registration information as necessary.\n(3) you have the legal capacity and you agree to comply with these Terms of Use."
        )
        LegalSection(
            title = "4. Prohibited Activities",
            body = "You may not access or use the Site for any purpose other than that for which we make the Site available. The Site may not be used in connection with any commercial endeavors except those that are specifically endorsed or approved by us."
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DataUsageScreen(onBack: () -> Unit) {
    LegalScreenShell(title = "Data Usage Policy", onBack = onBack) {
        LegalHeader(icon = Icons.Outlined.Description, text = "Transparency in Data")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "1. Local Storage First",
            body = "Planner is built on a Local-First architecture. This means 99% of your data (Goals, Tasks, Notes, Habits) lives directly on your device's internal storage. We do not transmit this data to any external server during normal operation without your explicit action (like Cloud Backups)."
        )
        LegalSection(
            title = "2. Cloud Sync (Optional)",
            body = "If you choose to enable Cloud Sync, an encrypted copy of your database is uploaded to our secure AWS S3 buckets. This data is accessible only by you using your credentials. We do not mine, read, or sell this data."
        )
        LegalSection(
            title = "3. Analytics",
            body = "We use anonymous usage analytics to understand which features are most popular. This data includes screen views and button clicks but never includes personal content like your journal entries or goal titles."
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LegalHeader(icon: ImageVector, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun LegalSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}
