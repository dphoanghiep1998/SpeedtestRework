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
import com.example.speedtest_rework.common.utils.buildMinVersionR

typealias WiFiStandardId = Int

private val unknown: WiFiStandardId = if (buildMinVersionR()) ScanResult.WIFI_STANDARD_UNKNOWN else 0
private val legacy: WiFiStandardId = if (buildMinVersionR()) ScanResult.WIFI_STANDARD_LEGACY else 1
private val n: WiFiStandardId = if (buildMinVersionR()) ScanResult.WIFI_STANDARD_11N else 4
private val ac: WiFiStandardId = if (buildMinVersionR()) ScanResult.WIFI_STANDARD_11AC else 5
private val ax: WiFiStandardId = if (buildMinVersionR()) ScanResult.WIFI_STANDARD_11AX else 6

enum class WiFiStandard(val wiFiStandardId: WiFiStandardId) {
    UNKNOWN(unknown,),
    LEGACY(legacy),
    N(n),
    AC(ac),
    AX(ax);

    companion   object {
        fun findOne(wiFiStandardId: WiFiStandardId): WiFiStandard =
            values().firstOrNull { it.wiFiStandardId == wiFiStandardId } ?: UNKNOWN
    }
}