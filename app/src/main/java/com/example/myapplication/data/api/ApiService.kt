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
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long, @Header("Authorization") token: String): User

    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): User

    // Facebook OAuth
    @GET("users/oauth2/redirect")
    suspend fun handleOAuth2Redirect(
        @Query("token") token: String, 
        @Query("userId") userId: Long
    ): AuthResponse

    @GET("users/oauth2/check-email")
    suspend fun checkEmailExists(@Query("email") email: String): Map<String, Boolean>

    @PUT("users/{id}")
    suspend fun updateUserProfile(
        @Path("id") id: Long,
        @Body request: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): User

    // Authentication
    @POST("users/login")
    suspend fun login(
        email: String,
        @Body request: String
    ): AuthResponse

    @POST("users/register")
    suspend fun register(
        name: String,
        email: String,
        password: String,
        @Body request: String
    ): AuthResponse

    @POST("users/{userId}/verify-email")
    suspend fun verifyEmail(
        @Path("userId") userId: Long,
        @Body request: String,
        @Header("Authorization") token: String
    ): String

    // Profile Management
    @Multipart
    @POST("users/me/avatar")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part,
        @Header("Authorization") token: String
    ): ImageUploadResponse

    @POST("users/me/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
        @Header("Authorization") token: String
    )

    @DELETE("users/{id}")
    suspend fun deleteAccount(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    )

    // Car Management
    @GET("cars")
    suspend fun getAllCars(): List<Car>

    @GET("cars/paged")
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

    @GET("cars/available/paged")
    suspend fun getAvailableCarsPaged(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("cars/brand/{brand}/paged")
    suspend fun getCarsByBrandPaged(
        @Path("brand") brand: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("cars/model/{model}/paged")
    suspend fun getCarsByModelPaged(
        @Path("model") model: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("cars/rating/paged")
    suspend fun getCarsByRatingRangePaged(
        @Query("minRating") minRating: Long,
        @Query("maxRating") maxRating: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "id"
    ): PagedResponse<Car>

    @GET("cars/{id}")
    suspend fun getCarById(@Path("id") id: Long): Car

    // User Car Browsing
    @GET("users/cars")
    suspend fun getAllAvailableCars(@Header("Authorization") token: String): List<Car>

    @GET("users/cars/brand/{brand}")
    suspend fun getCarsByBrand(
        @Path("brand") brand: String,
        @Header("Authorization") token: String
    ): List<Car>

    @GET("users/cars/model/{model}")
    suspend fun getCarsByModel(
        @Path("model") model: String,
        @Header("Authorization") token: String
    ): List<Car>

    @GET("users/cars/rating")
    suspend fun getCarsByRatingRange(
        @Query("minRating") minRating: Long,
        @Query("maxRating") maxRating: Long,
        @Header("Authorization") token: String
    ): List<Car>

    // Reservation Management
    @GET("reservations")
    suspend fun getAllReservations(@Header("Authorization") token: String): List<Reservation>

    @GET("reservations/{id}")
    suspend fun getReservationById(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Reservation

    @GET("reservations/user/{userId}")
    suspend fun getReservationsByUserId(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    @GET("reservations/car/{carId}")
    suspend fun getReservationsByCarId(
        @Path("carId") carId: Long,
        @Header("Authorization") token: String
    ): List<Reservation>

    @GET("reservations/status/{status}")
    suspend fun getReservationsByStatus(
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): List<Reservation>

    @POST("reservations")
    suspend fun createReservation(
        @Body reservation: Reservation,
        @Header("Authorization") token: String
    ): Reservation

    @PUT("reservations/{id}")
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

    // Password Reset
    @POST("users/password-reset/request")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequest): Unit

    @POST("users/password-reset/verify")
    suspend fun verifyPasswordReset(
        @Body request: PasswordResetVerifyRequest
    ): PasswordResetResponse

    // Address Management
    @PUT("users/{id}/address")
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
    @PUT("users/{id}/driving-license")
    suspend fun updateDrivingLicense(
        @Path("id") id: Long,
        @Body license: DrivingLicense,
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

    // Request/Response Data Classes
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val phone: String
    )

    data class AuthResponse(
        val id: Long,
        val token: String,
        val emailVerified: Boolean
    )

    data class UpdateProfileRequest(
        val name: String,
        val email: String,
        val phone: String
    )

    data class ChangePasswordRequest(
        val currentPassword: String,
        val newPassword: String
    )

    data class VerificationRequest(
        val verificationCode: String
    )

    data class NotificationPreferencesRequest(
        val enabled: Boolean
    )

    data class ThemePreferenceRequest(
        val theme: String
    )

    data class ImageUploadResponse(
        val url: String?
    )

    data class PagedResponse<T>(
        val content: List<T>,
        val pageable: Pageable,
        val totalElements: Long,
        val totalPages: Int,
        val last: Boolean,
        val size: Int,
        val number: Int,
        val sort: Sort,
        val numberOfElements: Int,
        val first: Boolean,
        val empty: Boolean
    )

    data class Pageable(
        val sort: Sort,
        val offset: Long,
        val pageNumber: Int,
        val pageSize: Int,
        val paged: Boolean,
        val unpaged: Boolean
    )

    data class Sort(
        val empty: Boolean,
        val sorted: Boolean,
        val unsorted: Boolean
    )

    data class PasswordResetRequest(
        val email: String
    )

    data class PasswordResetVerifyRequest(
        val email: String,
        val code: String,
        val newPassword: String
    )

    data class PasswordResetResponse(
        val success: Boolean,
        val message: String
    )
}
