package com.example.speedtest_rework.common.daily_notification


import android.content.Intent

/**
 * Created by Thangdd
 */
data class AlarmData(
    val alarm_id: Int,
    var time: Long,
    var type: String,
    val repeat: Int
)