package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.home.CarDetailsScreen
import com.example.myapplication.ui.screens.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CarDetails : Screen("car/{carId}") {
        fun createRoute(carId: Long) = "car/$carId"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCarClick = { carId ->
                    navController.navigate(Screen.CarDetails.createRoute(carId))
                },
                onProfileClick = { /* Handle profile click */ },
                onFavoriteClick = { /* Handle favorites click */ },
                onNotificationClick = { /* Handle notification click */ },
                onFilterClick = { /* Handle filter click */ },
                onCatalogClick = { /* Handle catalog click */ }
            )
        }
        
        composable(
            route = Screen.CarDetails.route,
            arguments = listOf(
                navArgument("carId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getLong("carId") ?: 0L
            CarDetailsScreen(
                navController = navController,
                viewModel = hiltViewModel(
                    viewModelStoreOwner = remember {
                        object : ViewModelStoreOwner {
                            override val viewModelStore = ViewModelStore()
                        }
                    }
                )
            )
        }
    }
}
