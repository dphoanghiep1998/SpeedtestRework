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
package com.example.speedtest_rework.ui.main.analyzer.graphutils

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.BaseSeries
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.Series


const val SIZE_MIN = 1024
const val SIZE_MAX = 4096

@OpenClass
class GraphViewWrapper(
    val context: Context,
    val graphView: GraphView,
    var graphLegend: GraphLegend,
    private val seriesCache: SeriesCache = SeriesCache(),
    private val seriesOptions: SeriesOptions = SeriesOptions(context)
) {

    fun removeSeries(newSeries: Set<WiFiDetail>): Unit =
        seriesCache.remove(differenceSeries(newSeries)).forEach {
            seriesOptions.removeSeriesColor(it)
            graphView.removeSeries(it)
        }

    fun differenceSeries(newSeries: Set<WiFiDetail>): List<WiFiDetail> =
        seriesCache.difference(newSeries)

    fun addSeries(
        wiFiDetail: WiFiDetail,
        series: BaseSeries<GraphDataPoint>,
        drawBackground: Boolean
    ): Boolean =
        if (seriesExists(wiFiDetail)) {
            false
        } else {
            seriesCache.put(wiFiDetail, series)
            series.title =
                wiFiDetail.wiFiIdentifier.ssid + " " + wiFiDetail.wiFiSignal.channelDisplay()
            seriesOptions.setSeriesColor(series)
            seriesOptions.drawBackground(series, drawBackground)
            graphView.addSeries(series)
            true
        }

    fun updateSeries(
        wiFiDetail: WiFiDetail,
        data: Array<GraphDataPoint>,
        drawBackground: Boolean
    ): Boolean =
        if (seriesExists(wiFiDetail)) {
            val series = seriesCache[wiFiDetail]
            series.resetData(data)
            if (!wiFiDetail.released) {
                seriesOptions.highlightSelected(series, wiFiDetail.selected)
            } else {
                seriesOptions.setSeriesColor(series)
            }
            seriesOptions.drawBackground(series, drawBackground)
            true
        } else {
            false
        }


    fun newSeries(wiFiDetail: WiFiDetail): Boolean = !seriesExists(wiFiDetail)

    fun setViewport(minX: Int, maxX: Int) {
        val viewport = graphView.viewport
        viewport.setMinX(minX.toDouble())
        viewport.setMaxX(maxX.toDouble())
    }

    val viewportCntX: Int
        get() = graphView.gridLabelRenderer.numHorizontalLabels - 1

    fun addSeries(series: BaseSeries<GraphDataPoint>) {
        graphView.addSeries(series)
    }


    fun updateLegend(graphLegend: GraphLegend) {
        resetLegendRenderer(graphLegend)
        val legendRenderer = graphView.legendRenderer
        legendRenderer.resetStyles()
        legendRenderer.width = 0
        legendRenderer.textSize = graphView.titleTextSize
        legendRenderer.textColor = Color.WHITE
        graphLegend.display(legendRenderer)
    }

    fun visibility(visibility: Int) {
        graphView.visibility = visibility
    }

    fun size(): Int = SIZE_MAX

    private fun newLegendRenderer(): LegendRenderer = LegendRenderer(graphView)

    private fun resetLegendRenderer(graphLegend: GraphLegend) {
        if (this.graphLegend != graphLegend) {
            graphView.legendRenderer = newLegendRenderer()
            this.graphLegend = graphLegend
        }
    }

    private fun seriesExists(wiFiDetail: WiFiDetail): Boolean = seriesCache.contains(wiFiDetail)


}