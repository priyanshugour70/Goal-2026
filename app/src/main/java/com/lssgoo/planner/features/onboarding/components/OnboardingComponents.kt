package com.lssgoo.planner.features.onboarding.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Gender
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.GradientColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WelcomeStep() {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCirc),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(scale)
                .shadow(elevation = 24.dp, shape = CircleShape, spotColor = GradientColors.purpleBlue.first().copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(GradientColors.purpleBlue)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Trophy,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Welcome to Planner",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your journey to crushing your goals starts here. Let's set up your profile to personalize your experience.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Feature highlights with cards for premium feel
        FeatureHighlight(
            icon = AppIcons.Target,
            title = "Track 11 Life Goals",
            description = "Organized system for your priorities"
        )
        
        FeatureHighlight(
            icon = AppIcons.Notification,
            title = "Smart Reminders",
            description = "Never miss important deadlines"
        )
        
        FeatureHighlight(
            icon = AppIcons.Lightbulb,
            title = "Daily Motivation",
            description = "500+ inspiring thoughts"
        )
    }
}

@Composable
fun FeatureHighlight(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NameStep(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    focusManager: FocusManager
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "What's your name?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "We'll use this to personalize your experience",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last Name (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayStep(
    dateOfBirth: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Cake,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "When's your birthday?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "We'll celebrate with you!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Surface(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (dateOfBirth != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Text(
                    text = dateOfBirth?.let { dateFormat.format(Date(it)) } ?: "Select your birthday",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (dateOfBirth != null) MaterialTheme.colorScheme.onSurface 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (dateOfBirth != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(
                onClick = { onDateSelected(null) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Selection", fontWeight = FontWeight.SemiBold)
            }
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateOfBirth ?: (System.currentTimeMillis() - 20L * 365 * 24 * 60 * 60 * 1000)
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(28.dp)
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun GenderStep(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Wc,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "How should we address you?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "This helps us personalize greetings",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Gender.entries.forEach { gender ->
                GenderOption(
                    gender = gender,
                    isSelected = selectedGender == gender,
                    onClick = { onGenderSelected(gender) }
                )
            }
        }
    }
}

@Composable
fun GenderOption(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (gender) {
        Gender.MALE -> Icons.Filled.Male
        Gender.FEMALE -> Icons.Filled.Female
        Gender.NON_BINARY -> Icons.Filled.Transgender
        Gender.PREFER_NOT_TO_SAY -> Icons.Filled.HelpOutline
    }
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) 
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Text(
                text = gender.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun DetailsStep(
    email: String,
    occupation: String,
    onEmailChange: (String) -> Unit,
    onOccupationChange: (String) -> Unit,
    focusManager: FocusManager
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.ContactMail,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "A bit more about you",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "These details are optional",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        OutlinedTextField(
            value = occupation,
            onValueChange = onOccupationChange,
            label = { Text("Occupation (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            placeholder = { Text("e.g., Software Engineer") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Composable
fun CompleteStep(firstName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutBack),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebration_scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .shadow(elevation = 32.dp, shape = CircleShape, spotColor = GradientColors.cyanGreen.first().copy(alpha = 0.5f))
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(GradientColors.cyanGreen)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Celebration,
                contentDescription = null,
                modifier = Modifier.size(90.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = if (firstName.isNotBlank()) "You're all set, $firstName!" else "You're all set!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your Planner journey begins now. Time to transform your dreams into reality!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Summary card with better visual design
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What's waiting for you:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryItem(value = "11", label = "Goals")
                    VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    SummaryItem(value = "500+", label = "Thoughts")
                    VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    SummaryItem(value = "∞", label = "Potential")
                }
            }
        }
    }
}

@Composable
fun SummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
