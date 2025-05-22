package com.example.myapplication.data.api

/**
 * Enum representing different statuses of an API response.
 */
enum class ApiStatus {
    /**
     * The API request is still in progress.
     */
    LOADING,
    
    /**
     * The API request has completed successfully.
     */
    SUCCESS,
    
    /**
     * The API request has failed with an error.
     */
    ERROR
} 