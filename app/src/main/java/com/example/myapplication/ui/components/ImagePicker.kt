package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider

@Composable
fun ImagePicker(
    imageUri: String?,
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Preview
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_photo),
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Image Picker Button
        Button(
            onClick = { showImagePicker = true },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = if (imageUri == null) "Select Image" else "Change Image")
        }

        // Handle Image Picker
        if (showImagePicker) {
            LaunchedEffect(Unit) {
                ImagePicker.with(context)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .provider(ImageProvider.BOTH)
                    .createIntent { intent ->
                        context.startActivityForResult(intent, ImagePicker.REQUEST_CODE)
                    }
                showImagePicker = false
            }
        }
    }
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Image Source") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Camera")
                }
                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gallery")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 