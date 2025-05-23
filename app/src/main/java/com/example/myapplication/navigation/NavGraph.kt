package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import android.util.Log
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.auth.CreateAccountScreen
import com.example.myapplication.ui.screens.auth.NewPasswordScreen
import com.example.myapplication.ui.screens.auth.SignInScreen
import com.example.myapplication.ui.screens.home.CarDetailsScreen
import com.example.myapplication.ui.screens.home.Filter
import com.example.myapplication.ui.screens.home.GalleryScreen
import com.example.myapplication.ui.screens.home.HomeScreen
import com.example.myapplication.ui.screens.home.NotificationScreen
import com.example.myapplication.ui.screens.home.bookings.CompletedBookingsScreen
import com.example.myapplication.ui.screens.home.bookings.MyBookingsScreen
import com.example.myapplication.ui.screens.payment.BillScreen
import com.example.myapplication.ui.screens.payment.CancelationScreen
import com.example.myapplication.ui.screens.payment.EdahabiaScreen
import com.example.myapplication.ui.screens.payment.FavoriteScreen
import com.example.myapplication.ui.screens.payment.PaymentDoneScreen
import com.example.myapplication.ui.screens.payment.PaymentMethodScreen
import com.example.myapplication.ui.screens.payment.PaymentPending
import com.example.myapplication.ui.screens.payment.UnsuccessfulPaymentScreen
import com.example.myapplication.ui.screens.profile.HelpCenterScreen
import com.example.myapplication.ui.screens.profile.NotificationSettingsScreen
import com.example.myapplication.ui.screens.profile.PrivacyPolicyScreen
import com.example.myapplication.ui.screens.profile.ProfileScreen
import com.example.myapplication.ui.screens.profile.UpdateProfileLocationScreen
import com.example.myapplication.ui.screens.profile.UpdateProfileScreen
import com.example.myapplication.ui.screens.profile.logoutScreen
import com.example.myapplication.ui.screens.settings.SettingsScreen
import com.example.myapplication.ui.screens.welcome.WelcomeScreen
import com.example.myapplication.ui.screens.welcome.SecondScreen
import com.example.myapplication.ui.screens.welcome.ThirdScreen
import com.example.myapplication.ui.screens.splashscreen.SplashScreenSequence
import com.example.myapplication.ui.screens.auth.CompleteProfileScreen
import com.example.myapplication.ui.screens.home.CarBookingScreen as HomeCarBookingScreen
import com.example.myapplication.ui.screens.payment.CompleteYourBookingScreen
import com.example.myapplication.ui.screens.password.ChangePasswordScreen
import com.example.myapplication.ui.screens.auth.OTPVerificationScreen
import com.example.myapplication.ui.screens.auth.ForgotPasswordScreen
import com.example.myapplication.ui.screens.auth.ResetPasswordScreen
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Enum class that contains all the possible screens in our app
 * This makes it easier to manage screen routes and prevents typos
 */
enum class Screen {
    // Splash Screen Sequence
    SplashSequence,

    // Onboarding Screens
    Welcome,
    Second,
    Third,
    
    // Authentication Screens
    SignIn,
    CreateAccount,
    ForgotPassword,
    ResetPassword,
    NewPassword,
    CompleteProfile,
    OTPVerification,
    
    // Main Screens
    Home,
    CarDetails,
    Filter,
    Notification,
    Profile,
    Gallery,
    
    // Booking Screens
    MyBooking,
    CompletedBooking,
    CompleteYourBooking,
    CarBooking,
    
    // Favorite Screen
    Favorite,
    
    // Settings Screens
    Settings,
    NotificationSettings,
    PasswordManager,
    
    // Payment Screens
    PaymentMethod,
    Edahabia,
    PaymentDone,
    PaymentPending,
    UnsuccessfulPayment,
    Bill,
    Cancelation,
    
    // Profile Screens
    HelpCenter,
    PrivacyPolicy,
    Logout,
    ProfileGeneral,
    ProfileLocation
}

