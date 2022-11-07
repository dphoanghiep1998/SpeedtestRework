
package com.example.speedtest_rework.ui.main.analyzer.graph

import android.content.Context
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.graphutils.GraphAdapter


private fun channelGraphViews(context: Context,wiFiBand: WiFiBand,wiFiChannelPair: WiFiChannelPair): ChannelGraphView =
    ChannelGraphView(context,wiFiBand,wiFiChannelPair)



@OpenClass
class ChannelGraphAdapter(
    context: Context,
    wiFiBand: WiFiBand,
    wiFiChannelPair: WiFiChannelPair
) : GraphAdapter(channelGraphViews(context,wiFiBand,wiFiChannelPair)) {

    override fun update(wiFiData: WiFiData) {
        super.update(wiFiData)
    }
    fun setData(wiFiData: WiFiData){
    }
}

