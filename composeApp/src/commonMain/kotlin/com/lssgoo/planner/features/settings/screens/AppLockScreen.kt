package com.lssgoo.planner.features.settings.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen(
    viewModel: PlannerViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val isPinSet = !settings.pinCode.isNullOrEmpty()
    
    // States: OVERVIEW, SETUP_ENTER, SETUP_CONFIRM, UNLOCK_TO_DISABLE
    var currentScreenState by remember { mutableStateOf<LockScreenState>(LockScreenState.Overview) }
    
    // Temp PIN storage
    var firstPinEntry by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Lock", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedContent(targetState = currentScreenState) { state ->
                when (state) {
                    LockScreenState.Overview -> {
                        OverviewContent(
                            isPinSet = isPinSet,
                            onSetupClick = { currentScreenState = LockScreenState.SetupEnter },
                            onDisableClick = { 
                                // To disable, we might want to confirm PIN first.
                                // For simplicity, we can clear directly or ask.
                                // Let's ask to unlock first for security.
                                currentScreenState = LockScreenState.UnlockToDisable 
                            }
                        )
                    }
                    LockScreenState.SetupEnter -> {
                        PinEntryContent(
                            title = "Create PIN",
                            instruction = "Enter a 4-digit PIN",
                            onPinEntered = { pin ->
                                firstPinEntry = pin
                                currentScreenState = LockScreenState.SetupConfirm
                            },
                             onCancel = { currentScreenState = LockScreenState.Overview }
                        )
                    }
                    LockScreenState.SetupConfirm -> {
                        PinEntryContent(
                            title = "Confirm PIN",
                            instruction = "Re-enter your PIN",
                            onPinEntered = { pin ->
                                if (pin == firstPinEntry) {
                                    viewModel.setPinCode(pin)
                                    currentScreenState = LockScreenState.Overview
                                } else {
                                    // Error feedback could be added here
                                    // For now, simple return to start
                                    currentScreenState = LockScreenState.SetupEnter
                                }
                            },
                             onCancel = { currentScreenState = LockScreenState.Overview },
                             isError = false // Could pass true if retry
                        )
                    }
                    LockScreenState.UnlockToDisable -> {
                         PinEntryContent(
                            title = "Enter PIN",
                            instruction = "Enter current PIN to disable lock",
                            onPinEntered = { pin ->
                                if (pin == settings.pinCode) {
                                    viewModel.setPinCode(null)
                                    currentScreenState = LockScreenState.Overview
                                } else {
                                    // Wrong PIN
                                }
                            },
                             onCancel = { currentScreenState = LockScreenState.Overview }
                        )
                    }
                }
            }
        }
    }
}

enum class LockScreenState {
    Overview, SetupEnter, SetupConfirm, UnlockToDisable
}

@Composable
fun OverviewContent(
    isPinSet: Boolean,
    onSetupClick: () -> Unit,
    onDisableClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isPinSet) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            if (isPinSet) "App Locked" else "App Unlocked",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isPinSet) 
                "Your app is protected with a PIN code. You will need to enter it every time you open the app." 
            else 
                "Secure your personal data by setting up a PIN code.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (isPinSet) {
             Button(
                onClick = onDisableClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Remove PIN Lock", fontSize = 18.sp)
            }
             Spacer(modifier = Modifier.height(16.dp))
             OutlinedButton(
                onClick = onSetupClick, // Leads to change pin -> fundamentally same as setup flow
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Change PIN", fontSize = 18.sp)
            }
        } else {
             Button(
                onClick = onSetupClick,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Setup PIN Lock", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PinEntryContent(
    title: String,
    instruction: String,
    onPinEntered: (String) -> Unit,
    onCancel: () -> Unit,
    isError: Boolean = false
) {
    var currentPin by remember { mutableStateOf("") }
    
    LaunchedEffect(currentPin) {
        if (currentPin.length == 4) {
            onPinEntered(currentPin)
            currentPin = ""
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top area
        Column(
            modifier = Modifier.weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(instruction, style = MaterialTheme.typography.bodyLarge, color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(4) { index ->
                    val filled = index < currentPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha=0.5f),
                                CircleShape
                            )
                    )
                }
            }
        }
        
        // Keypad
        Column(
            modifier = Modifier.weight(0.6f).fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("Cancel", "0", "Back")
            )
            
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                   row.forEach { key ->
                       PinKey(key) {
                           when (key) {
                               "Back" -> if (currentPin.isNotEmpty()) currentPin = currentPin.dropLast(1)
                               "Cancel" -> onCancel()
                               else -> if (currentPin.length < 4) currentPin += key
                           }
                       }
                   }
                }
            }
        }
    }
}

@Composable
fun PinKey(key: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (key == "Back") {
            Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface)
        } else if (key == "Cancel") {
             Text("Cancel", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
        } else {
            Text(key, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
