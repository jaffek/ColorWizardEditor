package com.affek.colorwizardeditor.presentation.color_transfer

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.affek.colorwizardeditor.data.image_processing.colorTransfer
import com.affek.colorwizardeditor.data.repository.ImageRepository
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColorTransferViewModel @Inject constructor(
    private val repository: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val navArgs: ColorTransferScreenNavArgs = savedStateHandle.navArgs()
    var sourceImage: Bitmap? = null
    var colorImage: Bitmap? = null
    private val _state = MutableStateFlow(ColorTransferState())
    var state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sourceImage = repository.uriToBitmap(Uri.parse(navArgs.sourceImageUri))
            colorImage = repository.uriToBitmap(Uri.parse(navArgs.colorImageUri))
            _state.update { currentState ->
                currentState.copy(calculatedImage = colorTransfer(sourceImage, colorImage),
                isLoading = false)
             }
        }
    }
//    init {
//        viewModelScope.launch {
//            sourceImage = repository.uriToBitmap(Uri.parse(navArgs.sourceImageUri))
//            colorImage = repository.uriToBitmap(Uri.parse(navArgs.colorImageUri))
//            calculatedImage.value = colorTransfer(sourceImage, colorImage)
//        }
//
//    }




//    fun loadImage(id: String) : Bitmap? {
//        var image : Bitmap? = null
//        viewModelScope.launch {
//            try {
//                image = repository.loadImage(id)
//            }
//            catch (e: Exception) {
//            }
//        }
//        return image
//    }

}