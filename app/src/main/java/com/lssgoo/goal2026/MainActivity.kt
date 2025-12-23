package com.lssgoo.goal2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lssgoo.goal2026.data.model.UserProfile
import com.lssgoo.goal2026.ui.components.AppIcons
import com.lssgoo.goal2026.ui.navigation.BottomNavDestination
import com.lssgoo.goal2026.ui.navigation.Routes
import com.lssgoo.goal2026.ui.screens.calendar.CalendarScreen
import com.lssgoo.goal2026.ui.screens.dashboard.DashboardScreen
import com.lssgoo.goal2026.ui.screens.goals.GoalDetailScreen
import com.lssgoo.goal2026.ui.screens.goals.GoalsScreen
import com.lssgoo.goal2026.ui.screens.notes.NotesScreen
import com.lssgoo.goal2026.ui.screens.onboarding.OnboardingScreen
import com.lssgoo.goal2026.ui.screens.reminders.RemindersScreen
import com.lssgoo.goal2026.ui.screens.settings.SettingsScreen
import com.lssgoo.goal2026.ui.screens.tasks.TasksScreen
import com.lssgoo.goal2026.ui.theme.Goal2026Theme
import com.lssgoo.goal2026.ui.theme.GradientColors
import com.lssgoo.goal2026.ui.viewmodel.Goal2026ViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: Goal2026ViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()
            
            Goal2026Theme(themeMode = settings.themeMode) {
                Goal2026App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Goal2026App() {
    val navController = rememberNavController()
    val viewModel: Goal2026ViewModel = viewModel()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Get onboarding status
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    
    // Dynamic status bar color based on current route
    val view = LocalView.current
    val statusBarColor = getStatusBarColorForRoute(currentRoute)
    val isDarkTheme = MaterialTheme.colorScheme.background == Color(0xFF0D0D1A) // backgroundDark
    
    LaunchedEffect(currentRoute, statusBarColor, isDarkTheme) {
        if (!view.isInEditMode) {
            val window = (view.context as android.app.Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            // Set status bar color - using deprecated method for compatibility
            @Suppress("DEPRECATION")
            window.statusBarColor = statusBarColor.toArgb()
        }
    }
    
    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute in BottomNavDestination.entries.map { it.route } && isOnboardingComplete
    
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    
    // Show snackbar when message changes
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }
    
    // Show onboarding if not complete
    if (!isOnboardingComplete) {
        OnboardingScreen(
            onComplete = { profile ->
                viewModel.saveUserProfile(profile)
                viewModel.setOnboardingComplete()
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    DynamicBottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            },
            contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.DASHBOARD,
                modifier = Modifier.padding(paddingValues)
            ) {
                // Dashboard
                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onGoalClick = { goalId ->
                            navController.navigate(Routes.goalDetail(goalId))
                        },
                        onViewAllGoals = {
                            navController.navigate(Routes.GOALS)
                        },
                        onViewAllTasks = {
                            navController.navigate(Routes.TASKS)
                        }
                    )
                }
                
                // Goals list
                composable(Routes.GOALS) {
                    GoalsScreen(
                        viewModel = viewModel,
                        onGoalClick = { goalId ->
                            navController.navigate(Routes.goalDetail(goalId))
                        }
                    )
                }
                
                // Goal detail
                composable(
                    route = Routes.GOAL_DETAIL,
                    arguments = listOf(navArgument("goalId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                    GoalDetailScreen(
                        goalId = goalId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                
                // Calendar
                composable(Routes.CALENDAR) {
                    CalendarScreen(viewModel = viewModel)
                }
                
                // Notes
                composable(Routes.NOTES) {
                    NotesScreen(viewModel = viewModel)
                }
                
                // Tasks
                composable(Routes.TASKS) {
                    TasksScreen(viewModel = viewModel)
                }
                
                // Reminders
                composable(Routes.REMINDERS) {
                    RemindersScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                
                // Settings
                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

/**
 * Get dynamic status bar color based on current route
 * Matches the background color to prevent visual collapse while maintaining edge-to-edge
 */
@Composable
fun getStatusBarColorForRoute(route: String?): Color {
    // Always match the background color to prevent collapse
    // The status bar will blend seamlessly with the app background
    return MaterialTheme.colorScheme.background
}

/**
 * Dynamic Bottom Navigation Bar with iOS-like polish
 * Matches the current page theme and uses icons instead of emojis
 */
@Composable
fun DynamicBottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    // Get accent color based on current route
    val accentColor = getAccentColorForRoute(currentRoute)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Glassmorphism effect card with dynamic colors
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = accentColor.copy(alpha = 0.3f),
                    spotColor = accentColor.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    val isSelected = currentRoute == destination.route
                    
                    DynamicNavItem(
                        destination = destination,
                        isSelected = isSelected,
                        accentColor = accentColor,
                        onClick = {
                            if (currentRoute != destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(Routes.DASHBOARD) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
                
                // Settings button
                DynamicNavItem(
                    icon = AppIcons.SettingsOutlined,
                    selectedIcon = AppIcons.Settings,
                    label = "Settings",
                    isSelected = currentRoute == Routes.SETTINGS,
                    accentColor = accentColor,
                    onClick = {
                        navController.navigate(Routes.SETTINGS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Get accent color based on current route for bottom bar styling
 */
@Composable
fun getAccentColorForRoute(route: String?): Color {
    return when (route) {
        Routes.DASHBOARD -> MaterialTheme.colorScheme.primary
        Routes.GOALS -> Color(0xFF6C63FF) // Purple
        Routes.CALENDAR -> Color(0xFF00D9FF) // Cyan
        Routes.NOTES -> Color(0xFFFF6B9D) // Pink
        Routes.TASKS -> Color(0xFF4ADE80) // Green
        Routes.SETTINGS -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun DynamicNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DynamicNavItem(
        icon = destination.unselectedIcon,
        selectedIcon = destination.selectedIcon,
        label = destination.label,
        isSelected = isSelected,
        accentColor = accentColor,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun DynamicNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(accentColor.copy(alpha = 0.15f), accentColor.copy(alpha = 0.1f))
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(if (isSelected) 36.dp else 32.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else icon,
                    contentDescription = label,
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Glassmorphism effect card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    val isSelected = currentRoute == destination.route
                    
                    NavItem(
                        destination = destination,
                        isSelected = isSelected,
                        onClick = {
                            if (currentRoute != destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(Routes.DASHBOARD) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
                
                // Settings button
                NavItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Settings",
                    isSelected = currentRoute == Routes.SETTINGS,
                    onClick = {
                        navController.navigate(Routes.SETTINGS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavItem(
        icon = destination.unselectedIcon,
        selectedIcon = destination.selectedIcon,
        label = destination.label,
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        colors = GradientColors.purpleBlue.map { it.copy(alpha = 0.15f) }
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(if (isSelected) 36.dp else 32.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else icon,
                    contentDescription = label,
                    tint = if (isSelected) 
                        MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}