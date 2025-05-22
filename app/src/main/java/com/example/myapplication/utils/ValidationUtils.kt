package com.example.myapplication.utils

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import java.util.regex.Pattern

/**
 * Utility class for input validation
 */
object ValidationUtils {
    // Regular expressions
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    )
    private val PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,13}$")
    private val NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$")
    private val LICENSE_PATTERN = Pattern.compile("^[A-Z0-9]{5,15}$")
    private val CREDIT_CARD_PATTERN = Pattern.compile("^[0-9]{16}$")
    private val CVV_PATTERN = Pattern.compile("^[0-9]{3,4}$")
    private val EXPIRY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$")

    /**
     * Validate email address
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password
     * Requirements:
     * - At least 8 characters
     * - At least one digit
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one special character
     * - No whitespace
     */
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && PASSWORD_PATTERN.matcher(password).matches()
    }

    /**
     * Validate phone number
     * Requirements:
     * - 10-13 digits
     * - Optional + prefix
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.isNotBlank() && PHONE_PATTERN.matcher(phone).matches()
    }

    /**
     * Validate name
     * Requirements:
     * - 2-50 characters
     * - Only letters and spaces
     */
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && NAME_PATTERN.matcher(name).matches()
    }

    /**
     * Validate driver's license number
     * Requirements:
     * - 5-15 characters
     * - Only uppercase letters and numbers
     */
    fun isValidLicenseNumber(license: String): Boolean {
        return license.isNotBlank() && LICENSE_PATTERN.matcher(license).matches()
    }

    /**
     * Validate credit card number
     * Requirements:
     * - Exactly 16 digits
     */
    fun isValidCreditCard(cardNumber: String): Boolean {
        return cardNumber.isNotBlank() && CREDIT_CARD_PATTERN.matcher(cardNumber).matches()
    }

    /**
     * Validate CVV
     * Requirements:
     * - 3-4 digits
     */
    fun isValidCVV(cvv: String): Boolean {
        return cvv.isNotBlank() && CVV_PATTERN.matcher(cvv).matches()
    }

    /**
     * Validate card expiry date
     * Requirements:
     * - Format: MM/YY
     * - Valid month (01-12)
     * - Valid year (current year or later)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isValidExpiryDate(expiry: String): Boolean {
        if (!EXPIRY_PATTERN.matcher(expiry).matches()) {
            return false
        }

        val parts = expiry.split("/")
        val month = parts[0].toInt()
        val year = parts[1].toInt()
        val currentYear = java.time.Year.now().value % 100
        val currentMonth = java.time.MonthDay.now().monthValue

        return when {
            year < currentYear -> false
            year == currentYear && month < currentMonth -> false
            else -> true
        }
    }

    /**
     * Validate rental dates
     * Requirements:
     * - Start date must be today or later
     * - End date must be after start date
     * - Maximum rental duration is 30 days
     */
    fun isValidRentalDates(startDate: String, endDate: String): Boolean {
        val start = DateTimeUtils.parseDate(startDate) ?: return false
        val end = DateTimeUtils.parseDate(endDate) ?: return false
        val today = java.util.Date()

        return when {
            start.before(today) -> false
            end.before(start) -> false
            DateTimeUtils.getDaysBetween(start, end) > 30 -> false
            else -> true
        }
    }

    /**
     * Validate age for car rental
     * Requirements:
     * - Must be at least 21 years old
     */
    fun isValidAgeForRental(birthDate: String): Boolean {
        val birth = DateTimeUtils.parseDate(birthDate) ?: return false
        val today = java.util.Date()
        val age = (today.time - birth.time) / (1000L * 60 * 60 * 24 * 365)
        return age >= 21
    }

    /**
     * Validate address
     * Requirements:
     * - Not blank
     * - At least 5 characters
     */
    fun isValidAddress(address: String): Boolean {
        return address.isNotBlank() && address.length >= 5
    }

    /**
     * Validate postal code
     * Requirements:
     * - Not blank
     * - 5-10 characters
     */
    fun isValidPostalCode(postalCode: String): Boolean {
        return postalCode.isNotBlank() && postalCode.length in 5..10
    }

    /**
     * Validate city
     * Requirements:
     * - Not blank
     * - 2-50 characters
     * - Only letters, spaces, and hyphens
     */
    fun isValidCity(city: String): Boolean {
        return city.isNotBlank() && 
               city.length in 2..50 && 
               city.matches(Regex("^[a-zA-Z\\s-]+$"))
    }

    /**
     * Validate state/province
     * Requirements:
     * - Not blank
     * - 2-50 characters
     * - Only letters and spaces
     */
    fun isValidState(state: String): Boolean {
        return state.isNotBlank() && 
               state.length in 2..50 && 
               state.matches(Regex("^[a-zA-Z\\s]+$"))
    }

    /**
     * Validate country
     * Requirements:
     * - Not blank
     * - 2-50 characters
     * - Only letters and spaces
     */
    fun isValidCountry(country: String): Boolean {
        return country.isNotBlank() && 
               country.length in 2..50 && 
               country.matches(Regex("^[a-zA-Z\\s]+$"))
    }
}