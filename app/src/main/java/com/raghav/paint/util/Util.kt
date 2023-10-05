package com.raghav.paint.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log

fun saveBitmapAsPNG(context: Context, bitmap: Bitmap?, uri: Uri) {
    val outputStream = context.contentResolver.openOutputStream(uri)
    Log.d("canvas", outputStream.toString())
    outputStream?.use { stream ->
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
//    context.contentResolver.notifyChange(uri, null)
}