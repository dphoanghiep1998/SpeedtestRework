package com.example.speedtest_rework.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun getDateConverted(date: Date?): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date).toString()
    }

    fun getDateConvertedToResult(time: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.US)
        return formatter.format(time).toString()
    }

    fun convertToDateMonth(date: Date): String {
        val cCalendar = Calendar.getInstance()
        val dCalendar = Calendar.getInstance()
        dCalendar.timeInMillis = date.time
        if (cCalendar.get(Calendar.DATE) == dCalendar.get(Calendar.DATE) && cCalendar.get(Calendar.MONTH) == dCalendar.get(
                Calendar.MONTH
            ) && cCalendar.get(Calendar.YEAR) == dCalendar.get(Calendar.YEAR)
        ) {
            return "Today"
        }
        val formatter = SimpleDateFormat("MM-dd")
        return formatter.format(date).toString()
    }
}