package com.wajahatkarim.groupphotos.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wajahatkarim.groupphotos.data.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProcessingState {
    object Idle : ProcessingState()
    object Processing : ProcessingState()
    data class Success(val bitmap: Bitmap) : ProcessingState()
    data class Error(val message: String) : ProcessingState()
}

class PhotoViewModel : ViewModel() {

    private val geminiService = GeminiService()

    var groupPhotoUri by mutableStateOf<Uri?>(null)
        private set

    var photographerPhotoUri by mutableStateOf<Uri?>(null)
        private set

    var resultBitmap by mutableStateOf<Bitmap?>(null)
        private set

    private val _processingState = MutableStateFlow<ProcessingState>(ProcessingState.Idle)
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    // Temporary URI for camera capture
    var tempCameraUri by mutableStateOf<Uri?>(null)

    fun setGroupPhoto(uri: Uri?) {
        groupPhotoUri = uri
    }

    fun setPhotographerPhoto(uri: Uri?) {
        photographerPhotoUri = uri
    }

    fun clearAll() {
        groupPhotoUri = null
        photographerPhotoUri = null
        resultBitmap = null
        tempCameraUri = null
        _processingState.value = ProcessingState.Idle
    }

    fun hasGroupPhoto(): Boolean = groupPhotoUri != null

    fun hasPhotographerPhoto(): Boolean = photographerPhotoUri != null

    /**
     * Start the photo merging process
     */
    fun mergePhotos(context: Context) {
        val groupUri = groupPhotoUri
        val photographerUri = photographerPhotoUri

        if (groupUri == null || photographerUri == null) {
            _processingState.value = ProcessingState.Error("Both photos are required")
            return
        }

        viewModelScope.launch {
            _processingState.value = ProcessingState.Processing

            val result = geminiService.mergePhotos(context, groupUri, photographerUri)

            result.fold(
                onSuccess = { bitmap ->
                    resultBitmap = bitmap
                    _processingState.value = ProcessingState.Success(bitmap)
                },
                onFailure = { error ->
                    _processingState.value = ProcessingState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    /**
     * Reset processing state to idle
     */
    fun resetProcessingState() {
        _processingState.value = ProcessingState.Idle
    }
}
