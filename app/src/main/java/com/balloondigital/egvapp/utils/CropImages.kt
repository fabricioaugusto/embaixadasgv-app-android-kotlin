package com.balloondigital.egvapp.utils

import android.app.Activity
import android.net.Uri
import androidx.core.content.ContextCompat
import com.balloondigital.egvapp.R
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File

class CropImages private constructor(){

    companion object {

        fun embassyPicture(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setAspectRatioOptions(2,
                AspectRatio("16:9", 16F, 9F),
                AspectRatio("4:3", 4F, 3F),
                AspectRatio("1:1", 1F, 1F),
                AspectRatio("3:4", 3F, 4F),
                AspectRatio("9:16", 9F, 16F)
            )
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withOptions(options)
                    .start(context)
            }

        }

        fun embassyCover(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withAspectRatio(16F,9F)
                    .withOptions(options)
                    .start(context)
            }

        }

        fun eventCover(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withAspectRatio(3F,2F)
                    .withOptions(options)
                    .start(context)
            }
        }

        fun postNote(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withAspectRatio(16F,9F)
                    .withOptions(options)
                    .start(context)
            }

        }

        fun postPicture(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setAspectRatioOptions(2,
                AspectRatio("16:9", 16F, 9F),
                AspectRatio("4:3", 4F, 3F),
                AspectRatio("1:1", 1F, 1F),
                AspectRatio("3:4", 3F, 4F)
            )
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withOptions(options)
                    .start(context)
            }

        }

        fun profilePicture(context: Activity, uri: Uri?) {
            val options : UCrop.Options = UCrop.Options()
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorLink))
            options.setToolbarTitle("Editar Foto")
            if(uri != null) {
                UCrop.of(uri, Uri.fromFile(File(context.cacheDir, "crop_image.jpg")))
                    .withAspectRatio(1F,1F)
                    .withOptions(options)
                    .start(context)
            }

        }
    }
}