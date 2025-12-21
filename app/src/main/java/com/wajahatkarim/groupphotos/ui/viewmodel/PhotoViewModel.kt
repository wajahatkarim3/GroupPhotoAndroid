package com.wajahatkarim.groupphotos.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {

    var groupPhotoUri by mutableStateOf<Uri?>(null)
        private set

    var photographerPhotoUri by mutableStateOf<Uri?>(null)
        private set

    var resultPhotoUri by mutableStateOf<Uri?>(null)
        private set

    // Temporary URI for camera capture
    var tempCameraUri by mutableStateOf<Uri?>(null)

    fun setGroupPhoto(uri: Uri?) {
        groupPhotoUri = uri
    }

    fun setPhotographerPhoto(uri: Uri?) {
        photographerPhotoUri = uri
    }

    fun setResultPhoto(uri: Uri?) {
        resultPhotoUri = uri
    }

    fun clearAll() {
        groupPhotoUri = null
        photographerPhotoUri = null
        resultPhotoUri = null
        tempCameraUri = null
    }

    fun hasGroupPhoto(): Boolean = groupPhotoUri != null

    fun hasPhotographerPhoto(): Boolean = photographerPhotoUri != null
}
