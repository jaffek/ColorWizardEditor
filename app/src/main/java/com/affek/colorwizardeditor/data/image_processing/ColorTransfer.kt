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
    val sourceMat = Mat()
    val colorMat = Mat()
    Utils.bitmapToMat(sourceImage, sourceMat)
    Utils.bitmapToMat(colorImage, colorMat)
    Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_RGB2Lab)
    Imgproc.cvtColor(colorMat, colorMat, Imgproc.COLOR_RGB2Lab)
    sourceMat.convertTo(sourceMat, CvType.CV_32F)
    colorMat.convertTo(colorMat, CvType.CV_32F)
    val sourceMeanStd = imageStats(sourceMat)
    val colorMeanStd = imageStats(colorMat)

    Core.subtract(sourceMat, Scalar(sourceMeanStd[0].get(0,0)[0], sourceMeanStd[0].get(1,0)[0], sourceMeanStd[0].get(2,0)[0]), sourceMat)
    Core.multiply(sourceMat, Scalar(colorMeanStd[1].get(0,0)[0], colorMeanStd[1].get(1,0)[0], colorMeanStd[1].get(2,0)[0]), sourceMat)
    Core.divide(sourceMat, Scalar(sourceMeanStd[1].get(0,0)[0], sourceMeanStd[1].get(1,0)[0], sourceMeanStd[1].get(2,0)[0]), sourceMat)
    Core.add(sourceMat, Scalar(colorMeanStd[0].get(0,0)[0], colorMeanStd[0].get(1,0)[0], colorMeanStd[0].get(2,0)[0]), sourceMat)

    Core.min(sourceMat, Scalar((255).toDouble(),(255).toDouble(),(255).toDouble()),sourceMat)
    Core.max(sourceMat, Scalar((0).toDouble(),(0).toDouble(),(0).toDouble()),sourceMat)

    sourceMat.convertTo(sourceMat, CvType.CV_8UC4)
    Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_Lab2RGB)
    val finalBitmap = sourceImage!!.copy(sourceImage.config, true)
    Utils.matToBitmap(sourceMat, finalBitmap)
    finalBitmap
}

private fun imageStats(image : Mat) : List<MatOfDouble> {
    val meanImage = MatOfDouble()
    val stdImage = MatOfDouble()
    Core.meanStdDev(image, meanImage, stdImage)
    return listOf(meanImage, stdImage)
}


