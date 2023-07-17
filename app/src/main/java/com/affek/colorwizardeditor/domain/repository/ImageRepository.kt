package com.affek.colorwizardeditor.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {

   suspend fun uriToBitmap(uri : Uri?) : Bitmap
}