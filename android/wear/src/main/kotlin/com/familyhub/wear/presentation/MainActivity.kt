package com.familyhub.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.familyhub.wear.presentation.screens.HomeScreen
import com.familyhub.wear.presentation.screens.TasksScreen
import com.familyhub.wear.presentation.screens.MessagesScreen
import com.familyhub.wear.presentation.screens.ShoppingListScreen
import com.familyhub.wear.presentation.screens.HealthScreen
import com.familyhub.wear.presentation.theme.FamilyHubWearTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 * Main entry point for Wear OS app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamilyHubWearApp()
        }
    }
}

@Composable
fun FamilyHubWearApp() {
    FamilyHubWearTheme {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToTasks = { navController.navigate("tasks") },
                    onNavigateToMessages = { navController.navigate("messages") },
                    onNavigateToShopping = { navController.navigate("shopping") },
                    onNavigateToHealth = { navController.navigate("health") }
                )
            }

            composable("tasks") {
                TasksScreen()
            }

            composable("messages") {
                MessagesScreen()
            }

            composable("shopping") {
                ShoppingListScreen()
            }

            composable("health") {
                HealthScreen()
            }
        }
    }
}
