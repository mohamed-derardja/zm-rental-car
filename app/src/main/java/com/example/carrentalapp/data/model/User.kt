package com.example.carrentalapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.Date

/**
 * Data class representing a user in the application
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @Json(name = "id")
    val id: String,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "email")
    val email: String,
    
    @Json(name = "phone_number")
    val phoneNumber: String? = null,
    
    @Json(name = "profile_image_url")
    val profileImageUrl: String? = null,
    
    @Json(name = "created_at")
    val createdAt: Date,
    
    @Json(name = "updated_at")
    val updatedAt: Date,
    
    // This field is for local use to track login status
    val isLoggedIn: Boolean = false
) 