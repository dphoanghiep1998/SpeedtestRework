package com.example.speedtest_rework.data.database.file_provider

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.FileUtils
import com.example.speedtest_rework.data.model.HistoryModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class DatabaseHelper
    (val context: Context) {

    private fun getCSVFile(): String {
        return "SpeedTestResult.csv"
    }

    fun exportDatabaseToCSVFile(item: HistoryModel) {
        Toast.makeText(context, context.getText(R.string.generating_csv), Toast.LENGTH_LONG)
            .show()
        val csvFile = FileUtils.generateFile(context, getCSVFile())
        if (csvFile != null) {
            exportHistoryModel(item, csvFile)
            val intent = FileUtils.shareFileIntent(context, csvFile)
            context.startActivity(Intent.createChooser(intent, "Share file"))
            Toast.makeText(context, context.getText(R.string.gen_csv_success), Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(context, context.getText(R.string.gen_csv_failed), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun exportHistoryModel(item: HistoryModel, file: File) {
        csvWriter().open(file, append = false) {
            writeRow(
                listOf(
                    "[Id]",
                    "[Name Network]",
                    "[Internal IP]",
                    "[External IP]",
                    "[ISP]",
                    "[Jitter]",
                    "[Loss]",
                    "[Network]",
                    "[Ping]",
                    "[Time]",
                    "[Upload]",
                    "[Download]",
                )
            )

            writeRow(
                listOf(
                    item.id,
                    item.name_network,
                    item.internalIP,
                    item.externalIP,
                    item.isp,
                    item.jitter,
                    item.loss,
                    item.network,
                    item.ping,
                    item.time,
                    item.upload,
                    item.download,
                )
            )


        }
    }
}