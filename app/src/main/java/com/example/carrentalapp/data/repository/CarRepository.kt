package com.example.carrentalapp.data.repository

import com.example.carrentalapp.data.model.Car
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for car-related operations
 */
interface CarRepository {
    /**
     * Get all available cars
     * @return Flow containing list of all available cars
     */
    fun getAllCars(): Flow<List<Car>>
    
    /**
     * Get car details by ID
     * @param carId The unique identifier of the car
     * @return Flow containing the car details or null if not found
     */
    fun getCarById(carId: String): Flow<Car?>
    
    /**
     * Search for cars based on query parameters
     * @param query Search parameters (make, model, etc.)
     * @return Flow containing list of cars matching the query
     */
    fun searchCars(query: Map<String, Any>): Flow<List<Car>>
    
    /**
     * Update car availability status
     * @param carId The unique identifier of the car
     * @param isAvailable The new availability status
     * @return Result of the operation
     */
    suspend fun updateCarAvailability(carId: String, isAvailable: Boolean): Result<Boolean>
    
    /**
     * Add a new car to the system (admin function)
     * @param car The car details to add
     * @return Result containing the added car with assigned ID
     */
    suspend fun addCar(car: Car): Result<Car>
} 