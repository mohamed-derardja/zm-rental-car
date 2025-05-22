package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiResource
import com.example.myapplication.data.api.PagedResponse
import com.example.myapplication.data.model.Car
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for car-related operations.
 */
interface CarRepository {
    // Car Browsing - Basic Operations
    fun getAllCars(): Flow<ApiResource<List<Car>>>
    fun getCarById(id: Long): Flow<ApiResource<Car>>
    fun getCarsByBrand(brand: String): Flow<ApiResource<List<Car>>>
    fun getCarsByModel(model: String): Flow<ApiResource<List<Car>>>
    fun getCarsByRatingRange(minRating: Long, maxRating: Long): Flow<ApiResource<List<Car>>>
    
    // Paged Car Browsing
    fun getCarsPaged(
        page: Int, 
        size: Int, 
        sort: String, 
        direction: String, 
        brand: String?, 
        model: String?, 
        minRating: Long?, 
        maxRating: Long?, 
        rentalStatus: String?
    ): Flow<ApiResource<PagedResponse<Car>>>
    
    fun getAvailableCarsPaged(
        page: Int, 
        size: Int, 
        sort: String
    ): Flow<ApiResource<PagedResponse<Car>>>
    
    fun getCarsByBrandPaged(
        brand: String,
        page: Int, 
        size: Int, 
        sort: String
    ): Flow<ApiResource<PagedResponse<Car>>>
    
    fun getCarsByModelPaged(
        model: String,
        page: Int, 
        size: Int, 
        sort: String
    ): Flow<ApiResource<PagedResponse<Car>>>
    
    fun getCarsByRatingRangePaged(
        minRating: Long,
        maxRating: Long,
        page: Int, 
        size: Int, 
        sort: String
    ): Flow<ApiResource<PagedResponse<Car>>>

    /**
     * Get cars by type (SUV, Sedan, etc.)
     */
    fun getCarsByType(type: String): Flow<ApiResource<List<Car>>>
    
    /**
     * Get cars by type with pagination
     */
    fun getCarsByTypePaged(
        type: String,
        page: Int,
        size: Int,
        sort: String
    ): Flow<ApiResource<PagedResponse<Car>>>
} 