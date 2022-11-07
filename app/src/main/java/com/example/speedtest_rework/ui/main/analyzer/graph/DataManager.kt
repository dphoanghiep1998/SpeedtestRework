
package com.example.speedtest_rework.ui.main.analyzer.graph

import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.example.speedtest_rework.ui.main.analyzer.graphutils.GraphDataPoint
import com.example.speedtest_rework.ui.main.analyzer.graphutils.GraphViewWrapper
import com.example.speedtest_rework.ui.main.analyzer.graphutils.MIN_Y

internal fun WiFiChannelPair.inRange(wiFiDetail: WiFiDetail): Boolean =
        wiFiDetail.wiFiSignal.centerFrequency in first.frequency..second.frequency

@OpenClass
internal class DataManager {
    fun newSeries(wiFiDetails: List<WiFiDetail>, wiFiChannelPair: WiFiChannelPair): Set<WiFiDetail> =
            wiFiDetails.filter { wiFiChannelPair.inRange(it) }.toSet()

    fun graphDataPoints(wiFiDetail: WiFiDetail, levelMax: Int): Array<GraphDataPoint> {
        val wiFiSignal = wiFiDetail.wiFiSignal
        val guardBand = wiFiSignal.wiFiWidth.guardBand
        val frequencyStart = wiFiSignal.frequencyStart
        val frequencyEnd = wiFiSignal.frequencyEnd
        val level = wiFiSignal.level.coerceAtMost(levelMax)
        return arrayOf(
                GraphDataPoint(frequencyStart, MIN_Y),
                GraphDataPoint(frequencyStart + guardBand, level),
                GraphDataPoint(wiFiSignal.centerFrequency, level),
                GraphDataPoint(frequencyEnd - guardBand, level),
                GraphDataPoint(frequencyEnd, MIN_Y)
        )
    }

    fun addSeriesData(graphViewWrapper: GraphViewWrapper, wiFiDetails: Set<WiFiDetail>, levelMax: Int) {
        wiFiDetails.forEach {
            val dataPoints = graphDataPoints(it, levelMax)
            if (graphViewWrapper.newSeries(it)) {
                graphViewWrapper.addSeries(it, TitleLineGraphSeries(dataPoints), true)
            } else {
                graphViewWrapper.updateSeries(it, dataPoints, true)
            }
        }
    }

}