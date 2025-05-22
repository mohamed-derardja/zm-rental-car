package com.example.myapplication.domain.model

data class Car(
    val id: String,
    val name: String,
    val brand: String,
    val image: String,
    val pricePerDay: Double,
    val seats: Int,
    val transmission: String,
    val fuelType: String,
    val description: String
) 