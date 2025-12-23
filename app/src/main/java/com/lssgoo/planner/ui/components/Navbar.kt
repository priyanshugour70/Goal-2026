package com.lssgoo.planner.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.theme.RouteColors

/**
 * Dynamic Bottom Navigation Bar with iOS-like polish
 * Optimized for reuse and dynamic theming
 */
@Composable
fun DynamicBottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val accentColor = getAccentColorForRoute(currentRoute)
    val scrollState = rememberScrollState()
    
    val destinations = BottomNavDestination.entries
    val currentIndex = remember(currentRoute) {
        destinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    // Scroll to active item when it changes
    LaunchedEffect(currentIndex) {
        scrollState.animateScrollTo(currentIndex * 70)
    }
    
    val canScrollLeft by remember { derivedStateOf { scrollState.value > 0 } }
    val canScrollRight by remember { derivedStateOf { scrollState.canScrollForward } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp) // Lifted bottom padding
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = accentColor.copy(alpha = 0.2f),
                    spotColor = accentColor.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Chevron
                AnimatedVisibility(
                    visible = canScrollLeft,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    navController.navigate(destinations[currentIndex - 1].route) {
                                        popUpTo(Routes.DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                null,
                                tint = accentColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Scrollable Destinations
                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        destinations.forEach { destination ->
                            DynamicNavItem(
                                destination = destination,
                                isSelected = currentRoute == destination.route,
                                accentColor = accentColor,
                                onClick = {
                                    if (currentRoute != destination.route) {
                                        navController.navigate(destination.route) {
                                            popUpTo(Routes.DASHBOARD) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Right Chevron
                AnimatedVisibility(
                    visible = canScrollRight,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = {
                                if (currentIndex < destinations.size - 1) {
                                    navController.navigate(destinations[currentIndex + 1].route) {
                                        popUpTo(Routes.DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ChevronRight,
                                null,
                                tint = accentColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.12f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = destination.label,
                tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(if (isSelected) 22.dp else 20.dp)
            )
            
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = destination.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun getAccentColorForRoute(route: String?): Color {
    return when (route) {
        Routes.DASHBOARD -> MaterialTheme.colorScheme.primary
        Routes.GOALS -> RouteColors.goals
        Routes.CALENDAR -> RouteColors.calendar
        Routes.NOTES -> RouteColors.notes
        Routes.TASKS -> RouteColors.tasks
        Routes.HABITS -> RouteColors.habits
        Routes.JOURNAL -> RouteColors.journal
        Routes.FINANCE -> RouteColors.finance
        Routes.SEARCH -> RouteColors.search
        Routes.ANALYTICS -> RouteColors.analytics
        Routes.SETTINGS -> RouteColors.settings
        else -> MaterialTheme.colorScheme.primary
    }
}
