package ke.eldohub.training.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ke.eldohub.training.R
import ke.eldohub.training.ui.accessibility.AccessibilityDialog
import ke.eldohub.training.ui.accessibility.AccessibilityViewModel
import ke.eldohub.training.ui.dashboard.DashboardScreen
import ke.eldohub.training.ui.list.ManualListScreen
import ke.eldohub.training.ui.splash.SplashScreen
import ke.eldohub.training.ui.viewer.PdfViewerScreen

private object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val MANUALS = "manuals"
    const val VIEWER = "viewer/{manualId}"
    fun viewer(manualId: String) = "viewer/$manualId"
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelRes: Int) {
    object Home : BottomNavItem(Routes.DASHBOARD, Icons.Default.Home, R.string.home)
    object Manuals : BottomNavItem(Routes.MANUALS, Icons.AutoMirrored.Filled.MenuBook, R.string.manuals)
    object Accessibility : BottomNavItem("accessibility_hub", Icons.Default.Accessibility, R.string.accessibility)
}

@Composable
fun AppNavigation(
    accessibilityViewModel: AccessibilityViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val accessibilityState by accessibilityViewModel.uiState.collectAsState()
    val isHighContrast = accessibilityState.highContrast

    var showAccessibilityDialog by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Manuals,
        BottomNavItem.Accessibility
    )

    val showBottomBar = currentDestination?.route in listOf(
        Routes.DASHBOARD,
        Routes.MANUALS
    )

    if (showAccessibilityDialog) {
        AccessibilityDialog(
            state = accessibilityState,
            onToggleHighContrast = { accessibilityViewModel.toggleHighContrast() },
            onSetSpeechRate = { accessibilityViewModel.setSpeechRate(it) },
            onToggleReducedMotion = { accessibilityViewModel.toggleReducedMotion() },
            onDismiss = { showAccessibilityDialog = false }
        )
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
                    contentColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface,
                    tonalElevation = if (isHighContrast) 0.dp else 4.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isAccessibility = item == BottomNavItem.Accessibility
                        val selected = if (isAccessibility) false else currentDestination?.hierarchy?.any { it.route == item.route } == true
                        val iconSize by animateDpAsState(if (selected) 26.dp else 22.dp, label = "iconSize")

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.labelRes),
                                    modifier = Modifier.size(iconSize)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.labelRes),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                                unselectedIconColor = if (isHighContrast) Color.Yellow.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                                unselectedTextColor = if (isHighContrast) Color.Yellow.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = if (isHighContrast) Color.Transparent else MaterialTheme.colorScheme.primaryContainer
                            ),
                            onClick = {
                                if (isAccessibility) {
                                    showAccessibilityDialog = true
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(250)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {

            composable(Routes.SPLASH) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToManuals = { navController.navigate(Routes.MANUALS) },
                    onNavigateToViewer = { manualId ->
                        navController.navigate(Routes.viewer(manualId))
                    }
                )
            }

            composable(Routes.MANUALS) {
                ManualListScreen(
                    onManualClick = { manualId ->
                        navController.navigate(Routes.viewer(manualId))
                    }
                )
            }

            composable(
                route = Routes.VIEWER,
                arguments = listOf(navArgument("manualId") { type = NavType.StringType })
            ) { backStackEntry ->
                val manualId = backStackEntry.arguments?.getString("manualId").orEmpty()
                PdfViewerScreen(
                    manualId = manualId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
