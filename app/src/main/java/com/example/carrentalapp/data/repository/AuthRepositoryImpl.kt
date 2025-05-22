package com.example.carrentalapp.data.repository

import com.example.carrentalapp.data.api.AuthApiService
import com.example.carrentalapp.data.db.UserDao
import com.example.carrentalapp.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.get
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository that communicates with remote API and local database
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<Result<User>> = flow {
        try {
            // Create request body
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", password)
            }
            val requestBody = jsonObject.toString().toRequestBody(get("application/json"))
            
            // Call API
            val response = authApiService.login(requestBody)
            
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                // Save user to local database
                userDao.insertUser(user)
                emit(Result.success(user))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun register(email: String, password: String, name: String): Flow<Result<User>> = flow {
        try {
            // Create request body
            val jsonObject = JSONObject().apply {
                put("email", email)
                put("password", password)
                put("name", name)
            }
            val requestBody = jsonObject.toString().toRequestBody(get("application/json"))
            
            // Call API
            val response = authApiService.register(requestBody)
            
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                // Save user to local database
                userDao.insertUser(user)
                emit(Result.success(user))
            } else {
                emit(Result.failure(Exception("Registration failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getLoggedInUser()
    }

    override suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Flow<Result<Boolean>> = flow {
        try {
            // Create request body
            val jsonObject = JSONObject()
            updates.forEach { (key, value) ->
                jsonObject.put(key, value)
            }
            val requestBody = jsonObject.toString().toRequestBody(get("application/json"))
            
            // Call API
            val response = authApiService.updateUserProfile(userId, requestBody)
            
            if (response.isSuccessful) {
                // Update local user data
                val currentUser = userDao.getUserById(userId)
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        name = updates["name"]?.toString() ?: currentUser.name,
                        email = updates["email"]?.toString() ?: currentUser.email,
                        // Add other fields as needed
                    )
                    userDao.updateUser(updatedUser)
                }
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception("Profile update failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            // Clear local user data
            userDao.clearLoggedInUser()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 