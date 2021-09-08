# PictureManager

> Step 1. Add the JitPack repository to your build file

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

> Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.dalvik31:PictureManager:Tag'
	}

> Step 3. Use

```
   buttonTakePhoto.setOnClickListener {
            cameraManager
                .message("Para una mejor experiencia es necesario que permitas el uso de la camara del dispositivo.")
                .selectImageWithCamera {
                    image.setImageBitmap(it)
                }
        }

        buttonGaleria.setOnClickListener{
            cameraManager.selectImageFromGallery{
                image.setImageBitmap(it)
            }
        }


