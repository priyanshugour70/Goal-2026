package com.lssgoo.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.screens.calendar.CalendarScreen
import com.lssgoo.planner.ui.screens.dashboard.DashboardScreen
import com.lssgoo.planner.ui.screens.goals.GoalDetailScreen
import com.lssgoo.planner.ui.screens.goals.GoalsScreen
import com.lssgoo.planner.ui.screens.notes.NotesScreen
import com.lssgoo.planner.ui.screens.onboarding.OnboardingScreen
import com.lssgoo.planner.ui.screens.reminders.RemindersScreen
import com.lssgoo.planner.ui.screens.settings.SettingsScreen
import com.lssgoo.planner.ui.screens.tasks.TasksScreen
import com.lssgoo.planner.ui.screens.habits.HabitsScreen
import com.lssgoo.planner.ui.screens.search.SearchScreen
import com.lssgoo.planner.ui.screens.analytics.AnalyticsScreen
import com.lssgoo.planner.ui.screens.journal.JournalScreen
import com.lssgoo.planner.ui.screens.finance.FinanceScreen
import com.lssgoo.planner.ui.theme.PlannerTheme
import com.lssgoo.planner.ui.theme.GradientColors
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PlannerViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()
            
            PlannerTheme(themeMode = settings.themeMode) {
                PlannerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerApp() {
    val navController = rememberNavController()
    val viewModel: PlannerViewModel = viewModel()
    
    // Initialize auto-sync
    LaunchedEffect(Unit) {
        viewModel.initializeAutoSync()
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Get onboarding status
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    
    // Dynamic status bar color - Black for dark, White for light
    val view = LocalView.current
    val statusBarColor = MaterialTheme.colorScheme.background
    val isDarkTheme = MaterialTheme.colorScheme.background == Color.Black
    
    LaunchedEffect(currentRoute, statusBarColor, isDarkTheme) {
        if (!view.isInEditMode) {
            val window = (view.context as android.app.Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Light icons for dark theme (black bg), dark icons for light theme (white bg)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            // Set status bar color to match background
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
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            var totalDragX by remember { mutableFloatStateOf(0f) }
            val swipeThreshold = 100f // Threshold for swipe detection
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .pointerInput(currentRoute) {
                        // Only enable swipe for bottom nav destinations
                        val isBottomDest = currentRoute in BottomNavDestination.entries.map { it.route }
                        if (!isBottomDest) return@pointerInput
                        
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (abs(totalDragX) > swipeThreshold) {
                                    val destinations = BottomNavDestination.entries
                                    val currentIndex = destinations.indexOfFirst { it.route == currentRoute }
                                    
                                    if (currentIndex != -1) {
                                        if (totalDragX < 0) { // Swipe Left -> Go Right
                                            if (currentIndex < destinations.size - 1) {
                                                navController.navigate(destinations[currentIndex + 1].route) {
                                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        } else { // Swipe Right -> Go Left
                                            if (currentIndex > 0) {
                                                navController.navigate(destinations[currentIndex - 1].route) {
                                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    }
                                }
                                totalDragX = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount
                            }
                        )
                    }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.DASHBOARD,
                    modifier = Modifier.fillMaxSize()
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
                        },
                        onViewAllHabits = {
                            navController.navigate(Routes.HABITS)
                        },
                        onViewAllJournal = {
                            navController.navigate(Routes.JOURNAL)
                        },
                        onViewAllNotes = {
                            navController.navigate(Routes.NOTES)
                        },
                        onSearchClick = {
                            navController.navigate(Routes.SEARCH)
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
                
                // Habits
                composable(Routes.HABITS) {
                    HabitsScreen(
                        viewModel = viewModel,
                        onHabitClick = { habitId ->
                            navController.navigate(Routes.habitDetail(habitId))
                        }
                    )
                }
                
                // Search
                composable(Routes.SEARCH) {
                    SearchScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onResultClick = { result ->
                            when (result.type) {
                                com.lssgoo.planner.data.model.SearchResultType.GOAL -> navController.navigate(Routes.goalDetail(result.id))
                                com.lssgoo.planner.data.model.SearchResultType.TASK -> navController.navigate(Routes.TASKS)
                                com.lssgoo.planner.data.model.SearchResultType.NOTE -> navController.navigate(Routes.noteDetail(result.id))
                                com.lssgoo.planner.data.model.SearchResultType.EVENT -> navController.navigate(Routes.CALENDAR)
                                com.lssgoo.planner.data.model.SearchResultType.REMINDER -> navController.navigate(Routes.REMINDERS)
                                com.lssgoo.planner.data.model.SearchResultType.MILESTONE -> navController.navigate(Routes.goalDetail(result.linkedGoalId ?: ""))
                                com.lssgoo.planner.data.model.SearchResultType.HABIT -> navController.navigate(Routes.HABITS)
                                com.lssgoo.planner.data.model.SearchResultType.JOURNAL -> navController.navigate(Routes.JOURNAL)
                                com.lssgoo.planner.data.model.SearchResultType.FINANCE -> navController.navigate(Routes.FINANCE)
                            }
                        }
                    )
                }
                
                // Analytics
                composable(Routes.ANALYTICS) {
                    AnalyticsScreen(viewModel = viewModel)
                }
                
                // Journal
                composable(Routes.JOURNAL) {
                    JournalScreen(
                        viewModel = viewModel,
                        onEntryClick = { entryId ->
                            navController.navigate(Routes.journalEntry(entryId))
                        }
                    )
                }
                
                // Finance
                composable(Routes.FINANCE) {
                    FinanceScreen(viewModel = viewModel)
                }
            }
        }
    }
    }
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
            val scrollState = rememberScrollState()
            
            // Observe scroll state changes
            val canScrollLeft by remember { derivedStateOf { scrollState.value > 0 } }
            val canScrollRight by remember { derivedStateOf { scrollState.canScrollForward } }
            
            // Capture surface color outside of drawWithContent (composable context)
            val surfaceColor = MaterialTheme.colorScheme.surface
            
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .drawWithContent {
                            drawContent()
                            // Left fade gradient to indicate scrollable content
                            if (canScrollLeft) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            surfaceColor.copy(alpha = 0.95f),
                                            surfaceColor.copy(alpha = 0f)
                                        ),
                                        startX = 0f,
                                        endX = 40.dp.toPx()
                                    )
                                )
                            }
                            // Right fade gradient to indicate scrollable content
                            if (canScrollRight) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            surfaceColor.copy(alpha = 0f),
                                            surfaceColor.copy(alpha = 0.95f)
                                        ),
                                        startX = size.width - 40.dp.toPx(),
                                        endX = size.width
                                    )
                                )
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    DynamicNavItemBase(
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
                
                // Scroll indicators (chevrons) on edges - subtle visual cue
                if (canScrollLeft) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Scroll left",
                        tint = accentColor.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(18.dp)
                    )
                }
                
                if (canScrollRight) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Scroll right",
                        tint = accentColor.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Get accent color based on current route for bottom bar styling - Light Blue variants
 */
