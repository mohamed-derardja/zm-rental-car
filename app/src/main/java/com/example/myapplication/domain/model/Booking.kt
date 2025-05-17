package com.example.myapplication.domain.model

import java.io.Serializable
import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val carId: String,
    val startDate: Date,
    val endDate: Date,
    val totalPrice: Double,
    val status: BookingStatus,
    val paymentStatus: PaymentStatus,
    val createdAt: Date,
    val updatedAt: Date,
    // These properties can be populated by the app after fetching the booking
    var car: Car? = null,
    var user: User? = null
) : Serializable

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class PaymentStatus {
    PENDING,
    PAID,
    REFUNDED,
    FAILED
} 