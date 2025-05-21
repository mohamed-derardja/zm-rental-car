package com.example.myapplication.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.repository.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCars()
        loadPopularCars()
    }


    private fun loadCars() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = carRepository.getAllCars()
                result.onSuccess { cars ->
                    _uiState.update { state ->
                        state.copy(
                            cars = cars,
                            filteredCars = cars,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load cars"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }


    private fun loadPopularCars() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPopular = true) }
            try {
                val result = carRepository.getCarsByRatingRange(4, 5)
                result.onSuccess { cars ->
                    _uiState.update { state ->
                        state.copy(
                            popularCars = cars,
                            isLoadingPopular = false,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoadingPopular = false,
                            error = exception.message ?: "Failed to load popular cars"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingPopular = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }


    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterCars()
    }

    private fun filterCars() {
        val query = _uiState.value.searchQuery.lowercase()
        val filtered = if (query.isBlank()) {
            _uiState.value.cars
        } else {
            _uiState.value.cars.filter { car ->
                car.brand?.lowercase()?.contains(query) == true ||
                        car.model?.lowercase()?.contains(query) == true
            }
        }
        _uiState.update { it.copy(filteredCars = filtered) }
    }

    fun onBrandSelected(brand: String) {
        _uiState.update { it.copy(selectedBrand = brand) }
        viewModelScope.launch {
            val result = carRepository.getCarsByBrand(brand)
            result.onSuccess { cars ->
                _uiState.update { state ->
                    state.copy(
                        filteredCars = cars,
                        error = null
                    )
                }
            }
        }
    }

    fun clearBrandFilter() {
        _uiState.update { it.copy(selectedBrand = null, filteredCars = it.cars) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HomeUiState(
    val cars: List<Car> = emptyList(),
    val filteredCars: List<Car> = emptyList(),
    val popularCars: List<Car> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingPopular: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedBrand: String? = null
)
