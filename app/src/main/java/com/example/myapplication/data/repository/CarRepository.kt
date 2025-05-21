package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.Car
import com.example.myapplication.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for car-related operations.
 */
interface CarRepository {
    // Car Browsing
    suspend fun getAllCars(): Result<List<Car>>
    suspend fun getCarById(id: Long): Result<Car>
    suspend fun getCarsByBrand(brand: String): Result<List<Car>>
    suspend fun getCarsByModel(model: String): Result<List<Car>>
    suspend fun getCarsByRatingRange(minRating: Long, maxRating: Long): Result<List<Car>>
    
    // Paged Car Browsing
    suspend fun getAllCarsPaged(
        page: Int = 0,
        size: Int = 10,
        sort: String = "id",
        direction: String = "asc",
        brand: String? = null,
        model: String? = null,
        minRating: Long? = null,
        maxRating: Long? = null,
        rentalStatus: String? = null
    ): Result<ApiService.PagedResponse<Car>>
    
    suspend fun getAvailableCarsPaged(
        page: Int = 0,
        size: Int = 10,
        sort: String = "id"
    ): Result<ApiService.PagedResponse<Car>>
    
    suspend fun getCarsByBrandPaged(
        brand: String,
        page: Int = 0,
        size: Int = 10,
        sort: String = "id"
    ): Result<ApiService.PagedResponse<Car>>
    
    suspend fun getCarsByModelPaged(
        model: String,
        page: Int = 0,
        size: Int = 10,
        sort: String = "id"
    ): Result<ApiService.PagedResponse<Car>>
    
    suspend fun getCarsByRatingRangePaged(
        minRating: Long,
        maxRating: Long,
        page: Int = 0,
        size: Int = 10,
        sort: String = "id"
    ): Result<ApiService.PagedResponse<Car>>
}

/**
 * Implementation of the CarRepository interface.
 */
@Singleton
class CarRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : CarRepository {
    
    // Car Browsing
    override suspend fun getAllCars(): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getAllCars()
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarById(id: Long): Result<Car> = withContext(Dispatchers.IO) {
        try {
            val car = apiService.getCarById(id)
            Result.success(car)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByBrand(brand: String): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val cars = apiService.getCarsByBrand(brand, "Bearer $token")
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByModel(model: String): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val cars = apiService.getCarsByModel(model, "Bearer $token")
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByRatingRange(minRating: Long, maxRating: Long): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val cars = apiService.getCarsByRatingRange(minRating, maxRating, "Bearer $token")
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Paged Car Browsing
    override suspend fun getAllCarsPaged(
        page: Int,
        size: Int,
        sort: String,
        direction: String,
        brand: String?,
        model: String?,
        minRating: Long?,
        maxRating: Long?,
        rentalStatus: String?
    ): Result<ApiService.PagedResponse<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getAllCarsPaged(
                page, size, sort, direction, brand, model, minRating, maxRating, rentalStatus
            )
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAvailableCarsPaged(
        page: Int,
        size: Int,
        sort: String
    ): Result<ApiService.PagedResponse<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getAvailableCarsPaged(page, size, sort)
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByBrandPaged(
        brand: String,
        page: Int,
        size: Int,
        sort: String
    ): Result<ApiService.PagedResponse<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getCarsByBrandPaged(brand, page, size, sort)
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByModelPaged(
        model: String,
        page: Int,
        size: Int,
        sort: String
    ): Result<ApiService.PagedResponse<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getCarsByModelPaged(model, page, size, sort)
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCarsByRatingRangePaged(
        minRating: Long,
        maxRating: Long,
        page: Int,
        size: Int,
        sort: String
    ): Result<ApiService.PagedResponse<Car>> = withContext(Dispatchers.IO) {
        try {
            val cars = apiService.getCarsByRatingRangePaged(minRating, maxRating, page, size, sort)
            Result.success(cars)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}