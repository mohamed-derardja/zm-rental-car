package com.example.myapplication.data.api

/**
 * Data class representing paginated responses from the API.
 */
data class PagedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val last: Boolean = false,
    val first: Boolean = false,
    val empty: Boolean = false
) 