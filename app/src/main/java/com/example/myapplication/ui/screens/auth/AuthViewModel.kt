package com.example.myapplication.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.FacebookAuthHelper
import com.example.myapplication.data.auth.GoogleAuthHelper
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

/**
 * ViewModel that handles authentication logic and UI state.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private var facebookAuthHelper: FacebookAuthHelper? = null
    private var googleAuthHelper: GoogleAuthHelper? = null

    init {
        initializeAuthHelpers()
    }

    private fun initializeAuthHelpers() {
        facebookAuthHelper = FacebookAuthHelper(
            context = context,
            onSuccess = { token ->
                handleFacebookToken(token)
            },
            onError = { error ->
                _uiState.value = AuthUiState.Error(error)
                Log.e(TAG, "Facebook authentication error: $error")
            }
        )

        googleAuthHelper = GoogleAuthHelper(
            context = context,
            onSuccess = { token ->
                handleGoogleToken(token)
            },
            onError = { error ->
                _uiState.value = AuthUiState.Error(error)
                Log.e(TAG, "Google authentication error: $error")
            }
        )
    }

    /**
     * Handle email/password login.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val result = authRepository.login(email, password)
                result.onSuccess { user ->
                    _uiState.value = AuthUiState.Success("email_${user.id}")
                    Log.d(TAG, "User logged in successfully: ${user.name}")
                }.onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Authentication failed")
                    Log.e(TAG, "Login failed", exception)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "An unknown error occurred")
                Log.e(TAG, "Error during email authentication", e)
            }
        }
    }

    /**
     * Handle Facebook login button click.
     */
    fun loginWithFacebook(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        if (facebookAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Facebook authentication not initialized")
            return
        }
        _uiState.value = AuthUiState.Loading
        facebookAuthHelper?.login(launcher)
    }

    /**
     * Handle Google login button click.
     */
    fun loginWithGoogle(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        if (googleAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Google authentication not initialized")
            return
        }
        _uiState.value = AuthUiState.Loading
        googleAuthHelper?.signIn(launcher)
    }

    /**
     * Handle Facebook login result from activity.
     */
    fun handleFacebookActivityResult(requestCode: Int, data: Intent?) {
        if (facebookAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Facebook authentication not initialized")
            return
        }
        facebookAuthHelper?.getCallbackManager()?.onActivityResult(requestCode, requestCode, data)
    }

    /**
     * Handle Google login result from activity.
     */
    fun handleGoogleActivityResult(task: Task<GoogleSignInAccount>) {
        if (googleAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Google authentication not initialized")
            return
        }
        googleAuthHelper?.handleSignInResult(task)
    }

    /**
     * Process Facebook access token and authenticate with backend.
     */
    private fun handleFacebookToken(token: String) {
        if (token.isBlank()) {
            _uiState.value = AuthUiState.Error("Invalid Facebook token")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val result = authRepository.loginWithFacebook(token)
                result.onSuccess { user ->
                    _uiState.value = AuthUiState.Success("fb_$token")
                    Log.d(TAG, "User logged in successfully: ${user.name}")
                }.onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Authentication failed")
                    Log.e(TAG, "Login failed", exception)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "An unknown error occurred")
                Log.e(TAG, "Error during Facebook authentication", e)
            }
        }
    }

    /**
     * Process Google ID token and authenticate with backend.
     */
    private fun handleGoogleToken(token: String) {
        if (token.isBlank()) {
            _uiState.value = AuthUiState.Error("Invalid Google token")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val result = authRepository.loginWithGoogle(token)
                result.onSuccess { user ->
                    _uiState.value = AuthUiState.Success("google_$token")
                    Log.d(TAG, "User logged in successfully: ${user.name}")
                }.onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Authentication failed")
                    Log.e(TAG, "Login failed", exception)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "An unknown error occurred")
                Log.e(TAG, "Error during Google authentication", e)
            }
        }
    }

    /**
     * Check if the user is already logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    /**
     * Log out the current user.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                facebookAuthHelper?.logout()
                googleAuthHelper?.signOut()
                authRepository.logout()
                _uiState.value = AuthUiState.Idle
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                _uiState.value = AuthUiState.Error("Failed to logout properly")
            }
        }
    }

    /**
     * Reset the UI state to Idle.
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        facebookAuthHelper = null
        googleAuthHelper = null
    }
}
