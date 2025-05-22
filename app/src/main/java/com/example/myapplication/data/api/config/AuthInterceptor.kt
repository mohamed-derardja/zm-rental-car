package com.example.myapplication.data.api.config

import com.example.myapplication.data.preference.AuthPreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authPreferenceManager: AuthPreferenceManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for login, register, and other public endpoints
        if (isPublicEndpoint(originalRequest)) {
            return chain.proceed(originalRequest)
        }
        
        // Add auth token to requests that need authentication
        val token = authPreferenceManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Create a new request with the Authorization header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
            
        return chain.proceed(newRequest)
    }
    
    private fun isPublicEndpoint(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/login") || 
               path.contains("/register") || 
               path.contains("/password-reset") ||
               path.contains("/oauth2") ||
               (path.contains("/cars") && request.method == "GET")
    }
} 