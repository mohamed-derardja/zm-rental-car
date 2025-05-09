package com.example.myapplication.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.*

@Composable
fun SecondScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.audi),
                contentDescription = "Audi ",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()

            )

            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(19.dp)
            ) {
                Text(
                    "Skip",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = poppins
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            welcomeTexts()
            Spacer(modifier = Modifier.height(40.dp))
            BottomSection(onNextClick)
        }
    }
}

@Composable
fun welcomeTexts() {
    Text(
        text = "Your Journey Starts Here",
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
        color = Color.Black,
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,

        )

    Text(
        text = "Book a Car Now!",
        fontSize = 26.sp,
        color = Page2Green,
        textAlign = TextAlign.Center,
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
    )

    Spacer(modifier = Modifier.height(35.dp))

    Text(
        text = "Search, compare, and book your ideal car\n all in one place. Whether it's a weekend\n getaway\n or a business trip, we've got you covered",
        fontSize = 18.sp,
        lineHeight = 22.sp,
        color = TextGray,
        textAlign = TextAlign.Center,
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.075.sp
    )
}

@Composable
fun BottomSection(onNextClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
    ) {
        pageIndicator(
            modifier = Modifier.align(Alignment.Center)
        )

        nextButton(
            onClick = onNextClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun pageIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Page2Green)
        )
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color.White)

        )
    }
}

@Composable
fun nextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(58.dp)
            .background(color = Page2Green, shape = RoundedCornerShape(25.dp))
    ) {
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Next",
            tint = Color.White,
            modifier = Modifier.size(35.dp)
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecondScreenPreview() {
    MyApplicationTheme {
        SecondScreen()
    }
} 