package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Car
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getAllCars(): Flow<List<Car>>
    suspend fun getCarById(id: String): Car?
    suspend fun searchCars(query: String): List<Car>
} 