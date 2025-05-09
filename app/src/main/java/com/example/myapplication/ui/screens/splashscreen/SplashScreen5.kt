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
fun SplashScreen5(
    onNavigateToNextScreen: () -> Unit = {}
) {
    var animationStage by remember { mutableStateOf(0) }

    // Control both bounce and growth with a single transition
    val transition = updateTransition(targetState = animationStage, label = "bounceAndGrow")

    // Bounce position - removed the small bounce at the end
    val bounceOffset by transition.animateFloat(
        transitionSpec = {
            when (targetState) {
                0, 1 -> repeatable(
                    iterations = 2,
                    animation = keyframes {
                        durationMillis = 1000
                        0f at 0 with LinearOutSlowInEasing
                        -60f at 300 with FastOutLinearInEasing  // Bounce up
                        0f at 600 with LinearOutSlowInEasing    // Bounce down directly to rest position
                    }
                )
                else -> tween(300)
            }
        },
        label = "bounce"
    ) { stage ->
        when (stage) {
            0, 1 -> 0f  // Will be controlled by animation spec
            2 -> 0f     // Stop bouncing
            else -> 0f
        }
    }

    // Growing animation
    val scale by transition.animateFloat(
        transitionSpec = {
            when (targetState) {
                1 -> tween(800, easing = FastOutSlowInEasing)
                2 -> tween(600, easing = EaseInOutCubic)
                else -> tween(300)
            }
        },
        label = "scale"
    ) { stage ->
        when (stage) {
            0 -> 1.0f    // Normal size during bouncing
            1 -> 1.5f    // Start growing
            2 -> 2.2f    // Grow more
            else -> 2.2f
        }
    }

    LaunchedEffect(Unit) {
        // Stage 0: Initial bouncing
        delay(1000)
        // Stage 1: Start growing while bouncing
        animationStage = 1
        delay(1500)
        // Stage 2: Grow more significantly
        animationStage = 2
        delay(1000)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Ball that bounces and grows
        Image(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Growing Ball",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationY = bounceOffset
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen5Preview() {
    SplashScreen5()
}