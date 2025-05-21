package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.Reservation
import com.example.myapplication.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for reservation-related operations.
 */
interface ReservationRepository {
    // Reservation Management
    suspend fun getAllReservations(): Result<List<Reservation>>
    suspend fun getReservationById(id: Long): Result<Reservation>
    suspend fun getReservationsByUserId(userId: Long): Result<List<Reservation>>
    suspend fun getReservationsByCarId(carId: Long): Result<List<Reservation>>
    suspend fun getReservationsByStatus(status: String): Result<List<Reservation>>
    suspend fun createReservation(reservation: Reservation): Result<Reservation>
    suspend fun updateReservation(id: Long, reservation: Reservation): Result<Reservation>
    suspend fun updateReservationStatus(id: Long, status: String): Result<Reservation>
    suspend fun cancelReservation(id: Long): Result<Reservation>
    suspend fun getUpcomingReservations(userId: Long): Result<List<Reservation>>
    suspend fun getPastReservations(userId: Long): Result<List<Reservation>>
}

/**
 * Implementation of the ReservationRepository interface.
 */
@Singleton
class ReservationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : ReservationRepository {
    
    override suspend fun getAllReservations(): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getAllReservations("Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReservationById(id: Long): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservation = apiService.getReservationById(id, "Bearer $token")
            Result.success(reservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReservationsByUserId(userId: Long): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getReservationsByUserId(userId, "Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReservationsByCarId(carId: Long): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getReservationsByCarId(carId, "Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getReservationsByStatus(status: String): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getReservationsByStatus(status, "Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createReservation(reservation: Reservation): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val createdReservation = apiService.createUserReservation(reservation, "Bearer $token")
            Result.success(createdReservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateReservation(id: Long, reservation: Reservation): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val updatedReservation = apiService.updateReservation(id, reservation, "Bearer $token")
            Result.success(updatedReservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateReservationStatus(id: Long, status: String): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val updatedReservation = apiService.updateReservationStatus(id, status, "Bearer $token")
            Result.success(updatedReservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelReservation(id: Long): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val cancelledReservation = apiService.cancelReservation(id, "Bearer $token")
            Result.success(cancelledReservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUpcomingReservations(userId: Long): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getUpcomingReservations(userId, "Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPastReservations(userId: Long): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val reservations = apiService.getPastReservations(userId, "Bearer $token")
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}