package com.example.myapplication.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> createService(): T {
        return createService(T::class.java)
    }

    companion object {
        // Updated to point to the actual backend location
        // Using 10.0.2.2 which is the special IP that Android emulators use to access the host machine
        const val BASE_URL = "http://10.0.2.2:8080/api/"

        // For direct usage in places where DI is not available
        @Volatile
        private var INSTANCE: RetrofitClient? = null

        fun getInstance(okHttpClient: OkHttpClient): RetrofitClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RetrofitClient(okHttpClient).also { INSTANCE = it }
            }
        }
    }
}

// Extension function to create API service
inline fun <reified T> RetrofitClient.createApiService(): T {
    return this.createService()
}
