package com.example.myapplication.data.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

/**
 * Helper class to handle Facebook authentication
 */
class FacebookAuthHelper(
    private val context: Context,
    private val onSuccess: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val callbackManager = CallbackManager.Factory.create()
    private val loginManager = LoginManager.getInstance()
    private var isLoggingIn = false

    init {
        setupFacebookCallback()
    }

    private fun setupFacebookCallback() {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                isLoggingIn = false
                val token = loginResult.accessToken.token
                Log.d(TAG, "Facebook login successful. Token: ${token.take(10)}...")
                onSuccess(token)
            }

            override fun onCancel() {
                isLoggingIn = false
                Log.d(TAG, "Facebook login cancelled")
                onError(context.getString(com.example.myapplication.R.string.facebook_login_canceled))
            }

            override fun onError(error: FacebookException) {
                isLoggingIn = false
                val errorMsg = error.message ?: context.getString(com.example.myapplication.R.string.facebook_login_error)
                Log.e(TAG, "Facebook login error: $errorMsg", error)
                onError(errorMsg)
            }
        })
    }

    /**
     * Initiate Facebook login flow
     */
    fun login(launcher: ActivityResultLauncher<Intent>) {
        if (isLoggingIn) {
            Log.d(TAG, "Login already in progress")
            return
        }
        
        isLoggingIn = true
        Log.d(TAG, "Starting Facebook login flow")
        
        val activity = context as? android.app.Activity
        if (activity == null) {
            isLoggingIn = false
            onError("Invalid context for Facebook login")
            return
        }

        try {
            loginManager.logInWithReadPermissions(
                activity,
                listOf("public_profile", "email")
            )
        } catch (e: Exception) {
            isLoggingIn = false
            Log.e(TAG, "Error starting Facebook login", e)
            onError("Failed to start Facebook login: ${e.message}")
        }
    }

    /**
     * Log out from Facebook
     */
    fun logout() {
        LoginManager.getInstance().logOut()
        Log.d(TAG, "Logged out from Facebook")
    }

    /**
     * Get the callback manager for handling activity results
     */
    fun getCallbackManager() = callbackManager

    companion object {
        private const val TAG = "FacebookAuthHelper"
        
        /**
         * Check if user is currently logged in with Facebook
         */
        fun isLoggedIn(): Boolean {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            Log.d(TAG, "isLoggedIn: $isLoggedIn")
            return isLoggedIn
        }

        /**
         * Get current Facebook access token
         */
        fun getAccessToken(): String? {
            return AccessToken.getCurrentAccessToken()?.token
        }
    }
}
