package hai.ithust.photopicker.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author conghai on 12/20/18.
 * http://developer.android.com/training/camera/photobasics.html
 */
class ImageCaptureManager(private val context: Context) {
    private var currentPath: String? = null
    var currentUri: Uri? = null

    @Throws(IOException::class)
    fun dispatchTakePictureIntent(): Intent {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            // Create the File where the photo should go
            val photoUri: Uri? = createImageUri()

            // Continue only if the File was successfully created
            if (photoUri != null) {
                currentUri = photoUri
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
        }
        return takePictureIntent
    }

    @Suppress("deprecation")
    fun galleryAddPic() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !currentPath.isNullOrEmpty()) {
            MediaScannerConnection.scanFile(context.applicationContext, arrayOf(currentPath), null, null)
        }
    }

    fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(CAPTURED_PHOTO_URI_KEY, currentUri)
        savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, currentPath)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        currentUri = savedInstanceState?.getParcelable(CAPTURED_PHOTO_URI_KEY)
        currentPath = savedInstanceState?.getParcelable(CAPTURED_PHOTO_PATH_KEY)
    }

    @Suppress("deprecation")
    @Throws(IOException::class, NullPointerException::class)
    private fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val fileName = "JPEG_$timeStamp.jpg"
        val values = ContentValues(4).apply {
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.DATE_ADDED, (System.currentTimeMillis() / 1000).toInt())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!storageDir.exists()) {
                    if (!storageDir.mkdir()) {
                        Log.e("TAG", "Throwing Errors....")
                        throw IOException()
                    }
                }
                val image = File(storageDir, fileName)
                currentPath = image.absolutePath
                put(MediaStore.Images.Media.DATA, image.absolutePath)
            } else {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            }
        }

        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    companion object {
        private const val CAPTURED_PHOTO_URI_KEY = "PHOTO_URI"
        private const val CAPTURED_PHOTO_PATH_KEY = "PHOTO_PATH"
        const val REQUEST_TAKE_PHOTO = 1
    }
}