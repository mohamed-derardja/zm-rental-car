package com.example.myapplication.data.api

import com.example.myapplication.domain.model.Booking
import com.example.myapplication.domain.model.Car
import com.example.myapplication.domain.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Car endpoints
    @GET("cars")
    suspend fun getAllCars(): Response<List<Car>>
    
    @GET("cars/{id}")
    suspend fun getCarById(@Path("id") id: String): Response<Car>
    
    @GET("cars/search")
    suspend fun searchCars(@Query("query") query: String): Response<List<Car>>
    
    // User endpoints
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>
    
    @POST("users")
    suspend fun createUser(@Body user: User): Response<User>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<User>
    
    @GET("users/{id}/favorites")
    suspend fun getUserFavorites(@Path("id") id: String): Response<List<Car>>
    
    @POST("users/{id}/favorites/{carId}")
    suspend fun addToFavorites(@Path("id") id: String, @Path("carId") carId: String): Response<Unit>
    
    @DELETE("users/{id}/favorites/{carId}")
    suspend fun removeFromFavorites(@Path("id") id: String, @Path("carId") carId: String): Response<Unit>
    
    // Authentication endpoints
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/password-reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
    
    // Booking endpoints
    @GET("bookings/user/{userId}")
    suspend fun getUserBookings(@Path("userId") userId: String): Response<List<Booking>>
    
    @GET("bookings/{id}")
    suspend fun getBookingById(@Path("id") id: String): Response<Booking>
    
    @POST("bookings")
    suspend fun createBooking(@Body booking: Booking): Response<Booking>
    
    @PUT("bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String): Response<Booking>
    
    @GET("bookings/user/{userId}/upcoming")
    suspend fun getUpcomingBookings(@Path("userId") userId: String): Response<List<Booking>>
    
    @GET("bookings/user/{userId}/completed")
    suspend fun getCompletedBookings(@Path("userId") userId: String): Response<List<Booking>>
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String
)

data class RegisterResponse(
    val token: String,
    val user: User
)

data class ResetPasswordRequest(
    val email: String
) 