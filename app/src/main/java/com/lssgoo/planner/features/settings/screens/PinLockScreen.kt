package com.lssgoo.planner.features.settings.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

@Composable
fun PinLockScreen(
    viewModel: PlannerViewModel,
    onUnlockSuccess: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val savedPin = settings.pinCode
    
    var currentInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isSettingUp by remember { mutableStateOf(savedPin == null) }
    var pinToConfirm by remember { mutableStateOf<String?>(null) }
    
    // Auto-check when exactly 4 digits entered
    LaunchedEffect(currentInput) {
        if (currentInput.length == 4) {
             if (isSettingUp) {
                if (pinToConfirm == null) {
                    pinToConfirm = currentInput
                    currentInput = ""
                    errorMessage = ""
                } else {
                    if (currentInput == pinToConfirm) {
                        viewModel.updateSettings(settings.copy(pinCode = currentInput))
                        onUnlockSuccess()
                    } else {
                        errorMessage = "PINs do not match. Try again."
                        currentInput = ""
                        pinToConfirm = null
                    }
                }
            } else {
                if (currentInput == savedPin) {
                    onUnlockSuccess()
                } else {
                    errorMessage = "Incorrect PIN"
                    currentInput = ""
                }
            }
        }
    }

    Scaffold { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val title = when {
                isSettingUp && pinToConfirm == null -> "Create a 4-digit PIN"
                isSettingUp -> "Confirm your PIN"
                else -> "Enter PIN to Unlock"
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // PIN Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(4) { index ->
                    val filled = index < currentInput.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )
                
                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            if (key.isEmpty()) {
                                Spacer(modifier = Modifier.size(72.dp))
                            } else if (key == "DEL") {
                                IconButton(
                                    onClick = { 
                                        if (currentInput.isNotEmpty()) {
                                            currentInput = currentInput.dropLast(1)
                                        }
                                    },
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(Icons.Default.Backspace, null)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            if (currentInput.length < 4) {
                                                currentInput += key
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
