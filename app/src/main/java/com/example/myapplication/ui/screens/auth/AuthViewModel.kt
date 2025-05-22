package com.example.myapplication.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.ApiStatus
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
import kotlinx.coroutines.flow.collectLatest
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
        
        // Check if user is already logged in
        if (authRepository.isLoggedIn()) {
            _uiState.value = AuthUiState.Success("auto_login")
        }
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

        Log.d(TAG, "Starting login process for email: $email")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            Log.d(TAG, "Set UI state to Loading")
            
            authRepository.login(email, password).collectLatest { result ->
                Log.d(TAG, "Received login result with status: ${result.status}")
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { responseData ->
                            Log.d(TAG, "Login successful, response data type: ${responseData::class.java.name}")
                            // Handle different possible response formats
                            val userId = when (responseData) {
                                is com.example.myapplication.data.api.AuthResponse -> {
                                    Log.d(TAG, "Response is AuthResponse type with id: ${responseData.id}")
                                    responseData.id.toString()
                                }
                                is Map<*, *> -> {
                                    val id = (responseData["userId"] ?: responseData["id"])?.toString() ?: "unknown"
                                    Log.d(TAG, "Response is Map type with userId/id: $id")
                                    id
                                }
                                else -> {
                                    // Use reflection to try to access properties
                                    try {
                                        val userIdField = responseData.javaClass.getDeclaredField("userId")
                                        userIdField.isAccessible = true
                                        val id = userIdField.get(responseData)?.toString() ?: "unknown"
                                        Log.d(TAG, "Response is other type with reflected userId: $id")
                                        id
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Failed to get userId via reflection", e)
                                        "unknown"
                                    }
                                }
                            }
                            
                            val token = when (responseData) {
                                is com.example.myapplication.data.api.AuthResponse -> {
                                    Log.d(TAG, "Got token from AuthResponse: ${responseData.token.take(10)}...")
                                    responseData.token
                                }
                                is Map<*, *> -> {
                                    val tokenStr = responseData["token"]?.toString() ?: ""
                                    Log.d(TAG, "Got token from Map: ${tokenStr.take(10.coerceAtMost(tokenStr.length))}...")
                                    tokenStr
                                }
                                else -> {
                                    // Use reflection to try to access properties
                                    try {
                                        val tokenField = responseData.javaClass.getDeclaredField("token")
                                        tokenField.isAccessible = true
                                        val tokenStr = tokenField.get(responseData)?.toString() ?: ""
                                        Log.d(TAG, "Got token via reflection: ${tokenStr.take(10.coerceAtMost(tokenStr.length))}...")
                                        tokenStr
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Failed to get token via reflection", e)
                                        ""
                                    }
                                }
                            }
                            
                            // Old preference manager for backward compatibility
                            preferenceManager.userEmail = email
                            preferenceManager.isLoggedIn = true
                            preferenceManager.userId = userId
                            preferenceManager.authToken = token
                            
                            Log.d(TAG, "Saved to preferences - userId: $userId, isLoggedIn: true")
                            _uiState.value = AuthUiState.Success("email_$userId")
                            Log.d(TAG, "Set UI state to Success with value: email_$userId")
                        } ?: run {
                            Log.e(TAG, "Login succeeded but response data is null")
                            _uiState.value = AuthUiState.Error("Login succeeded but no user data returned")
                        }
                    }
                    ApiStatus.ERROR -> {
                        Log.e(TAG, "Login API error: ${result.message}")
                        _uiState.value = AuthUiState.Error(result.message ?: "Login failed")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                        Log.d(TAG, "Login API status is still loading")
                    }
                }
            }
        }
    }

    /**
     * Handle user registration with name, email and password
     */
    fun register(name: String, email: String, password: String, phone: String = "") {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            authRepository.register(name, email, password, phone).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { responseData ->
                            // Handle different possible response formats
                            val userId = when (responseData) {
                                is com.example.myapplication.data.api.AuthResponse -> responseData.id.toString()
                                is Map<*, *> -> (responseData["userId"] ?: responseData["id"])?.toString() ?: "unknown"
                                else -> {
                                    // Use reflection to try to access properties
                                    try {
                                        val userIdField = responseData.javaClass.getDeclaredField("userId")
                                        userIdField.isAccessible = true
                                        userIdField.get(responseData)?.toString() ?: "unknown"
                                    } catch (e: Exception) {
                                        "unknown"
                                    }
                                }
                            }
                            
                            val token = when (responseData) {
                                is com.example.myapplication.data.api.AuthResponse -> responseData.token
                                is Map<*, *> -> responseData["token"]?.toString() ?: ""
                                else -> {
                                    // Use reflection to try to access properties
                                    try {
                                        val tokenField = responseData.javaClass.getDeclaredField("token")
                                        tokenField.isAccessible = true
                                        tokenField.get(responseData)?.toString() ?: ""
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }
                            }
                            
                            // Old preference manager for backward compatibility
                            preferenceManager.userName = name
                            preferenceManager.userEmail = email
                            preferenceManager.isLoggedIn = true
                            preferenceManager.userId = userId
                            preferenceManager.authToken = token
                            
                            _uiState.value = AuthUiState.Success("email_$userId")
                            Log.d(TAG, "User registered successfully: ID $userId")
                        } ?: run {
                            _uiState.value = AuthUiState.Error("Registration succeeded but no user data returned")
                        }
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = AuthUiState.Error(result.message ?: "Registration failed")
                        Log.e(TAG, "Registration failed: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }

    /**
     * Check if an email exists in the system
     */
    fun checkEmailExists(email: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank()) {
            onResult(false)
            return
        }
        
        viewModelScope.launch {
            authRepository.checkEmailExists(email).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        result.data?.let { exists ->
                            onResult(exists)
                        } ?: run {
                            onResult(false)
                        }
                    }
                    ApiStatus.ERROR -> {
                        onResult(false)
                        Log.e(TAG, "Email check failed: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Do nothing while loading
                    }
                }
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
            _uiState.value = AuthUiState.Loading
            
            try {
                // Attempt to use loginWithFacebook method if it exists
                authRepository.loginWithFacebook(token).collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            result.data?.let { responseData ->
                                // Handle different possible response formats
                                val userId = when (responseData) {
                                    is com.example.myapplication.data.api.AuthResponse -> responseData.id.toString()
                                    is Map<*, *> -> (responseData["userId"] ?: responseData["id"])?.toString() ?: "unknown"
                                    else -> {
                                        // Use reflection to try to access properties
                                        try {
                                            val userIdField = responseData.javaClass.getDeclaredField("userId")
                                            userIdField.isAccessible = true
                                            userIdField.get(responseData)?.toString() ?: "unknown"
                                        } catch (e: Exception) {
                                            "unknown"
                                        }
                                    }
                                }
                                
                                _uiState.value = AuthUiState.Success("fb_$userId")
                                Log.d(TAG, "User logged in with Facebook successfully: ID $userId")
                            } ?: run {
                                _uiState.value = AuthUiState.Error("Facebook login succeeded but no user data returned")
                            }
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = AuthUiState.Error(result.message ?: "Facebook login failed")
                            Log.e(TAG, "Facebook login failed: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            // Already set loading state above
                        }
                    }
                }
            } catch (e: Exception) {
                // If the method doesn't exist, fallback to a generic error message
                _uiState.value = AuthUiState.Error("Facebook login is not implemented yet")
                Log.e(TAG, "Facebook login method not available", e)
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
            _uiState.value = AuthUiState.Loading
            
            try {
                // Attempt to use loginWithGoogle method if it exists
                authRepository.loginWithGoogle(token).collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            result.data?.let { responseData ->
                                // Handle different possible response formats
                                val userId = when (responseData) {
                                    is com.example.myapplication.data.api.AuthResponse -> responseData.id.toString()
                                    is Map<*, *> -> (responseData["userId"] ?: responseData["id"])?.toString() ?: "unknown"
                                    else -> {
                                        // Use reflection to try to access properties
                                        try {
                                            val userIdField = responseData.javaClass.getDeclaredField("userId")
                                            userIdField.isAccessible = true
                                            userIdField.get(responseData)?.toString() ?: "unknown"
                                        } catch (e: Exception) {
                                            "unknown"
                                        }
                                    }
                                }
                                
                                _uiState.value = AuthUiState.Success("google_$userId")
                                Log.d(TAG, "User logged in with Google successfully: ID $userId")
                            } ?: run {
                                _uiState.value = AuthUiState.Error("Google login succeeded but no user data returned")
                            }
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = AuthUiState.Error(result.message ?: "Google login failed")
                            Log.e(TAG, "Google login failed: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            // Already set loading state above
                        }
                    }
                }
            } catch (e: Exception) {
                // If the method doesn't exist, fallback to a generic error message
                _uiState.value = AuthUiState.Error("Google login is not implemented yet")
                Log.e(TAG, "Google login method not available", e)
            }
        }
    }

    /**
     * Log the user out.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            preferenceManager.clear()
            _uiState.value = AuthUiState.Idle
        }
    }
    
    /**
     * Request a password reset for the given email.
     */
    fun requestPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Email cannot be empty")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            authRepository.requestPasswordReset(email).collectLatest { result ->
                when (result.status) {
                    ApiStatus.SUCCESS -> {
                        _uiState.value = AuthUiState.Success("password_reset_requested")
                        Log.d(TAG, "Password reset requested successfully for email: $email")
                    }
                    ApiStatus.ERROR -> {
                        _uiState.value = AuthUiState.Error(result.message ?: "Password reset request failed")
                        Log.e(TAG, "Password reset request failed: ${result.message}")
                    }
                    ApiStatus.LOADING -> {
                        // Already set loading state above
                    }
                }
            }
        }
    }
    
    /**
     * Verify a password reset code and set a new password.
     */
    fun resetPassword(email: String, code: String, newPassword: String) {
        if (email.isBlank() || code.isBlank() || newPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("All fields are required")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            try {
                // Attempt to use verifyPasswordReset method if it exists
                authRepository.verifyPasswordReset(email, code, newPassword).collectLatest { result ->
                    when (result.status) {
                        ApiStatus.SUCCESS -> {
                            _uiState.value = AuthUiState.Success("password_reset_success")
                            Log.d(TAG, "Password reset completed successfully for email: $email")
                        }
                        ApiStatus.ERROR -> {
                            _uiState.value = AuthUiState.Error(result.message ?: "Password reset failed")
                            Log.e(TAG, "Password reset failed: ${result.message}")
                        }
                        ApiStatus.LOADING -> {
                            // Already set loading state above
                        }
                    }
                }
            } catch (e: Exception) {
                // If the method doesn't exist, fallback to a generic error message
                _uiState.value = AuthUiState.Error("Password reset is not implemented yet")
                Log.e(TAG, "Password reset method not available", e)
            }
        }
    }
}
