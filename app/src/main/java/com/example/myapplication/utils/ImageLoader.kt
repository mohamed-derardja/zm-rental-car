package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication.R

/**
 * A wrapper around Coil's AsyncImage with common configurations
 */
@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Int = R.drawable.edit_icon,
    error: Int = R.drawable.edit_icon
) {
    val context = LocalContext.current
    val imageUrl = url?.takeIf { it.isNotBlank() }
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .placeholder(placeholder)
            .error(error)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Load an image from a local file or resource
 */
@Composable
fun LocalImage(
    uri: Uri?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(uri)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Remember an image painter for use with Image composable
 */
@Composable
fun rememberImagePainter(
    url: String?,
    placeholder: Int = R.drawable.edit_icon,
    error: Int = R.drawable.edit_icon
) = rememberAsyncImagePainter(
    ImageRequest.Builder(LocalContext.current)
        .data(url)
        .apply {
            placeholder(placeholder)
            error(error)
            crossfade(true)
        }
        .build()
)

/**
 * Image loading states
 */
sealed class ImageLoadState {
    object Loading : ImageLoadState()
    data class Success(val imageUrl: String) : ImageLoadState()
    data class Error(val throwable: Throwable) : ImageLoadState()
}
