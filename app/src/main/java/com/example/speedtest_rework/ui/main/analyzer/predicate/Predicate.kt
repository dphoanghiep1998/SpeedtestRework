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
package com.vrem.wifianalyzer.wifi.predicate

import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.model.SSID
import com.example.speedtest_rework.ui.main.analyzer.model.Security
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail


internal typealias Predicate = (wiFiDetail: WiFiDetail) -> Boolean
internal typealias ToPredicate<T> = (T) -> Predicate

internal val truePredicate: Predicate = { true }
internal val falsePredicate: Predicate = { false }

internal fun List<Predicate>.anyPredicate(): Predicate =
    { wiFiDetail -> this.any { predicate -> predicate(wiFiDetail) } }

internal fun List<Predicate>.allPredicate(): Predicate =
    { wiFiDetail -> this.all { predicate -> predicate(wiFiDetail) } }

fun WiFiBand.predicate(): Predicate =
    { wiFiDetail -> wiFiDetail.wiFiSignal.wiFiBand == this }


internal fun SSID.predicate(): Predicate =
    { wiFiDetail -> wiFiDetail.wiFiIdentifier.ssid.contains(this) }

internal fun Security.predicate(): Predicate =
    { wiFiDetail -> wiFiDetail.securities.contains(this) }

private fun Set<SSID>.ssidPredicate(): Predicate =
    if (this.isEmpty())
        truePredicate
    else
        this.map { it.predicate() }.anyPredicate()

internal fun <T : Enum<T>> makePredicate(values: Array<T>, filter: Set<T>, toPredicate: ToPredicate<T>): Predicate =
    if (filter.size >= values.size)
        truePredicate
    else
        filter.map { toPredicate(it) }.anyPredicate()


