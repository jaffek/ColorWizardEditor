package com.affek.colorwizardeditor.data.image_processing

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

suspend fun colorTransfer(sourceImage: Bitmap?, colorImage: Bitmap?): Bitmap = withContext(Dispatchers.Default) {
    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    colorTransferC(sourceImage!!, colorImage!!, finalBitmap)
    finalBitmap
}


fun colorTransferIntensity(sourceImage: Bitmap?, colorImage: Bitmap?, colorIntensity: Float, exposureCompensation: Float): Bitmap{

    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    colorTransferIntensity(sourceImage, colorImage!!, finalBitmap, colorIntensity, exposureCompensation)

    return finalBitmap
}
external fun colorTransferC(sourceBitmap: Bitmap, colorBitmap: Bitmap, finalBitmap: Bitmap)

external fun colorTransferIntensity(sourceBitmap: Bitmap, colorBitmap: Bitmap, finalBitmap: Bitmap, colorIntensity: Float, exposureCompensation: Float)
