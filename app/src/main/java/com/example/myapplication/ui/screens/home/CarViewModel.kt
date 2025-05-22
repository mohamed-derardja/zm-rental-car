package com.example.myapplication.ui.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.ApiStatus
import com.example.myapplication.data.api.PagedResponse
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CarViewModel"

/**
 * Sealed class representing different UI states for car data.
 */
sealed class CarUiState {
    object Loading : CarUiState()
    data class Success(val cars: List<Car>) : CarUiState()
    data class PaginatedSuccess(val pagedResponse: PagedResponse<Car>) : CarUiState()
    data class SingleCarSuccess(val car: Car) : CarUiState()
    data class Error(val message: String) : CarUiState()
    object Idle : CarUiState()
}

/**
 * ViewModel for car-related operations.
 */
@HiltViewModel
class CarViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {
    
    // Car list UI state
    private val _uiState = MutableStateFlow<CarUiState>(CarUiState.Idle)
    val uiState: StateFlow<CarUiState> = _uiState
    
    // Car details UI state
    private val _carDetailsState = MutableStateFlow<CarUiState>(CarUiState.Idle)
    val carDetailsState: StateFlow<CarUiState> = _carDetailsState
    
    // Pagination
    private var currentPage = 0
    private var totalPages = 1
    private var isLastPage = false
    private val pageSize = 10
    
    // Filters
    private var brandFilter: String? = null
    private var modelFilter: String? = null
    private var minRatingFilter: Long? = null
    private var maxRatingFilter: Long? = null
    
    init {
        loadAllCars()
    }
    
    /**
     * Load cars with pagination.
     */
    private fun loadCarsPaged(page: Int, resetFilters: Boolean = true) {
        if (resetFilters) {
            brandFilter = null
            modelFilter = null
            minRatingFilter = null
            maxRatingFilter = null
        }
        
        currentPage = page
        _uiState.value = CarUiState.Loading
        
        if (brandFilter != null) {
            brandFilter?.let { filterByBrand(it, page) }
        } else if (modelFilter != null) {
            modelFilter?.let { filterByModel(it, page) }
        } else if (minRatingFilter != null && maxRatingFilter != null) {
            filterByRatingRange(minRatingFilter!!, maxRatingFilter!!, page)
        } else {
            // Load all cars or apply available filters
            loadCarsWithFilters(
                page = page,
                size = pageSize,
                brand = brandFilter,
                model = modelFilter,
                minRating = minRatingFilter,
                maxRating = maxRatingFilter
            )
        }
    }
    
