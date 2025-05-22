package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiResource
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.api.ApiStatus
import com.example.myapplication.data.model.Reservation
import com.example.myapplication.data.preference.AuthPreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ReservationRepository interface.
 */
@Singleton
class ReservationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authPreferenceManager: AuthPreferenceManager
) : ReservationRepository {

    override fun getAllReservations(): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val reservations = apiService.getAllReservations("Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load reservations"))
        }
    }

    override fun getReservationById(id: Long): Flow<ApiResource<Reservation>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val reservation = apiService.getReservationById(id, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservation))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load reservation details"))
        }
    }

    override fun getUserReservations(): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val userId = authPreferenceManager.getUserId()?.toLongOrNull() ?: throw IllegalStateException("User ID not found")
            val reservations = apiService.getReservationsByUserId(userId, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load user reservations"))
        }
    }

    override fun getReservationsByCarId(carId: Long): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val reservations = apiService.getReservationsByCarId(carId, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load car reservations"))
        }
    }

    override fun getReservationsByStatus(status: String): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val reservations = apiService.getReservationsByStatus(status, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load reservations by status"))
        }
    }

    override fun createReservation(
        carId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        totalPrice: Double
    ): Flow<ApiResource<Reservation>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val userId = authPreferenceManager.getUserId()?.toLongOrNull() ?: throw IllegalStateException("User ID not found")
            
            // Create a reservation object
            val reservation = Reservation(
                userId = userId,
                carId = carId,
                startDate = startDate,
                endDate = endDate,
                totalPrice = totalPrice,
                status = "PENDING",
                paymentStatus = "UNPAID"
            )
            
            val createdReservation = apiService.createUserReservation(reservation, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = createdReservation))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to create reservation"))
        }
    }

    override fun updateReservation(
        reservationId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        totalPrice: Double
    ): Flow<ApiResource<Reservation>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            
            // First get the existing reservation
            val existingReservation = apiService.getReservationById(reservationId, "Bearer $token")
            
            // Update the reservation
            val updatedReservation = existingReservation.copy(
                startDate = startDate,
                endDate = endDate,
                totalPrice = totalPrice
            )
            
            val result = apiService.updateReservation(reservationId, updatedReservation, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = result))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to update reservation"))
        }
    }

    override fun updateReservationStatus(
        reservationId: Long,
        status: String
    ): Flow<ApiResource<Reservation>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val result = apiService.updateReservationStatus(reservationId, status, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = result))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to update reservation status"))
        }
    }

    override fun cancelReservation(reservationId: Long): Flow<ApiResource<Boolean>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            apiService.cancelReservation(reservationId, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = true))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to cancel reservation"))
        }
    }

    override fun getUpcomingReservations(): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val userId = authPreferenceManager.getUserId()?.toLongOrNull() ?: throw IllegalStateException("User ID not found")
            val reservations = apiService.getUpcomingReservations(userId, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load upcoming reservations"))
        }
    }

    override fun getPastReservations(): Flow<ApiResource<List<Reservation>>> = flow {
        emit(ApiResource(status = ApiStatus.LOADING))
        try {
            val token = authPreferenceManager.getAuthToken() ?: throw IllegalStateException("Not authenticated")
            val userId = authPreferenceManager.getUserId()?.toLongOrNull() ?: throw IllegalStateException("User ID not found")
            val reservations = apiService.getPastReservations(userId, "Bearer $token")
            emit(ApiResource(status = ApiStatus.SUCCESS, data = reservations))
        } catch (e: Exception) {
            emit(ApiResource(status = ApiStatus.ERROR, message = e.message ?: "Failed to load past reservations"))
        }
    }
} 