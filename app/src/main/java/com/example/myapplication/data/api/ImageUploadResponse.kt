package com.example.myapplication.data.api

/**
 * Data class representing image upload responses from the API.
 */
data class ImageUploadResponse(
    val url: String,
    val success: Boolean = true,
    val message: String? = null
) 