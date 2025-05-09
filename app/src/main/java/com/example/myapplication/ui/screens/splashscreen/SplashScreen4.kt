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
internal fun SplashScreen4(
    onNavigateToNextScreen: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Controlled bounce animation with keyframes
    val bounceTransition = rememberInfiniteTransition(label = "bounce")

    val bounceOffset by bounceTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 800
                0f at 0
                -60f at 300 with FastOutLinearInEasing
                0f at 500 with LinearOutSlowInEasing
                -20f at 650 with FastOutLinearInEasing
                0f at 800 with LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bounceOffset"
    )

    val scaleYAnim by bounceTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 800
                1f at 0
                1.1f at 300
                0.8f at 500
                1f at 800
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scaleY"
    )

    val scaleXAnim by bounceTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 800
                1f at 0
                0.9f at 300
                1.2f at 500
                1f at 800
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scaleX"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Bouncing Ball",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationY = bounceOffset
                    scaleX = scaleXAnim
                    scaleY = scaleYAnim
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen4Preview() {
    SplashScreen4()
}
