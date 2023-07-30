package com.affek.colorwizardeditor.data.image_processing

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun lightEditing(sourceImage: Bitmap?, exposureVal: Float, contrastVal: Float, gamma: Float): Bitmap{

    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    exposureContrast(sourceImage, finalBitmap, exposureVal, contrastVal, gamma)
    return finalBitmap
}

external fun exposureContrast(sourceBitmap: Bitmap, finalBitmap: Bitmap, exposureVal: Float, contrastVal: Float, gamma: Float)
