package com.familyhub.app.navigation

/**
 * NavRoute
 * Sealed class defining all navigation routes in the app
 */
sealed class NavRoute(val route: String) {
    // Auth Routes
    object SignIn : NavRoute("sign_in")
    object SignUp : NavRoute("sign_up")
    object ForgotPassword : NavRoute("forgot_password")

    // Main Routes
    object Home : NavRoute("home")
    object FamilySelection : NavRoute("family_selection")

    // Feature Routes
    object Tasks : NavRoute("tasks")
    object Chat : NavRoute("chat")
    object Calendar : NavRoute("calendar")
    object ShoppingList : NavRoute("shopping_list")
    object Profile : NavRoute("profile")

    companion object {
        const val START_DESTINATION = "sign_in"
    }
}