    /**
     * Debug function to print all car types
     */
    private fun debugPrintAllCarTypes() {
        viewModelScope.launch {
            carRepository.getAllCars().collectLatest { result ->
                if (result.status == ApiStatus.SUCCESS) {
                    result.data?.let { cars ->
                        val types = cars.map { it.type }.distinct().sorted()
                        Log.d(TAG, "All available car types: $types")
                        cars.forEach { car ->
                            Log.d(TAG, "Car ${car.id}: ${car.brand} ${car.model}, Type: '${car.type}'")
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Load all cars from the repository.
     */
    fun loadAllCars() {
        _uiState.value = CarUiState.Loading
        
        // Debug: print all car types
        debugPrintAllCarTypes()
        
        viewModelScope.launch {
            carRepository.getAllCars().collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { cars ->
                            _uiState.value = CarUiState.Success(cars)
                            Log.d(TAG, "Loaded ${cars.size} cars")
                        } ?: run {
                            _uiState.value = CarUiState.Error("No cars found")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to load cars")
                        Log.e(TAG, "Error loading cars: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Load car details by ID.
     */
    fun loadCarById(carId: Long) {
        _carDetailsState.value = CarUiState.Loading
        
        viewModelScope.launch {
            carRepository.getCarById(carId).collectLatest { result ->
                _carDetailsState.value = when (result.status) {
                    ApiStatus.SUCCESS -> {
                        val car = result.data
                        if (car != null) {
                            CarUiState.Success(listOf(car))
                        } else {
                            CarUiState.Error("Car not found")
                        }
                    }
                    ApiStatus.ERROR -> {
                        CarUiState.Error(result.message ?: "Failed to load car details")
                    }
                    ApiStatus.LOADING -> {
                        CarUiState.Loading
                    }
                }
            }
        }
    }
    
    /**
     * Load the next page of cars.
     */
    fun loadNextPage() {
        if (!isLastPage) {
            currentPage++
            loadCarsPaged(currentPage, false)
        }
    }
    
    /**
     * Load the previous page of cars.
     */
    fun loadPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            loadCarsPaged(currentPage, false)
        }
    }
    
    /**
     * Get the current page number.
     */
    fun getCurrentPage(): Int = currentPage
    
    /**
     * Get the total number of pages.
     */
    fun getTotalPages(): Int = totalPages
    
    /**
     * Check if this is the last page.
     */
    fun isLastPage(): Boolean = isLastPage
    
    /**
     * Load cars with filters.
     */
    fun loadCarsWithFilters(
        page: Int = 0,
        size: Int = 10,
        brand: String? = null,
        model: String? = null,
        minRating: Long? = null,
        maxRating: Long? = null
    ) {
        _uiState.value = CarUiState.Loading
        
        Log.d(TAG, "Loading cars with filters - brand: $brand, model/type: $model, rating: $minRating-$maxRating")
        
        viewModelScope.launch {
            carRepository.getCarsPaged(
                page = page,
                size = size,
                sort = "brand",  // Default sorting
                direction = "ASC", // Default direction
                brand = brand,
                model = model,
                minRating = minRating,
                maxRating = maxRating,
                rentalStatus = "AVAILABLE" // Only show available cars by default
            ).collect { resource ->
                _uiState.value = when (resource.status) {
                    ApiStatus.LOADING -> CarUiState.Loading
                    ApiStatus.SUCCESS -> {
                        val response = resource.data
                        if (response != null) {
                            Log.d(TAG, "Filter results: ${response.content.size} cars found")
                            CarUiState.PaginatedSuccess(response)
                        } else {
                            Log.d(TAG, "Filter results: No cars found")
                            CarUiState.Error("No cars found")
                        }
                    }
                    ApiStatus.ERROR -> {
                        Log.e(TAG, "Error filtering cars: ${resource.message}")
                        CarUiState.Error(resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }
    
    /**
     * Get available cars.
     */
    fun getAvailableCars(page: Int = 0, size: Int = 10) {
        _uiState.value = CarUiState.Loading
        
        viewModelScope.launch {
            carRepository.getAvailableCarsPaged(
                page = page,
                size = size,
                sort = "brand" // Default sorting
            ).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { pagedResponse ->
                            _uiState.value = CarUiState.PaginatedSuccess(pagedResponse)
                            totalPages = pagedResponse.totalPages
                            isLastPage = pagedResponse.last
                            Log.d(TAG, "Loaded available cars: ${pagedResponse.content.size} items")
                        } ?: run {
                            _uiState.value = CarUiState.Error("No available cars found")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to load available cars")
                        Log.e(TAG, "Error loading available cars: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Filter cars by brand with pagination.
     */
    fun filterByBrand(brand: String, page: Int = 0, size: Int = 10) {
        if (brand.isEmpty()) {
            loadAllCars()
            return
        }
        
        _uiState.value = CarUiState.Loading
        
        viewModelScope.launch {
            carRepository.getCarsByBrandPaged(
                brand = brand,
                page = page,
                size = size,
                sort = "model" // Default sorting
            ).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { pagedResponse ->
                            _uiState.value = CarUiState.PaginatedSuccess(pagedResponse)
                            totalPages = pagedResponse.totalPages
                            isLastPage = pagedResponse.last
                            Log.d(TAG, "Filtered cars by brand '$brand': ${pagedResponse.content.size} items")
                        } ?: run {
                            _uiState.value = CarUiState.Error("No cars found for brand: $brand")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to filter cars by brand")
                        Log.e(TAG, "Error filtering cars by brand: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Filter cars by model with pagination.
     */
    fun filterByModel(model: String, page: Int = 0, size: Int = 10) {
        if (model.isEmpty()) {
            loadAllCars()
            return
        }
        
        _uiState.value = CarUiState.Loading
        
        viewModelScope.launch {
            carRepository.getCarsByModelPaged(
                model = model,
                page = page,
                size = size,
                sort = "rating" // Default sorting
            ).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { pagedResponse ->
                            _uiState.value = CarUiState.PaginatedSuccess(pagedResponse)
                            totalPages = pagedResponse.totalPages
                            isLastPage = pagedResponse.last
                            Log.d(TAG, "Filtered cars by model '$model': ${pagedResponse.content.size} items")
                        } ?: run {
                            _uiState.value = CarUiState.Error("No cars found for model: $model")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to filter cars by model")
                        Log.e(TAG, "Error filtering cars by model: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Filter cars by rating range with pagination.
     */
    fun filterByRatingRange(minRating: Long, maxRating: Long, page: Int = 0, size: Int = 10) {
        _uiState.value = CarUiState.Loading
        
        viewModelScope.launch {
            carRepository.getCarsByRatingRangePaged(
                minRating = minRating,
                maxRating = maxRating,
                page = page,
                size = size,
                sort = "rating" // Default sorting
            ).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { pagedResponse ->
                            _uiState.value = CarUiState.PaginatedSuccess(pagedResponse)
                            totalPages = pagedResponse.totalPages
                            isLastPage = pagedResponse.last
                            Log.d(TAG, "Filtered cars by rating range $minRating-$maxRating: ${pagedResponse.content.size} items")
                        } ?: run {
                            _uiState.value = CarUiState.Error("No cars found for rating range: $minRating-$maxRating")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to filter cars by rating")
                        Log.e(TAG, "Error filtering cars by rating: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Filter cars by type (SUV, Sedan, etc.) with pagination.
     */
    fun filterByType(type: String, page: Int = 0, size: Int = 10) {
        if (type.isEmpty() || type == "All") {
            Log.d(TAG, "Type is empty or 'All', loading all cars")
            loadAllCars()
            return
        }
        
        _uiState.value = CarUiState.Loading
        Log.d(TAG, "Filtering cars by type: '$type'")
        
        viewModelScope.launch {
            // Use the dedicated type filter method
            carRepository.getCarsByType(type).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { cars ->
                            Log.d(TAG, "Got ${cars.size} cars matching type '$type'")
                            
                            if (cars.isNotEmpty()) {
                                _uiState.value = CarUiState.Success(cars)
                            } else {
                                _uiState.value = CarUiState.Error("No cars found for type: $type")
                            }
                        } ?: run {
                            _uiState.value = CarUiState.Error("No cars available")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = CarUiState.Error(result.message ?: "Failed to filter cars by type")
                    }
                    ApiStatus.LOADING -> {
                        // Already in loading state
                    }
                }
            }
        }
    }
} 