package com.lssgoo.planner.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import com.lssgoo.planner.features.dashboard.screens.DashboardScreen
import com.lssgoo.planner.features.goals.screens.GoalsScreen
import com.lssgoo.planner.features.goals.screens.GoalDetailScreen
import com.lssgoo.planner.features.tasks.screens.TasksScreen
import com.lssgoo.planner.features.habits.screens.HabitsScreen
import com.lssgoo.planner.features.journal.screens.JournalScreen
import com.lssgoo.planner.features.notes.screens.NotesScreen
import com.lssgoo.planner.features.notes.screens.NoteDetailScreen
import com.lssgoo.planner.features.calendar.screens.CalendarScreen
import com.lssgoo.planner.features.settings.screens.SettingsScreen
import com.lssgoo.planner.features.search.screens.SearchScreen
import com.lssgoo.planner.features.reminders.screens.RemindersScreen
import com.lssgoo.planner.features.finance.screens.FinanceScreen
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.fillMaxSize
import com.lssgoo.planner.features.habits.screens.HabitDetailScreen
import com.lssgoo.planner.features.journal.screens.JournalEntryScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: PlannerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val bottomDestinations = BottomNavDestination.entries
    val pagerState = rememberPagerState(pageCount = { bottomDestinations.size })

    // Sync Navigation -> Pager
    LaunchedEffect(currentRoute) {
        val index = bottomDestinations.indexOfFirst { it.route == currentRoute }
        if (index != -1 && pagerState.currentPage != index) {
            pagerState.animateScrollToPage(index)
        }
    }

    // Sync Pager -> Navigation (Only when settled to avoid jumpy nav)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            val targetRoute = bottomDestinations[pagerState.currentPage].route
            if (currentRoute != targetRoute && shouldShowBottomBar(currentRoute)) {
                navController.navigate(targetRoute) {
                    popUpTo(Routes.DASHBOARD) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                com.lssgoo.planner.ui.components.DynamicBottomNavBar(navController, currentRoute)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Disable automatic inset padding to prevent double spacing
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()) // Only apply bottom padding for the nav bar
        ) {
            // Top Level Destinations - all show the same Pager
            bottomDestinations.forEach { destination ->
                composable(destination.route) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        beyondViewportPageCount = 1
                    ) { page ->
                        when (bottomDestinations[page]) {
                            BottomNavDestination.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onGoalClick = { goalId -> navController.navigate(Routes.goalDetail(goalId)) },
                                onViewAllGoals = { navController.navigate(Routes.GOALS) },
                                onViewAllTasks = { navController.navigate(Routes.TASKS) },
                                onViewAllHabits = { navController.navigate(Routes.HABITS) },
                                onViewAllFinance = { navController.navigate(Routes.FINANCE) },
                                onViewAllJournal = { navController.navigate(Routes.JOURNAL) },
                                onViewAllNotes = { navController.navigate(Routes.NOTES) },
                                onSearchClick = { navController.navigate(Routes.SEARCH) }
                            )
                            BottomNavDestination.GOALS -> GoalsScreen(
                                viewModel = viewModel,
                                onGoalClick = { goalId -> navController.navigate(Routes.goalDetail(goalId)) }
                            )
                            BottomNavDestination.TASKS -> TasksScreen(viewModel = viewModel)
                            BottomNavDestination.FINANCE -> FinanceScreen(viewModel = viewModel)
                            BottomNavDestination.HABITS -> HabitsScreen(
                                viewModel = viewModel,
                                onHabitClick = { habitId -> navController.navigate(Routes.habitDetail(habitId)) }
                            )
                            BottomNavDestination.JOURNAL -> JournalScreen(
                                viewModel = viewModel,
                                onEntryClick = { entryId -> navController.navigate(Routes.journalEntry(entryId)) }
                            )
                            BottomNavDestination.NOTES -> NotesScreen(viewModel = viewModel)
                            BottomNavDestination.CALENDAR -> CalendarScreen(viewModel = viewModel)
                            BottomNavDestination.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onNavigateToPin = { navController.navigate(Routes.APPLOCK) },
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                    }
                }
            }
            
            composable(Routes.GOAL_DETAIL) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                GoalDetailScreen(
                    goalId = goalId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Routes.NOTE_DETAIL) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                NoteDetailScreen(
                    noteId = noteId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Routes.NOTE_CREATE) {
                NotesScreen(viewModel = viewModel)
            }
            
            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onResultClick = { route -> navController.navigate(route) }
                )
            }
            
            composable(Routes.REMINDERS) {
                RemindersScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.HABIT_DETAIL) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
                HabitDetailScreen(
                    habitId = habitId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.JOURNAL_ENTRY) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                JournalEntryScreen(
                    entryId = entryId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Settings Pages
            composable(Routes.VERSION_HISTORY) {
                com.lssgoo.planner.features.settings.screens.VersionHistoryScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.ABOUT_DEVELOPER) {
                com.lssgoo.planner.features.settings.screens.AboutDeveloperScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.PRIVACY_POLICY) {
                com.lssgoo.planner.features.settings.screens.PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.TERMS_OF_SERVICE) {
                com.lssgoo.planner.features.settings.screens.TermsOfServiceScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.DATA_USAGE) {
                com.lssgoo.planner.features.settings.screens.DataUsageScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.APPLOCK) {
                com.lssgoo.planner.features.settings.screens.AppLockScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }    // In real app, this would be PinSetupScreen
        }
    }
}


fun shouldShowBottomBar(route: String?): Boolean {
    return route in BottomNavDestination.entries.map { it.route }
}
