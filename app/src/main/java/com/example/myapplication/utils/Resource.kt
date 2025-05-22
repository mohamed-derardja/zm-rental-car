package com.example.myapplication.utils

import com.example.myapplication.R
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A sealed class that represents a resource state with its data and status
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String): Resource<Nothing> = Error(message)
        fun loading(): Resource<Nothing> = Loading

        // Common error messages
        const val ERROR_OCCURRED = "An error occurred. Please try again."
        const val ERROR_NO_INTERNET = "No internet connection. Please check your connection and try again."
        const val ERROR_SERVER_NOT_FOUND = "Server not found. Please try again later."
        const val ERROR_UNKNOWN = "An unknown error occurred."
        const val ERROR_TIMEOUT = "Request timed out. Please try again."
        const val ERROR_AUTHENTICATION = "Authentication failed. Please login again."
        const val ERROR_PERMISSION = "Permission denied. Please grant required permissions."
    }
}

/**
 * Extension function to map Resource data
 */
inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> Resource.Error(message)
        is Resource.Loading -> Resource.Loading
    }
}

/**
 * Extension function to handle Resource states
 */
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

inline fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message)
    }
    return this
}

inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}

/**
 * Extension function to get data or null
 */
fun <T> Resource<T>.getOrNull(): T? {
    return when (this) {
        is Resource.Success -> data
        else -> null
    }
}

/**
 * Extension function to get data or throw exception
 */
fun <T> Resource<T>.getOrThrow(): T {
    return when (this) {
        is Resource.Success -> data
        is Resource.Error -> throw IllegalStateException(message)
        is Resource.Loading -> throw IllegalStateException("Resource is loading")
    }
}

/**
 * Maps the success value using the given [transform] function that returns a [Resource].
 */
fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
    is Resource.Success -> transform(data)
    is Resource.Error -> this
    is Resource.Loading -> Resource.Loading
}

/**
 * Maps the success value using the given [transform] suspend function.
 */
suspend fun <T, R> Resource<T>.mapSuspend(transform: suspend (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
    is Resource.Loading -> Resource.Loading
}

/**
 * Maps the success value using the given [transform] suspend function that returns a [Resource].
 */
suspend fun <T, R> Resource<T>.flatMapSuspend(transform: suspend (T) -> Resource<R>): Resource<R> = when (this) {
    is Resource.Success -> transform(data)
    is Resource.Error -> this
    is Resource.Loading -> Resource.Loading
}