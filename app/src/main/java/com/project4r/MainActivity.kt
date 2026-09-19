package com.project4r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.project4r.data.UserPreferences
import com.project4r.ui.navigation.AppNavGraph
import com.project4r.ui.screens.OnboardingScreen
import com.project4r.ui.screens.SplashScreen
import com.project4r.ui.theme.Project4RTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project4RTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash      by remember { mutableStateOf(true) }
                    var showOnboarding  by remember { mutableStateOf(false) }

                    when {
                        showSplash -> SplashScreen(
                            onFinished = {
                                showSplash = false
                                // Show onboarding only on first install
                                showOnboarding = !userPreferences.onboardingDone
                            }
                        )
                        showOnboarding -> OnboardingScreen(
                            prefs      = userPreferences,
                            onFinished = { showOnboarding = false }
                        )
                        else -> {
                            val navController = rememberNavController()
                            AppNavGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
