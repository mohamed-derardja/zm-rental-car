package com.example.myapplication.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import androidx.compose.animation.*
import androidx.compose.ui.draw.alpha

@Composable
fun SplashScreen6(
    onNavigateToNextScreen: () -> Unit = {}
) {
    var animationStage by remember { mutableStateOf(0) }

    // Enhanced transition with better timing
    val transition = updateTransition(targetState = animationStage, label = "transformToLogo")

    // Circle scale animation
    val circleScale by transition.animateFloat(
        transitionSpec = {
            when {
                targetState == 1 -> tween(700, easing = FastOutSlowInEasing)
                targetState == 2 -> tween(500, easing = FastOutLinearInEasing)
                else -> tween(300)
            }
        },
        label = "circleScale"
    ) { stage ->
        when (stage) {
            0 -> 2f    // Initial size
            1 -> 2.8f    // Medium growth
            2 -> 3.5f    // Large expansion before disappearing
            else -> 3.5f
        }
    }

    // Circle opacity
    val circleAlpha by transition.animateFloat(
        transitionSpec = {
            when {
                targetState == 2 -> tween(400)
                else -> tween(300)
            }
        },
        label = "circleAlpha"
    ) { stage ->
        when (stage) {
            0, 1 -> 1.0f  // Fully visible
            2 -> 0.0f     // Disappear
            else -> 0.0f
        }
    }

    // Logo animation with pulse effect
    val logoAlpha by transition.animateFloat(
        transitionSpec = { tween(600) },
        label = "logoAlpha"
    ) { stage ->
        when (stage) {
            0, 1 -> 0.0f  // Hidden
            2 -> 1.0f     // Visible
            else -> 1.0f
        }
    }

    val logoScale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "logoScale"
    ) { stage ->
        when (stage) {
            0, 1 -> 0.7f  // Initial small size
            2 -> 1.0f     // Final size with bounce effect
            else -> 1.0f
        }
    }

    // Optional pulse effect for logo
    val logoPulse = rememberInfiniteTransition()
    val logoExtraScale by logoPulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(300)
        animationStage = 1  // Start growing
        delay(1000)
        animationStage = 2  // Transform to logo
        delay(1500)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Circle that grows and fades
        Image(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Growing Circle",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = circleScale
                    scaleY = circleScale
                }
                .alpha(circleAlpha)
        )

        // Logo that appears with pulse effect
        Image(
            painter = painterResource(id = R.drawable.img_3),
            contentDescription = "ZM Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(3500.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    // Apply both base scale and pulse effect
                    val finalScale = if (animationStage == 2) logoScale * logoExtraScale else logoScale
                    scaleX = finalScale
                    scaleY = finalScale
                }
                .alpha(logoAlpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen6Preview() {
    SplashScreen6()
}
