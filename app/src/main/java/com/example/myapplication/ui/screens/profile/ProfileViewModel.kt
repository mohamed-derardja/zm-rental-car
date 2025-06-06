package com.example.myapplication.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.ApiStatus
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.utils.FileUtil
import com.example.myapplication.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

// Define ProfileUiState for UI state management
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: Any? = null) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Idle : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager,
    private val fileUtil: FileUtil
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    // UI state
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState
    
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
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .catch { e ->
                    Log.e("ProfileViewModel", "Error loading profile", e)
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
                }
                .collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            result.data?.let { user ->
                                _user.value = user
                                _uiState.value = ProfileUiState.Success()
                                Log.d("ProfileViewModel", "Profile loaded successfully: ${user.name}")
                            } ?: run {
                                _uiState.value = ProfileUiState.Error("User profile data is null")
                            }
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = ProfileUiState.Error(result.message ?: "Failed to load profile")
                            Log.e("ProfileViewModel", "Error loading profile: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            _uiState.value = ProfileUiState.Loading
                        }
                    }
                }
        }
    }
    
    fun updateProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userRepository.updateProfile(
                name = name.value,
                email = email.value,
                phone = phone.value
            )
                .catch { e ->
                    Log.e("ProfileViewModel", "Error updating profile", e)
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update profile")
                }
                .collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            result.data?.let { user ->
                                _user.value = user
                                _uiState.value = ProfileUiState.Success("Profile updated successfully")
                                Log.d("ProfileViewModel", "Profile updated successfully: ${user.name}")
                            } ?: run {
                                _uiState.value = ProfileUiState.Error("Updated user data is null")
                            }
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = ProfileUiState.Error(result.message ?: "Failed to update profile")
                            Log.e("ProfileViewModel", "Error updating profile: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            _uiState.value = ProfileUiState.Loading
                        }
                    }
                }
        }
    }
    
    fun uploadProfileImage(imageUri: Uri) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                // Convert Uri to File
                val imageFile = fileUtil.getFileFromUri(imageUri)
                    ?: throw IllegalStateException("Failed to get file from Uri")

                // Create multipart form data
                val mediaType = "image/*".toMediaType()
                val requestBody = imageFile.asRequestBody(mediaType)
                val part = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                // Upload image
                userRepository.uploadProfileImage(part)
                    .catch { e ->
                        Log.e("ProfileViewModel", "Error uploading profile image", e)
                        _uiState.value = ProfileUiState.Error(e.message ?: "Failed to upload profile image")
                    }
                    .collectLatest { result ->
                        when (result.status) {
                            ApiStatus.SUCCESS -> {
                                result.data?.let { imageUrl ->
                                    // Update local user data with new image URL
                                    _user.value = _user.value?.copy(profileImage = imageUrl)
                                    _uiState.value = ProfileUiState.Success("Profile image updated")
                                    Log.d("ProfileViewModel", "Profile image uploaded successfully: $imageUrl")
                                } ?: run {
                                    _uiState.value = ProfileUiState.Error("Image URL is null")
                                }
                            }
                            ApiStatus.ERROR -> {
                                _uiState.value = ProfileUiState.Error(result.message ?: "Failed to upload profile image")
                                Log.e("ProfileViewModel", "Error uploading profile image: ${result.message}")
                            }
                            ApiStatus.LOADING -> {
                                _uiState.value = ProfileUiState.Loading
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to process image")
                Log.e("ProfileViewModel", "Error processing image", e)
            }
        }
    }
    
    fun changePassword(currentPassword: String, newPassword: String) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userRepository.changePassword(currentPassword, newPassword)
                .catch { e ->
                    Log.e("ProfileViewModel", "Error changing password", e)
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to change password")
                }
                .collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            _uiState.value = ProfileUiState.Success("Password changed successfully")
                            Log.d("ProfileViewModel", "Password changed successfully")
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = ProfileUiState.Error(result.message ?: "Failed to change password")
                            Log.e("ProfileViewModel", "Error changing password: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            _uiState.value = ProfileUiState.Loading
                        }
                    }
                }
        }
    }
    
    fun deleteAccount() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userRepository.deleteAccount()
                .catch { e ->
                    Log.e("ProfileViewModel", "Error deleting account", e)
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to delete account")
                }
                .collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            _uiState.value = ProfileUiState.Success("Account deleted successfully")
                            _user.value = null
                            Log.d("ProfileViewModel", "Account deleted successfully")
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = ProfileUiState.Error(result.message ?: "Failed to delete account")
                            Log.e("ProfileViewModel", "Error deleting account: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            _uiState.value = ProfileUiState.Loading
                        }
                    }
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
