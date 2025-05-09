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
import androidx.compose.ui.draw.alpha


@Composable
fun SplashScreen(
    onNavigateToNextScreen: () -> Unit = {}
) {
    LaunchedEffect(key1 = true) {
        delay(1500)
        onNavigateToNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_3),
            contentDescription = "Splash Screen Logo",
            modifier = Modifier
                .size(200.dp)
                .alpha(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}