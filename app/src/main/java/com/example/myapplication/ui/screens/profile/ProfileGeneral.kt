package com.example.myapplication.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.Dimens

@Composable
fun ProfileGeneral(
    profileImageUrl: String?,
    userName: String,
    userEmail: String,
    onEditProfileClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),  // Using direct dp value temporarily
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),  // Using direct dp value temporarily
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Profile Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),  // Using direct dp value temporarily
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.audi),
                    fallback = painterResource(id = R.drawable.audi)
                )

                Spacer(modifier = Modifier.height(16.dp))  // Using direct dp value temporarily

                // User Name
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))  // Using direct dp value temporarily

                // User Email
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))  // Using direct dp value temporarily

                // Edit Profile Button
                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
} 