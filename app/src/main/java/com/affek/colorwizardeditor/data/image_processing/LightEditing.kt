package com.affek.colorwizardeditor.data.image_processing

import android.graphics.Bitmap

fun lightEditing(sourceImage: Bitmap?, colorImage: Bitmap?, exposureVal: Float, contrastVal: Float, gamma: Float, colorIntensity: Float): Bitmap{

    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    if(colorImage != null) {
        val finalBitmap1 = sourceImage!!.copy(sourceImage.config, true)
        colorTransferIntenisty(sourceImage, colorImage, finalBitmap1, colorIntensity)
        exposureContrast( finalBitmap1!!, finalBitmap!!, exposureVal, contrastVal, gamma)
    } else
        exposureContrast(sourceImage!!, finalBitmap!!, exposureVal, contrastVal, gamma)
    return finalBitmap
}

external fun exposureContrast(sourceBitmap: Bitmap, finalBitmap: Bitmap, exposureVal: Float, contrastVal: Float, gamma: Float)
external fun colorTransferIntenisty(sourceBitmap: Bitmap, colorBitmap: Bitmap, finalBitmap: Bitmap, colorIntensity: Float)