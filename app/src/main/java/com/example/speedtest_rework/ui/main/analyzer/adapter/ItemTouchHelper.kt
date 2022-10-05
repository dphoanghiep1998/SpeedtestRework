package com.example.speedtest_rework.ui.main.analyzer.adapter

import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail

interface ItemTouchHelper {
    fun onClickItemWifi(wiFiDetail: WiFiDetail?,released:Boolean)
}
