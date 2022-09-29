package com.example.speedtest_rework.data.converter

import androidx.room.TypeConverter
import java.util.*

class Convertes {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

}