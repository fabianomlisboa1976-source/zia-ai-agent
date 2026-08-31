package com.zia.agent.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zia.agent.ZiaApplication
import com.zia.agent.data.repository.SettingsRepository
import com.zia.agent.ui.screens.ChatScreen
import com.zia.agent.ui.screens.PluginsScreen
import com.zia.agent.ui.screens.ProviderSelectorScreen
import com.zia.agent.ui.screens.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Chat : Screen("chat", "Chat", Icons.Filled.Chat)
    data object Providers : Screen("providers", "Modelos", Icons.Filled.SwapHoriz)
    data object Plugins : Screen("plugins", "Plugins", Icons.Filled.Extension)
    data object Settings : Screen("settings", "Config", Icons.Filled.Settings)
}

private val screens = listOf(Screen.Chat, Screen.Providers, Screen.Plugins, Screen.Settings)

@Composable
fun ZiaApp(
    application: ZiaApplication,
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().route!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Chat.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Chat.route) {
                ChatScreen(
                    application = application,
                    settingsRepository = settingsRepository
                )
            }
            composable(Screen.Providers.route) {
                ProviderSelectorScreen(
                    application = application,
                    settingsRepository = settingsRepository
                )
            }
            composable(Screen.Plugins.route) {
                PluginsScreen(
                    settingsRepository = settingsRepository
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsRepository = settingsRepository
                )
            }
        }
    }
}
