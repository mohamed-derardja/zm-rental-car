package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.api.RetrofitClient
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.utils.NetworkStateManager
import com.example.myapplication.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofitClient(okHttpClient: OkHttpClient): RetrofitClient {
        return RetrofitClient.getInstance(okHttpClient)
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofitClient: RetrofitClient): ApiService {
        return retrofitClient.createService()
    }
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
    
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }
    
    @Provides
    @Singleton
    fun provideNetworkStateManager(@ApplicationContext context: Context): NetworkStateManager {
        return NetworkStateManager(context)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        preferenceManager: PreferenceManager
    ): UserRepository {
        return UserRepositoryImpl(apiService, preferenceManager)
    }
}
