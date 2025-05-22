package com.example.myapplication.di

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        // Add auth interceptor if needed
        // builder.addInterceptor(AuthInterceptor(preferenceManager))


        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): RetrofitClient {
        return RetrofitClient.getInstance(okHttpClient)
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: RetrofitClient): ApiService {
        return retrofit.createService()
    }
}
