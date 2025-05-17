package com.example.myapplication.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api.zmrentals.com/api/v1/" // Replace with actual backend URL
    private const val TIMEOUT = 30L
    
    // Authentication token
    private var authToken: String? = null
    
    // Create API service instance
    val apiService: ApiService by lazy {
        createRetrofit().create(ApiService::class.java)
    }
    
    // Function to set auth token
    fun setToken(token: String) {
        authToken = token
    }
    
    // Function to clear auth token
    fun clearToken() {
        authToken = null
    }
    
    // Create Retrofit instance
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // Create OkHttp client with interceptors
    private fun createOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(createAuthInterceptor())
            .build()
    }
    
    // Auth interceptor for adding the token to requests
    private fun createAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            
            // If no auth token is set, proceed with the original request
            if (authToken == null) {
                return@Interceptor chain.proceed(originalRequest)
            }
            
            // Add the auth token to the request header
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            
            chain.proceed(newRequest)
        }
    }
} 