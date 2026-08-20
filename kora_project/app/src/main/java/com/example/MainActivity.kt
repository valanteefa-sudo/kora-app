package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavDarkBackground
import com.example.ui.theme.PitchDarkCanvas
import com.example.ui.theme.PitchDarkSurface
import com.example.ui.theme.StadiumGreenPrimary
import com.example.ui.viewmodel.KooraViewModel

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavItem("home", "الرئيسية", Icons.Default.Home)
    object Pitch : NavItem("pitch", "التشكيل", Icons.Default.SportsSoccer)
    object LiveMatch : NavItem("live_match", "المباراة", Icons.Default.Sports)
    object PointsRules : NavItem("points_rules", "لوائح النقاط", Icons.Default.FormatListNumbered)
    object Stats : NavItem("stats", "إحصائيات", Icons.Default.EmojiEvents)
    object Admin : NavItem("admin", "الأدمن", Icons.Default.AdminPanelSettings)
    object Developer : NavItem("developer", "المصمم", Icons.Default.Badge)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                val navController = rememberNavController()
                val viewModel: KooraViewModel = viewModel()
                val items = listOf(
                    NavItem.Home,
                    NavItem.Pitch,
                    NavItem.LiveMatch,
                    NavItem.PointsRules,
                    NavItem.Stats,
                    NavItem.Admin,
                    NavItem.Developer
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavItem.Home.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PitchDarkCanvas,
                    bottomBar = {
                        NavigationBar(
                            containerColor = NavDarkBackground,
                            contentColor = GoldAccent,
                            tonalElevation = 8.dp
                        ) {
                            items.forEach { item ->
                                val isSelected = currentRoute == item.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.4f)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GoldAccent else Color.White.copy(alpha = 0.4f)
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = StadiumGreenPrimary
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavItem.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavItem.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToPitch = { navController.navigate(NavItem.Pitch.route) }
                            )
                        }
                        composable(NavItem.Pitch.route) {
                            PitchScreen(
                                viewModel = viewModel,
                                onNavigateToLiveMatch = { navController.navigate(NavItem.LiveMatch.route) }
                            )
                        }
                        composable(NavItem.LiveMatch.route) {
                            LiveMatchScreen(
                                viewModel = viewModel,
                                onMatchFinished = { navController.navigate(NavItem.Stats.route) }
                            )
                        }
                        composable(NavItem.PointsRules.route) {
                            PointsRulesScreen()
                        }
                        composable(NavItem.Stats.route) {
                            StatsScreen(viewModel = viewModel)
                        }
                        composable(NavItem.Admin.route) {
                            AdminScreen(viewModel = viewModel)
                        }
                        composable(NavItem.Developer.route) {
                            DeveloperScreen()
                        }
                    }
                }
            }
        }
    }
}