@Composable
fun getAccentColorForRoute(route: String?): Color {
    return when (route) {
        Routes.DASHBOARD -> MaterialTheme.colorScheme.primary  // Light Blue
        Routes.GOALS -> Color(0xFF0097A7)  // Teal Blue
        Routes.CALENDAR -> Color(0xFF00ACC1)  // Cyan Blue
        Routes.NOTES -> Color(0xFF4DD0E1)  // Light Cyan
        Routes.TASKS -> Color(0xFF26C6DA)  // Light Blue Cyan
        Routes.HABITS -> Color(0xFF4DD0E1)  // Light Cyan
        Routes.JOURNAL -> Color(0xFF0097A7)  // Teal Blue
        Routes.FINANCE -> Color(0xFF26C6DA)  // Light Blue Cyan
        Routes.SEARCH -> Color(0xFF00ACC1)  // Cyan Blue
        Routes.ANALYTICS -> Color(0xFF0288D1)  // Blue
        Routes.SETTINGS -> Color(0xFF0288D1)  // Blue
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
    DynamicNavItemBase(
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
fun DynamicNavItemBase(
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
                NavItemBase(
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
    NavItemBase(
        icon = destination.unselectedIcon,
        selectedIcon = destination.selectedIcon,
        label = destination.label,
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun NavItemBase(
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