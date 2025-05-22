package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiResource
import com.example.myapplication.data.model.Reservation
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for reservation-related operations.
 */
interface ReservationRepository {
    /**
     * Get all reservations (admin function)
     */
    fun getAllReservations(): Flow<ApiResource<List<Reservation>>>
    
    /**
     * Get reservation by ID
     */
    fun getReservationById(id: Long): Flow<ApiResource<Reservation>>
    
    /**
     * Get reservations for current user
     */
    fun getUserReservations(): Flow<ApiResource<List<Reservation>>>
    
    /**
     * Get reservations for a specific car
     */
    fun getReservationsByCarId(carId: Long): Flow<ApiResource<List<Reservation>>>
    
    /**
     * Get reservations by status
     */
    fun getReservationsByStatus(status: String): Flow<ApiResource<List<Reservation>>>
    
    /**
     * Create a new reservation
     */
    fun createReservation(
        carId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        totalPrice: Double
    ): Flow<ApiResource<Reservation>>
    
    /**
     * Update an existing reservation
     */
    fun updateReservation(
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        totalPrice: Double
    ): Flow<ApiResource<Reservation>>
    
    /**
     * Update reservation status
     */
    fun updateReservationStatus(
        reservationId: Long,
        status: String
    ): Flow<ApiResource<Reservation>>
    
    /**
     * Cancel a reservation
     */
    fun cancelReservation(reservationId: Long): Flow<ApiResource<Boolean>>
    
    /**
     * Get upcoming reservations for the current user
     */
    fun getUpcomingReservations(): Flow<ApiResource<List<Reservation>>>
    
    /**
     * Get past reservations for the current user
     */
    fun getPastReservations(): Flow<ApiResource<List<Reservation>>>
} 