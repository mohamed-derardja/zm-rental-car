package com.example.myapplication.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.repository.CarRepository
import com.example.myapplication.data.repository.ReservationRepository
import com.example.myapplication.ui.navigation.CAR_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val reservationRepository: ReservationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val carId: Long = savedStateHandle.get<Long>(CAR_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(CarDetailsUiState())
    val uiState: StateFlow<CarDetailsUiState> = _uiState.asStateFlow()

    init {
        loadCarDetails()
    }

    private fun loadCarDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = carRepository.getCarById(carId)
                result.onSuccess { car ->
                    _uiState.update { state ->
                        state.copy(
                            car = car,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load car details"
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

    fun toggleFavorite() {
        _uiState.update { state ->
            val currentCar = state.car ?: return@update state
            state.copy(
                car = currentCar.copy(isFavorite = !currentCar.isFavorite),
                isFavoriteUpdated = true
            )
        }
        
        // In a real app, you would update the favorite status in the repository
        viewModelScope.launch {
            _uiState.value.car?.let { car ->
                // Call repository to update favorite status
                // For example: carRepository.updateFavoriteStatus(car.id, car.isFavorite)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onBookingConfirmed(
        startDate: Date,
        endDate: Date,
        totalPrice: Double,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBookingInProgress = true) }
            try {
                val car = _uiState.value.car ?: return@launch
                val result = reservationRepository.createReservation(
                    carId = car.id ?: 0,
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = totalPrice,
                    paymentMethod = paymentMethod
                )
                
                result.onSuccess { reservation ->
                    _uiState.update {
                        it.copy(
                            isBookingInProgress = false,
                            bookingSuccess = true,
                            error = null
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isBookingInProgress = false,
                            error = exception.message ?: "Failed to create reservation"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBookingInProgress = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }
}

data class CarDetailsUiState(
    val car: Car? = null,
    val isLoading: Boolean = false,
    val isBookingInProgress: Boolean = false,
    val bookingSuccess: Boolean = false,
    val isFavoriteUpdated: Boolean = false,
    val error: String? = null
)
