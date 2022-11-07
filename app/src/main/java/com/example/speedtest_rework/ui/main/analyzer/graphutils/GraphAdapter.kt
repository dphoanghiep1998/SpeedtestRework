package com.example.speedtest_rework.ui.main.analyzer.graphutils

import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.jjoe64.graphview.GraphView

interface UpdateNotifier {
    fun update(wiFiData: WiFiData)
}

open class GraphAdapter(private val graphViewNotifiers: GraphViewNotifier) : UpdateNotifier {
    fun graphViews(): GraphView = graphViewNotifiers.graphView()

    override fun update(wiFiData: WiFiData) = graphViewNotifiers.update(wiFiData)

}