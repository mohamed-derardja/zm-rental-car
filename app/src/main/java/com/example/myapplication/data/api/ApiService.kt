package com.example.myapplication.data.api

import com.example.myapplication.data.model.Address
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.model.DrivingLicense
import com.example.myapplication.data.model.Reservation
import com.example.myapplication.data.model.User
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    // User Profile
    @GET("https://3b9e-105-235-135-2.ngrok-free.app/users/{id}")
    suspend fun getUserById(@Path("id") id: Long, @Header("Authorization") token: String): User

    @GET("https://3b9e-105-235-135-2.ngrok-free.app/users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): User

    // Facebook OAuth
    @GET("https://3b9e-105-235-135-2.ngrok-free.app/users/oauth2/redirect")
    suspend fun handleOAuth2Redirect(
        @Query("token") token: String, 
        @Query("userId") userId: Long
    ): AuthResponse

    @GET("https://3b9e-105-235-135-2.ngrok-free.app/users/oauth2/check-email")
    suspend fun checkEmailExists(@Query("email") email: String): Map<String, Boolean>

    @PUT("https://3b9e-105-235-135-2.ngrok-free.app/users/{id}")
    suspend fun updateUserProfile(
        @Path("id") id: Long,
        @Body request: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): User

    // Authentication
    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): AuthResponse

    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/register")
    suspend fun register(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("phone") phone: String
    ): AuthResponse

    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/{userId}/verify-email")
    suspend fun verifyEmail(
        @Path("userId") userId: Long,
        @Query("code") code: String,
        @Header("Authorization") token: String
    ): String

    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/password-reset/request")
    suspend fun requestPasswordReset(@Query("email") email: String)

    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/password-reset/verify")
    suspend fun verifyPasswordReset(
        @Query("email") email: String,
        @Query("code") code: String,
        @Query("newPassword") newPassword: String
    ): PasswordResetResponse

    @POST("https://3b9e-105-235-135-2.ngrok-free.app/users/me/change-password")
    suspend fun changePassword(
        @Query("currentPassword") currentPassword: String,
        @Query("newPassword") newPassword: String,
        @Header("Authorization") token: String
    )

    @DELETE("tcp://7.tcp.eu.ngrok.io:17908/users/{id}")
    suspend fun deleteAccount(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    )

    // Profile Management
    @Multipart
    @POST("tcp://7.tcp.eu.ngrok.io:17908/users/me/avatar")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): ImageUploadResponse

    // Car Management
    @GET("cars")
    suspend fun getAllCars(): List<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/paged")
    suspend fun getAllCarsPaged(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id",
        @Query("direction") direction: String = "asc",
        @Query("brand") brand: String? = null,
        @Query("model") model: String? = null,
        @Query("minRating") minRating: Long? = null,
        @Query("maxRating") maxRating: Long? = null,
        @Query("rentalStatus") rentalStatus: String? = null
    ): PagedResponse<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/available/paged")
    suspend fun getAvailableCarsPaged(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/brand/{brand}/paged")
    suspend fun getCarsByBrandPaged(
        @Path("brand") brand: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/model/{model}/paged")
    suspend fun getCarsByModelPaged(
        @Path("model") model: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/rating/paged")
    suspend fun getCarsByRatingRangePaged(
        @Query("minRating") minRating: Long,
        @Query("maxRating") maxRating: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/cars/{id}")
    suspend fun getCarById(@Path("id") id: Long): Car

    // User Car Browsing
    @GET("tcp://7.tcp.eu.ngrok.io:17908/users/cars")
    suspend fun getAllAvailableCars(@Header("Authorization") token: String): List<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/users/cars/brand/{brand}")
    suspend fun getCarsByBrand(
        @Path("brand") brand: String,
        @Header("Authorization") token: String
    ): List<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/users/cars/model/{model}")
    suspend fun getCarsByModel(
        @Path("model") model: String,
        @Header("Authorization") token: String
    ): List<Car>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/users/cars/rating")
    suspend fun getCarsByRatingRange(
        @Query("minRating") minRating: Long,
        @Query("maxRating") maxRating: Long,
        @Header("Authorization") token: String
    ): List<Car>

    // Reservation Management
    @GET("tcp://7.tcp.eu.ngrok.io:17908/reservations")
    suspend fun getAllReservations(@Header("Authorization") token: String): List<Reservation>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/reservations/{id}")
    suspend fun getReservationById(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Reservation

    @GET("tcp://7.tcp.eu.ngrok.io:17908/reservations/user/{userId}")
    suspend fun getReservationsByUserId(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/reservations/car/{carId}")
    suspend fun getReservationsByCarId(
        @Path("carId") carId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    @GET("tcp://7.tcp.eu.ngrok.io:17908/reservations/status/{status}")
    suspend fun getReservationsByStatus(
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): List<Reservation>

    @POST("tcp://7.tcp.eu.ngrok.io:17908/reservations")
    suspend fun createReservation(
        @Body reservation: Reservation,
        @Header("Authorization") token: String
    ): Reservation

    @PUT("tcp://7.tcp.eu.ngrok.io:17908/reservations/{id}")
    suspend fun updateReservation(
        @Path("id") id: Long,
        @Body reservation: Reservation,
        @Header("Authorization") token: String
    ): Reservation

    @PATCH("reservations/{id}/status")
    suspend fun updateReservationStatus(
        @Path("id") id: Long,
        @Body status: String,
        @Header("Authorization") token: String
    ): Reservation

    @POST("users/reservations")
    suspend fun createUserReservation(
        @Body reservation: Reservation,
        @Header("Authorization") token: String
    ): Reservation

    @POST("users/reservations/{id}/cancel")
    suspend fun cancelReservation(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Reservation

    @GET("users/{userId}/reservations/upcoming")
    suspend fun getUpcomingReservations(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    @GET("users/{userId}/reservations/past")
    suspend fun getPastReservations(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    // Address Management
    @PUT("https://3b9e-105-235-135-2.ngrok-free.app/users/{id}/address")
    suspend fun updateAddress(
        @Path("id") id: Long,
        @Body address: Address,
        @Header("Authorization") token: String
    ): Address

    @GET("users/{id}/address")
    suspend fun getAddress(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Address

    // Driving License Management
    @PUT("https://3b9e-105-235-135-2.ngrok-free.app/users/{id}/driving-license")
    suspend fun updateDrivingLicense(
        @Path("id") id: Long,
        @Body drivingLicense: DrivingLicense,
        @Header("Authorization") token: String
    ): DrivingLicense

    @GET("users/{id}/driving-license")
    suspend fun getDrivingLicense(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): DrivingLicense

    @Multipart
    @POST("users/{id}/driving-license/image")
    suspend fun uploadDrivingLicenseImage(
        @Path("id") id: Long,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): ImageUploadResponse

    // Favorites Management
    @GET("users/{id}/favorites")
    suspend fun getFavorites(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): List<Long>

    @POST("users/{id}/favorites/{carId}")
    suspend fun addToFavorites(
        @Path("id") id: Long,
        @Path("carId") carId: Long,
        @Header("Authorization") token: String
    ): List<Long>

    @DELETE("users/{id}/favorites/{carId}")
    suspend fun removeFromFavorites(
        @Path("id") id: Long,
        @Path("carId") carId: Long,
        @Header("Authorization") token: String
    ): List<Long>
} 