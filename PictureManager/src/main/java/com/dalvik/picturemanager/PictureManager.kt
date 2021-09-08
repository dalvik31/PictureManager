package com.dalvik.picturemanager

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import id.zelory.compressor.Compressor
import java.io.File
import java.lang.ref.WeakReference
import android.provider.OpenableColumns
import java.io.FileOutputStream
import java.io.InputStream


class PictureManager private constructor(private val activity: WeakReference<AppCompatActivity>) {

    private val requiredPermissions = Manifest.permission.CAMERA
    private var latestTmpUri: Uri? = null
    private var message: String = String()
    private var callback: (Bitmap) -> Unit = {}
    private lateinit var tmpFile: File

    companion object {
        fun from(fragment: AppCompatActivity) = PictureManager(WeakReference(fragment))
    }

    private val permissionCheck =
        activity.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { grantResults ->
                sendResult(grantResults)
            }


    private val takeImageResult = activity.get()
        ?.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let {
                    val compressedImageFile = Compressor(activity.get()).compressToBitmap(tmpFile)
                    callback(compressedImageFile)
                    cleanUp()
                }
            }
        }

    private val selectImageFromGalleryResult = activity.get()
        ?.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val compressedImageFile =
                    Compressor(activity.get()).compressToBitmap(getFile(uri))
                callback(compressedImageFile)
                cleanUp()
            }
        }


    private fun takeImage() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takeImageResult!!.launch(uri)
        }
    }

    fun message(description: String): PictureManager {
        message = description
        return this
    }

    fun selectImageWithCamera(callback: (Bitmap) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun selectImageFromGallery(callback: (Bitmap) -> Unit) {
        this.callback = callback
        selectImageFromGalleryResult!!.launch("image/*")
    }

    private fun displayRationale(fragment: AppCompatActivity) {
        AlertDialog.Builder(fragment)
            .setTitle(fragment.getString(R.string.dialog_permission_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(fragment.getString(android.R.string.ok)) { _, _ ->
                requestPermissions()
            }
            .show()
    }


    private fun sendResult(grantPermission: Boolean) {
        if (grantPermission) {
            takeImage()
        }
    }
    private fun handlePermissionRequest() {
        activity.get()?.let { fragment ->
            when {
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment)
                else -> requestPermissions()
            }
        }
    }
    private fun requestPermissions() {
        permissionCheck?.launch(requiredPermissions)
    }

    private fun shouldShowPermissionRationale(fragment: AppCompatActivity) =
        ActivityCompat.shouldShowRequestPermissionRationale(fragment, requiredPermissions)

    private fun cleanUp() {
        message = String()
        callback = {}
    }

    /**
     * Metodo para crear un archivo temporal y poder guardar la imagen tomada de la camara
     * despues esa la comprimimos para poder regresarla en el callback
     *
     * */
    private fun getTmpFileUri(): Uri {
        tmpFile = File.createTempFile("tmp_image_file", ".png", activity.get()!!.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            activity.get()!!,
            BuildConfig.LIBRARY_PACKAGE_NAME + ".providers.FileProvider",
            tmpFile
        )
    }



    /**
     * Metodos para guardar temporalmente la imagen recuperada de la galeria
     * para poder posteriormente comprimirla
     * */
    private fun getFile( uri: Uri): File {
    val destinationFilename =
            File(activity.get()!!.filesDir.path + File.separatorChar + queryName(activity.get()!!, uri))
        try {
            activity.get()!!.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins,
                    destinationFilename
                )
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
    }


    private fun createFileFromStream(ins: InputStream?, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins!!.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}

