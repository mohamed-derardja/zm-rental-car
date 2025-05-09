package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarBookingScreen(
    onBackPressed: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    var rentType by remember { mutableStateOf("Self-Driver") }
    var pickUpDate by remember { mutableStateOf("Date") }
    var pickUpTime by remember { mutableStateOf("Time") }
    var dropOffDate by remember { mutableStateOf("Date") }
    var dropOffTime by remember { mutableStateOf("Time") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Reuse TopImageSection from CarDetailsScreen, but with title 'Car Booking' and no favorite
            TopImageSection(
                isFavorite = false,
                onFavoriteClick = {},
                onBackPressed = onBackPressed,
                title = "Car Booking",
                showFavorite = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Car Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                CarTagAndRating()
                CarNameSection()
            }
            Divider(color = Color.Black, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))
            // Rent Type
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                Text(
                    text = "Rent type",
                    fontSize = 17.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = { rentType = "Self-Driver" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (rentType == "Self-Driver") Color(0xFF149459) else Color.White,
                            contentColor = if (rentType == "Self-Driver") Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Self-Driver",fontSize = 16.sp , fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { rentType = "With Driver" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (rentType == "With Driver") Color(0xFF149459) else Color.White,
                            contentColor = if (rentType == "With Driver") Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("With Driver", fontSize = 16.sp , fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (rentType == "With Driver") {
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Additional 00.00DA Driver Cost if you Choose With Driver Option",
                            fontSize = 13.sp,
                            fontFamily = poppins,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Pick-up and Drop-off
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                Text(
                    text = "Pick-up date and time",
                    fontSize = 17.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Show date picker */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Date",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pickUpDate,fontSize = 16.sp , color = Color.Black, fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { /* TODO: Show time picker */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.timepick),
                            contentDescription = "Time",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(pickUpTime,fontSize = 16.sp , color = Color.Black, fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Drop-off date and time",
                    fontSize = 17.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Show date picker */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Date",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dropOffDate,fontSize = 16.sp , color = Color.Black, fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = { /* TODO: Show time picker */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.timepick),
                            contentDescription = "Time",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dropOffTime,fontSize = 16.sp , color = Color.Black, fontFamily = poppins, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
            // Continue Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Button(
                    onClick = { onContinue() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF149459))
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CarBookingScreenPreview() {
    CarBookingScreen(
        onBackPressed = {},
        onContinue = {}
    )
}