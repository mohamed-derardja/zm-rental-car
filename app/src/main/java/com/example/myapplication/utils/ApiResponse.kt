package com.example.myapplication.utils

import com.example.myapplication.R
import retrofit2.HttpException
import java.io.IOException

/**
 * A generic class that holds a value with its loading status.
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>() {
        val isSuccess: Boolean = true
    }
    
    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String? = null,
        val throwable: Throwable? = null
    ) : ApiResponse<Nothing>() {
        val isError: Boolean = true
        
        companion object {
            fun fromThrowable(throwable: Throwable): Error {
                return when (throwable) {
                    is HttpException -> {
                        Error(
                            errorCode = throwable.code(),
                            errorMessage = throwable.message(),
                            throwable = throwable
                        )
                    }
                    is IOException -> {
                        Error(
                            errorCode = null,
                            errorMessage = "Network error occurred",
                            throwable = throwable
                        )
                    }
                    else -> {
                        Error(
                            errorCode = null,
                            errorMessage = throwable.message ?: "Unknown error occurred",
                            throwable = throwable
                        )
                    }
                }
            }
        }
    }
    
    object Loading : ApiResponse<Nothing>() {
        val isLoading: Boolean = true
    }
    
    object Empty : ApiResponse<Nothing>()
    
    /**
     * Returns the success value as a [Result.Success] or the error as a [Result.Failure].
     */
    fun toResult(): Result<T> = when (this) {
        is Success -> Result.success(data)
        is Error -> Result.failure(throwable ?: Exception(errorMessage ?: "Unknown error"))
        else -> Result.failure(IllegalStateException("No data available"))
    }
    
    /**
     * Returns the success value or null if this is not a [Success].
     */
    fun getOrNull(): T? = (this as? Success)?.data
    
    /**
     * Returns the success value or throws an exception if this is not a [Success].
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw throwable ?: Exception(errorMessage ?: "Unknown error")
        else -> throw IllegalStateException("No data available")
    }
    
    /**
     * Returns the success value or a default value if this is not a [Success].
     */
    fun getOrDefault(defaultValue: @UnsafeVariance T): T = (this as? Success)?.data ?: defaultValue
    
    /**
     * Executes the given [block] if this is a [Success].
     */
    fun onSuccess(block: (T) -> Unit): ApiResponse<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }
    
    /**
     * Executes the given [block] if this is an [Error].
     */
    fun onError(block: (Error) -> Unit): ApiResponse<T> {
        if (this is Error) {
            block(this)
        }
        return this
    }
    
    /**
     * Executes the given [block] if this is [Loading].
     */
    fun onLoading(block: () -> Unit): ApiResponse<T> {
        if (this is Loading) {
            block()
        }
        return this
    }
    
    /**
     * Maps the success value using the given [transform] function.
     */
    fun <R> map(transform: (T) -> R): ApiResponse<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
        is Empty -> Empty
    }
    
    /**
     * Maps the success value using the given [transform] function that returns an [ApiResponse].
     */
    suspend fun <R> flatMap(transform: suspend (T) -> ApiResponse<R>): ApiResponse<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
        is Loading -> this
        is Empty -> Empty
    }
}

/**
 * Helper function to create a [ApiResponse.Success] with the given [data].
 */
fun <T> apiResponseOf(data: T): ApiResponse<T> = ApiResponse.Success(data)

/**
 * Helper function to create a [ApiResponse.Error] with the given [throwable].
 */
fun <T> apiErrorOf(throwable: Throwable): ApiResponse<T> = 
    ApiResponse.Error.fromThrowable(throwable)

/**
 * Helper function to create a [ApiResponse.Loading].
 */
fun <T> apiLoading(): ApiResponse<T> = ApiResponse.Loading

/**
 * Helper function to create a [ApiResponse.Empty].
 */
fun <T> apiEmpty(): ApiResponse<T> = ApiResponse.Empty
