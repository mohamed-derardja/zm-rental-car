package com.example.myapplication.ui.screens.auth

/**
 * Represents the different states of the authentication process.
 */
sealed class AuthUiState {
    /**
     * Initial state, no authentication action has been performed yet.
     */
    object Idle : AuthUiState()

    /**
     * Authentication is in progress.
     */
    object Loading : AuthUiState()

    /**
     * Authentication was successful.
     * @property token The authentication token received from the server.
     * @property provider The authentication provider (email, facebook, google).
     */
    data class Success(
        val token: String,
        val provider: String = "email"
    ) : AuthUiState()

    /**
     * Authentication failed.
     * @property message The error message describing what went wrong.
     * @property code Optional error code for specific error handling.
     */
    data class Error(
        val message: String,
        val code: Int? = null
    ) : AuthUiState()

    /**
     * User registration is required.
     * @property email The email used for registration.
     * @property name The user's name.
     * @property provider The authentication provider that triggered registration.
     */
    data class RegistrationRequired(
        val email: String,
        val name: String,
        val provider: String = "email"
    ) : AuthUiState()
}
