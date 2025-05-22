package com.example.myapplication.ui.screens.payment



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.poppins

@Composable
fun BillScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
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
                    Image(
                        painter = painterResource(id = R.drawable.fleche_icon_lonly),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                    }
                }

                // Title "Bill" at the center
                Text(
                    text = "Bill",
                    fontSize = 23.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Empty spacer for alignment
                Spacer(modifier = Modifier.size(45.dp).align(Alignment.CenterEnd))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Rest of the content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Car image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.36f))
                            .padding(horizontal = 5.dp)
                    ) {
                        Text(
                            text = "Auto",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(11.dp))

                    Text(
                        text = "Audi R8 2020",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "00.00DA/day",
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.36f))
                        .padding(vertical = 2.dp, horizontal = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.star_for_the_review_lonly),
                            contentDescription = "Rating icon",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "4.9",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(27.dp))

            BillDetailItem("Return-Up Date & Time", "MM 00 | 00:00 A/PM")
            BillDetailItem("Return Date & Time", "MM 00 | 00:00 A/PM")
            BillDetailItem("Driver option", "Self-Driver", withDivider = false)
            BillDetailItem("Amount", "00.00DA/day")

            Divider(
                color = Color.Black.copy(alpha = 0.4f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 35.dp)
            )

            BillDetailItem("Total Days", "00", textColor = Color(0xFF323232))
            BillDetailItem("Driver Fees", "00.00Da", textColor = Color(0xFF323232))

            Divider(
                color = Color.Black.copy(alpha = 0.4f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 22.dp)
            )

            BillDetailItem("Total", "00.00DA", textColor = Color(0xFF323232))

            Divider(
                color = Color.Black.copy(alpha = 0.4f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 13.dp)
            )

            Row(
                modifier = Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.credit_card_icon),
                    contentDescription = "Payment icon",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Edahabia",
                    fontSize = 13.sp,
                    color = Color(0xFF323232)
                )
            }
            }
        }


        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF149459)
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun BillDetailItem(
    label: String,
    value: String,
    textColor: Color = Color.Black,
    withDivider: Boolean = true
) {
    Column(modifier = Modifier.padding(horizontal = 13.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = textColor
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = textColor
            )
        }

        if (withDivider) {
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BillScreenPreview() {
    BillScreen(
        onBackClick = {},
        onContinueClick = {}
    )
}