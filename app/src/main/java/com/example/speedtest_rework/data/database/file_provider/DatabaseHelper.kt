package com.example.speedtest_rework.data.database.file_provider

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.utils.DateTimeUtils
import com.example.speedtest_rework.common.utils.FileUtils
import com.example.speedtest_rework.common.utils.format
import com.example.speedtest_rework.data.model.HistoryModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class DatabaseHelper
    (val context: Context, val unitType: UnitType) {

    private fun getCSVFile(): String {
        return "SpeedTestResult.csv"
    }

    fun exportDatabaseToCSVFile(item: HistoryModel) {
        Toast.makeText(context, context.getText(R.string.generating_csv), Toast.LENGTH_LONG).show()
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
                    item.name_network,
                    item.internalIP,
                    item.externalIP,
                    item.isp,
                    "${item.jitter} ms",
                    "${item.loss} ms",
                    item.network,
                    "${item.ping} ms",
                    DateTimeUtils.getDateConvertedToResult(item.time),
                    "${format(convert(item.upload))} ${context.getString(unitType.unit)}",
                    "${format(convert(item.download))} ${context.getString(unitType.unit)}",
                )
            )


        }
    }

    private fun convert(value: Double): Double {
        if (unitType == UnitType.MBS) {
            return convertMbpsToMbs(value)
        }
        if (unitType == UnitType.KBS) {
            return convertMbpsToKbs(value)
        }
        return value
    }

    private fun convertMbpsToMbs(value: Double): Double {
        return value * .125
    }

    private fun convertMbpsToKbs(value: Double): Double {
        return value * 125
    }
}