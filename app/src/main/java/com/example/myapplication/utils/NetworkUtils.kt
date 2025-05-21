package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.myapplication.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Utility class for network-related operations
 */
object NetworkUtils {
    private const val TIMEOUT_SECONDS = 30L

    /**
     * Check if the device has an active internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Create a logging interceptor for debug builds
     */
    fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    /**
     * Create an interceptor for adding common headers
     */
    fun getHeaderInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * Create an OkHttpClient with timeout configuration
     */
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Check if the exception is due to a network error
     */
    fun isNetworkError(throwable: Throwable): Boolean {
        return throwable is IOException
    }
    
    /**
     * Check if the response is successful (status code 200-299)
     */
    fun isResponseSuccessful(response: Response): Boolean {
        return response.isSuccessful
    }
    
    /**
     * Parse error response from the API
     */
    fun parseErrorResponse(response: Response): String {
        return try {
            response.body?.string() ?: "Unknown error occurred"
        } catch (e: Exception) {
            "Error parsing response: ${e.message}"
        }
    }
}
