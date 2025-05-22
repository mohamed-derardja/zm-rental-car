package com.example.myapplication.data.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class representing a car reservation in the system.
 */
data class Reservation(
    val id: Long = 0,
    val userId: Long = 0,
    val carId: Long = 0,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(1),
    val status: String = "PENDING", // PENDING, CONFIRMED, CANCELLED, COMPLETED
    val totalPrice: Double = 0.0,
    val paymentStatus: String = "UNPAID", // UNPAID, PAID, REFUNDED
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val car: Car? = null,
    val user: User? = null
)