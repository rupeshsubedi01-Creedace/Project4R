package com.project4r.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.project4r.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: String) {
    object Route    : Screen("route",    "Route",    "\u2708")  // ✈
    object Currency : Screen("currency", "Currency", "\uD83D\uDCB1") // 💱
    object Remind   : Screen("remind",   "Remind",   "\uD83D\uDD14") // 🔔
    object Review   : Screen("review",   "Review",   "\uD83D\uDCCA") // 📊
    object Reset    : Screen("reset",    "Reset",    "\uD83E\uDDD8") // 🧘
}

val screens = listOf(
    Screen.Route, Screen.Currency, Screen.Remind, Screen.Review, Screen.Reset
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    var currentTab by remember { mutableStateOf(Screen.Route.route) }

    Column(Modifier.fillMaxSize()) {
        // Tab row
        ScrollableTabRow(
            selectedTabIndex = screens.indexOfFirst { it.route == currentTab }.coerceAtLeast(0)
        ) {
            screens.forEach { screen ->
                Tab(
                    selected  = currentTab == screen.route,
                    onClick   = { currentTab = screen.route },
                    text      = { Text("${screen.icon} ${screen.label}") }
                )
            }
        }

        // Screen content
        Box(Modifier.fillMaxSize()) {
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
