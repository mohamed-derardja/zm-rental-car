package com.example.carrentalapp.data.repository

import com.example.carrentalapp.data.api.CarApiService
import com.example.carrentalapp.data.db.CarDao
import com.example.carrentalapp.data.model.Car
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.get
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CarRepository that communicates with remote API and local database
 */
@Singleton
class CarRepositoryImpl @Inject constructor(
    private val carApiService: CarApiService,
    private val carDao: CarDao
) : CarRepository {

    override fun getAllCars(): Flow<List<Car>> {
        return carDao.getAllCars()
    }

    override fun getCarById(carId: String): Flow<Car?> {
        return carDao.getCarById(carId)
    }

    override fun searchCars(query: Map<String, Any>): Flow<List<Car>> {
        // This implementation searches the local database
        // You could extend this to call the API with search parameters
        
        return when {
            query.containsKey("make") -> {
                val make = query["make"] as String
                carDao.searchCarsByMake(make)
            }
            query.containsKey("model") -> {
                val model = query["model"] as String
                carDao.searchCarsByModel(model)
            }
            query.containsKey("category") -> {
                val category = query["category"] as String
                carDao.searchCarsByCategory(category)
            }
            query.containsKey("priceMax") -> {
                val maxPrice = query["priceMax"] as Double
                carDao.searchCarsByMaxPrice(maxPrice)
            }
            else -> carDao.getAllCars()
        }
    }

    override suspend fun updateCarAvailability(carId: String, isAvailable: Boolean): Result<Boolean> = try {
        // Create request body
        val jsonObject = JSONObject().apply {
            put("available", isAvailable)
        }
        val requestBody = jsonObject.toString().toRequestBody(get("application/json"))
        
        // Call API
        val response = carApiService.updateCarAvailability(carId, requestBody)
        
        if (response.isSuccessful) {
            // Update local database
            val car = carDao.getCarByIdSync(carId)
            if (car != null) {
                val updatedCar = car.copy(isAvailable = isAvailable)
                carDao.updateCar(updatedCar)
            }
            Result.success(true)
        } else {
            Result.failure(Exception("Failed to update availability: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun addCar(car: Car): Result<Car> = try {
        // Call API to add car
        val response = carApiService.addCar(car)
        
        if (response.isSuccessful && response.body() != null) {
            val addedCar = response.body()!!
            // Save to local database
            carDao.insertCar(addedCar)
            Result.success(addedCar)
        } else {
            Result.failure(Exception("Failed to add car: ${response.message()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    // Method to refresh the local car database from the remote API
    suspend fun refreshCars() {
        try {
            val cars = carApiService.getAllCars().body() ?: emptyList()
            carDao.deleteAllCars()
            carDao.insertAllCars(cars)
        } catch (e: Exception) {
            // Handle error, perhaps log it or notify the user
        }
    }
} 