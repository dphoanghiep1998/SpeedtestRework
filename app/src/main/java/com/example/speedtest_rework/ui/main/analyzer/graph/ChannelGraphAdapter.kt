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
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.vrem.wifianalyzer.wifi.graphutils.GraphAdapter


private fun channelGraphViews(context: Context): List<ChannelGraphView> =
    WiFiBand.values().flatMap { wiFiBand ->
        wiFiBand.wiFiChannels.wiFiChannelPairs().map { ChannelGraphView(context, wiFiBand, it) }
    }

@OpenClass
class ChannelGraphAdapter(
    context: Context,
    private val channelGraphNavigation: ChannelGraphNavigation
) : GraphAdapter(channelGraphViews(context)) {
    override fun update(wiFiData: WiFiDetail) {
        channelGraphNavigation.update()
    }
}

