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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.model.Car
import com.example.myapplication.ui.theme.poppins
import com.example.myapplication.ui.theme.primaryGreen
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    navController: NavController,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val car = uiState.car
    
    // Show loading indicator if data is loading
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Show error message if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message to user
            // You can replace this with a Snackbar or other UI element
            println("Error: $error")
            // Clear error after showing
            viewModel.clearError()
        }
    }

    
    // Show car details if available
    car?.let { currentCar ->
        CarDetailsContent(
            car = currentCar,
            isFavorite = currentCar.isFavorite,
            onBackClick = { navController.navigateUp() },
            onBookNowClick = { 
                // Navigate to booking screen
                // navController.navigate("booking/${currentCar.id}")
            },
            onFavoriteClick = { viewModel.toggleFavorite() },
            onShareClick = { /* Handle share */ }
        )
    } ?: run {
        // Show error if car is not found
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text("Car not found")
        }
    }
}

@Composable
private fun CarDetailsContent(
    car: Car,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onBookNowClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Car Images Gallery
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Main Car Image
            Image(
                painter = painterResource(id = car.imageRes ?: R.drawable.car_placeholder),
                contentDescription = car.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Top Bar with Back and Favorite Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                
                // Favorite and Share Buttons
                Row {
                    // Favorite Button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Share Button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
        
        // Car Details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Car Name and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = car.name ?: "Car",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                
                Text(
                    text = "${car.price ?: "0"} DA/day",
                    fontFamily = poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryGreen,
                    fontSize = 20.sp
                )
            }
            
            // Car Rating and Reviews
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating Stars
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = "${car.rating ?: 0.0} (${car.reviewCount ?: 0} reviews)",
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            // Car Features
            Text(
                text = "Features",
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Feature Icons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CarFeature(icon = Icons.Outlined.DirectionsCar, text = "${car.seats ?: 4} Seats")
                CarFeature(icon = Icons.Outlined.Air, text = "AC")
                CarFeature(icon = Icons.Outlined.Speed, text = "${car.fuelType ?: "Petrol"}")
                CarFeature(icon = Icons.Outlined.Settings, text = "${car.transmission ?: "Automatic"}")
            }
            
            // Description
            Text(
                text = "Description",
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = car.description ?: "No description available.",
                fontFamily = poppins,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
            
            // Similar Cars
            Text(
                text = "Similar Cars",
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Similar Cars List
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // In a real app, you would fetch similar cars from the repository
                // For now, we'll just show a few placeholders
                items((1..3).toList()) { index ->
                    SimilarCarItem(
                        name = "Similar Car $index",
                        price = "${(car.price?.toIntOrNull() ?: 100) + (index * 10)} DA/day",
                        imageRes = R.drawable.car_placeholder,
                        onClick = { /* Handle similar car click */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Add space for the bottom button
        }
        
        // Book Now Button (Fixed at bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = onBookNowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Book Now",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun CarFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = primaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontFamily = poppins,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun SimilarCarItem(
    name: String,
    price: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = price,
            fontFamily = poppins,
            color = primaryGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
