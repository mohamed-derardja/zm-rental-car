package com.example.myapplication.domain.model

import java.io.Serializable

data class Car(
    val id: String,
    val name: String,
    val brand: String,
    val model: String,
    val year: Int,
    val category: String,
    val imageUrl: String,
    val rating: Double,
    val transmission: String,
    val fuel: String,
    val seats: Int,
    val price: Double,
    val priceUnit: String = "DA/day",
    val description: String,
    val availability: Boolean = true,
    val features: List<String> = emptyList(),
    val location: String
) : Serializable 