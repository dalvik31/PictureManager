# PictureManager

> Step 1. Add the JitPack repository to your build file

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

> Step 2. Add the dependency


	dependencies {
	        implementation 'com.github.dalvik31:PictureManager:Tag'
	}



> Step 3. Use java
	
	
	private PictureManager pictureManager =  PictureManager.Companion.from(this);
	
	pictureManager
	.message("Para una mejor experiencia es necesario que permitas el uso de la camara del dispositivo.")
	.selectImageWithCamera((bitmap, absolutePath) -> {
	    imageView.imgBackground.setImageBitmap(bitmap);
	     return null;
	});
	
	pictureManager
	.selectImageFromGallery((bitmap, absolutePath) -> {
	   imageView.imgBackground.setImageBitmap(bitmap);        
	    return null;
	});
		

> Step 3. Use Kotlin
	
	private val pictureManager = PictureManager.from(this)
	
	
   	buttonTakePhoto.setOnClickListener {
            pictureManager
               .message("Para una mejor experiencia es necesario que permitas el uso de la camara del dispositivo.")
                .selectImageWithCamera { thumbnail, absolutePath ->
                    image.setImageBitmap(thumbnail)
                }
        }

        buttonGaleria.setOnClickListener{
            pictureManager.selectImageFromGallery{ thumbnail, absolutePath ->
                image.setImageBitmap(thumbnail)
            }
        }
	


