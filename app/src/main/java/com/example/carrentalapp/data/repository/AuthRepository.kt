package com.example.carrentalapp.data.repository

import com.example.carrentalapp.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication-related operations
 */
interface AuthRepository {
    /**
     * Authenticate user with email and password
     * @param email User's email address
     * @param password User's password
     * @return Flow containing result of the operation with User data or error
     */
    suspend fun login(email: String, password: String): Flow<Result<User>>
    
    /**
     * Register new user
     * @param email User's email address
     * @param password User's password
     * @param name User's display name
     * @return Flow containing result of the operation with User data or error
     */
    suspend fun register(email: String, password: String, name: String): Flow<Result<User>>
    
    /**
     * Get current authenticated user
     * @return Flow containing the current user or null if not authenticated
     */
    fun getCurrentUser(): Flow<User?>
    
    /**
     * Update user profile information
     * @param userId User identifier
     * @param updates Map of fields to update with their new values
     * @return Flow containing result of the operation
     */
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Flow<Result<Boolean>>
    
    /**
     * Sign out current user
     * @return Result of the operation
     */
    suspend fun logout(): Result<Boolean>
} 