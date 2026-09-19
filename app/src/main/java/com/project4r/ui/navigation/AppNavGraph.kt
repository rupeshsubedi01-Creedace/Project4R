package com.project4r.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.project4r.ui.screens.*
import com.project4r.ui.theme.*

sealed class Screen(val route: String, val label: String, val emoji: String) {
    object Route    : Screen("route",    "Route",    "\u2708\uFE0F")
    object Currency : Screen("currency", "Currency", "\uD83D\uDCB1")
    object Remind   : Screen("remind",   "Remind",   "\uD83D\uDD14")
    object Review   : Screen("review",   "Review",   "\uD83D\uDCCA")
    object Reset    : Screen("reset",    "Reset",    "\uD83E\uDDD8")
}

val screens = listOf(
    Screen.Route, Screen.Currency, Screen.Remind, Screen.Review, Screen.Reset
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    var currentTab by remember { mutableStateOf(Screen.Route.route) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier
            ) {
                screens.forEach { screen ->
                    val selected = currentTab == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick  = { currentTab = screen.route },
                        icon = {
                            Text(
                                screen.emoji,
                                fontSize = if (selected) 22.sp else 18.sp
                            )
                        },
                        label = {
                            Text(
                                screen.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor   = Green600,
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor      = GreenCard
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (currentTab) {
                Screen.Route.route    -> RouteScreen()
                Screen.Currency.route -> CurrencyScreen()
                Screen.Remind.route   -> RemindScreen()
                Screen.Review.route   -> ReviewScreen()
                Screen.Reset.route    -> ResetScreen()
            }
        }
    }
}
