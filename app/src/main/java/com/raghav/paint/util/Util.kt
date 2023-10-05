package com.raghav.paint.util

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

fun createFile(fileName: String = "sample.png", launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    // file type
    intent.type = "image/*"
    // file name
    intent.putExtra(Intent.EXTRA_TITLE, fileName)
    intent.addFlags(
        Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
    )
    launcher.launch(intent)
}