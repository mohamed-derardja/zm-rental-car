package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.model.Car
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarBookingScreen(
    car: Car,
    onBackPressed: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    // Define booking state variables
    val pickupDate = remember { mutableStateOf(LocalDate.now()) }
    val dropoffDate = remember { mutableStateOf(LocalDate.now().plusDays(3)) }
    
    // Calculate formatted dates and number of days
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val formattedPickupDate = pickupDate.value.format(formatter)
    val formattedDropoffDate = dropoffDate.value.format(formatter)
    val numDays = ChronoUnit.DAYS.between(pickupDate.value, dropoffDate.value).toInt()
    
    // Get existing variables from existing code if needed
    var pickUpDate by remember { mutableStateOf("Date") }
    var pickUpTime by remember { mutableStateOf("Time") }
    var dropOffDate by remember { mutableStateOf("Date") }
    var dropOffTime by remember { mutableStateOf("Time") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Booking header with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Book Your Car",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Display booking summary
            BookingSummarySection(
                carModel = car.model,
                carBrand = car.brand,
                carPrice = car.rentalPricePerDay.toDouble(),
                pickupDate = formattedPickupDate,
                dropoffDate = formattedDropoffDate,
                numDays = numDays,
                transmission = car.transmission,
                rating = car.rating,
                carName = "${car.brand} ${car.model}",
                year = car.year
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue to Payment",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BookingSummarySection(
    modifier: Modifier = Modifier,
    carModel: String,
    carBrand: String,
    carPrice: Double,
    pickupDate: String,
    dropoffDate: String,
    numDays: Int,
    transmission: String,
    rating: Long,
    carName: String,
    year: Long
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Booking Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Car details
            Text(
                text = "$carBrand $carModel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Year: $year | $transmission | Rating: $rating",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Booking period
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pickup Date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = pickupDate,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Column {
                    Text(
                        text = "Dropoff Date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = dropoffDate,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Price calculation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Rate",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$$carPrice",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Number of Days",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$numDays days",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${carPrice * numDays}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun CarBookingScreenPreview() {
    CarBookingScreen(
        car = Car(
            id = 1,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            rentalPricePerDay = BigDecimal(50),
            transmission = "Automatic",
            rating = 4
        ),
        onBackPressed = {},
        onContinue = {}
    )
}