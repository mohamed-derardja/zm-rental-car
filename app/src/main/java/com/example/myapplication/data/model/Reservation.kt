package com.example.myapplication.data.model

import java.math.BigDecimal

/**
 * Data class representing a reservation in the rental system.
 * This model corresponds to the Reservation entity in the backend.
 */
data class Reservation(
    val id: Long = 0,
    val startDate: String = "",
    val endDate: String = "",
    val carId: Long = 0,
    val userId: Long = 0,
    val driverId: Long? = null,
    val selfDrive: Boolean = false,
    val status: String = "Pending",
    val fee: BigDecimal = BigDecimal.ZERO,
    val cancellationFee: BigDecimal? = null,
    val paymentMethod: PaymentMethodType? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    
    // Nested objects for detailed views
    val car: Car? = null,
    val user: User? = null
)