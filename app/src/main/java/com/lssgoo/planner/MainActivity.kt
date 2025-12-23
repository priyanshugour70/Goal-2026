package com.lssgoo.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lssgoo.planner.features.calendar.screens.CalendarScreen
import com.lssgoo.planner.features.dashboard.screens.DashboardScreen
import com.lssgoo.planner.features.goals.screens.GoalDetailScreen
import com.lssgoo.planner.features.goals.screens.GoalsScreen
import com.lssgoo.planner.features.notes.screens.NotesScreen
import com.lssgoo.planner.features.onboarding.screens.OnboardingScreen
import com.lssgoo.planner.features.reminders.screens.RemindersScreen
import com.lssgoo.planner.features.settings.screens.SettingsScreen
import com.lssgoo.planner.features.tasks.screens.TasksScreen
import com.lssgoo.planner.features.habits.screens.HabitsScreen
import com.lssgoo.planner.features.search.screens.SearchScreen
import com.lssgoo.planner.features.analytics.screens.AnalyticsScreen
import com.lssgoo.planner.features.journal.screens.JournalScreen
import com.lssgoo.planner.features.finance.screens.FinanceScreen
import com.lssgoo.planner.ui.components.DynamicBottomNavBar
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.theme.PlannerTheme
import com.lssgoo.planner.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val rootViewModel: PlannerViewModel = viewModel()
            val settings by rootViewModel.settings.collectAsState()
            
            PlannerTheme(themeMode = settings.themeMode) {
                PlannerApp(rootViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerApp(rootViewModel: PlannerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnboardingComplete by rootViewModel.isOnboardingComplete.collectAsState()
    
    // Status bar theme sync
    val view = LocalView.current
    val statusBarColor = MaterialTheme.colorScheme.background
    val isDarkTheme = statusBarColor == Color.Black || statusBarColor.toArgb() == Color(0xFF1C1B1F).toArgb() // More robust check
    
    LaunchedEffect(currentRoute, statusBarColor, isDarkTheme) {
        val window = (view.context as android.app.Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        @Suppress("DEPRECATION")
        window.statusBarColor = statusBarColor.toArgb()
    }
    
    if (!isOnboardingComplete) {
        OnboardingScreen(viewModel = rootViewModel)
    } else {
        val showBottomBar = currentRoute in BottomNavDestination.entries.map { it.route }
        
        val haptic = LocalHapticFeedback.current
        val destinations = BottomNavDestination.entries

        Scaffold(
            bottomBar = { if (showBottomBar) DynamicBottomNavBar(navController, currentRoute) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0) // Fully controlled space
        ) { paddingValues ->
            var dragOffset by remember { mutableStateOf(0f) }
            
            NavHost(
                navController = navController,
                startDestination = Routes.DASHBOARD,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(currentRoute) {
                        if (!showBottomBar) return@pointerInput
                        
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragOffset > 150f) {
                                    // Swipe Right -> Go Left
                                    val currentIndex = destinations.indexOfFirst { it.route == currentRoute }
                                    if (currentIndex > 0) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        navController.navigate(destinations[currentIndex - 1].route) {
                                            popUpTo(Routes.DASHBOARD) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                } else if (dragOffset < -150f) {
                                    // Swipe Left -> Go Right
                                    val currentIndex = destinations.indexOfFirst { it.route == currentRoute }
                                    if (currentIndex != -1 && currentIndex < destinations.size - 1) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        navController.navigate(destinations[currentIndex + 1].route) {
                                            popUpTo(Routes.DASHBOARD) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                                dragOffset = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragOffset += dragAmount
                            }
                        )
                    }
            ) {
                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        viewModel = rootViewModel,
                        onGoalClick = { navController.navigate(Routes.goalDetail(it)) },
                        onViewAllGoals = { navController.navigate(Routes.GOALS) },
                        onViewAllTasks = { navController.navigate(Routes.TASKS) },
                        onViewAllHabits = { navController.navigate(Routes.HABITS) },
                        onViewAllJournal = { navController.navigate(Routes.JOURNAL) },
                        onViewAllNotes = { navController.navigate(Routes.NOTES) },
                        onSearchClick = { navController.navigate(Routes.SEARCH) }
                    )
                }
                
                composable(Routes.GOALS) {
                    val vm: GoalsViewModel = viewModel()
                    GoalsScreen(viewModel = vm, onGoalClick = { navController.navigate(Routes.goalDetail(it)) })
                }
                
                composable(Routes.GOAL_DETAIL, arguments = listOf(navArgument("goalId") { type = NavType.StringType })) { bse ->
                    val vm: GoalsViewModel = viewModel()
                    GoalDetailScreen(goalId = bse.arguments?.getString("goalId") ?: "", viewModel = vm, onBack = { navController.popBackStack() })
                }
                
                composable(Routes.TASKS) {
                    val vm: TasksViewModel = viewModel()
                    val gvm: GoalsViewModel = viewModel()
                    TasksScreen(viewModel = vm, goalsViewModel = gvm)
                }
                
                composable(Routes.NOTES) {
                    NotesScreen(viewModel = rootViewModel)
                }
                
                composable(Routes.FINANCE) {
                    FinanceScreen(viewModel = rootViewModel)
                }
                
                composable(Routes.CALENDAR) {
                    CalendarScreen(viewModel = rootViewModel)
                }

                composable(Routes.SEARCH) {
                    SearchScreen(viewModel = rootViewModel, onBack = { navController.popBackStack() }, onResultClick = { /* Handle */ })
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(viewModel = rootViewModel, onBack = { navController.popBackStack() })
                }
                
                composable(Routes.HABITS) {
                    HabitsScreen(viewModel = rootViewModel, onHabitClick = { /* Edit Logic */ })
                }
                
                composable(Routes.JOURNAL) {
                    JournalScreen(viewModel = rootViewModel, onEntryClick = { /* Detail Logic */ })
                }
                
                composable(Routes.ANALYTICS) {
                    AnalyticsScreen(viewModel = rootViewModel)
                }
            }
        }
    }
}