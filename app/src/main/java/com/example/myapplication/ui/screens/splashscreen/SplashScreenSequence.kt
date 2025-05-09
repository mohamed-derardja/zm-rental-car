package com.example.myapplication.ui.screens.splashscreen

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.screens.splash.SplashScreen
import com.example.myapplication.screens.splash.SplashScreen2
import com.example.myapplication.screens.splash.SplashScreen3
import com.example.myapplication.screens.splash.SplashScreen4
import com.example.myapplication.screens.splash.SplashScreen5
import com.example.myapplication.screens.splash.SplashScreen6
import com.example.myapplication.screens.splash.SplashScreen7
import com.example.myapplication.ui.screens.welcome.WelcomeScreen

@Composable
fun SplashScreenSequence(
    onNavigateToWelcome: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(1) }

    val navigateToNextScreen: () -> Unit = {
        currentScreen = when (currentScreen) {
            1 -> 2
            2 -> 3
            3 -> 4
            4 -> 5
            5 -> 6
            6 -> 7
            7 -> 8
            else -> 8
        }
    }

    when (currentScreen) {
        1 -> SplashScreen(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 900
        )
        2 -> SplashScreen2(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 2000
        )
        3 -> SplashScreen3(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 2000
        )
        4 -> SplashScreen4(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 1700
        )
        5 -> SplashScreen5(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 2000
        )
        6 -> SplashScreen6(
            
            duration = 2000
        )
        7 -> SplashScreen7(
            onNavigateToNextScreen = navigateToNextScreen,
            duration = 1400
        )
        8 -> onNavigateToWelcome()
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenSequencePreview() {
    SplashScreenSequence()
}
