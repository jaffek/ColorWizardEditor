package com.affek.colorwizardeditor.presentation.image_editor

import android.view.View

sealed class ImageEditorEvent {

    data class SelectTab(val index: Int, val isSelected: Boolean): ImageEditorEvent()
    object ExpandDropDownMenu : ImageEditorEvent()
    data class FullScreen(val view: View) : ImageEditorEvent()
    data class BasicPanelSliders(val value: Float, val sliderIndex: Int) : ImageEditorEvent()
    object UndoEdit : ImageEditorEvent()


}