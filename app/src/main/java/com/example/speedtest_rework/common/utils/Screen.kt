package com.example.speedtest_rework.common.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.view.View

object Screen {
    val width: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val height: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun store(bitmap: Bitmap,fileName:String){
        val dirPath = Environment.getExternalStorageDirectory().absolutePath
    }
}