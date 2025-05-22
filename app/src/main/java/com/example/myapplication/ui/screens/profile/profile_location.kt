package com.example.myapplication.ui.screens.profile


import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileLocationScreen(navController: NavController) {
    val street = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val state = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("") }

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
            .verticalScroll(rememberScrollState())
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
                        .clickable { navController.popBackStack() }
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
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

                // Title "Profile" at the center
            Text(
                text = "Profile",
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
                    .padding(horizontal = 16.dp)
            ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Image
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    ) {
            Image(
                painter = painterResource(id = R.drawable.account),
                            contentDescription = "Profile Image",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    
                    // Edit button
                    Box(
                modifier = Modifier
                            .size(30.dp)
                            .offset(x = 80.dp, y = 80.dp)
                    .clip(CircleShape)
                            .background(Color(0xFF149459))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vectorpen),
                            contentDescription = "Edit Profile",
                modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.Center)
            )
                    }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                            .clickable { navController.navigate("ProfileGeneral") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "General",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF149459))
                    .clickable { /* Location tab */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Location Fields
                // Street
                Text(
                    text = "Street",
                    fontSize = 18.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.08.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = street.value,
                    onValueChange = { street.value = it },
                    placeholder = { 
                        Text("Enter your street address",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            letterSpacing = 0.08.sp) 
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        containerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // City
                Text(
                    text = "City",
                    fontSize = 18.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.08.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                    value = city.value,
                    onValueChange = { city.value = it },
                    placeholder = { 
                        Text("Enter your city",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            letterSpacing = 0.08.sp) 
                    },
                    singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        containerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

            Spacer(modifier = Modifier.height(16.dp))

                // State
                Text(
                    text = "State",
                    fontSize = 18.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.08.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.value,
                    onValueChange = { state.value = it },
                    placeholder = { 
                        Text("Enter your state",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            letterSpacing = 0.08.sp) 
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        containerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

        Spacer(modifier = Modifier.height(16.dp))

                // Country
                Text(
                    text = "Country",
                    fontSize = 18.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.08.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = country.value,
                    onValueChange = { country.value = it },
                    placeholder = { 
                        Text("Enter your country",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            letterSpacing = 0.08.sp) 
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD9D9D9),
                        unfocusedBorderColor = Color(0xFFD9D9D9),
                        containerColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Update Button
        Button(
            onClick = { /* Handle update */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF149459)),
                    shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                        .height(56.dp)
        ) {
                    Text(
                        "Update Profile",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.08.sp
                    )
        }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateProfileLocationScreen() {
    val navController = rememberNavController()
    UpdateProfileLocationScreen(navController)
}
