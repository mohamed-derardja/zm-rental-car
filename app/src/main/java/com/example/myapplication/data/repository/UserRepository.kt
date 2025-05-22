package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiResource
import com.example.myapplication.data.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

/**
 * Repository interface for user-related operations.
 */
interface UserRepository {
    /**
     * Get current user profile
     */
    fun getCurrentUser(): Flow<ApiResource<User>>
    
    /**
     * Update user profile
     */
    fun updateProfile(name: String, email: String, phone: String): Flow<ApiResource<User>>
    
    /**
     * Change user password
     */
    fun changePassword(currentPassword: String, newPassword: String): Flow<ApiResource<Boolean>>
    
    /**
     * Upload profile image
     */
    fun uploadProfileImage(image: MultipartBody.Part): Flow<ApiResource<String>>
    
    /**
     * Delete user account
     */
    fun deleteAccount(): Flow<ApiResource<Boolean>>
} 