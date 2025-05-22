package com.example.myapplication.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun rememberImagePicker(
    onImagePicked: (Uri) -> Unit,
    onError: (String) -> Unit = {}
): ImagePickerState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    onImagePicked(it)
                }
            } ?: run {
                onError("No image selected")
            }
        }
    )

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            imagePicker.launch("image/*")
        } else {
            onError("Storage permission denied")
        }
    }

    return remember {
        ImagePickerState(
            context = context,
            imagePicker = imagePicker,
            permissionLauncher = permissionLauncher,
            permissions = permissions,
            onError = onError
        )
    }
}

class ImagePickerState(
    private val context: Context,
    private val imagePicker: androidx.activity.result.ActivityResultLauncher<String>,
    private val permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    private val permissions: Array<String>,
    private val onError: (String) -> Unit
) {
    fun pickImage() {
        when {
            hasPermissions() -> launchImagePicker()
            else -> requestPermissions()
        }
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        permissionLauncher.launch(permissions)
    }

    private fun launchImagePicker() {
        try {
            imagePicker.launch("image/*")
        } catch (e: Exception) {
            Log.e("ImagePicker", "Error launching image picker", e)
            onError("Failed to open image picker")
        }
    }

    companion object {
        suspend fun getFileFromUri(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val fileName = getFileName(contentResolver, uri) ?: "image_${System.currentTimeMillis()}"
                val file = File.createTempFile(
                    "img_${UUID.randomUUID()}",
                    ".${fileName.substringAfterLast(".", "jpg")}",
                    context.cacheDir
                )
                
                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error creating file from URI", e)
                null
            }
        }

        private fun getFileName(contentResolver: android.content.ContentResolver, uri: Uri): String? {
            var fileName: String? = null
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    if (displayName.isNotBlank()) {
                        fileName = displayName
                    }
                }
            }
            return fileName ?: uri.path?.substringAfterLast("/")
        }
    }
}
