package com.example.myapplication.domain.repository

import com.example.myapplication.data.api.LoginRequest
import com.example.myapplication.data.api.RegisterRequest
import com.example.myapplication.data.api.ResetPasswordRequest
import com.example.myapplication.domain.model.Car
import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(request: RegisterRequest): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun updateUser(user: User): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun logout()
    
    fun getFavorites(): Flow<List<Car>>
    suspend fun addToFavorites(carId: String): Result<Unit>
    suspend fun removeFromFavorites(carId: String): Result<Unit>
    
    fun isLoggedIn(): Boolean
    fun getUserId(): String?
} 