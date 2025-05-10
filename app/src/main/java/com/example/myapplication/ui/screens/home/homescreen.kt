package com.example.myapplication.ui.screens.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.poppins


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCarClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var expandedBrands by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf("All") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F5FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 80.dp)
                .verticalScroll(scrollState)
        ) {
            // Top Bar with Profile and Notification Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Icon - now clickable to navigate to profile
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .size(40.dp)
                        .background(Color.White)
                        .padding(8.dp)
                        .clickable { onProfileClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile",
                        tint = Color.Black,
                        modifier = Modifier.size(25.dp)
                    )
                }

                // Notification Icon
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .size(40.dp)
                        .background(Color.White)
                        .padding(8.dp)
                        .clickable { onNotificationClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar with Filter Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search any car...",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            letterSpacing = 0.08.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            tint = Color.Black,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(35.dp))

                // Filter Button
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(13.dp)
                        .clickable { onFilterClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Top Brands Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Brands",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brand Logos Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // First brand is always "All"
                item {
                    CarBrandItem(
                        brandName = "All",
                        iconRes = R.drawable.all,
                        isSelected = selectedBrand == "All",
                        onClick = { 
                            selectedBrand = "All"
                            expandedBrands = !expandedBrands 
                        }
                    )
                }

                // Always visible brands
                val visibleBrands = listOf(
                    Pair("Tesla", R.drawable.teslatopbrand),
                    Pair("BMW", R.drawable.bmwtopbrand),
                    Pair("Audi", R.drawable.auditopbrand)
                )

                items(visibleBrands) { (brand, icon) ->
                    CarBrandItem(
                        brandName = brand,
                        iconRes = icon,
                        isSelected = selectedBrand == brand,
                        onClick = { selectedBrand = brand }
                    )
                }

                // Expandable brands - only visible if expanded
                if (expandedBrands) {
                    val extendedBrands = listOf(
                        Pair("Mercedes", R.drawable.mercedestopbrand),
                        Pair("Volkswagen", R.drawable.wolswagen)
                    )

                    items(extendedBrands) { (brand, icon) ->
                        CarBrandItem(
                            brandName = brand,
                            iconRes = icon,
                            isSelected = selectedBrand == brand,
                            onClick = { selectedBrand = brand }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Top Rated Cars
            Text(
                text = "Top Rated Cars",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Car Cards
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(3) { index ->
                    val images = listOf(
                        R.drawable.ic_launcher_background,
                        R.drawable.ic_launcher_background,
                        R.drawable.ic_launcher_background
                    )

                    TopRatedCarCard(
                        carName = "Toyota RAV4 2024",
                        price = "00.00DA/day",
                        rating = 4.9f,
                        imageRes = images[index],
                        onClick = { onCarClick("Toyota RAV4 2024") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Most Popular Car
            Text(
                text = "Most Popular Car",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multiple Most Popular Cars
            val popularCars = listOf(
                Triple("Toyota RAV4 2024", "00.00DA/day", R.drawable.ic_launcher_background),
                Triple("Audi RS e-tron GT", "00.00DA/day", R.drawable.ic_launcher_background),
                Triple("Mercedes AMG GT", "00.00DA/day", R.drawable.ic_launcher_background),
                Triple("BMW i7 2024", "00.00DA/day", R.drawable.ic_launcher_background)
            )

            popularCars.forEach { (carName, price, imageRes) ->
                MostPopularCarCard(
                    carName = carName,
                    price = price,
                    rating = 4.9f,
                    imageRes = imageRes,
                    onClick = { onCarClick(carName) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Navigation Bar
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = { /* Already on Home */ },
            onCatalogClick = onCatalogClick,
            onFavoriteClick = onFavoriteClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun CarBrandItem(
    brandName: String,
    iconRes: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) Color(0xFF149459) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = brandName,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = brandName,
            fontSize = 12.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF149459) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TopRatedCarCard(
    carName: String,
    price: String,
    rating: Float,
    imageRes: Int,
    onClick: () -> Unit = {}
) {
    // State to track if this car is favorited
    var isFavorite by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Car Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = carName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Badge
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopStart)
            ) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.green_star),
                            contentDescription = "Rating",
                            tint = Color(0xFF149459),
                            modifier = Modifier.size(12.dp)
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = rating.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }

            // Favorite Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { isFavorite = !isFavorite }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Add to favorites",
                    tint = if (isFavorite) Color(0xFFFF4444) else Color.Gray,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.Center)
                )
            }

            // Car name and price - overlay at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = carName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = price,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun MostPopularCarCard(
    carName: String,
    price: String,
    rating: Float,
    imageRes: Int,
    onClick: () -> Unit = {}
) {
    // State to track if this car is favorited
    var isFavorite by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick)
    ) {
        // Outer white card containing everything
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Car Image with padding inside a card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = carName,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )

                        // Rating Badge
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp, top = 8.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Card(
                                shape = RoundedCornerShape(5.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.green_star),
                                        contentDescription = "Rating",
                                        tint = Color(0xFF149459),
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = "4.9",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        // Heart Icon - Now clickable
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp, top = 8.dp)
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { isFavorite = !isFavorite }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.heart),
                                contentDescription = "Add to favorites",
                                tint = if (isFavorite) Color(0xFFFF4444) else Color.Gray,
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Car Title and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Car Name
                    Text(
                        text = "Toyota RAV4 2024",
                        fontSize = 18.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                    )

                    // Price
                    Text(
                        text = "00.00DA/day",
                        fontSize = 15.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Add divider here
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Feature Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    // Manual
                    FeatureItem(
                        iconRes = R.drawable.manual,
                        text = "Manual"
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    // Petrol
                    FeatureItem(
                        iconRes = R.drawable.petrol,
                        text = "Petrol"
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    // Seat
                    FeatureItem(
                        iconRes = R.drawable.seat,
                        text = "Seat"
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = Color(0xFF149459),
            modifier = Modifier.size(25.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 17.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun CarFeatureTag(
    modifier: Modifier = Modifier,
    iconRes: Int,
    text: String,
    iconTint: Color = Color(0xFF149459),
    textColor: Color = Color.Black
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                iconRes = R.drawable.home,
                label = "Home",
                isSelected = true,
                onClick = onHomeClick
            )

            BottomNavItem(
                iconRes = R.drawable.catalog,
                label = "Bookings",
                onClick = onCatalogClick
            )

            BottomNavItem(
                iconRes = R.drawable.heart, // Using heart for Favorite
                label = "Favorite",
                onClick = onFavoriteClick
            )

            BottomNavItem(
                iconRes = R.drawable.profilenav,
                label = "Profile",
                onClick = onProfileClick
            )
        }
    }
}

@Composable
fun BottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val itemColor = if (isSelected) Color.Black else Color.Gray
    val bgColor = if (isSelected) Color(0xFFEADDFA) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bgColor)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = itemColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = itemColor,
                fontFamily = poppins
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true,)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}