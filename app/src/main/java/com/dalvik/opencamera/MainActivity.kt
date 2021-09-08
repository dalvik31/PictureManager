package com.dalvik.opencamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dalvik.picturemanager.PictureManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val cameraManager = PictureManager.from(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        buttonTakePhoto.setOnClickListener {
            cameraManager
                .message("Para una mejor experiencia es necesario que permitas el uso de la camara del dispositivo.")
                .selectImageWithCamera { thumbnail, absolutePath ->
                    image.setImageBitmap(thumbnail)
                }
        }

        buttonGaleria.setOnClickListener{
            cameraManager.selectImageFromGallery{ thumbnail, absolutePath ->
                image.setImageBitmap(thumbnail)
            }
        }
    }

}
