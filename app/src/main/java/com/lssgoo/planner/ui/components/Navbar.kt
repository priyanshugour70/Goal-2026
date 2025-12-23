package com.lssgoo.planner.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
        // Approximate width for item (width varies due to expanded text)
        // Scroll to the current item's approximate position
        scrollState.animateScrollTo(currentIndex * 70)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp) // Reduced padding
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
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Button
                IconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            val prev = destinations[currentIndex - 1]
                            navController.navigate(prev.route) {
                                popUpTo(Routes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = currentIndex > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = if (currentIndex > 0) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }

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

                // Right Button
                IconButton(
                    onClick = {
                        if (currentIndex < destinations.size - 1) {
                            val next = destinations[currentIndex + 1]
                            navController.navigate(next.route) {
                                popUpTo(Routes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = currentIndex < destinations.size - 1
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = if (currentIndex < destinations.size - 1) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
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
            .background(
                if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onClick, modifier = Modifier.size(if (isSelected) 36.dp else 32.dp)) {
                Icon(
                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                    contentDescription = destination.label,
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                )
            }
            
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = destination.label,
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
