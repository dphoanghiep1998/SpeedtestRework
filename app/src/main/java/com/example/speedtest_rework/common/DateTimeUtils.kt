package com.example.speedtest_rework.common

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun getDateConverted(date: Date?): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return formatter.format(date).toString()
    }

    fun getDateConvertedToResult(date: Date?): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy, hh:mm", Locale.ENGLISH)
        return formatter.format(date).toString()
    }
}