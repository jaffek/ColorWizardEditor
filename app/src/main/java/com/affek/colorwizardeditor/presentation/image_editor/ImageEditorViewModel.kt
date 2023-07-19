package com.affek.colorwizardeditor.presentation.image_editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.affek.colorwizardeditor.data.image_processing.colorTransfer
import com.affek.colorwizardeditor.data.image_processing.downScale
import com.affek.colorwizardeditor.data.image_processing.lightEditing
import com.affek.colorwizardeditor.data.repository.ImageRepositoryImpl
import com.affek.colorwizardeditor.domain.model.ImageParams
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.image_editor.components.ImageEditBottomBarItems
import com.affek.colorwizardeditor.presentation.image_editor.components.LightEditPanelSliders
import com.affek.colorwizardeditor.presentation.navArgs
import com.affek.colorwizardeditor.presentation.util.request_full_screen.exitFullScreen
import com.affek.colorwizardeditor.presentation.util.request_full_screen.requestFullScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageEditorViewModel @Inject constructor(
    private val repository: ImageRepositoryImpl,
    @ApplicationContext application: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navArgs: ColorTransferScreenNavArgs = savedStateHandle.navArgs()

    private var sourceImage: Bitmap? = null
    private var colorImage: Bitmap? = null

    private val _state = MutableStateFlow(ImageEditorState(
        isColorTransfer = navArgs.colorImageUri != null
    ))
    var state = _state.asStateFlow()

    val bottomAppItems = ImageEditBottomBarItems.values().filter {
        when(_state.value.isColorTransfer) {
            true -> true
            false -> it != ImageEditBottomBarItems.ColorTransferEditor
        }
    }
    init {
        viewModelScope.launch {
            sourceImage = downScale(repository.uriToBitmap(Uri.parse(navArgs.sourceImageUri)),2000)
            if(navArgs.colorImageUri != null) {
                colorImage = downScale(repository.uriToBitmap(Uri.parse(navArgs.colorImageUri)), 2000)
                _state.update { currentState ->
                    currentState.copy(
                        calculatedImage = colorTransfer(sourceImage, colorImage),
                        isLoading = false
                    )
                }
            } else {
                _state.update { currentState ->
                    currentState.copy(
                        calculatedImage = sourceImage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: ImageEditorEvent) {
        when(event) {
            is ImageEditorEvent.SelectTab -> {
                _state.value = _state.value.copy(
                    bottomBarSelectedItem = event.index,
                    isBottomBarItemSelected = event.isSelected
                )
            }
            is ImageEditorEvent.ExpandDropDownMenu -> {
                _state.value = _state.value.copy(
                    isDropDownMenuExpanded = !_state.value.isDropDownMenuExpanded
                )
            }
            is ImageEditorEvent.FullScreen -> {
                _state.value = _state.value.copy(
                    isFullScreen = !_state.value.isFullScreen
                )
                if(_state.value.isFullScreen)
                    requestFullScreen(event.view)
                else
                    exitFullScreen(event.view)
            }
            is ImageEditorEvent.BasicPanelSliders -> {
                var currentChangesIndex = _state.value.currentIndexOfEditChanges
                val tempValues = _state.value.imageParams[currentChangesIndex].clone()
                tempValues.indexedParams[event.sliderIndex] = event.value
                val tempEditHistory : List<ImageParams>
                if(currentChangesIndex < _state.value.imageParams.size - 1)
                    tempEditHistory = _state.value.imageParams.slice(0..currentChangesIndex).toMutableList()
               else
                    tempEditHistory = _state.value.imageParams.toMutableList()
                if(event.sliderIndex == tempValues.indexOfLastEdited) {
                    tempEditHistory[tempEditHistory.lastIndex] = tempValues
                }
                else {
                    tempValues.indexOfLastEdited = event.sliderIndex
                    tempEditHistory.add(tempValues)
                    currentChangesIndex++
                }
                _state.value = _state.value.copy(
                    imageParams = tempEditHistory,
                    calculatedImage = lightEditing(
                        sourceImage,
                        tempEditHistory.last().indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                        tempEditHistory.last().indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                        tempEditHistory.last().indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                    ),
                    isUndoButtonEnabled = true,
                    isResetButtonEnabled = true,
                    currentIndexOfEditChanges = currentChangesIndex,
                    isRedoButtonEnabled = false
                )
            }
            is ImageEditorEvent.UndoEdit -> {

                val currentIndex = _state.value.currentIndexOfEditChanges - 1
                _state.value = _state.value.copy(
                    calculatedImage = lightEditing(
                        sourceImage,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                    ),
                    isUndoButtonEnabled = currentIndex > 0,
                    isResetButtonEnabled = currentIndex > 0,
                    currentIndexOfEditChanges = currentIndex,
                    isRedoButtonEnabled = true
                )

            }
            is ImageEditorEvent.RedoEdit -> {
                val currentIndex = _state.value.currentIndexOfEditChanges + 1

                _state.value = _state.value.copy(
                    calculatedImage = lightEditing(
                        sourceImage,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                    ),
                    isUndoButtonEnabled = true,
                    isResetButtonEnabled = true,
                    currentIndexOfEditChanges = currentIndex,
                    isRedoButtonEnabled = currentIndex < _state.value.imageParams.size - 1
                )

            }
            is ImageEditorEvent.ResetEdit -> {

                _state.value = _state.value.copy(
                    calculatedImage = lightEditing(
                        sourceImage,
                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                    ),
                    isUndoButtonEnabled = false,
                    isResetButtonEnabled = false,
                    isRedoButtonEnabled = true,
                    currentIndexOfEditChanges = 0
                )
            }
            is ImageEditorEvent.ImagePress -> {
                val currentIndex = _state.value.currentIndexOfEditChanges
                if(event.isImagePressed) {
                    _state.value = _state.value.copy(
                        calculatedImage = lightEditing(
                            sourceImage,
                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                        ),
                        isShowingBeforeEdit = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        calculatedImage = lightEditing(
                            sourceImage,
                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                        ),
                        isShowingBeforeEdit = false
                    )
                }
            }
        }
    }

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