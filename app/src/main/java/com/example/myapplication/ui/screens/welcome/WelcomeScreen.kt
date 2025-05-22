package com.example.myapplication.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.*

@Composable
fun WelcomeScreen(
    onNextClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        BackgroundImage()
        MainContent(onNextClick)
    }
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.image2),
        contentDescription = "Background Car image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MainContent(onNextClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 80.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeTitle()
            Spacer(modifier = Modifier.height(16.dp))
            WelcomeDescription()
        }
        
        GetStartedButton(onNextClick)
    }
}

@Composable
fun WelcomeTitle() {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(
            color = PrimaryGreen,
            fontSize = 28.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Normal
        )) {
            append("Your Ultimate ")
        }
        withStyle(SpanStyle(
            color = SecondaryGreen,
            fontSize = 28.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Normal
        )) {
            append("Car Rental")
        }
        append("\n")
        withStyle(SpanStyle(
            color = PrimaryGreen,
            fontSize = 28.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Normal
        )) {
            append("Experience Starts Here!")
        }
    }

    Text(
        text = text,
        textAlign = TextAlign.Center,
        lineHeight = 36.sp
    )
}

@Composable
fun WelcomeDescription() {
    Text(
        text = "From compact cars to luxury rides, find the perfect vehicle for every journey.\n Book with ease, drive with confidence, and\n make every trip unforgettable.",
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = TextDark,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 20.dp),
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.075.sp
    )
}

@Composable
fun GetStartedButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            "Let's Get Started",
            color = White,
            fontSize = 17.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MyApplicationTheme {
        WelcomeScreen()
    }
}


