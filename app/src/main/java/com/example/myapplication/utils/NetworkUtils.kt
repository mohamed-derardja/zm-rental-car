package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.myapplication.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkUtils {
    
    /**
     * Check if the device has an active internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // For 29+ (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return activeNetwork.isConnected && (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE ||
                    activeNetwork.type == ConnectivityManager.TYPE_ETHERNET)
        }
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
    fun getHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            
            // Add common headers here
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            
            chain.proceed(request)
        }
    }
    
    /**
     * Create an interceptor for handling network timeouts
     */
    fun getTimeoutInterceptor() = Interceptor { chain ->
        val request = chain.request()
        
        // Set timeout values
        val timeout = 30L // seconds
        val newRequest = request.newBuilder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
            
        chain.proceed(newRequest)
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
    fun isResponseSuccessful(response: okhttp3.Response): Boolean {
        return response.isSuccessful
    }
    
    /**
     * Parse error response from the API
     */
    fun parseErrorResponse(response: okhttp3.Response): String {
        return try {
            response.body?.string() ?: "Unknown error occurred"
        } catch (e: Exception) {
            "Error parsing error response: ${e.message}"
        }
    }
}
