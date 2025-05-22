package com.example.myapplication.data.api

/**
 * A generic wrapper class that contains data and status about loading the data.
 */
data class ApiResource<out T>(
    val status: ApiStatus,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> loading(data: T? = null): ApiResource<T> {
            return ApiResource(ApiStatus.LOADING, data)
        }

        fun <T> success(data: T): ApiResource<T> {
            return ApiResource(ApiStatus.SUCCESS, data)
        }

        fun <T> error(message: String, data: T? = null): ApiResource<T> {
            return ApiResource(ApiStatus.ERROR, data, message)
        }
    }
} 