package com.affek.colorwizardeditor.data.image_processing

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


suspend fun downScale(sourceImage: Bitmap?, maxSize: Int): Bitmap = withContext(Dispatchers.Default) {
    if(maxOf(sourceImage!!.height,sourceImage!!.width) > maxSize) {
        val mat = Mat()
        Utils.bitmapToMat(sourceImage, mat)
        var scale = maxSize.toDouble() / maxOf(mat.height(), mat.width())

        val newHeight = (mat.height().toDouble() * scale)
        val newWidth = (mat.width().toDouble() * scale)
        Imgproc.resize(mat, mat, Size(newWidth, newHeight), 1.toDouble(),1.toDouble(),Imgproc.INTER_CUBIC)

        val conf = Bitmap.Config.ARGB_8888;

        var finalBitmap = Bitmap.createBitmap(mat.width(), mat.height(), conf)
        Utils.matToBitmap(mat, finalBitmap)
        finalBitmap
    }
    else
        sourceImage
}
