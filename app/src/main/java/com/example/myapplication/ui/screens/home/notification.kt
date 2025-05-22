package com.example.myapplication.ui.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.poppins

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                NotificationScreen()
            }
        }
    }
}

data class NotificationItem(
    val title: String,
    val message: String,
    val isNew: Boolean,
    val iconType: NotificationIconType
)

enum class NotificationIconType {
    CALENDAR, CLOCK, STAR
}

@Composable
fun NotificationScreen(onBackPressed: () -> Unit = {}) {
    val notifications = listOf(
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", false, NotificationIconType.CALENDAR),
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", true, NotificationIconType.CLOCK),
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", false, NotificationIconType.STAR),
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", false, NotificationIconType.CALENDAR),
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", true, NotificationIconType.CALENDAR),
        NotificationItem("Rental Return Due Soon", "friendly reminder—your car rental is due for return tomorrow. thank you for choosing us!", false, NotificationIconType.CALENDAR)
    )

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
                        .clickable { onBackPressed() }
                ) {
                    IconButton(
                        onClick = { onBackPressed() },
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

                // Title "Notification" at the center
                Text(
                    text = "Notification",
                    fontSize = 23.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                // "2 NEW" badge
            Text(
                text = "2 NEW",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                        .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

            // Content
        LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("Today")
            }

            items(notifications.take(3)) { item ->
                NotificationCard(item)
            }

            item {
                SectionHeader("Yesterday")
            }

            items(notifications.drop(3)) { item ->
                NotificationCard(item)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text("Mark all as read", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    val backgroundColor = if (item.isNew) Color(0xFF017A51) else Color.White
    val textColor = if (item.isNew) Color.White else Color.Black
    val iconBackgroundColor = if (item.isNew) Color.Transparent else Color(0xFFE9F7F1)
    val iconTint = if (item.isNew) Color.White else Color(0xFF017A51)

    val icon = when (item.iconType) {
        NotificationIconType.CALENDAR -> Icons.Default.CalendarToday
        NotificationIconType.CLOCK -> Icons.Default.AccessTime
        NotificationIconType.STAR -> Icons.Default.Star
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBackgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                Text(item.message, fontSize = 12.sp, color = textColor)
            }

            Text("2 M", color = textColor, fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    MyApplicationTheme {
        NotificationScreen()
    }
}
