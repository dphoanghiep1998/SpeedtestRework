
package com.example.speedtest_rework.ui.main.analyzer.graph

import android.content.Context
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.graphutils.GraphAdapter


private fun channelGraphViews(context: Context): ChannelGraphView =
    ChannelGraphView(context,WiFiBand.GHZ5,WiFiBand.GHZ5.wiFiChannels.wiFiChannelPairs()[0])



@OpenClass
class ChannelGraphAdapter(
    context: Context,
    private val channelGraphNavigation: ChannelGraphNavigation
) : GraphAdapter(channelGraphViews(context)) {

    override fun update(wiFiData: WiFiData) {
        super.update(wiFiData)
        channelGraphNavigation.update()
    }
    fun setData(wiFiData: WiFiData){
        channelGraphNavigation.update()
    }
}

