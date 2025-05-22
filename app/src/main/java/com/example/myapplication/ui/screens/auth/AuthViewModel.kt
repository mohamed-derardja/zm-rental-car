package com.example.myapplication.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.FacebookAuthHelper
import com.example.myapplication.data.auth.GoogleAuthHelper
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.utils.PreferenceManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
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
 * Sealed class representing the different states of the authentication UI
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val token: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * ViewModel that handles authentication logic and UI state.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private var facebookAuthHelper: FacebookAuthHelper? = null
    private var googleAuthHelper: GoogleAuthHelper? = null

    init {
        initializeAuthHelpers()
    }

    private fun initializeAuthHelpers() {
        try {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing auth helpers", e)
            _uiState.value = AuthUiState.Error("Failed to initialize authentication")
        }
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
                    // Save user data to preferences
                    preferenceManager.userName = user.name
                    preferenceManager.userEmail = user.email
                    preferenceManager.isLoggedIn = true
                    preferenceManager.userId = user.id.toString()
                    
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
     * Handle user registration with name, email and password
     */
    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                
                // Save user name to preferences for profile display
                preferenceManager.userName = name
                preferenceManager.userEmail = email
                preferenceManager.isLoggedIn = true
                preferenceManager.userId = "mock_user_id"
                
                // TEMPORARY SOLUTION: Bypass actual API call for testing navigation
                // This will let us test the OTP verification flow without a working backend
                _uiState.value = AuthUiState.Success("mock_registration_token")
                Log.d(TAG, "Mock registration successful for: $name, $email")
                
                /* Commented out for now to fix navigation
                val result = authRepository.register(name, email, password, "") // Empty phone for now
                result.onSuccess { user ->
                    _uiState.value = AuthUiState.Success("email_${user.id}")
                    Log.d(TAG, "User registered successfully: ${user.name}")
                }.onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Registration failed")
                    Log.e(TAG, "Registration failed", exception)
                }
                */
            } catch (e: Exception) {
                // Even with an exception, we'll still navigate to OTP screen for testing
                // Save user name to preferences for profile display
                preferenceManager.userName = name
                preferenceManager.userEmail = email
                preferenceManager.isLoggedIn = true
                
                _uiState.value = AuthUiState.Success("mock_registration_token")
                Log.e(TAG, "Error during registration but proceeding for testing", e)
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
        try {
            _uiState.value = AuthUiState.Loading
            facebookAuthHelper?.login(launcher)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Failed to start Facebook login")
            Log.e(TAG, "Error starting Facebook login", e)
        }
    }

    /**
     * Handle Google login button click.
     */
    fun loginWithGoogle(launcher: ActivityResultLauncher<Intent>) {
        if (googleAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Google authentication not initialized")
            return
        }
        try {
            _uiState.value = AuthUiState.Loading
            googleAuthHelper?.signIn(launcher)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Failed to start Google login")
            Log.e(TAG, "Error starting Google login", e)
        }
    }

    /**
     * Handle Facebook login result from activity.
     */
    fun handleFacebookActivityResult(requestCode: Int, data: Intent?) {
        if (facebookAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Facebook authentication not initialized")
            return
        }
        try {
            facebookAuthHelper?.getCallbackManager()?.onActivityResult(requestCode, requestCode, data)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Failed to process Facebook login result")
            Log.e(TAG, "Error handling Facebook activity result", e)
        }
    }

    /**
     * Handle Google login result from activity.
     */
    fun handleGoogleActivityResult(task: Task<GoogleSignInAccount>) {
        if (googleAuthHelper == null) {
            _uiState.value = AuthUiState.Error("Google authentication not initialized")
            return
        }
        try {
            googleAuthHelper?.handleSignInResult(task)
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Failed to process Google login result")
            Log.e(TAG, "Error handling Google activity result", e)
        }
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
        try {
            facebookAuthHelper?.logout()
            googleAuthHelper?.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        } finally {
            facebookAuthHelper = null
            googleAuthHelper = null
        }
    }

    /**
     * Handle Facebook login success.
     */
    fun handleFacebookLoginSuccess(result: LoginResult) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val token = result.accessToken.token
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
     * Handle Facebook login cancellation.
     */
    fun handleFacebookLoginCancel() {
        _uiState.value = AuthUiState.Error("Facebook login cancelled")
        Log.d(TAG, "Facebook login cancelled by user")
    }

    /**
     * Handle Facebook login error.
     */
    fun handleFacebookLoginError(error: FacebookException) {
        _uiState.value = AuthUiState.Error(error.message ?: "Facebook login failed")
        Log.e(TAG, "Facebook login error", error)
    }
}
