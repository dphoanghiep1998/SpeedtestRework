package com.example.speedtest_rework.ui.data_usage.model

import java.util.*

class DataUsageModel(
    val day: Date,
    val mobile_usage: Double,
    val wifi_usage: Double
) {
    var total = 0.0

    init {
        total = mobile_usage + wifi_usage
    }
}