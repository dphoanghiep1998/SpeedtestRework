package com.example.speedtest_rework.ui.main.analyzer.model

class WifiModel(
    var wifi_name: String?,
    val wifi_secure_type: String,
    val wifi_level: Int,
    val wifi_frequency: Int,
    val wifi_bssid: String,
    val wifi_channel: Int,
    val isWifi_isConnected: Boolean,
    val r_range: Int,
    val l_range: Int
) {

}