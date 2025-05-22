package com.example.myapplication.ui.screens.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

// Define ProfileUiState for UI state management
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Idle : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    // UI state
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    // For UI state
    var name = mutableStateOf(preferenceManager.userName ?: "")
    var email = mutableStateOf(preferenceManager.userEmail ?: "")
    var phone = mutableStateOf("")
    
    // Add profile image URI state
    var profileImageUri by mutableStateOf<Uri?>(null)
        private set
    
    init {
        loadUserProfile()
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userProfile = userRepository.getUserProfile()
                _user.value = userProfile
                
                // Update UI fields
                name.value = userProfile.name
                email.value = userProfile.email ?: ""
                phone.value = userProfile.phone ?: ""
                
                _uiState.value = ProfileUiState.Success(userProfile)
            } catch (e: Exception) {
                // If repository call fails, try to use data from preferences
                name.value = preferenceManager.userName ?: ""
                email.value = preferenceManager.userEmail ?: ""
                
                val errorMsg = "Failed to load profile: ${e.message}"
                _uiState.value = ProfileUiState.Error(errorMsg)
                Timber.e(e, errorMsg)
            }
        }
    }
    
    fun updateProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val updatedUser = userRepository.updateProfile(
                    name = name.value,
                    email = email.value,
                    phone = phone.value
                )
                
                // Update local user data
                _user.value = updatedUser
                
                // Also update preferences directly as a backup
                preferenceManager.userName = name.value
                preferenceManager.userEmail = email.value
                
                _uiState.value = ProfileUiState.Success(updatedUser)
            } catch (e: Exception) {
                // Even if API call fails, update preferences
                preferenceManager.userName = name.value
                preferenceManager.userEmail = email.value
                
                val errorMsg = "Failed to update profile: ${e.message}"
                _uiState.value = ProfileUiState.Error(errorMsg)
                Timber.e(e, errorMsg)
            }
        }
    }
    
    fun uploadProfileImage(uri: Uri, inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                // Create a temporary file
                val tempFile = File.createTempFile("profile", "").apply {
                    deleteOnExit()
                    outputStream().use { output ->
                        inputStream.copyTo(output)
                    }
                }
                
                // Upload the file
                val imageUrl = userRepository.uploadProfileImage(tempFile)
                
                // Update local user data
                _user.value = _user.value?.copy(profileImage = imageUrl)
                preferenceManager.userProfileImage = imageUrl
                
                _uiState.value = _user.value?.let { ProfileUiState.Success(it) } ?: ProfileUiState.Idle
            } catch (e: Exception) {
                val errorMsg = "Failed to upload image: ${e.message}"
                _uiState.value = ProfileUiState.Error(errorMsg)
                Timber.e(e, errorMsg)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = ProfileUiState.Idle
    }
    
    // Rename these methods to match what's used in ProfileScreen.kt
    fun onNameChange(newName: String) {
        name.value = newName
    }
    
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }
    
    fun onPhoneChange(newPhone: String) {
        phone.value = newPhone
    }
    
    // Add method to handle profile image selection
    fun onProfileImageSelected(uri: Uri) {
        profileImageUri = uri
        // In a real app, we would upload the image here
    }
}
