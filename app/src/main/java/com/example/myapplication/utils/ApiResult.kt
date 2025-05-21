package com.example.myapplication.utils

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and throwable.
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>() {
        val isSuccess: Boolean = true
    }

    data class Error(
        val message: String? = null,
        val throwable: Throwable? = null,
        val code: Int = -1
    ) : ApiResult<Nothing>() {
        val isError: Boolean = true
    }

    object Loading : ApiResult<Nothing>() {
        val isLoading: Boolean = true
    }

    companion object {
        fun <T> success(data: T): ApiResult<T> = Success(data)
        
        fun error(
            message: String? = null,
            throwable: Throwable? = null,
            code: Int = -1
        ): ApiResult<Nothing> = Error(message, throwable, code)
        
        fun <T> loading(): ApiResult<T> = Loading
    }
}

/**
 * Converts a Retrofit [Response] to an [ApiResult].
 */
fun <T> retrofit2.Response<T>.toApiResult(): ApiResult<T> {
    return try {
        if (isSuccessful) {
            val body = body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Response body is null")
            }
        } else {
            ApiResult.Error(
                message = "${code()}: ${errorBody()?.string() ?: message()}",
                code = code()
            )
        }
    } catch (e: Exception) {
        ApiResult.Error("An error occurred: ${e.message}", e)
    }
}

/**
 * Executes the given [block] and wraps the result in an [ApiResult].
 */
suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.success(block())
    } catch (e: Exception) {
        ApiResult.error(
            message = e.message ?: "Unknown error occurred",
            throwable = e
        )
    }
}

/**
 * Maps the success value of this [ApiResult] using the given [transform] function.
 */
fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> ApiResult.Error(message, throwable, code)
        is ApiResult.Loading -> ApiResult.Loading
    }
}

/**
 * Maps the success value of this [ApiResult] using the given [transform] suspend function.
 */
suspend fun <T, R> ApiResult<T>.suspendMap(transform: suspend (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> ApiResult.Error(message, throwable, code)
        is ApiResult.Loading -> ApiResult.Loading
    }
}

/**
 * Executes [onSuccess] if this [ApiResult] is a success, [onError] if it's an error,
 * and [onLoading] if it's loading.
 */
fun <T> ApiResult<T>.onResult(
    onSuccess: (T) -> Unit = {},
    onError: (String?, Throwable?) -> Unit = { _, _ -> },
    onLoading: () -> Unit = {}
) {
    when (this) {
        is ApiResult.Success -> onSuccess(data)
        is ApiResult.Error -> onError(message, throwable)
        is ApiResult.Loading -> onLoading()
    }
}

/**
 * Collects the value from this [ApiResult] and applies the appropriate transformation
 * based on the result state.
 */
fun <T, R> ApiResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (String?, Throwable?) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is ApiResult.Success -> onSuccess(data)
        is ApiResult.Error -> onError(message, throwable)
        is ApiResult.Loading -> onLoading()
    }
}
