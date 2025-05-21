package com.example.myapplication.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    
    /**
     * Create a file from a URI
     */
    fun createFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File.createTempFile("temp_${System.currentTimeMillis()}", getFileExtension(context, uri))
            file.deleteOnExit()
            
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get file extension from URI
     */
    private fun getFileExtension(context: Context, uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return ".${mime.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"}"
    }
    
    /**
     * Create a multipart body part from a file
     */
    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
    
    /**
     * Get MIME type from file extension
     */
    fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "application/octet-stream"
        }
    }
    
    /**
     * Get file size in a human-readable format
     */
    fun getReadableFileSize(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var fileSize = size.toDouble()
        var i = 0
        
        while (fileSize >= 1024 && i < units.size - 1) {
            fileSize /= 1024
            i++
        }
        
        return "%.1f %s".format(fileSize, units[i])
    }
}
