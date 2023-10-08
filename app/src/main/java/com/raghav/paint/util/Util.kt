package com.raghav.paint.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View

const val ERROR_SAVING = "Your project was not saved!\nPlease try again!"

fun captureCanvasScreenshot(view: View, density: Float): Bitmap {

    val bitmap = Bitmap.createBitmap(
        (view.width * density).toInt(),
        (view.height * density).toInt(),
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bitmap)
    canvas.scale(density, density)

    view.draw(canvas)

    return bitmap
}

fun saveBitmapToStorage(context: Context, uri: Uri, bitmap: Bitmap) {
    try {
        val outputStream = context.contentResolver.openOutputStream(uri)
        outputStream?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}