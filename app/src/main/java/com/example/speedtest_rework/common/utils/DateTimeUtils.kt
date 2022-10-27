package com.example.speedtest_rework.common.utils

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

    fun convertToDateMonth(date: Date): String {
        val cCalendar = Calendar.getInstance()
        val dCalendar = Calendar.getInstance()
        dCalendar.timeInMillis = date.time
        if (cCalendar.get(Calendar.DATE) == dCalendar.get(Calendar.DATE)) {
            return "Today"
        }
        val formatter = SimpleDateFormat("MM-dd", Locale.ENGLISH)
        return formatter.format(date).toString()
    }
}