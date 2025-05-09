package com.example.myapplication.ui.screens.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.poppins

@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit = {}) {
    val scrollState = rememberScrollState()

    // Calculate top padding based on status bar height
    val topPadding = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .verticalScroll(scrollState)
        ) {
            // Header with back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 15.dp, end = 15.dp, bottom = 10.dp)
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(45.dp)
                    .clip(CircleShape)
                        .background(Color(0xFFFFFFFF))
                        .clickable { onBackClick() }
            ) {
                IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.size(45.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                    )
                }
            }

                // Title "Privacy Policy" at the center
            Text(
                text = "Privacy Policy",
                    fontSize = 23.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                
                // Empty spacer for alignment
                Spacer(modifier = Modifier.size(45.dp).align(Alignment.CenterEnd))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Last Update: 14/08/2024",
            fontSize = 14.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF149459)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
                    text = "Enjoy a seamless car rental experience with our app. Discover a wide selection of vehicles tailored to your journey's needs—whether it's luxury, economy, or adventure. With user-friendly navigation and efficient booking, you'll be on the road in no time.\n\n" +
                            "Our app keeps you in the driver's seat with real-time updates, flexible options, and personalized recommendations. Say goodbye to hassle and hello to convenience as you embark on your next journey with confidence.",
            fontSize = 14.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Terms & Conditions",
            fontSize = 16.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF149459)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val terms = listOf(
            "Effortless Navigation: Our user-friendly Android app makes finding and booking a car quick and simple, ensuring a hassle-free experience.",
            "Wide Selection of Vehicles: Choose from a variety of cars, including luxury, economy, and adventure-ready options, tailored to meet your travel needs.",
            "Real-Time Control: Stay updated with notifications, easily modify bookings, and manage rentals conveniently all from your mobile device.",
            "Seamless Travel: Enjoy smooth service and reliable vehicles, making every journey safe, comfortable, and memorable.",
            "Customer Support: Access 24/7 customer service for assistance with any issues or queries."
        )

        terms.forEachIndexed { index, term ->
            Text(
                text = "${index + 1}. $term",
                fontSize = 14.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    MaterialTheme {
        PrivacyPolicyScreen()
    }
}
