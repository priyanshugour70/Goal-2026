package com.lssgoo.planner.features.onboarding.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.ThemeMode
import com.lssgoo.planner.data.model.UserProfile
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: PlannerViewModel) {
    var step by remember { mutableStateOf(OnboardingStep.WELCOME) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    
    // User data state
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val themeMode by viewModel.settings.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Dynamic Animated Background
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button at the top (only show if not on FINISH step)
            if (step != OnboardingStep.FINISH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.setOnboardingComplete(true)
                        }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
            
            // Step Indicator
            OnboardingProgress(currentStep = step)
            
            Spacer(modifier = Modifier.height(32.dp))

            // Main Content Area
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.9f)).togetherWith(fadeOut() + scaleOut(targetScale = 1.1f))
                    }
                ) { currentStep ->
                    when (currentStep) {
                        OnboardingStep.WELCOME -> WelcomeView()
                        OnboardingStep.PROFILE -> ProfileView(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            onFirstNameChange = { firstName = it },
                            onLastNameChange = { lastName = it },
                            onEmailChange = { email = it }
                        )
                        OnboardingStep.THEME -> ThemeView(
                            currentMode = themeMode.themeMode,
                            onThemeSelect = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateSettings(viewModel.settings.value.copy(themeMode = it)) 
                            }
                        )
                        OnboardingStep.FINISH -> FinishView(firstName = firstName.ifBlank { "there" })
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Button
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    when (step) {
                        OnboardingStep.WELCOME -> step = OnboardingStep.PROFILE
                        OnboardingStep.PROFILE -> if (firstName.isNotBlank()) step = OnboardingStep.THEME
                        OnboardingStep.THEME -> step = OnboardingStep.FINISH
                        OnboardingStep.FINISH -> {
                            scope.launch {
                                if (firstName.isNotBlank()) {
                                    viewModel.saveUserProfile(
                                        UserProfile(
                                            firstName = firstName,
                                            lastName = lastName,
                                            email = email,
                                            isOnboardingComplete = true
                                        )
                                    )
                                }
                                viewModel.setOnboardingComplete(true)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                enabled = if (step == OnboardingStep.PROFILE) firstName.isNotBlank() else true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (step == OnboardingStep.FINISH) "Let's Begin" else "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (step == OnboardingStep.FINISH) Icons.Default.RocketLaunch else Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WelcomeView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Master Your 2026",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Organize your goals, track your habits, and build the life you've always dreamed of.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    firstName: String,
    lastName: String,
    email: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Person, null) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Badge, null) },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Email, null) },
            singleLine = true
        )
    }
}

@Composable
fun ThemeView(
    currentMode: ThemeMode,
    onThemeSelect: (ThemeMode) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Choose your style",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeCard(
                mode = ThemeMode.LIGHT,
                isSelected = currentMode == ThemeMode.LIGHT,
                icon = Icons.Default.LightMode,
                label = "Light",
                modifier = Modifier.weight(1f),
                onClick = { onThemeSelect(ThemeMode.LIGHT) }
            )
            ThemeCard(
                mode = ThemeMode.DARK,
                isSelected = currentMode == ThemeMode.DARK,
                icon = Icons.Default.DarkMode,
                label = "Dark",
                modifier = Modifier.weight(1f),
                onClick = { onThemeSelect(ThemeMode.DARK) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ThemeCard(
            mode = ThemeMode.SYSTEM,
            isSelected = currentMode == ThemeMode.SYSTEM,
            icon = Icons.Default.SettingsSuggest,
            label = "System Default",
            modifier = Modifier.fillMaxWidth(),
            onClick = { onThemeSelect(ThemeMode.SYSTEM) }
        )
    }
}

@Composable
fun ThemeCard(
    mode: ThemeMode,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FinishView(firstName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f), CircleShape)
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Welcome aboard, $firstName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your personalized planner is ready. Let's make 2026 your best year yet.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun OnboardingProgress(currentStep: OnboardingStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OnboardingStep.entries.forEach { step ->
            val isActive = step.ordinal <= currentStep.ordinal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val color1 = Color(0xFF6366F1).copy(alpha = 0.05f)
        val color2 = Color(0xFFA855F7).copy(alpha = 0.05f)
        val color3 = Color(0xFFEC4899).copy(alpha = 0.05f)
        
        drawCircle(
            brush = Brush.radialGradient(listOf(color1, Color.Transparent)),
            radius = 600f,
            center = Offset(100f + (offsetX / 10f), 200f)
        )
        
        drawCircle(
            brush = Brush.radialGradient(listOf(color2, Color.Transparent)),
            radius = 800f,
            center = Offset(size.width - (offsetX / 5f), size.height / 2f)
        )
        
        drawCircle(
            brush = Brush.radialGradient(listOf(color3, Color.Transparent)),
            radius = 700f,
            center = Offset(offsetX / 8f, size.height - 100f)
        )
    }
}

enum class OnboardingStep {
    WELCOME, PROFILE, THEME, FINISH
}
