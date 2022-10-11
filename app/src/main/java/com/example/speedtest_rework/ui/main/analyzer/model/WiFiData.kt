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

import com.example.speedtest_rework.common.annotation.OpenClass

@OpenClass
class WiFiData(var wiFiDetails: List<WiFiDetail>, val wiFiConnection: WiFiConnection) {

    fun connection(): WiFiDetail =
            wiFiDetails
                    .find { connected(it) }
                    ?.let { copy(it) }
                    ?: WiFiDetail.EMPTY

    fun wiFiDetails() : List<WiFiDetail> = wiFiDetails




    private fun connected(it: WiFiDetail): Boolean =
            wiFiConnection.wiFiIdentifier.equals(it.wiFiIdentifier, true)

    private fun copy(wiFiDetail: WiFiDetail): WiFiDetail {
        val vendorName: String = ""
        val wiFiAdditional =
        return WiFiDetail(wiFiDetail, listOf<WiFiDetail>())
    }

    companion object {
        val EMPTY = WiFiData(listOf(), WiFiConnection.EMPTY)
    }

}