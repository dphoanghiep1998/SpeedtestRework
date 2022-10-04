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
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiChannelPair

typealias NavigationSets = Map<Int, WiFiChannelPair>
typealias NavigationLines = Map<Int, NavigationSets>

internal val navigationGHZ2Lines = mapOf<Int, NavigationSets>()


@OpenClass
class ChannelGraphNavigation(private val view: View, private val mainContext: Context) {

    internal fun update() {
        val wiFiBand = WiFiBand.values()[0]
        val navigationLines = navigationLines(wiFiBand)
        navigationLines.entries.forEach { entry ->
            view.findViewById<LinearLayout>(entry.key).visibility = visibility(entry.value)

        }
    }


    private fun visibility(map: Map<Int, Any>) =
        if (map.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

    internal fun onClickListener(wiFiBand: WiFiBand, wiFiChannelPair: WiFiChannelPair) {

    }

    private fun navigationLines(wiFiBand: WiFiBand): NavigationLines =
        when (wiFiBand) {
            WiFiBand.GHZ2 -> navigationGHZ2Lines
        }

}