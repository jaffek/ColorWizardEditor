package com.affek.colorwizardeditor.presentation.color_transfer

import android.graphics.Bitmap

data class ColorTransferState(
    val isLoading : Boolean = true,
    val calculatedImage : Bitmap? = null
)