/**
 * The main navigation graph of the application
 * This sets up all possible navigation paths between screens
 *
 * @param navController The navigation controller that handles the navigation
 * @param startDestination The screen to show when the app first launches
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.SplashSequence
) {
    // Add global navigation logging
    navController.addOnDestinationChangedListener { _, destination, _ ->
        Log.d("Navigation", "Navigated to: ${destination.route}")
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.name
    ) {
        // Splash Screen Sequence
        composable(Screen.SplashSequence.name) {
            SplashScreenSequence(
                onNavigateToWelcome = { navController.navigate(Screen.Welcome.name) }
            )
        }

        // Onboarding Screens
        composable(Screen.Welcome.name) {
            WelcomeScreen(
                onNextClick = { navController.navigate(Screen.Second.name) }
            )
        }

        composable(Screen.Second.name) {
            SecondScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate(Screen.Third.name) },
                onSkipClick = { navController.navigate(Screen.SignIn.name) }
            )
        }

        composable(Screen.Third.name) {
            ThirdScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate(Screen.SignIn.name) }
            )
        }

        // Authentication Screens
        composable(Screen.SignIn.name) {
            SignInScreen(
                onNavigateToRegister = { navController.navigate(Screen.CreateAccount.name) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.name) },
                onSignInSuccess = { navController.navigateAndClear(Screen.Home.name) }
            )
        }

        composable(Screen.CreateAccount.name) {
            CreateAccountScreen(
                onSignInClick = { navController.navigate(Screen.SignIn.name) },
                onCreateAccountSuccess = { 
                    // Add debug logging
                    Log.d("NavGraph", "Navigating to OTP verification after successful registration")
                    // Navigate to OTP verification and remove CreateAccount from back stack
                    navController.navigate("${Screen.OTPVerification.name}?fromForgotPassword=false") {
                        popUpTo(Screen.CreateAccount.name) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.name) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onResetSent = { 
                    // Navigate to reset password screen
                    navController.navigate("${Screen.ResetPassword.name}") {
                        popUpTo(Screen.ForgotPassword.name) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Screen.ResetPassword.name}?email={email}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            
            ResetPasswordScreen(
                email = email,
                onBack = { navController.popBackStack() },
                onResetSuccess = { 
                    // Navigate to sign in screen after successful password reset
                    navController.navigate(Screen.SignIn.name) {
                        popUpTo(Screen.ResetPassword.name) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NewPassword.name) {
            NewPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onPasswordResetSuccess = { navController.navigate(Screen.SignIn.name) }
            )
        }

        composable(Screen.CompleteProfile.name) {
            CompleteProfileScreen(
                onBackClick = { navController.popBackStack() },
                onProfileCompleted = { navController.navigateAndClear(Screen.Home.name) }
            )
        }

        composable(
            route = "${Screen.OTPVerification.name}?fromForgotPassword={fromForgotPassword}",
            arguments = listOf(
                navArgument("fromForgotPassword") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromForgotPassword = backStackEntry.arguments?.getBoolean("fromForgotPassword") ?: false
            
            OTPVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onVerifySuccess = { 
                    if (fromForgotPassword) {
                        navController.navigate(Screen.NewPassword.name)
                    } else {
                        navController.navigate(Screen.CompleteProfile.name)
                    }
                },
                fromForgotPassword = fromForgotPassword
            )
        }

        // Main Screens
        composable(Screen.Home.name) {
            HomeScreen(
                onCarClick = { carId -> 
                    // Navigate to car details
                    navController.navigate("${Screen.CarDetails.name}/$carId")
                },
                onProfileClick = { navController.navigate(Screen.Profile.name) },
                onFavoriteClick = { navController.navigate(Screen.Favorite.name) },
                onFilterClick = { navController.navigate(Screen.Filter.name) },
                onNotificationClick = { navController.navigate(Screen.Notification.name) },
                onCatalogClick = { navController.navigate(Screen.MyBooking.name) },
                onBookNowClick = { carId ->
                    // Navigate directly to booking screen
                    navController.navigate("${Screen.CarBooking.name}/$carId")
                }
            )
        }

        composable(
            route = "${Screen.CarDetails.name}/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            CarDetailsScreen(
                carId = carId,
                onBackPressed = { navController.popBackStack() },
                onGalleryClick = { navController.navigate(Screen.Gallery.name) },
                onBookNowClick = { navController.navigate(Screen.CarBooking.name) }
            )
        }

        composable(Screen.Filter.name) {
            val homeViewModel = hiltViewModel<com.example.myapplication.ui.screens.home.CarViewModel>()
            
            Filter(
                onBackClick = { navController.popBackStack() },
                onApplyFilters = { filterParams -> 
                    // Apply the filters in the view model
                    Log.d("NavGraph", "Applying filters: $filterParams")
                    
                    // Log details about each filter to help debug
                    if (filterParams.type != null) {
                        Log.d("NavGraph", "Type filter: ${filterParams.type}")
                    }
                    if (filterParams.brand != null) {
                        Log.d("NavGraph", "Brand filter: ${filterParams.brand}")
                    }
                    if (filterParams.minRating > 0) {
                        Log.d("NavGraph", "Rating filter: ${filterParams.minRating}")
                    }
                    Log.d("NavGraph", "Max price: ${filterParams.maxPrice}")
                    
                    // Handle different filter combinations
                    when {
                        // If we have only a type filter, use the specific type filter method
                        filterParams.type != null && filterParams.brand == null -> {
                            Log.d("NavGraph", "Using filterByType with type: '${filterParams.type}'")
                            homeViewModel.filterByType(filterParams.type)
                        }
                        
                        // If we have only a brand filter, use the specific brand filter method
                        filterParams.brand != null && filterParams.type == null -> {
                            Log.d("NavGraph", "Using filterByBrand with brand: '${filterParams.brand}'")
                            homeViewModel.filterByBrand(filterParams.brand)
                        }
                        
                        // If we have only a rating filter, use the specific rating filter method
                        filterParams.minRating > 0 && filterParams.type == null && filterParams.brand == null -> {
                            Log.d("NavGraph", "Using filterByRatingRange with rating: ${filterParams.minRating}")
                            homeViewModel.filterByRatingRange(
                                minRating = filterParams.minRating.toLong(),
                                maxRating = 5
                            )
                        }
                        
                        // For combinations of filters, use the combined filter method
                        else -> {
                            Log.d("NavGraph", "Using loadCarsWithFilters with multiple filters")
                            Log.d("NavGraph", "  - Type: '${filterParams.type}'")
                            Log.d("NavGraph", "  - Brand: '${filterParams.brand}'")
                            Log.d("NavGraph", "  - Rating: ${filterParams.minRating}")
                            
                            homeViewModel.loadCarsWithFilters(
                                brand = filterParams.brand,
                                model = filterParams.type,
                                minRating = if (filterParams.minRating > 0) filterParams.minRating.toLong() else null,
                                maxRating = 5L
                            )
                        }
                    }
                    
                    // Navigate back to home screen
                    navController.popBackStack()
                },
                onResetFilters = {
                    // Reset filters in view model
                    Log.d("NavGraph", "Resetting filters")
                    homeViewModel.loadAllCars()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Notification.name) {
            NotificationScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.name) {
            ProfileScreen(
                navController = navController,
                onHomeClick = { navController.navigateAndClear(Screen.Home.name) },
                onBookingsClick = { navController.navigate(Screen.MyBooking.name) },
                onFavoriteClick = { navController.navigate(Screen.Favorite.name) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Booking Screens
        composable(Screen.MyBooking.name) {
            MyBookingsScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigateAndClear(Screen.Home.name) },
                onFavoriteClick = { navController.navigate(Screen.Favorite.name) },
                onProfileClick = { navController.navigate(Screen.Profile.name) },
                onCompletedTabClick = { navController.navigate(Screen.CompletedBooking.name) }
            )
        }

        composable(Screen.CompletedBooking.name) {
            CompletedBookingsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigateAndClear(Screen.Home.name) },
                onMyBookingsClick = { navController.navigate(Screen.MyBooking.name) },
                onFavoriteClick = { navController.navigate(Screen.Favorite.name) },
                onProfileClick = { navController.navigate(Screen.Profile.name) },
                onUpcomingTabClick = { navController.navigate(Screen.MyBooking.name) },
                onRebookClick = { 
                    navController.navigate("${Screen.CarDetails.name}/rebook") 
                }
            )
        }

        composable(Screen.CarBooking.name) {
            // Create a mock car for now
            val mockCar = com.example.myapplication.data.model.Car(
                id = 1,
                brand = "Toyota",
                model = "Corolla",
                year = 2022,
                rentalPricePerDay = java.math.BigDecimal(55.0),
                transmission = "Automatic",
                rating = 4,
                colour = "White",
                fuel = "Petrol",
                type = "Sedan"
            )
            
            HomeCarBookingScreen(
                car = mockCar,
                onBackPressed = { navController.popBackStack() },
                onContinue = { navController.navigate("${Screen.CompleteYourBooking.name}/$mockCar.id") }
            )
        }

        composable(
            route = "${Screen.CompleteYourBooking.name}/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            CompleteYourBookingScreen(
                carId = carId,
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { bookingId ->
                    // Navigate to payment method after booking is complete
                    navController.navigate(Screen.PaymentMethod.name)
                }
            )
        }

        // Favorite Screen
        composable(Screen.Favorite.name) {
            FavoriteScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigateAndClear(Screen.Home.name) },
                onBookingsClick = { navController.navigate(Screen.MyBooking.name) },
                onProfileClick = { navController.navigate(Screen.Profile.name) },
                onCarClick = { carId ->
                    navController.navigate("car_details/$carId")
                }
            )
        }

        composable(Screen.Gallery.name) {
            GalleryScreen(
                onBackPressed = { navController.popBackStack() },
                onAboutClick = { navController.popBackStack() },
                onBookNowClick = { navController.navigate(Screen.CarBooking.name) }
            )
        }

        // Payment Screens
        composable(Screen.PaymentMethod.name) {
            PaymentMethodScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Screen.PaymentPending.name) },
                onEdahabiaClick = { navController.navigate(Screen.Edahabia.name) }
            )
        }

        composable(Screen.Edahabia.name) {
            EdahabiaScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Screen.Bill.name) }
            )
        }

        composable(Screen.PaymentDone.name) {
            PaymentDoneScreen(
                onBackToMainClick = { navController.navigateAndClear(Screen.Home.name) }
            )
        }

        composable(Screen.PaymentPending.name) {
            PaymentPending(
                onBackToMainClick = { navController.navigateAndClear(Screen.Home.name) }
            )
        }

        composable(Screen.UnsuccessfulPayment.name) {
            UnsuccessfulPaymentScreen(
                onBackClick = { navController.popBackStack() },
                onTryAgainClick = { navController.navigate(Screen.PaymentMethod.name) }
            )
        }

        composable(Screen.Bill.name) {
            BillScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { 
                    navController.navigate(Screen.PaymentDone.name)
                }
            )
        }

        composable(Screen.Cancelation.name) {
            CancelationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Profile Screens
        composable(Screen.Settings.name) {
            SettingsScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.NotificationSettings.name) {
            NotificationSettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.HelpCenter.name) {
            HelpCenterScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.PrivacyPolicy.name) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Logout.name) {
            logoutScreen(
                navController = navController,
                onHomeClick = { navController.navigateAndClear(Screen.Home.name) },
                onBookingsClick = { navController.navigate(Screen.MyBooking.name) },
                onFavoriteClick = { navController.navigate(Screen.Favorite.name) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ProfileGeneral.name) {
            UpdateProfileScreen(
                navController = navController
            )
        }

        composable(Screen.ProfileLocation.name) {
            UpdateProfileLocationScreen(
                navController = navController
            )
        }

        composable(Screen.PasswordManager.name) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onPasswordChangeSuccess = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Extension function to help with navigation and clearing the back stack
 * Use this when you want to navigate to a screen and remove all previous screens from history
 */
fun NavHostController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(0) {
            inclusive = true
        }
    }
} 