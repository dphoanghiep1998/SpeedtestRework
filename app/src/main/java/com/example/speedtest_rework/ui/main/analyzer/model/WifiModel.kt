package com.example.speedtest_rework.ui.main.analyzer.model

class WifiModel(
    var wifi_name: String?,
    val wifi_secure_type: String,
    val wifi_level: Int,
    val wifi_frequency: Int,
    val wifi_channel: Int,
    val isWifi_isConnected: Boolean,
) {

}