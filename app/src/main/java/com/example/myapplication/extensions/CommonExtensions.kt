package com.example.myapplication.extensions

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to show a toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Extension function to format a date string
 */
fun String.toFormattedDate(inputFormat: String, outputFormat: String): String {
    return try {
        val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        val date = inputFormatter.parse(this) ?: return this
        outputFormatter.format(date)
    } catch (e: Exception) {
        this
    }
}

/**
 * Extension function to format a number as currency
 */
fun Number.toCurrencyFormat(currencyCode: String = "USD"): String {
    val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(currencyCode)
    return format.format(this)
}

/**
 * Extension function to get file name from URI
 */
fun Uri.getFileName(context: Context): String {
    var result = ""
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                result = cursor.getString(nameIndex)
            }
        }
    }
    return result.ifEmpty { "file_${System.currentTimeMillis()}" }
}

/**
 * Extension function to handle loading states
 */
@Composable
fun <T> Flow<T>.collectAsState(
    initial: T,
    context: Context = LocalContext.current,
    onError: ((Throwable) -> Unit)? = null
): State<T> {
    val state = remember { mutableStateOf(initial) }
    
    LaunchedEffect(key1 = this) {
        try {
            this@collectAsState.collect { value ->
                state.value = value
            }
        } catch (e: Exception) {
            onError?.invoke(e)
            context.showToast("An error occurred")
        }
    }
    
    return state
}

/**
 * Extension function to handle loading states with error handling
 */
@Composable
fun <T> Flow<Result<T>>.collectAsState(
    initial: T? = null,
    onSuccess: (T) -> Unit = {},
    onError: (Throwable) -> Unit = {}
): State<T?> {
    val state = remember { mutableStateOf(initial) }
    
    LaunchedEffect(key1 = this) {
        this@collectAsState.collect { result ->
            result.fold(
                onSuccess = {
                    state.value = it
                    onSuccess(it)
                },
                onFailure = {
                    onError(it)
                }
            )
        }
    }
    
    return state
}

/**
 * Extension function to observe a Flow with loading and error states
 */
@Composable
fun <T> Flow<T>.observe(
    initial: T? = null,
    onLoading: @Composable () -> Unit = { /* Default loading state */ },
    onError: @Composable (Throwable) -> Unit = { /* Default error state */ },
    onSuccess: @Composable (T) -> Unit
) {
    val state = remember(initial) { mutableStateOf<Result<T>?>(null) }
    
    LaunchedEffect(key1 = this) {
        this@observe
            .onEach { value ->
                state.value = Result.success(value)
            }
            .catch { e ->
                state.value = Result.failure(e)
            }
            .collect()
    }
    
    when (val currentState = state.value) {
        null -> onLoading()
        else -> {
            currentState.fold(
                onSuccess = { value -> onSuccess(value) },
                onFailure = { error -> onError(error) }
            )
        }
    }
}
