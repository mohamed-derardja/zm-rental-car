package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Extension function to show a toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Extension function to show a snackbar message
 */
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

/**
 * Extension function to show a snackbar message with action
 */
fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    Snackbar.make(this, message, duration)
        .setAction(actionText) { action() }
        .show()
}

/**
 * Extension function to check if the device has internet connection
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

/**
 * Extension function to show a toast message in a Fragment
 */
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

/**
 * Extension function to show a snackbar message in a Fragment
 */
fun Fragment.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    requireView().showSnackbar(message, duration)
}

/**
 * Extension function to show a snackbar message with action in a Fragment
 */
fun Fragment.showSnackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    requireView().showSnackbarWithAction(message, actionText, duration, action)
}

/**
 * Extension function to check if the device has internet connection in a Fragment
 */
fun Fragment.isNetworkAvailable(): Boolean {
    return requireContext().isNetworkAvailable()
}

/**
 * Extension function to format a price with currency symbol
 */
fun Double.formatPrice(currencySymbol: String = "$"): String {
    return "$currencySymbol${String.format(Locale.US, "%.2f", this)}"
}

/**
 * Extension function to format a date string
 */
fun String.formatDate(inputFormat: String = "yyyy-MM-dd", outputFormat: String = "MMM dd, yyyy"): String {
    return try {
        val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date = inputFormatter.parse(this)
        date?.let { outputFormatter.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

/**
 * Extension function to format a phone number
 */
fun String.formatPhoneNumber(): String {
    return if (length == 10 && all { it.isDigit() }) {
        "(${substring(0, 3)}) ${substring(3, 6)}-${substring(6)}"
    } else {
        this
    }
}

/**
 * Extension function to validate an email address
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Extension function to validate a password
 */
fun String.isValidPassword(): Boolean {
    return length >= 8 && any { it.isDigit() } && any { it.isUpperCase() } && any { it.isLowerCase() }
}

/**
 * Extension function to validate a phone number
 */
fun String.isValidPhoneNumber(): Boolean {
    return length == 10 && all { it.isDigit() }
}

/**
 * Extension function to capitalize the first letter of each word
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Extension function to truncate text with ellipsis
 */
fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) {
        "${substring(0, maxLength)}..."
    } else {
        this
    }
}

/**
 * Extension function to show a snackbar with a message
 */
@Composable
fun ShowSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel
        )
        when (result) {
            SnackbarResult.ActionPerformed -> onAction?.invoke()
            SnackbarResult.Dismissed -> { /* Handle dismissal if needed */ }
        }
    }
}

/**
 * Extension function to show a snackbar with a message and action
 */
fun SnackbarHostState.showSnackbarWithAction(
    scope: CoroutineScope,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    scope.launch {
        val result = showSnackbar(
            message = message,
            actionLabel = actionLabel
        )
        if (result == SnackbarResult.ActionPerformed) {
            onAction()
        }
    }
}

/**
 * Extension function to show a snackbar with a message and duration
 */
fun SnackbarHostState.showSnackbarWithDuration(
    scope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    scope.launch {
        showSnackbar(
            message = message,
            duration = duration
        )
    }
}

/**
 * Extension function to show a snackbar with a message, action, and duration
 */
fun SnackbarHostState.showSnackbarWithActionAndDuration(
    scope: CoroutineScope,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    scope.launch {
        val result = showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
        if (result == SnackbarResult.ActionPerformed) {
            onAction()
        }
    }
}

/**
 * Composable function to show a snackbar with a message and error styling
 */
@Composable
fun ShowErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
        when (result) {
            SnackbarResult.ActionPerformed -> onAction?.invoke()
            SnackbarResult.Dismissed -> { /* Handle dismissal if needed */ }
        }
    }
}

/**
 * Composable function to show a snackbar with a message and success styling
 */
@Composable
fun ShowSuccessSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short
        )
        when (result) {
            SnackbarResult.ActionPerformed -> onAction?.invoke()
            SnackbarResult.Dismissed -> { /* Handle dismissal if needed */ }
        }
    }
}