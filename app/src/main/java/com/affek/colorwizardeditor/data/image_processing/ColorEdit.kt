package com.affek.colorwizardeditor.data.image_processing


import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun colorEditing(sourceImage: Bitmap?, saturation: Float): Bitmap{

    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    colorEdit(sourceImage, finalBitmap, saturation)
    return finalBitmap
}

external fun colorEdit(sourceBitmap: Bitmap, finalBitmap: Bitmap, saturation: Float)
