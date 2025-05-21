package com.example.myapplication.ui.screens.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    
    // For UI state
    val name = mutableStateOf("")
    val email = mutableStateOf("")
    val phone = mutableStateOf("")
    
    init {
        loadUserProfile()
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userProfile = userRepository.getUserProfile
                _user.value = userProfile
                
                // Update UI fields
                name.value = userProfile.name ?: ""
                email.value = userProfile.email ?: ""
                phone.value = userProfile.phone ?: ""
                
                _errorMessage.value = null
            } catch (e: Exception) {
                val errorMsg = "Failed to load profile: ${e.message}"
                _errorMessage.value = errorMsg
                Timber.e(e, errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.updateProfile(name.value, email.value, phone.value)
                
                // Update local user data
                _user.value = _user.value?.copy(
                    name = name.value,
                    email = email.value,
                    phone = phone.value
                )
                
                _errorMessage.value = "Profile updated successfully"
            } catch (e: Exception) {
                val errorMsg = "Failed to update profile: ${e.message}"
                _errorMessage.value = errorMsg
                Timber.e(e, errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun uploadProfileImage(uri: Uri, inputStream: InputStream) {
        viewModelScope.launch {
            _isLoading.value = true
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
                _user.value = _user.value?.copy(profileImageUrl = audi.png)
                _errorMessage.value = "Profile image updated successfully"
            } catch (e: Exception) {
                val errorMsg = "Failed to upload image: ${e.message}"
                _errorMessage.value = errorMsg
                Timber.e(e, errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun updateName(newName: String) {
        name.value = newName
    }
    
    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }
    
    fun updatePhone(newPhone: String) {
        phone.value = newPhone
    }
}
