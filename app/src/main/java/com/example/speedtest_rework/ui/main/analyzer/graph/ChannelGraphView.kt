/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2022 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.example.speedtest_rework.ui.main.analyzer.graph

import android.content.Context
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannels
import com.jjoe64.graphview.GraphView
import com.vrem.wifianalyzer.wifi.graphutils.*

internal fun WiFiChannelPair.numX(): Int {
    val channelFirst = this.first.channel - WiFiChannels.CHANNEL_OFFSET
    val channelLast = this.second.channel + WiFiChannels.CHANNEL_OFFSET
    return channelLast - channelFirst + 1
}

internal fun WiFiChannelPair.selected(wiFiBand: WiFiBand): Boolean {

    return false
}

internal fun makeGraphView(
    context: Context,
    graphMaximumY: Int,

    wiFiBand: WiFiBand,
    wiFiChannelPair: WiFiChannelPair
): GraphView {
    return GraphViewBuilder(wiFiChannelPair.numX(), graphMaximumY, true)
        .setLabelFormatter(ChannelAxisLabel(wiFiBand, wiFiChannelPair))
        .setVerticalTitle("dbm")
        .setHorizontalTitle("Channel id")
        .build(context)
}

internal fun makeDefaultSeries(frequencyEnd: Int, minX: Int): TitleLineGraphSeries<GraphDataPoint> {
    val dataPoints = arrayOf(
        GraphDataPoint(minX, MIN_Y),
        GraphDataPoint(frequencyEnd + WiFiChannels.FREQUENCY_OFFSET, MIN_Y)
    )
    val series = TitleLineGraphSeries(dataPoints)
    series.color = transparent.primary.toInt()
    series.thickness = THICKNESS_INVISIBLE
    return series
}

internal fun makeGraphViewWrapper(
    context: Context,
    wiFiBand: WiFiBand,
    wiFiChannelPair: WiFiChannelPair
): GraphViewWrapper {
    val graphMaximumY = -20
    val graphView = makeGraphView(context, graphMaximumY, wiFiBand, wiFiChannelPair)
    val graphViewWrapper = GraphViewWrapper(context,graphView, GraphLegend.HIDE)
    val minX = wiFiChannelPair.first.frequency - WiFiChannels.FREQUENCY_OFFSET
    val maxX = minX + graphViewWrapper.viewportCntX * WiFiChannels.FREQUENCY_SPREAD
    graphViewWrapper.setViewport(minX, maxX)
    graphViewWrapper.addSeries(makeDefaultSeries(wiFiChannelPair.second.frequency, minX))
    return graphViewWrapper
}

@OpenClass
internal class ChannelGraphView(
    val context:Context,
    private val wiFiBand: WiFiBand,
    private val wiFiChannelPair: WiFiChannelPair,
    private var dataManager: DataManager = DataManager(),
    private var graphViewWrapper: GraphViewWrapper = makeGraphViewWrapper(context,wiFiBand, wiFiChannelPair)
) : GraphViewNotifier {

//    override fun update(wiFiData: WiFiData) {
//        val predicate = predicate(MainContext.INSTANCE.settings)
//        val wiFiDetails = wiFiData.wiFiDetails(predicate, MainContext.INSTANCE.settings.sortBy())
//        val newSeries = dataManager.newSeries(wiFiDetails, wiFiChannelPair)
//        dataManager.addSeriesData(
//            graphViewWrapper,
//            newSeries,
//            MainContext.INSTANCE.settings.graphMaximumY()
//        )
//        graphViewWrapper.removeSeries(newSeries)
//        graphViewWrapper.updateLegend(MainContext.INSTANCE.settings.channelGraphLegend())
//        graphViewWrapper.visibility(if (selected()) View.VISIBLE else View.GONE)
//    }

    fun selected(): Boolean = wiFiChannelPair.selected(wiFiBand)


    override fun graphView(): GraphView {
        return graphViewWrapper.graphView
    }

}