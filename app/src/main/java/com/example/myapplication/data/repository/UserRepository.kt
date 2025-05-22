package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Address
import com.example.myapplication.data.model.DrivingLicense
import com.example.myapplication.data.model.User
import okhttp3.MultipartBody
import java.io.File

/**
 * Repository interface for user-related operations.
 */
interface UserRepository {
    suspend fun getUserProfile(): User
    suspend fun updateProfile(name: String, email: String, phone: String): User
    suspend fun uploadProfileImage(imageFile: File): String
    suspend fun getUserById(id: Long): Result<User>
    suspend fun updateUser(id: Long, user: User): Result<User>
    suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String>
    suspend fun checkEmailExists(email: String): Result<Boolean>
    suspend fun updateAddress(id: Long, address: Address): Result<Address>
    suspend fun getAddress(id: Long): Result<Address>
    suspend fun updateDrivingLicense(id: Long, license: DrivingLicense): Result<DrivingLicense>
    suspend fun getDrivingLicense(id: Long): Result<DrivingLicense>
    suspend fun uploadDrivingLicenseImage(id: Long, image: MultipartBody.Part): Result<String>
    suspend fun getFavorites(id: Long): Result<List<Long>>
    suspend fun addToFavorites(id: Long, carId: Long): Result<List<Long>>
    suspend fun removeFromFavorites(id: Long, carId: Long): Result<List<Long>>
}