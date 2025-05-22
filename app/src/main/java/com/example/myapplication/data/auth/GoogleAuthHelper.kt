package com.example.myapplication.data.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.myapplication.R
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

/**
 * Helper class to handle Google authentication
 */
class GoogleAuthHelper(
    private val context: Context,
    private val onSuccess: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val googleSignInClient: GoogleSignInClient
    private var isLoggingIn = false

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Initiate Google Sign-In flow
     */
    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        if (isLoggingIn) {
            Log.d(TAG, "Login already in progress")
            return
        }

        isLoggingIn = true
        Log.d(TAG, "Starting Google Sign-In flow")
        
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    /**
     * Handle Google Sign-In result
     */
    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val idToken = it.idToken
                if (idToken != null) {
                    Log.d(TAG, "Google Sign-In successful. Token: ${idToken.take(10)}...")
                    onSuccess(idToken)
                } else {
                    onError(context.getString(R.string.google_sign_in_error))
                }
            } ?: run {
                onError(context.getString(R.string.google_sign_in_error))
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed", e)
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    onError(context.getString(R.string.google_sign_in_canceled))
                }
                GoogleSignInStatusCodes.NETWORK_ERROR -> {
                    onError(context.getString(R.string.google_sign_in_network_error))
                }
                else -> {
                    onError(context.getString(R.string.google_sign_in_error))
                }
            }
        } finally {
            isLoggingIn = false
        }
    }

    /**
     * Sign out from Google
     */
    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Signed out from Google")
        }
    }

    /**
     * Check if user is currently signed in with Google
     */
    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val isSignedIn = account != null
        Log.d(TAG, "isSignedIn: $isSignedIn")
        return isSignedIn
    }

    companion object {
        private const val TAG = "GoogleAuthHelper"
    }
}