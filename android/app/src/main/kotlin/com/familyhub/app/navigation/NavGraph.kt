package com.familyhub.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.familyhub.feature.profile.ui.SignInScreen
import com.familyhub.feature.profile.ui.SignUpScreen
import com.familyhub.feature.tasks.ui.TaskListScreen

/**
 * NavGraph
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.START_DESTINATION,
        modifier = modifier
    ) {
        // Sign In Screen
        composable(NavRoute.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(NavRoute.SignUp.route)
                },
                onForgotPassword = {
                    navController.navigate(NavRoute.ForgotPassword.route)
                }
            )
        }

        // Sign Up Screen
        composable(NavRoute.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        // Forgot Password Screen (placeholder)
        composable(NavRoute.ForgotPassword.route) {
            // TODO: Implement ForgotPasswordScreen
        }

        // Home Screen (redirects to tasks for now)
        composable(NavRoute.Home.route) {
            // TODO: Implement proper home dashboard
            // For now, navigate to tasks
            TaskListScreen(
                familyId = "demo-family-123", // TODO: Get actual family ID from auth
                onNavigateToTaskDetail = { taskId ->
                    // TODO: Navigate to task detail
                },
                onNavigateToCreateTask = {
                    // TODO: Navigate to create task
                }
            )
        }

        // Family Selection Screen (placeholder)
        composable(NavRoute.FamilySelection.route) {
            // TODO: Implement FamilySelectionScreen
        }

        // Tasks Screen
        composable(NavRoute.Tasks.route) {
            TaskListScreen(
                familyId = "demo-family-123", // TODO: Get actual family ID from auth
                onNavigateToTaskDetail = { taskId ->
                    // TODO: Navigate to task detail
                },
                onNavigateToCreateTask = {
                    // TODO: Navigate to create task
                }
            )
        }

        // Chat Screen (placeholder)
        composable(NavRoute.Chat.route) {
            // TODO: Implement ChatScreen
        }

        // Calendar Screen (placeholder)
        composable(NavRoute.Calendar.route) {
            // TODO: Implement CalendarScreen
        }

        // Shopping List Screen (placeholder)
        composable(NavRoute.ShoppingList.route) {
            // TODO: Implement ShoppingListScreen
        }

        // Profile Screen (placeholder)
        composable(NavRoute.Profile.route) {
            // TODO: Implement ProfileScreen
        }
    }
}
