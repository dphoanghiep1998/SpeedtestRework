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
package com.example.speedtest_rework.ui.main.analyzer.model

import android.net.wifi.ScanResult
import com.example.speedtest_rework.common.buildMinVersionM
import kotlin.math.abs

typealias ChannelWidth = Int

private val channelWidth20: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_20MHZ else 0
private val channelWidth40: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_40MHZ else 1

typealias CalculateCenter = (primary: Int, center: Int) -> Int

internal val calculateCenter20: CalculateCenter = { primary, _ -> primary }

internal val calculateCenter40: CalculateCenter = { primary, center ->
    if (abs(primary - center) >= WiFiWidth.MHZ_40.frequencyWidthHalf) {
        (primary + center) / 2
    } else {
        center
    }
}

internal val calculateCenter80: CalculateCenter = { _, center -> center }

internal val calculateCenter160: CalculateCenter = { primary, center ->
    when (primary) {
        // 5GHz
        in 5170..5330 -> 5250
        in 5490..5730 -> 5570
        in 5735..5895 -> 5815
        // 6GHz
        in 5950..6100 -> 6025
        in 6110..6260 -> 6185
        in 6270..6420 -> 6345
        in 6430..6580 -> 6505
        in 6590..6740 -> 6665
        in 6750..6900 -> 6825
        in 6910..7120 -> 6985
        else -> center
    }
}

enum class WiFiWidth(val channelWidth: ChannelWidth, val frequencyWidth: Int, val guardBand: Int, val calculateCenter: CalculateCenter) {
    MHZ_20(channelWidth20, 20, 2, calculateCenter20),
    MHZ_40(channelWidth40, 40, 3, calculateCenter40);

    val frequencyWidthHalf: Int = frequencyWidth / 2

    companion object {
        fun findOne(channelWidth: ChannelWidth): WiFiWidth =
                values().firstOrNull { it.channelWidth == channelWidth } ?: MHZ_20
    }
}
