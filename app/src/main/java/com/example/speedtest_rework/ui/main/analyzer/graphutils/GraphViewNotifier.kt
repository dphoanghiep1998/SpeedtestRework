package com.example.speedtest_rework.ui.main.analyzer.graphutils

import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.jjoe64.graphview.GraphView

interface GraphViewNotifier {
    fun graphView(): GraphView
    fun update(wiFiData: WiFiData)
}