package com.example.myapplication.domain.model

import java.time.LocalDateTime

data class Booking(
    val id: String,
    val carId: String,
    val userId: String,
    val pickupDateTime: LocalDateTime,
    val dropoffDateTime: LocalDateTime,
    val isWithDriver: Boolean,
    val status: BookingStatus,
    val totalPrice: Double
)

enum class BookingStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
} 