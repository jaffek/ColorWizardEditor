package com.affek.colorwizardeditor.image_picker.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.affek.colorwizardeditor.image_picker.data.repository.ImagePickRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickImageViewModel @Inject constructor(
    private val repository: ImagePickRepository
) : ViewModel() {

    fun saveImage(uri: Uri?) {
        viewModelScope.launch {
            repository.saveImage(uri)
        }
    }

}