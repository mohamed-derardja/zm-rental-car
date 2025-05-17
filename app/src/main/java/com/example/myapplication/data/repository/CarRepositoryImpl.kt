package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.domain.model.Car
import com.example.myapplication.domain.repository.CarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CarRepositoryImpl : CarRepository {
    
    private val apiService = ApiClient.apiService
    
    override fun getAllCars(): Flow<List<Car>> = flow {
        try {
            val response = apiService.getAllCars()
            if (response.isSuccessful) {
                response.body()?.let { cars ->
                    emit(cars)
                } ?: emit(emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun getCarById(id: String): Car? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCarById(id)
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            return@withContext null
        }
    }
    
    override suspend fun searchCars(query: String): List<Car> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchCars(query)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }
} 