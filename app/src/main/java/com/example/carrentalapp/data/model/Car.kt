package com.example.carrentalapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

/**
 * Data class representing a car in the rental system
 */
@Entity(tableName = "cars")
data class Car(
    @PrimaryKey
    @Json(name = "id")
    val id: String,
    
    @Json(name = "make")
    val make: String,
    
    @Json(name = "model")
    val model: String,
    
    @Json(name = "year")
    val year: Int,
    
    @Json(name = "price_per_day")
    val pricePerDay: Double,
    
    @Json(name = "image_url")
    val imageUrl: String,
    
    @Json(name = "description")
    val description: String,
    
    @Json(name = "available")
    val isAvailable: Boolean = true,
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "features")
    val features: List<String>,
    
    @Json(name = "rating")
    val rating: Float = 0.0f,
    
    @Json(name = "mileage")
    val mileage: Int,
    
    @Json(name = "fuel_type")
    val fuelType: String,
    
    @Json(name = "transmission")
    val transmission: String,
    
    @Json(name = "location")
    val location: String
) 