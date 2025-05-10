package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myapplication.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBackPressed: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onBookNowClick: () -> Unit = {},
    photos: List<Int> = listOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )
) {
    var isFavorite by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA))
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp) // Add padding at the bottom for the fixed price section
                .verticalScroll(rememberScrollState())
        ) {
            // Top Car Image Section with Back Button and Favorite Button
            TopImageSection(
                isFavorite = false,
                onFavoriteClick = {},
                onBackPressed = onBackPressed,
                title = "Car Details",
                showFavorite = true
            )

            // Car Info Section (before tabs)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F5FA))
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                // Top Row - Auto tag and Rating
                GalleryCarTagAndRating()

                // Car Name
                GalleryCarNameSection()

                // Tab titles
                GalleryTabTitles(onAboutClick)
            }

            // Tab indicator/divider (full width)
            GalleryTabDivider()

            // Gallery Content
            GalleryContent(photos = photos)
        }

        // Fixed Price and Book Now Section at the bottom
        Box(
            modifier = Modifier
                .padding(top = 720.dp)
                .fillMaxWidth()
        ) {
            GalleryPriceAndBookSection(onBookNowClick = onBookNowClick)
        }
    }
}

@Composable
fun GalleryTopImageSection(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
    ) {
        // Main car image
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Car Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Title "Car Details" at the top center
        Text(
            text = "Car Details",
            fontSize = 23.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
        )

        // Back button
        Box(
            modifier = Modifier
                .padding(start = 15.dp, top = 20.dp)
                .size(45.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F5FA))
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier.size(150.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(33.dp)
                )
            }
        }

        // Favorite button
        Box(
            modifier = Modifier
                .padding(end = 15.dp, top = 20.dp)
                .size(45.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F5FA))
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopEnd),
        ) {
            IconButton(
                onClick = onFavoriteClick,
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Car image thumbnails
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 35.dp)
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(listOf(0, 1, 2, 3, 4, 5)) { index ->
                    val isMore = index == 5

                    Box(
                        modifier = Modifier
                            .size(45.dp, 45.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isMore) {
                            Text(
                                text = "+99",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        } else {
                            Image(
                                painter = painterResource(
                                    id = when (index) {
                                        0 -> R.drawable.ic_launcher_background
                                        1 -> R.drawable.ic_launcher_background
                                        2 -> R.drawable.ic_launcher_background
                                        3 -> R.drawable.ic_launcher_background
                                        else -> R.drawable.ic_launcher_background
                                    }
                                ),
                                contentDescription = "Car thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryCarTagAndRating() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Auto",
            fontSize = 14.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.green_star),
                contentDescription = "Rating",
                tint = Color(0xFF149459),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "4.9",
                fontSize = 17.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun GalleryCarNameSection() {
    Text(
        text = "Ford Mustang GT Premium 2024",
        fontSize = 19.sp,
        fontFamily = poppins,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
    )
}

@Composable
fun GalleryTabTitles(onAboutClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable { onAboutClick() }
        ) {
            Text(
                text = "About",
                fontSize = 18.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(90.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Gallery",
                fontSize = 18.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GalleryTabDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        // Gray divider under "About" (half width)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )

        // Green divider under "Gallery" (half width)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(Color(0xFF149459))
        )
    }
}

@Composable
fun GalleryContent(
    photos: List<Int> = listOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        // Photos Header with View All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Photos",
                fontSize = 20.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Text(
                text = "View all",
                fontSize = 14.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
        }

        // Display all photos in a grid
        val rows = photos.chunked(2)

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            rows.forEach { rowPhotos ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowPhotos.forEach { photo ->
                        Image(
                            painter = painterResource(id = photo),
                            contentDescription = "Car Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(170.dp,130.dp)

                                .clip(RoundedCornerShape(15.dp))
                        )

                        // If there's only one photo in the row, add a spacer
                        if (rowPhotos.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryPriceAndBookSection(onBookNowClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 30.dp)
            .clip(RoundedCornerShape(0.dp))
            .background(Color(0xFFF2F5FA))
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Price",
                    fontSize = 15.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )

                Text(
                    text = "00.00DA/hr",
                    fontSize = 21.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Book Now Button
            Button(
                onClick = onBookNowClick,
                modifier = Modifier
                    .height(40.dp)
                    .width(150.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF149459)
                )
            ) {
                Text(
                    text = "Book Now",
                    fontSize = 18.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GalleryScreenPreview() {
    GalleryScreen(
        photos = listOf(
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,

        )
    )
}