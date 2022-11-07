package com.example.speedtest_rework.ui.main.analyzer.band

import java.util.*

internal class WiFiChannelCountryGHZ2 {
    private val channels = sortedSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    private val world = channels.union(setOf(12, 13)).toSortedSet()

    fun findChannels(countryCode: String): SortedSet<Int> = world

}