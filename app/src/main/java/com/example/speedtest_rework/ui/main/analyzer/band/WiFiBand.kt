
package com.example.speedtest_rework.ui.main.analyzer.band

import com.example.speedtest_rework.common.utils.NetworkUtils

typealias Available = () -> Boolean

internal val availableGHZ2: Available = { true }
internal val availableGHZ5: Available =
    { NetworkUtils.is5GHzBandSupported() }


enum class WiFiBand(val wiFiChannels: WiFiChannels, val available: Available) {
    GHZ2(WiFiChannelsGHZ2(), availableGHZ2),
    GHZ5(WiFiChannelsGHZ5(), availableGHZ5);


    val ghz2: Boolean get() = GHZ2 == this
    val ghz5: Boolean get() = GHZ5 == this


    companion object {
        fun find(frequency: Int): WiFiBand = values().find { it.wiFiChannels.inRange(frequency) }
            ?: GHZ2
        fun toWifiBand(enumString: String): WiFiBand {
            return try {
                valueOf(enumString)
            } catch (ex: Exception) {
                GHZ2
            }
        }
    }

}