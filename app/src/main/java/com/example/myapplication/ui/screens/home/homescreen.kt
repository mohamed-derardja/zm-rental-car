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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCarClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    viewModel: CarViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var expandedBrands by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf("All") }
    
    // Collect the UI state from the ViewModel
    val carUiState by viewModel.uiState.collectAsState()
    
    // State for storing the cars to display
    var cars by remember { mutableStateOf<List<com.example.myapplication.data.model.Car>>(emptyList()) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Use LaunchedEffect to show Toast in a Composable context
    LaunchedEffect(carUiState) {
        when (carUiState) {
            is CarUiState.Error -> {
                android.widget.Toast.makeText(
                    context,
                    (carUiState as CarUiState.Error).message,
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }
    
    // Handle changes to UI state
    LaunchedEffect(carUiState) {
        when (carUiState) {
            is CarUiState.Success -> {
                cars = (carUiState as CarUiState.Success).cars
            }
            is CarUiState.PaginatedSuccess -> {
                val pagedResponse = (carUiState as CarUiState.PaginatedSuccess).pagedResponse
                cars = pagedResponse.content
            }
            else -> {}
        }
    }
    
    // Handle brand selection
    LaunchedEffect(selectedBrand) {
        if (selectedBrand == "All") {
            viewModel.loadAllCars()
        } else {
            viewModel.filterByBrand(selectedBrand)
        }
    }
    
    // Search handling
    var debouncedSearchQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        // Shorter debounce delay for better responsiveness
        kotlinx.coroutines.delay(300) // Debounce delay
        debouncedSearchQuery = searchQuery
        
        // Add debug output
        Log.d("HomeScreen", "Search query: '$searchQuery'")
    }
    
    LaunchedEffect(debouncedSearchQuery) {
        if (debouncedSearchQuery.isNotBlank()) {
            // Apply search filter using the view model
            Log.d("HomeScreen", "Filtering by model/brand: '$debouncedSearchQuery'")
            viewModel.filterByModel(debouncedSearchQuery)
        } else if (selectedBrand == "All") {
            // If search is empty and no brand filter, load all cars
            Log.d("HomeScreen", "Loading all cars (no search, no brand filter)")
            viewModel.loadAllCars()
        } else {
            // If search is empty but brand is selected, apply brand filter
            Log.d("HomeScreen", "Filtering by brand: '$selectedBrand'")
            viewModel.filterByBrand(selectedBrand)
        }
    }

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
                    onValueChange = { 
                        searchQuery = it
                    },
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
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                viewModel.loadAllCars()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear Search",
                                    tint = Color.Gray
                                )
                            }
                        }
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    Pair("Toyota", R.drawable.toyota),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Rated Cars",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                
                if (carUiState is CarUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF149459),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Show loading or error state
            when (carUiState) {
                is CarUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF149459)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading cars...",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                is CarUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (carUiState as CarUiState.Error).message,
                                fontFamily = poppins,
                                fontWeight = FontWeight.Medium,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    if (selectedBrand == "All") {
                                        viewModel.loadAllCars()
                                    } else {
                                        viewModel.filterByBrand(selectedBrand)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF149459)
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {
                    // Display cars if we have them
                    if (cars.isNotEmpty()) {
                        CarListSection(cars, onCarClick)
                    } else {
                        // No cars found
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No cars found",
                                fontFamily = poppins,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                }
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
    // Special case for "All" - only show border when explicitly selected
    val showBorder = when {
        brandName == "All" && isSelected -> true   // "All" is selected, show border
        brandName != "All" && isSelected -> true   // Other brand is selected, show border
        else -> false                              // Not selected, no border
    }
    
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
                    width = if (showBorder) 1.5.dp else 0.dp,
                    color = if (showBorder) Color(0xFF149459) else Color.Transparent,
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
fun CarItem(
    car: com.example.myapplication.data.model.Car,
    onClick: () -> Unit
) {
    // State to track if this car is favorited
    var isFavorite by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        // Outer white card containing everything
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // If car image URL is available, load it using Coil
                        // For now, we'll use a placeholder
                        Image(
                            painter = painterResource(id = R.drawable.car_placeholder),
                            contentDescription = "${car.brand} ${car.model}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Rating Badge
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, top = 12.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Rating",
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = car.rating.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        // Heart Icon - Now clickable
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp, top = 12.dp)
                                .align(Alignment.TopEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { isFavorite = !isFavorite }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFFF4444) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Car Title and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Car Name
                    Text(
                        text = "${car.brand} ${car.model}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Price
                    Text(
                        text = "${car.rentalPricePerDay}DA / day",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF149459)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add divider here
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Feature Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    // Type
                    FeatureItem(
                        icon = Icons.Outlined.Settings,
                        text = car.type
                    )
                    
                    // Transmission
                    FeatureItem(
                        icon = Icons.Outlined.Speed,
                        text = car.transmission
                    )
                    
                    // Fuel
                    FeatureItem(
                        icon = Icons.Outlined.LocalGasStation,
                        text = car.fuel
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Book button
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF149459)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Book Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF149459),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
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

@Composable
fun CarListSection(cars: List<com.example.myapplication.data.model.Car>, onCarClick: (String) -> Unit = {}) {
    cars.forEach { car ->
        CarItem(
            car = car,
            onClick = { onCarClick(car.id.toString()) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true,)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}