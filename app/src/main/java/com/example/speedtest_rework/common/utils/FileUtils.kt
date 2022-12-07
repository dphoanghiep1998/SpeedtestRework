package com.example.speedtest_rework.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {
    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()
        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }
    }


    fun shareFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val mimType = context.contentResolver.getType(contentUri)
        Log.d("TAG", "shareFileIntent: "+contentUri+"     "+ mimType)
        intent.setDataAndType(contentUri, mimType)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }
}