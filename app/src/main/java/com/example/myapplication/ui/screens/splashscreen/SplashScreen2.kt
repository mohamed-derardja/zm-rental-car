package com.example.myapplication.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen2(
    onNavigateToNextScreen: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade-in animation for main image
    val imageAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "imageAlpha"
    )

    // Ball animation
    val ballAnim = rememberInfiniteTransition()
    val ballOffset by ballAnim.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Main splash image
        Image(
            painter = painterResource(id = R.drawable.img_4),
            contentDescription = "Splash Image 2",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 0.dp)
                .align(Alignment.Center)
                .alpha(imageAlpha)
        )

        // Small ball with subtle movement
        Image(
            painter = painterResource(id = R.drawable.ellipse_1),
            contentDescription = "Animated Ball",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .graphicsLayer {
                    translationY = ballOffset
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreen2Preview() {
    SplashScreen2()
}