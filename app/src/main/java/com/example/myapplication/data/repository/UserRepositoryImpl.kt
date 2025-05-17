package com.example.myapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.data.api.LoginRequest
import com.example.myapplication.data.api.RegisterRequest
import com.example.myapplication.data.api.ResetPasswordRequest
import com.example.myapplication.domain.model.Car
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val context: Context) : UserRepository {
    
    private val apiService = ApiClient.apiService
    private val gson = Gson()
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("zm_rental_prefs", Context.MODE_PRIVATE)
    }
    
    override suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val loginRequest = LoginRequest(email, password)
            val response = apiService.login(loginRequest)
            
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    // Save token and user to shared preferences
                    saveToken(loginResponse.token)
                    saveUser(loginResponse.user)
                    
                    // Update ApiClient with new token
                    ApiClient.setToken(loginResponse.token)
                    
                    return@withContext Result.success(loginResponse.user)
                } ?: return@withContext Result.failure(Exception("Empty response body"))
            } else {
                return@withContext Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun register(request: RegisterRequest): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(request)
            
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    // Save token and user to shared preferences
                    saveToken(registerResponse.token)
                    saveUser(registerResponse.user)
                    
                    // Update ApiClient with new token
                    ApiClient.setToken(registerResponse.token)
                    
                    return@withContext Result.success(registerResponse.user)
                } ?: return@withContext Result.failure(Exception("Empty response body"))
            } else {
                return@withContext Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(USER_KEY, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }
    
    override suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            val response = apiService.updateUser(userId, user)
            
            if (response.isSuccessful) {
                response.body()?.let { updatedUser ->
                    // Update the saved user
                    saveUser(updatedUser)
                    return@withContext Result.success(updatedUser)
                } ?: return@withContext Result.failure(Exception("Empty response body"))
            } else {
                return@withContext Result.failure(Exception("Update failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ResetPasswordRequest(email)
            val response = apiService.resetPassword(request)
            
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Password reset failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        // Clear token from ApiClient
        ApiClient.clearToken()
        
        // Clear shared preferences
        sharedPreferences.edit().apply {
            remove(TOKEN_KEY)
            remove(USER_KEY)
            apply()
        }
    }
    
    override fun getFavorites(): Flow<List<Car>> = flow {
        try {
            val userId = getUserId()
            if (userId != null) {
                val response = apiService.getUserFavorites(userId)
                if (response.isSuccessful) {
                    response.body()?.let { cars ->
                        emit(cars)
                    } ?: emit(emptyList())
                } else {
                    emit(emptyList())
                }
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun addToFavorites(carId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            val response = apiService.addToFavorites(userId, carId)
            
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Add to favorites failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override suspend fun removeFromFavorites(carId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId() ?: return@withContext Result.failure(Exception("User not logged in"))
            val response = apiService.removeFromFavorites(userId, carId)
            
            if (response.isSuccessful) {
                return@withContext Result.success(Unit)
            } else {
                return@withContext Result.failure(Exception("Remove from favorites failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
    
    override fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
    
    override fun getUserId(): String? {
        val user = getCurrentUser() ?: return null
        return user.id
    }
    
    // Helper methods
    private fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }
    
    private fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }
    
    private fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(USER_KEY, userJson).apply()
    }
    
    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_KEY = "current_user"
    }
} 