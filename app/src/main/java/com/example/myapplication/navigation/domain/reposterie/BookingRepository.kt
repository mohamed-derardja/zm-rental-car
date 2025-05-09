package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getUpcomingBookings(): Flow<List<Booking>>
    fun getCompletedBookings(): Flow<List<Booking>>
    suspend fun createBooking(booking: Booking): Result<Booking>
    suspend fun cancelBooking(bookingId: String): Result<Unit>
} 