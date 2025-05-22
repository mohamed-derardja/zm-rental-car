package com.example.myapplication.data.api

/**
 * Data class representing password reset responses from the API.
 */
data class PasswordResetResponse(
    val success: Boolean = true,
    val message: String
) 