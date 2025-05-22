package com.example.myapplication.data.api

/**
 * Data class representing authentication responses from the API.
 */
data class AuthResponse(
    val id: Long = 0,
    val userId: Long = 0,
    val token: String,
    val email: String? = null,
    val name: String? = null,
    val success: Boolean = true,
    val message: String? = null
) 