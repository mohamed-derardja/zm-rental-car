package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides authentication-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    /**
     * Provides the [AuthRepository] implementation.
     * @param apiService The API service for making network requests.
     * @param preferenceManager The preference manager for storing user data.
     * @param userRepository The user repository for user-related operations.
     * @return An instance of [AuthRepository].
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        preferenceManager: PreferenceManager,
        userRepository: UserRepository
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, userRepository, preferenceManager)
    }
}
