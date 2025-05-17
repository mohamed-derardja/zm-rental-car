package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.domain.model.Booking
import com.example.myapplication.domain.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BookingRepositoryImpl(private val userId: String) : BookingRepository {
    
    private val apiService = ApiClient.apiService
    
    override fun getUpcomingBookings(): Flow<List<Booking>> = flow {
        try {
            val response = apiService.getUpcomingBookings(userId)
            if (response.isSuccessful) {
                response.body()?.let { bookings ->
                    emit(bookings)
                } ?: emit(emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getCompletedBookings(): Flow<List<Booking>> = flow {
        try {
            val response = apiService.getCompletedBookings(userId)
            if (response.isSuccessful) {
                response.body()?.let { bookings ->
                    emit(bookings)
                } ?: emit(emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun createBooking(booking: Booking): Result<Booking> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createBooking(booking)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext Result.success(it)
                } ?: return@withContext Result.failure(Exception("Empty response body"))
            } else {
                return@withContext Result.failure(Exception("HTTP error: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun cancelBooking(bookingId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.cancelBooking(bookingId)
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("HTTP error: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
} 