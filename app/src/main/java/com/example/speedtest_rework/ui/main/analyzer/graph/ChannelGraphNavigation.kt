
package com.example.speedtest_rework.ui.main.analyzer.graph

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.Configuration
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.compatColor
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelsGHZ5

typealias NavigationSets = Map<Int, WiFiChannelPair>
typealias NavigationLines = Map<Int, NavigationSets>

internal val navigationGHZ2Lines = mapOf<Int, NavigationSets>()
internal val navigationGHZ5Lines = mapOf(
    R.id.graphNavigationLine1 to mapOf(
        R.id.graphNavigationSet1 to WiFiChannelsGHZ5.SET1,
        R.id.graphNavigationSet2 to WiFiChannelsGHZ5.SET2,
        R.id.graphNavigationSet3 to WiFiChannelsGHZ5.SET3
    )
)

@OpenClass
class ChannelGraphNavigation(private val view: View, private val mainContext: Context) {

    internal fun update() {
        val wiFiBand = AppSharePreference.getInstance(mainContext)
            .getWifiBand(R.string.wifi_band_key, WiFiBand.GHZ2)
//        val wiFiBand = AppSharePreference.getInstance(mainContext)
//            .getWifiBand(R.string.wifi_band_key, WiFiBand.GHZ2)
        Log.d("TAG", "update: "+wiFiBand)
        val selectedWiFiChannelPair = Configuration.getInstance()!!.wiFiChannelPair(wiFiBand)
        Log.d("TAG", "update: "+selectedWiFiChannelPair)
        val navigationLines = navigationLines(wiFiBand)
        navigationLines.entries.forEach { entry ->
            Log.d("TAG", "update: " + view + entry.key)
            view.findViewById<LinearLayout>(entry.key).visibility = visibility(entry.value)
            buttons(entry.value, wiFiBand, selectedWiFiChannelPair)
        }
    }

    private fun buttons(
        navigationSets: NavigationSets,
        wiFiBand: WiFiBand,
        selectedWiFiChannelPair: WiFiChannelPair
    ) {
        navigationSets.forEach { entry ->
            with(view.findViewById<Button>(entry.key)) {
                val value = entry.value
                val selected = value == selectedWiFiChannelPair
                val color =
                    mainContext.compatColor(if (selected) R.color.selected else R.color.background_main)
                val textValue =
                    """<strong>${value.first.channel} &#8722 ${value.second.channel}</strong>""".parseAsHtml()
                        .toString()
                setBackgroundColor(color)
                text = textValue
                isSelected = selected
                setOnClickListener { onClickListener(wiFiBand, value) }
            }
        }
    }


    private fun visibility(map: Map<Int, Any>) =
        if (map.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

    private fun onClickListener(wiFiBand: WiFiBand, wiFiChannelPair: WiFiChannelPair) {
        Configuration.getInstance()!!.wiFiChannelPair(wiFiBand, wiFiChannelPair)
        update()
    }

    private fun navigationLines(wiFiBand: WiFiBand): NavigationLines =
        when (wiFiBand) {
            WiFiBand.GHZ2 -> navigationGHZ2Lines
            WiFiBand.GHZ5 -> navigationGHZ5Lines
        }

}