package com.example.carrentalapp.data.repository

import com.example.carrentalapp.data.model.Booking
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for booking-related operations
 */
interface BookingRepository {
    /**
     * Create a new booking
     * @param userId The user making the booking
     * @param carId The car being booked
     * @param startDate Start date of the rental
     * @param endDate End date of the rental
     * @param totalPrice Total price of the booking
     * @return Result containing the created booking or error
     */
    suspend fun createBooking(
        userId: String,
        carId: String,
        startDate: Date,
        endDate: Date,
        totalPrice: Double
    ): Result<Booking>
    
    /**
     * Get all bookings for a user
     * @param userId The user ID
     * @return Flow containing list of bookings for the user
     */
    fun getUserBookings(userId: String): Flow<List<Booking>>
    
    /**
     * Get booking details by ID
     * @param bookingId The booking ID
     * @return Flow containing the booking details or null if not found
     */
    fun getBookingById(bookingId: String): Flow<Booking?>
    
    /**
     * Cancel a booking
     * @param bookingId The booking ID
     * @return Result of the cancellation operation
     */
    suspend fun cancelBooking(bookingId: String): Result<Boolean>
    
    /**
     * Update booking status
     * @param bookingId The booking ID
     * @param status The new status
     * @return Result of the update operation
     */
    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Boolean>
} 