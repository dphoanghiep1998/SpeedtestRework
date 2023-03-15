
package com.example.speedtest_rework.ui.main.analyzer.graph

import android.content.Context
import android.view.View
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannels
import com.example.speedtest_rework.ui.main.analyzer.graphutils.*
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.jjoe64.graphview.GraphView

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
        .setVerticalTitle(context.resources.getString(R.string.graph_y))
        .setHorizontalTitle(context.resources.getString(R.string.graph_x))
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
    val graphMaximumY = 0
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

    override fun update(wiFiData: WiFiData) {
        val wiFiDetails = wiFiData.wiFiDetails()
        val newSeries = dataManager.newSeries(wiFiDetails, wiFiChannelPair)
        dataManager.addSeriesData(
            graphViewWrapper,
            newSeries,
            0
        )
//        graphViewWrapper.removeSeries(newSeries)
        graphViewWrapper.updateLegend(GraphLegend.HIDE)
        graphViewWrapper.visibility(if (selected()) View.VISIBLE else View.GONE)
    }

    fun selected(): Boolean = wiFiChannelPair.selected(wiFiBand)


    override fun graphView(): GraphView {
        return graphViewWrapper.graphView
    }


}