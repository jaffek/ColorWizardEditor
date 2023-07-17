package com.affek.colorwizardeditor.data.repository


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.affek.colorwizardeditor.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context) : ImageRepository {
//    suspend fun saveImage(uri : Uri?, id : String): Unit {
//
//        val image = uriToBitmap(uri)
//        Log.e("TAGYZ", getFileNameFromUri(uri))
//
//        loadInternalStorageImages()
//            .filter { it.name.startsWith(id) }
//            .forEach(){
//                deleteImage(it.name)
//            }
//        val fileName = getFileNameFromUri(uri)
//        context.openFileOutput("$id" + "_" + "$fileName", MODE_PRIVATE).use { stream ->
//            if(!image.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
//                throw IOException()
//            }
//        }
//    }
//
//    suspend fun loadImage(id: String) : Bitmap {
//        val images = loadInternalStorageImages()
//        return images.filter { it.name.startsWith(id) }[0].bmp ?: throw IOException()
//    }
//
//    suspend fun deleteImage(fileName : String) {
//        context.deleteFile(fileName)
//    }
//
//    private fun loadInternalStorageImages() : List<InternalStorageImage> {
//        val files = context.filesDir.listFiles()
//        return files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
//               val bytes = it.readBytes()
//               val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//               InternalStorageImage(it.name, bmp)
//               } ?: listOf()
//    }
//



    override suspend fun uriToBitmap(uri : Uri?) : Bitmap = withContext(Dispatchers.IO){

        if (Build.VERSION.SDK_INT < 28)
        {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        else
        {
            val source = ImageDecoder.createSource(context.contentResolver, uri!!)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        }
    }

//    private fun getFileNameFromUri(uri: Uri?): String {
//        uri?.let {
//            context.contentResolver.query(uri, null, null, null, null)
//        }?.use {cursor ->
//            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            cursor.moveToFirst()
//            val fileName = cursor.getString(nameIndex)
//            return fileName
//        }
//        throw IOException()
//    }
}