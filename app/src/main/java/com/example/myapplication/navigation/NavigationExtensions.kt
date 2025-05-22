package com.example.myapplication.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Extension function to navigate to a destination and clear the back stack.
 * This is useful for scenarios like logging in, where you want to prevent
 * the user from going back to the login screen with the back button.
 */
fun NavController.navigateAndClear(route: String) {
    Log.d("Navigation", "Navigating to $route and clearing back stack")
    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        popUpTo(graph.findStartDestination().id) {
            // Set inclusive to true to remove the start destination from the back stack as well
            inclusive = true
            saveState = false
        }
        // Restore state when reselecting a previously selected item
        restoreState = false
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
    }
    Log.d("Navigation", "Navigation completed to $route")
} 