package com.example.myapplication.ui.screens.payment

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.screens.home.CarViewModel
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Data class to hold the booking form state
data class BookingFormState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val wilaya: String = "",
    val hasDriverLicense: Boolean = false,
    val driverLicenseUri: Uri? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val pickupLocation: String = "",
    val dropoffLocation: String = "",
    val specialRequests: String = "",
    val acceptTerms: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteYourBookingScreen(
    carId: String? = null,
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: CarViewModel = hiltViewModel()

    // Form state
    var formState by remember {
        mutableStateOf(BookingFormState())
    }

    // Form errors
    var formErrors by remember {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    // UI state
    var isSubmitting by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var wilayaDropdownExpanded by remember { mutableStateOf(false) }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            formState = formState.copy(driverLicenseUri = it)
            formErrors = formErrors - "driverLicense"
        }
    }

    // Date formatter
    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    }

    // Wilayas list
    val wilayas = listOf(
        "Adrar", "Chlef", "Laghouat", "Oum El Bouaghi", "Batna", "Béjaïa", "Biskra",
        "Béchar", "Blida", "Bouira", "Tamanrasset", "Tébessa", "Tlemcen", "Tiaret",
        "Tizi Ouzou", "Algiers", "Djelfa", "Jijel", "Sétif", "Saïda", "Skikda",
        "Sidi Bel Abbès", "Annaba", "Guelma", "Constantine", "Médéa", "Mostaganem",
        "M'Sila", "Mascara", "Ouargla", "Oran", "El Bayadh", "Illizi", "Bordj Bou Arreridj",
        "Boumerdès", "El Tarf", "Tindouf", "Tissemsilt", "El Oued", "Khenchela", "Souk Ahras",
        "Tipaza", "Mila", "Aïn Defla", "Naâma", "Aïn Témouchent", "Ghardaïa", "Relizane", "Timimoun",
        "Bordj Badji Mokhtar", "Ouled Djellal", "Béni Abbès", "In Salah", "In Guezzam", "Touggourt",
        "Djanet", "El M'Ghair", "El Menia"
    )

    // Scroll state for the form
    val scrollState = rememberScrollState()

    // Validation function
    fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        // First Name validation
        if (formState.firstName.isBlank()) {
            errors["firstName"] = "First name is required"
        } else if (formState.firstName.length < 2) {
            errors["firstName"] = "First name is too short"
        }

        // Last Name validation
        if (formState.lastName.isBlank()) {
            errors["lastName"] = "Last name is required"
        } else if (formState.lastName.length < 2) {
            errors["lastName"] = "Last name is too short"
        }

        // Email validation
        if (formState.email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(formState.email).matches()) {
            errors["email"] = "Please enter a valid email address"
        }

        // Phone validation - accept 10-13 digits, optional + at start
        if (formState.phoneNumber.isBlank()) {
            errors["phoneNumber"] = "Phone number is required"
        } else if (!formState.phoneNumber.matches(Regex("^\\+?[0-9]{10,13}$"))) {
            errors["phoneNumber"] = "Please enter a valid phone number"
        }

        // Wilaya validation
        if (formState.wilaya.isBlank()) {
            errors["wilaya"] = "Please select a wilaya"
        }

        // Date validation
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (formState.startDate == null) {
            errors["startDate"] = "Start date is required"
        } else if (formState.startDate!!.before(today)) {
            errors["startDate"] = "Start date cannot be in the past"
        }

        if (formState.endDate == null) {
            errors["endDate"] = "End date is required"
        } else if (formState.startDate != null) {
            if (formState.endDate!!.before(formState.startDate)) {
                errors["endDate"] = "End date must be after start date"
            } else if (formState.endDate == formState.startDate) {
                errors["endDate"] = "Minimum rental period is 1 day"
            }
        }

        // Location validation
        if (formState.pickupLocation.isBlank()) {
            errors["pickupLocation"] = "Pickup location is required"
        }

        // Driver's license validation
        if (formState.driverLicenseUri == null) {
            errors["driverLicense"] = "Driver's license is required"
        }

        // Terms acceptance validation
        if (!formState.acceptTerms) {
            errors["terms"] = "You must accept the terms and conditions"
        }

        formErrors = errors
        return errors.isEmpty()
    }

    // Handle form submission
    fun submitBooking() {
        if (validateForm()) {
            isSubmitting = true
        } else {
            // Scroll to the first error - handled by LaunchedEffect
        }
    }

    // Handle submission with LaunchedEffect
    LaunchedEffect(isSubmitting) {
        if (isSubmitting) {
            try {
                // Simulate network delay
                delay(1500)

                // Generate a random booking ID (replace with actual API call)
                val bookingId = "BK${(100000..999999).random()}"

                // Show success message before navigation
                Toast.makeText(
                    context,
                    "Booking confirmed! ID: $bookingId",
                    Toast.LENGTH_LONG
                ).show()

                // Navigate to booking confirmation
                onBookingComplete(bookingId)

                // Reset form state after navigation
                formState = BookingFormState()
            } catch (e: Exception) {
                // Handle API error
                Toast.makeText(
                    context,
                    "Failed to complete booking. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isSubmitting = false
            }
        }
    }

    // Scroll to top when validation fails
    LaunchedEffect(formErrors) {
        if (formErrors.isNotEmpty() && !isSubmitting) {
            scrollState.animateScrollTo(0)
        }
    }

    // Date picker dialogs
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.startDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            formState = formState.copy(
                                startDate = Date(millis)
                            )
                            formErrors = formErrors - "startDate"
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.endDate?.time ?: formState.startDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            formState = formState.copy(
                                endDate = Date(millis)
                            )
                            formErrors = formErrors - "endDate"
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Car details from ViewModel (assuming CarUiState exists)
    // val carDetailsState by viewModel.carDetailsState.collectAsState()
    // val car = remember(carDetailsState) {
    //     if (carDetailsState is CarUiState.SingleCarSuccess) {
    //         (carDetailsState as CarUiState.SingleCarSuccess).car
    //     } else null
    // }

    // Mock car data for now
    val car = remember {
        object {
            val name = "Toyota Corolla"
            val pricePerDay = 5000
        }
    }

    // Load car details if carId is provided
    LaunchedEffect(carId) {
        carId?.toLongOrNull()?.let { id ->
            // viewModel.loadCarById(id)
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
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp) // Space for the fixed button
        ) {
            // Header with back button and title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF149459)) // Using direct color instead of PrimaryColor
                    .padding(16.dp)
            ) {
                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF149459)
                    )
                }

                // Title
                Text(
                    text = "Complete Your Booking",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Car details card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Car name and price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = car.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${car.pricePerDay} DA/day",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF149459),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rental period
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Rental Period",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Rental Period",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Start date button
                        TextButton(
                            onClick = { showStartDatePicker = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (formState.startDate == null) Color.Gray else Color.Black
                            )
                        ) {
                            Text(
                                text = formState.startDate?.let { dateFormatter.format(it) }
                                    ?: "Start Date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text("to", style = MaterialTheme.typography.bodyMedium)

                        // End date button
                        TextButton(
                            onClick = { showEndDatePicker = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (formState.endDate == null) Color.Gray else Color.Black
                            )
                        ) {
                            Text(
                                text = formState.endDate?.let { dateFormatter.format(it) }
                                    ?: "End Date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Show date errors if any
                    formErrors["startDate"]?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }

                    formErrors["endDate"]?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp)
                        )
                    }
                }
            }

            // Personal Information Section
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // First Name and Last Name Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // First Name
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = formState.firstName,
                                onValueChange = {
                                    formState = formState.copy(firstName = it)
                                    formErrors = formErrors - "firstName"
                                },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = formErrors["firstName"] != null,
                                supportingText = {
                                    formErrors["firstName"]?.let { error ->
                                        Text(
                                            text = error,
                                            color = Color.Red,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Last Name
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = formState.lastName,
                                onValueChange = {
                                    formState = formState.copy(lastName = it)
                                    formErrors = formErrors - "lastName"
                                },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = formErrors["lastName"] != null,
                                supportingText = {
                                    formErrors["lastName"]?.let { error ->
                                        Text(
                                            text = error,
                                            color = Color.Red,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = {
                            formState = formState.copy(email = it)
                            formErrors = formErrors - "email"
                        },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formErrors["email"] != null,
                        supportingText = {
                            formErrors["email"]?.let { error ->
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = formState.phoneNumber,
                        onValueChange = {
                            formState = formState.copy(phoneNumber = it)
                            formErrors = formErrors - "phoneNumber"
                        },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formErrors["phoneNumber"] != null,
                        supportingText = {
                            formErrors["phoneNumber"]?.let { error ->
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Wilaya Dropdown
                    Box {
                        OutlinedTextField(
                            value = formState.wilaya,
                            onValueChange = {},
                            label = { Text("Wilaya") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { wilayaDropdownExpanded = true },
                            readOnly = true,
                            isError = formErrors["wilaya"] != null,
                            supportingText = {
                                formErrors["wilaya"]?.let { error ->
                                    Text(
                                        text = error,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Wilaya"
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        DropdownMenu(
                            expanded = wilayaDropdownExpanded,
                            onDismissRequest = { wilayaDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            wilayas.forEach { wilaya ->
                                DropdownMenuItem(
                                    text = { Text(wilaya) },
                                    onClick = {
                                        formState = formState.copy(wilaya = wilaya)
                                        formErrors = formErrors - "wilaya"
                                        wilayaDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pickup Location
                    OutlinedTextField(
                        value = formState.pickupLocation,
                        onValueChange = {
                            formState = formState.copy(pickupLocation = it)
                            formErrors = formErrors - "pickupLocation"
                        },
                        label = { Text("Pickup Location") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formErrors["pickupLocation"] != null,
                        supportingText = {
                            formErrors["pickupLocation"]?.let { error ->
                                Text(
                                    text = error,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Pickup Location"
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Special Requests (Optional)
                    OutlinedTextField(
                        value = formState.specialRequests,
                        onValueChange = {
                            formState = formState.copy(specialRequests = it)
                        },
                        label = { Text("Special Requests (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Driver's License Section
            Text(
                text = "Driver's License",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Driver's License Upload
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                filePickerLauncher.launch("image/*")
                                // Clear error when user tries to upload a file
                                formErrors = formErrors - "driverLicense"
                            }
                            .border(
                                width = 1.dp,
                                color = if (formErrors["driverLicense"] != null) Color.Red else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        if (formState.driverLicenseUri != null) {
                            // Show preview of the selected image
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "License Uploaded",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "License Uploaded",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Green,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to change",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        } else {
                            Icon(
                                Icons.Default.Upload,
                                contentDescription = "Upload License",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upload Driver's License",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "JPG, PNG (max 5MB)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    // Error message if any
                    formErrors["driverLicense"]?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Terms and Conditions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = formState.acceptTerms,
                            onCheckedChange = {
                                formState = formState.copy(acceptTerms = it)
                                if (it) formErrors = formErrors - "terms"
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF149459),
                                checkmarkColor = Color.White
                            )
                        )

                        Text(
                            text = "I agree to the ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Text(
                            text = "Terms and Conditions",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF149459),
                            modifier = Modifier.clickable {
                                // TODO: Open terms and conditions
                            }
                        )
                    }

                    // Show error if terms not accepted
                    formErrors["terms"]?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = { submitBooking() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF149459),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Complete Booking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun PreviewCompleteYourBookingScreen() {
    MaterialTheme {
        CompleteYourBookingScreen(
            carId = "1",
            onBackClick = {},
            onBookingComplete = {}
        )
    }
}