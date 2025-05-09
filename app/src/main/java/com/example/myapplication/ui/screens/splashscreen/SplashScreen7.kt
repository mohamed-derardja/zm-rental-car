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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer

@Composable
internal fun SplashScreen7(
    onNavigateToNextScreen: () -> Unit = {}
) {
    // Animation states
    var animationPhase by remember { mutableStateOf(0) }
    var startExitAnimation by remember { mutableStateOf(false) }

    // Main background animation
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (startExitAnimation) 0f else 1f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = EaseInOutCubic
        ),
        label = "backgroundFade"
    )

    // Advanced logo animations
    // Entry animation
    val entryScale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.8f
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = 700,
            easing = EaseOutBack
        ),
        label = "entryScale"
    )

    // Subtle floating effect
    val floatTransition = rememberInfiniteTransition(label = "floating")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    // Subtle rotation
    val rotationAngle by floatTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Glow effect
    val glowIntensity by floatTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Exit animation
    val exitScale by animateFloatAsState(
        targetValue = if (startExitAnimation) 1.3f else 1f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "exitScale"
    )

    val exitAlpha by animateFloatAsState(
        targetValue = if (startExitAnimation) 0f else 1f,
        animationSpec = tween(
            durationMillis = 600,
            easing = EaseInQuart
        ),
        label = "exitAlpha"
    )

    val exitBlur by animateFloatAsState(
        targetValue = if (startExitAnimation) 15f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = EaseInCubic
        ),
        label = "exitBlur"
    )

    LaunchedEffect(Unit) {
        delay(100) // Short initial delay
        animationPhase = 1 // Start entry animation
        delay(2200) // Display time
        startExitAnimation = true // Start exit animations
        delay(900) // Wait for exit animation
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = backgroundAlpha)),
        contentAlignment = Alignment.Center // Ensure logo is centered
    ) {
        // Optional subtle spotlight effect
        if (!startExitAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .alpha(0.1f * (1 - exitAlpha))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }

        // Main logo
        Box(
            modifier = Modifier
                .size(250.dp)
                .scale(entryScale * (if (startExitAnimation) exitScale else 1f))
                .alpha(exitAlpha)
                .blur(exitBlur.dp)
                .graphicsLayer {
                    translationY = if (startExitAnimation) 0f else floatY
                    rotationZ = if (startExitAnimation) 0f else rotationAngle
                    scaleX = if (startExitAnimation) exitScale else glowIntensity
                    scaleY = if (startExitAnimation) exitScale else glowIntensity
                },
            contentAlignment = Alignment.Center
        ) {
            // Shadow/glow effect (optional)
            Image(
                painter = painterResource(id = R.drawable.img_3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.2f)
                    .scale(1.1f)
                    .blur(8.dp),
                colorFilter = ColorFilter.tint(Color(0xFF4D7EF7), BlendMode.SrcIn)
            )

            // Main logo
            Image(
                painter = painterResource(id = R.drawable.img_3),
                contentDescription = "App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen7Preview() {
    SplashScreen7()
}