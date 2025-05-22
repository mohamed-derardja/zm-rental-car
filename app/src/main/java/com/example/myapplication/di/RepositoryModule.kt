package com.example.myapplication.di

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        preferenceManager: PreferenceManager
    ): UserRepository {
        return UserRepositoryImpl(apiService, preferenceManager)
    }
    
    // Add other repository providers here as needed
    
    // Example for other repositories:
    // @Provides
    // @Singleton
    // fun provideCarRepository(apiService: ApiService): CarRepository {
    //     return CarRepositoryImpl(apiService)
    // }
}
