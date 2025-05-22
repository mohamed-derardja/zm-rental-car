package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for date and time operations
 */
object DateTimeUtils {
    
    // Common date formats
    const val SERVER_DATE_FORMAT = "yyyy-MM-dd"
    const val SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    const val DISPLAY_TIME_FORMAT = "hh:mm a"
    const val DISPLAY_DATE_TIME_FORMAT = "MMM dd, yyyy hh:mm a"
    
    /**
     * Get current date in the specified format
     */
    fun getCurrentDate(format: String = SERVER_DATE_FORMAT): String {
        return SimpleDateFormat(format, Locale.getDefault())
            .format(Date())
    }
    
    /**
     * Format a date string from one format to another
     */
    fun formatDate(
        dateString: String,
        inputFormat: String = SERVER_DATE_FORMAT,
        outputFormat: String = DISPLAY_DATE_FORMAT
    ): String {
        return try {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            val date = inputFormatter.parse(dateString) ?: return dateString
            outputFormatter.format(date)
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Format a date to a relative time string (e.g., "2 hours ago")
     */
    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diffInSeconds = (now.time - date.time) / 1000
        
        return when {
            diffInSeconds < 60 -> "Just now"
            diffInSeconds < 3600 -> "${diffInSeconds / 60} minutes ago"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} hours ago"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400} days ago"
            diffInSeconds < 2592000 -> "${diffInSeconds / 604800} weeks ago"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} months ago"
            else -> "${diffInSeconds / 31536000} years ago"
        }
    }
    
    /**
     * Parse a date string to Date object
     */
    fun parseDate(
        dateString: String,
        format: String = SERVER_DATE_FORMAT
    ): Date? {
        return try {
            SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Format a Date object to string
     */
    fun formatDate(
        date: Date,
        format: String = DISPLAY_DATE_FORMAT
    ): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }
    
    /**
     * Check if a date is today
     */
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == inputDate.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Get the difference between two dates in days
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Int {
        val diffInMillies = endDate.time - startDate.time
        return (diffInMillies / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * Add days to a date
     */
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, days)
        return calendar.time
    }
}
