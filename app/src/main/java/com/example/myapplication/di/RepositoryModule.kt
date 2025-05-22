package com.example.myapplication.di

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.preference.AuthPreferenceManager
import com.example.myapplication.data.repository.*
import com.example.myapplication.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    // For backward compatibility, we keep the old repository implementation
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        preferenceManager: PreferenceManager
    ): UserRepository {
        return UserRepositoryImpl(apiService, preferenceManager)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        authPreferenceManager: AuthPreferenceManager
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, authPreferenceManager)
    }
    
    @Provides
    @Singleton
    fun provideCarRepository(
        apiService: ApiService,
        authPreferenceManager: AuthPreferenceManager
    ): CarRepository {
        return CarRepositoryImpl(apiService, authPreferenceManager)
    }
    
    @Provides
    @Singleton
    fun provideReservationRepository(
        apiService: ApiService,
        authPreferenceManager: AuthPreferenceManager
    ): ReservationRepository {
        return ReservationRepositoryImpl(apiService, authPreferenceManager)
    }
}
