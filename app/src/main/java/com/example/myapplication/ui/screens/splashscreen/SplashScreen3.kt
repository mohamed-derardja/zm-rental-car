package com.example.myapplication.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SplashScreen3(
    onNavigateToNextScreen: () -> Unit = {}
) {
    var currentStage by remember { mutableStateOf(0) }

    // Enhanced transition with more visible effects
    val transition = updateTransition(targetState = currentStage, label = "ballDrop")

    // Vertical position animation
    val yOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = when (targetState) {
                    0 -> 0        // Initial position
                    1 -> 800      // Falling from top
                    2 -> 300      // First bounce up
                    3 -> 200      // Second smaller bounce
                    else -> 100
                },
                easing = when (targetState) {
                    1 -> FastOutSlowInEasing  // More dramatic fall
                    else -> LinearOutSlowInEasing
                }
            )
        },
        label = "yOffset"
    ) { state ->
        when (state) {
            0 -> -800f   // Start offscreen
            1 -> 200f    // Fall with overshoot
            2 -> -100f   // First bounce
            3 -> 0f      // Final position
            else -> 0f
        }
    }

    // Scale animation to simulate ball squish
    val scale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = when (targetState) {
                    1 -> 800
                    2 -> 300
                    3 -> 200
                    else -> 100
                }
            )
        },
        label = "scale"
    ) { state ->
        when (state) {
            0 -> 1.0f
            1 -> 0.8f    // Squish on impact
            2 -> 1.2f    // Stretch when bouncing
            3 -> 1.0f    // Return to normal
            else -> 1.0f
        }
    }

    LaunchedEffect(Unit) {
        // Stage 0 is initial setup
        delay(300)
        currentStage = 1  // Start falling
        delay(800)
        currentStage = 2  // First bounce
        delay(300)
        currentStage = 3  // Settle
        delay(1000)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Ball with enhanced animation
        Image(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Bouncing Ball",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationY = yOffset
                    scaleX = if (currentStage == 1) 1.2f else scale
                    scaleY = if (currentStage == 1) 0.8f else scale
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen3Preview() {
    SplashScreen3()
}
