package com.affek.colorwizardeditor.presentation.image_editor

import android.graphics.Bitmap
import com.affek.colorwizardeditor.domain.model.ImageParams
import com.affek.colorwizardeditor.presentation.image_editor.components.ColorTransferPanelSliders
import com.affek.colorwizardeditor.presentation.image_editor.components.LightEditPanelSliders

data class ImageEditorState(
    val isColorTransfer: Boolean,
    val isLoading: Boolean = true,
    val calculatedImage: Bitmap? = null,
    val bottomBarSelectedItem: Int = 0,
    val isBottomBarItemSelected: Boolean = false,
    val isDropDownMenuExpanded: Boolean = false,
    val isFullScreen: Boolean = false,
    val imageParams: List<ImageParams> = listOf(ImageParams()),
    val isUndoButtonEnabled: Boolean = false,
    val isRedoButtonEnabled: Boolean = false,
    val isResetButtonEnabled: Boolean = false,
    val isShowingBeforeEdit: Boolean = false,
    val isAcceptChangesButtonEnabled: Boolean = false,
    val isDismissChangesButtonEnabled: Boolean = false,
    val currentIndexOfEditChanges: Int = 0,
    val historyImages: List<Bitmap> = emptyList(),
    val currentImageParams : ImageParams = ImageParams()

)

