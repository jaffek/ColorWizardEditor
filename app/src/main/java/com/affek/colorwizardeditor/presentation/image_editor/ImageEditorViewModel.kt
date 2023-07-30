package com.affek.colorwizardeditor.presentation.image_editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.affek.colorwizardeditor.data.image_processing.colorEditing
import com.affek.colorwizardeditor.data.image_processing.colorTransfer
import com.affek.colorwizardeditor.data.image_processing.colorTransferIntensity
import com.affek.colorwizardeditor.data.image_processing.downScale
import com.affek.colorwizardeditor.data.image_processing.lightEditing
import com.affek.colorwizardeditor.data.repository.ImageRepositoryImpl
import com.affek.colorwizardeditor.domain.model.ImageParams
import com.affek.colorwizardeditor.navigation.ColorTransferScreenNavArgs
import com.affek.colorwizardeditor.presentation.image_editor.components.ColorEditPanelSliders
import com.affek.colorwizardeditor.presentation.image_editor.components.ColorTransferPanelSliders
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
    private val initialColorTransferIntensity = 1f
    var sourceImage: Bitmap? = null
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
                val tempValues = ImageParams(colorTransferIntensity = initialColorTransferIntensity, lastEditexTabIndex = ColorTransferPanelSliders.ColorTransferIntensity.index)
                colorImage = downScale(repository.uriToBitmap(Uri.parse(navArgs.colorImageUri)), 2000)
                colorImage = colorTransfer(sourceImage, colorImage)
                _state.update { currentState ->
                    currentState.copy(
                        calculatedImage = colorImage,
                        isLoading = false,
                        historyImages = listOf(sourceImage!!, colorImage!!),
                        currentImageParams = tempValues,
                        isBottomBarItemSelected = true,
                        bottomBarSelectedItem = ImageEditBottomBarItems.ColorTransferEditor.index,
                        isAcceptChangesButtonEnabled = true,
                        isDismissChangesButtonEnabled = true
                    )
                }
            } else {
                _state.update { currentState ->
                    currentState.copy(
                        calculatedImage = sourceImage,
                        isLoading = false,
                        historyImages = listOf(sourceImage!!)
                    )
                }
            }
        }
    }

    fun onEvent(event: ImageEditorEvent) {
        when(event) {
            is ImageEditorEvent.SelectTab -> {
                if(_state.value.bottomBarSelectedItem == event.index) {
                    _state.value = _state.value.copy(
                        bottomBarSelectedItem = event.index,
                        isBottomBarItemSelected = event.isSelected
                    )
                }
                else {
                    _state.value = _state.value.copy(
                        calculatedImage = _state.value.historyImages[_state.value.currentIndexOfEditChanges],
                        bottomBarSelectedItem = event.index,
                        isBottomBarItemSelected = event.isSelected,
                        currentImageParams = ImageParams(),
                        isAcceptChangesButtonEnabled = false,
                        isDismissChangesButtonEnabled = false
                    )
                }
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
                //var currentChangesIndex = _state.value.currentIndexOfEditChanges

                var tempValues = _state.value.currentImageParams.clone()
                tempValues.indexedParams[event.sliderIndex] = event.value
                tempValues.lastEditexTabIndex = 1

                //val tempEditHistory : List<ImageParams>
                //if(currentChangesIndex < _state.value.imageParams.size - 1)
                //    tempEditHistory = _state.value.imageParams.slice(0..currentChangesIndex).toMutableList()
                //else
                //    tempEditHistory = _state.value.imageParams.toMutableList()

                //if(_state.value.currentTabIndex == 1) {
                //    tempEditHistory[tempEditHistory.lastIndex] = tempValues
                //}
                //else {
                //    tempValues = ImageParams()
                //    tempValues.indexedParams[event.sliderIndex] = event.value
                //    tempEditHistory.add(tempValues)
                //}

                _state.value = _state.value.copy(
                    //imageParams = tempEditHistory,
                    currentImageParams = tempValues,
                    calculatedImage = lightEditing(
                        _state.value.historyImages[_state.value.currentIndexOfEditChanges],
                        tempValues.indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
                        tempValues.indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
                        tempValues.indexedParams[LightEditPanelSliders.GammaSlider.index]!!
                    ),
                    isAcceptChangesButtonEnabled = true,
                    isDismissChangesButtonEnabled = true
                    //isUndoButtonEnabled = true,
                    //isResetButtonEnabled = true,
                    //currentIndexOfEditChanges = currentChangesIndex,
                    //isRedoButtonEnabled = false
                )
            }
            is ImageEditorEvent.UndoEdit -> {

                val currentIndex = _state.value.currentIndexOfEditChanges - 1
                _state.value = _state.value.copy(
                    calculatedImage = _state.value.historyImages[currentIndex],
//                    lightEditing(
//                        sourceImage,
//                        colorImage,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!
//                    ),
                    isUndoButtonEnabled = currentIndex > 0,
                    isResetButtonEnabled = currentIndex > 0,
                    currentIndexOfEditChanges = currentIndex,
                    isRedoButtonEnabled = true,
                    currentImageParams = ImageParams(),
                    isAcceptChangesButtonEnabled = false,
                    isDismissChangesButtonEnabled = false
                )

            }
            is ImageEditorEvent.RedoEdit -> {
                val currentIndex = _state.value.currentIndexOfEditChanges + 1

                _state.value = _state.value.copy(
                    calculatedImage = _state.value.historyImages[currentIndex],
//                    lightEditing(
//                        sourceImage,
//                        colorImage,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!,
//                        _state.value.imageParams[currentIndex].indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!
//                    ),
                    isUndoButtonEnabled = true,
                    isResetButtonEnabled = true,
                    currentIndexOfEditChanges = currentIndex,
                    isRedoButtonEnabled = currentIndex < _state.value.imageParams.size - 1,
                    currentImageParams = ImageParams(),
                    isAcceptChangesButtonEnabled = false,
                    isDismissChangesButtonEnabled = false
                )

            }
            is ImageEditorEvent.ResetEdit -> {

                _state.value = _state.value.copy(
                    calculatedImage = _state.value.historyImages[0],
//                    lightEditing(
//                        sourceImage,
//                        colorImage,
//                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
//                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
//                        _state.value.imageParams[0].indexedParams[LightEditPanelSliders.GammaSlider.index]!!,
//                        _state.value.imageParams[0].indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!
//                    ),
                    isUndoButtonEnabled = false,
                    isResetButtonEnabled = false,
                    isRedoButtonEnabled = true,
                    currentIndexOfEditChanges = 0,
                    currentImageParams = ImageParams(),
                    isAcceptChangesButtonEnabled = false,
                    isDismissChangesButtonEnabled = false
                )
            }
            is ImageEditorEvent.ImagePress -> {
                val currentIndex = _state.value.currentIndexOfEditChanges
                if(event.isImagePressed) {
                    _state.value = _state.value.copy(
                        //calculatedImage = sourceImage,
//                        lightEditing(
//                            sourceImage,
//                            colorImage,
//                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
//                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
//                            _state.value.imageParams.first().indexedParams[LightEditPanelSliders.GammaSlider.index]!!,
//                            _state.value.imageParams.first().indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!
//
//                        ),
                        isShowingBeforeEdit = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        //calculatedImage = _state.value.historyImages[currentIndex],
//                        lightEditing(
//                            sourceImage,
//                            colorImage,
//                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ExposureSlider.index]!!,
//                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.ContrastSlider.index]!!,
//                            _state.value.imageParams[currentIndex].indexedParams[LightEditPanelSliders.GammaSlider.index]!!,
//                            _state.value.imageParams[currentIndex].indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!
//                        ),
                        isShowingBeforeEdit = false
                    )
                }
            }
            is ImageEditorEvent.ColorTransferPanelSliders -> {
                var tempValues = _state.value.currentImageParams.clone()
                tempValues.indexedParams[event.sliderIndex] = event.value
                tempValues.lastEditexTabIndex = 0
//                var currentChangesIndex = _state.value.currentIndexOfEditChanges
//                val tempValues = _state.value.imageParams[currentChangesIndex].clone()
//                tempValues.indexedParams[event.sliderIndex] = event.value
//                val tempEditHistory : List<ImageParams>
//                if(currentChangesIndex < _state.value.imageParams.size - 1)
//                    tempEditHistory = _state.value.imageParams.slice(0..currentChangesIndex).toMutableList()
//                else
//                    tempEditHistory = _state.value.imageParams.toMutableList()
//                if(event.sliderIndex == tempValues.indexOfLastEditedSlider) {
//                    tempEditHistory[tempEditHistory.lastIndex] = tempValues
//                }
//                else {
//                    tempValues.indexOfLastEditedSlider = event.sliderIndex
//                    tempEditHistory.add(tempValues)
//                    currentChangesIndex++
//                }
                _state.value = _state.value.copy(
                    //imageParams = tempEditHistory,
                    currentImageParams = tempValues,
                    calculatedImage = colorTransferIntensity(
                        _state.value.historyImages[_state.value.currentIndexOfEditChanges],
                        colorImage,
                        tempValues.indexedParams[ColorTransferPanelSliders.ColorTransferIntensity.index]!!,
                        tempValues.indexedParams[ColorTransferPanelSliders.ColorTransferExposureCompensation.index]!!
                    ),
                    isAcceptChangesButtonEnabled = true,
                    isDismissChangesButtonEnabled = true
                )
            }
            is ImageEditorEvent.AcceptEdit -> {
                var currentChangesIndex = _state.value.currentIndexOfEditChanges
                val tempEditHistory : List<ImageParams>
                val tempImageHistory: List<Bitmap>
                if(currentChangesIndex < _state.value.imageParams.size - 1) {
                    tempEditHistory = _state.value.imageParams.slice(0..currentChangesIndex).toMutableList()
                    tempImageHistory = _state.value.historyImages.slice(0..currentChangesIndex).toMutableList()
                }
                else {
                    tempEditHistory = _state.value.imageParams.toMutableList()
                    tempImageHistory = _state.value.historyImages.toMutableList()
                }
                tempEditHistory.add(_state.value.currentImageParams)
                tempImageHistory.add(_state.value.calculatedImage!!)

                _state.value = _state.value.copy(
                    imageParams = tempEditHistory,
                    currentImageParams = ImageParams(),
                    historyImages = tempImageHistory,
                    isUndoButtonEnabled = true,
                    isResetButtonEnabled = true,
                    isRedoButtonEnabled = false,
                    isAcceptChangesButtonEnabled = false,
                    isDismissChangesButtonEnabled = false,
                    currentIndexOfEditChanges = currentChangesIndex + 1
                )
            }
            is ImageEditorEvent.DismissEdit -> {
                _state.value = _state.value.copy(
                    calculatedImage = _state.value.historyImages[_state.value.currentIndexOfEditChanges],
                    currentImageParams = ImageParams(),
                    isAcceptChangesButtonEnabled = false,
                    isDismissChangesButtonEnabled = false
                )
            }
            is ImageEditorEvent.ColorPanelSliders -> {
                //var currentChangesIndex = _state.value.currentIndexOfEditChanges

                var tempValues = _state.value.currentImageParams.clone()
                tempValues.indexedParams[event.sliderIndex] = event.value
                tempValues.lastEditexTabIndex = 2

                //val tempEditHistory : List<ImageParams>
                //if(currentChangesIndex < _state.value.imageParams.size - 1)
                //    tempEditHistory = _state.value.imageParams.slice(0..currentChangesIndex).toMutableList()
                //else
                //    tempEditHistory = _state.value.imageParams.toMutableList()

                //if(_state.value.currentTabIndex == 1) {
                //    tempEditHistory[tempEditHistory.lastIndex] = tempValues
                //}
                //else {
                //    tempValues = ImageParams()
                //    tempValues.indexedParams[event.sliderIndex] = event.value
                //    tempEditHistory.add(tempValues)
                //}

                _state.value = _state.value.copy(
                    //imageParams = tempEditHistory,
                    currentImageParams = tempValues,
                    calculatedImage = colorEditing(
                        _state.value.historyImages[_state.value.currentIndexOfEditChanges],
                        tempValues.indexedParams[ColorEditPanelSliders.SaturationSlider.index]!!
                    ),
                    isAcceptChangesButtonEnabled = true,
                    isDismissChangesButtonEnabled = true
                    //isUndoButtonEnabled = true,
                    //isResetButtonEnabled = true,
                    //currentIndexOfEditChanges = currentChangesIndex,
                    //isRedoButtonEnabled = false
                )
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