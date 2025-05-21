package com.example.myapplication.utils

import com.example.myapplication.R
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A generic class that holds a value with its loading status.
 * This is useful for representing the state of a resource that is typically requested from the network or database.
 */
sealed class Resource<out T> {
    /**
     * Represents a successful operation with the result [data].
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Represents a failed operation with an [errorCode] and [errorMessage].
     */
    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String? = null,
        val errorResId: Int = R.string.error_occurred
    ) : Resource<Nothing>() {
        constructor(errorResId: Int) : this(null, null, errorResId)

        companion object {
            fun fromThrowable(throwable: Throwable): Error {
                return when (throwable) {
                    is ConnectException -> Error(
                        errorMessage = "No internet connection",
                        errorResId = R.string.error_no_internet
                    )
                    is SocketTimeoutException -> Error(
                        errorMessage = "Connection timeout",
                        errorResId = R.string.error_timeout
                    )
                    is UnknownHostException -> Error(
                        errorMessage = "Server not found",
                        errorResId = R.string.error_server_not_found
                    )
                    is HttpException -> {
                        val errorMessage = when (throwable.code()) {
                            400 -> "Bad request"
                            401 -> "Unauthorized"
                            403 -> "Forbidden"
                            404 -> "Resource not found"
                            408 -> "Request timeout"
                            500 -> "Internal server error"
                            502 -> "Bad gateway"
                            503 -> "Service unavailable"
                            504 -> "Gateway timeout"
                            else -> "An error occurred"
                        }
                        Error(
                            errorCode = throwable.code(),
                            errorMessage = errorMessage,
                            errorResId = R.string.error_occurred
                        )
                    }
                    else -> Error(
                        errorMessage = throwable.message ?: "An unknown error occurred",
                        errorResId = R.string.error_occurred
                    )
                }
            }
        }
    }

    /**
     * Represents a loading state with optional [data] that was loaded before.
     */
    data class Loading<out T>(val data: T? = null) : Resource<T>()

    /**
     * Executes the given [block] if this is a [Success].
     */
    fun onSuccess(block: (T) -> Unit): Resource<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }

    /**
     * Executes the given [block] if this is an [Error].
     */
    fun onError(block: (Error) -> Unit): Resource<T> {
        if (this is Error) {
            block(this)
        }
        return this
    }

    /**
     * Executes the given [block] if this is [Loading].
     */
    fun onLoading(block: (T?) -> Unit): Resource<T> {
        if (this is Loading) {
            block(data)
        }
        return this
    }

    /**
     * Maps the success value using the given [transform] function.
     */
    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading(data?.let(transform))
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
        is Error -> throw IllegalStateException(errorMessage ?: "Unknown error")
        is Loading -> throw IllegalStateException("No data available while loading")
    }

    /**
     * Returns the success value or a default value if this is not a [Success].
     */
    fun getOrDefault(defaultValue: T): T = (this as? Success)?.data ?: defaultValue

    companion object {
        /**
         * Creates a [Resource] with the given [data].
         */
        fun <T> success(data: T): Resource<T> = Success(data)

        /**
         * Creates a [Resource] with the given [throwable].
         */
        fun <T> error(throwable: Throwable): Resource<T> = Error.fromThrowable(throwable)

        /**
         * Creates a [Resource] with the given [errorMessage] and optional [errorCode].
         */
        fun <T> error(errorMessage: String, errorCode: Int? = null): Resource<T> =
            Error(errorCode, errorMessage)

        /**
         * Creates a [Resource] with the given [errorResId].
         */
        fun <T> error(errorResId: Int): Resource<T> = Error(errorResId = errorResId)

        /**
         * Creates a [Resource] with the given [data] (can be null).
         */
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)
    }
}

/**
 * Maps the success value using the given [transform] function that returns a [Resource].
 */
fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
    is Resource.Success -> transform(data)
    is Resource.Error -> this
    is Resource.Loading -> Resource.loading()
}

/**
 * Maps the success value using the given [transform] suspend function.
 */
suspend fun <T, R> Resource<T>.mapSuspend(transform: suspend (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
    is Resource.Loading -> Resource.loading()
}

/**
 * Maps the success value using the given [transform] suspend function that returns a [Resource].
 */
suspend fun <T, R> Resource<T>.flatMapSuspend(transform: suspend (T) -> Resource<R>): Resource<R> = when (this) {
    is Resource.Success -> transform(data)
    is Resource.Error -> this
    is Resource.Loading -> Resource.loading()
}