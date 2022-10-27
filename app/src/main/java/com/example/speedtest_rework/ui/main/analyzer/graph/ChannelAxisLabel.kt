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

import com.example.speedtest_rework.common.utils.EMPTY
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannel
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair
import com.jjoe64.graphview.LabelFormatter
import com.jjoe64.graphview.Viewport

internal class ChannelAxisLabel(private val wiFiBand: WiFiBand, private val wiFiChannelPair: WiFiChannelPair) : LabelFormatter {
    override fun formatLabel(value: Double, isValueX: Boolean): String {
        val valueAsInt = (value + if (value < 0) -0.5 else 0.5).toInt()
        return when {
            isValueX -> findChannel(valueAsInt)
            valueAsInt in (MIN_Y + 1)..MAX_Y -> valueAsInt.toString()
            else -> String.EMPTY
        }
    }

    override fun setViewport(viewport: Viewport) {
        // ignore
    }

    private fun findChannel(value: Int): String {
        val wiFiChannels = wiFiBand.wiFiChannels
        val wiFiChannel = wiFiChannels.wiFiChannelByFrequency(value, wiFiChannelPair)
        return if (wiFiChannel == WiFiChannel.UNKNOWN) {
            String.EMPTY
        } else {
            val channel = wiFiChannel.channel
            if (wiFiChannels.channelAvailable("vn", channel)) {
                channel.toString()
            } else {
                String.EMPTY
            }
        }
    }

}