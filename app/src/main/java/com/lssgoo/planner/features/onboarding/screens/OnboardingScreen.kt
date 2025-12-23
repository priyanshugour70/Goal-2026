package com.lssgoo.planner.features.onboarding.screens

import androidx.compose.animation.*
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Gender
import com.lssgoo.planner.features.onboarding.components.*
import com.lssgoo.planner.features.settings.models.UserProfile
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val isCheckingSync by viewModel.isCheckingSync.collectAsState()
    val savedProfile by viewModel.userProfile.collectAsState()
    
    var currentStep by remember { mutableIntStateOf(0) }
    var firstName by remember { mutableStateOf(savedProfile.firstName) }
    var lastName by remember { mutableStateOf(savedProfile.lastName) }
    var dateOfBirth by remember { mutableStateOf(savedProfile.dateOfBirth) }
    var gender by remember { mutableStateOf(savedProfile.gender) }
    var email by remember { mutableStateOf(savedProfile.email) }
    var occupation by remember { mutableStateOf(savedProfile.occupation) }
    
    // Sync UI with saved profile if data is restored from cloud
    LaunchedEffect(savedProfile) {
        if (firstName.isEmpty() && savedProfile.firstName.isNotEmpty()) {
            firstName = savedProfile.firstName
            lastName = savedProfile.lastName
            dateOfBirth = savedProfile.dateOfBirth
            gender = savedProfile.gender
            email = savedProfile.email
            occupation = savedProfile.occupation
            // If we restored a complete profile, maybe skip to the end
            if (savedProfile.firstName.isNotEmpty()) currentStep = 5
        }
    }

    val focusManager = LocalFocusManager.current
    
    val steps = listOf(
        "Welcome",
        "Name",
        "Birthday",
        "Gender",
        "Details",
        "Complete"
    )
    
    val isNextEnabled = when (currentStep) {
        0 -> true // Welcome step
        1 -> firstName.isNotBlank() // Name step
        2 -> true // DOB is optional
        3 -> true // Gender selection
        4 -> true // Details are optional
        else -> true
    }
    
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Decorative background shapes
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-175).dp, y = (-75).dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 125.dp, y = 75.dp)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f), CircleShape)
        )

        if (isCheckingSync) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(strokeWidth = 6.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Checking for cloud backup...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(systemBarsPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicator
                if (currentStep > 0 && currentStep < steps.size - 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LinearProgressIndicator(
                            progress = { currentStep.toFloat() / (steps.size - 2) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = steps[currentStep],
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Step content
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                        } using SizeTransform(clip = false)
                    },
                    label = "onboarding_step",
                    modifier = Modifier.weight(1f)
                ) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        when (step) {
                            0 -> WelcomeStep()
                            1 -> NameStep(
                                firstName = firstName,
                                lastName = lastName,
                                onFirstNameChange = { firstName = it },
                                onLastNameChange = { lastName = it },
                                focusManager = focusManager
                            )
                            2 -> BirthdayStep(
                                dateOfBirth = dateOfBirth,
                                onDateSelected = { dateOfBirth = it }
                            )
                            3 -> GenderStep(
                                selectedGender = gender,
                                onGenderSelected = { gender = it }
                            )
                            4 -> DetailsStep(
                                email = email,
                                occupation = occupation,
                                onEmailChange = { email = it },
                                onOccupationChange = { occupation = it },
                                focusManager = focusManager
                            )
                            5 -> CompleteStep(firstName = firstName)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Back button
                    if (currentStep > 0 && currentStep < steps.size - 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Next/Complete button
                    Button(
                        onClick = {
                            if (currentStep == steps.size - 1) {
                                // Complete onboarding
                                val userProfile = UserProfile(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    email = email.trim(),
                                    occupation = occupation.trim(),
                                    isOnboardingComplete = true
                                )
                                viewModel.saveUserProfile(userProfile)
                                viewModel.setOnboardingComplete(true)
                            } else {
                                currentStep++
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        enabled = isNextEnabled,
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = when (currentStep) {
                                0 -> "Get Started"
                                steps.size - 1 -> "Launch Planner"
                                else -> "Continue"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (currentStep == steps.size - 1) Icons.Filled.RocketLaunch else Icons.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                
                // Skip option for optional steps
                if (currentStep in 2..4) {
                    TextButton(
                        onClick = { currentStep++ },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Skip for now", fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
