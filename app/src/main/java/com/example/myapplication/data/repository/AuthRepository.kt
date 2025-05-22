package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.User
import com.example.myapplication.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String, phone: String): Result<User>
    suspend fun loginWithFacebook(token: String): Result<User>
    suspend fun loginWithGoogle(token: String): Result<User>
    suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String>
    suspend fun checkEmailExists(email: String): Result<Boolean>
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun verifyPasswordReset(email: String, code: String, newPassword: String): Result<String>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Long?
    fun getAuthToken(): String?
    fun logout()
}

/**
 * Implementation of the AuthRepository interface.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(email, password)
            
            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true
            
            // Get the user profile
            val user = userRepository.getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String, phone: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(name, email, password, phone)
            
            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true
            
            // Get the user profile
            val user = userRepository.getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithFacebook(token: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.handleOAuth2Redirect(token, 0)

            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true

            // Get the user profile
            val user = userRepository.getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(token: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.handleOAuth2Redirect(token, 0)

            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true

            // Get the user profile
            val user = userRepository.getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val response = apiService.verifyEmail(userId, verificationCode, "Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkEmailExists(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.checkEmailExists(email)
            Result.success(response["exists"] ?: false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.requestPasswordReset(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPasswordReset(email: String, code: String, newPassword: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.verifyPasswordReset(email, code, newPassword)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            apiService.changePassword(currentPassword, newPassword, "Bearer $token")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val userId = preferenceManager.userId?.toLongOrNull() ?: return@withContext Result.failure(
                IllegalStateException("User ID not found")
            )
            
            apiService.deleteAccount(userId, "Bearer $token")
            logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return preferenceManager.isLoggedIn && preferenceManager.authToken != null
    }

    override fun getCurrentUserId(): Long? {
        return preferenceManager.userId?.toLongOrNull()
    }

    override fun getAuthToken(): String? {
        return preferenceManager.authToken
    }

    override fun logout() {
        preferenceManager.clear()
    }
}