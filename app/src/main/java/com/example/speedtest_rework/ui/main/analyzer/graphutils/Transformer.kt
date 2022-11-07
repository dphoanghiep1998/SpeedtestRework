package com.example.speedtest_rework.ui.main.analyzer.graphutils

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import com.example.speedtest_rework.common.utils.EMPTY
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.common.utils.buildMinVersionM
import com.example.speedtest_rework.common.utils.buildMinVersionR
import com.example.speedtest_rework.ui.main.analyzer.model.*
import com.example.speedtest_rework.ui.main.analyzer.model.Cache
import com.example.speedtest_rework.ui.main.analyzer.model.CacheResult


@OpenClass
internal class Transformer(private val cache: Cache) {

    internal fun transformWifiInfo(): WiFiConnection {
        val wifiInfo: WifiInfo? = cache.wifiInfo()
        return if (wifiInfo == null || wifiInfo.networkId == -1) {
            WiFiConnection.EMPTY
        } else {
            val ssid = convertSSID(wifiInfo.ssid ?: String.EMPTY)
            val wiFiIdentifier = WiFiIdentifier(ssid, wifiInfo.bssid ?: String.EMPTY)
            WiFiConnection(wiFiIdentifier, convertIpAddress(wifiInfo.ipAddress), wifiInfo.linkSpeed)
        }
    }

    internal fun transformCacheResults(): List<WiFiDetail> =
        cache.scanResults().map { transform(it) }

    internal fun transformToWiFiData(): WiFiData =
        WiFiData(transformCacheResults(), transformWifiInfo())

    internal fun channelWidth(scanResult: ScanResult): ChannelWidth =
        if (minVersionM()) {
            scanResult.channelWidth
        } else {
            WiFiWidth.MHZ_20.channelWidth
        }

    internal fun wiFiStandard(scanResult: ScanResult): WiFiStandardId =
        if (minVersionR()) {
            scanResult.wifiStandard
        } else {
            WiFiStandard.UNKNOWN.wiFiStandardId
        }

    internal fun centerFrequency(scanResult: ScanResult, wiFiWidth: WiFiWidth): Int =
        if (minVersionM()) {
            wiFiWidth.calculateCenter(scanResult.frequency, scanResult.centerFreq0)
        } else {
            scanResult.frequency
        }

    internal fun mc80211(scanResult: ScanResult): Boolean = minVersionM() && scanResult.is80211mcResponder

    internal fun minVersionM(): Boolean = buildMinVersionM()

    internal fun minVersionR(): Boolean = buildMinVersionR()

    private fun transform(cacheResult: CacheResult): WiFiDetail {
        val scanResult = cacheResult.scanResult
        val wiFiWidth = WiFiWidth.findOne(channelWidth(scanResult))
        val centerFrequency = centerFrequency(scanResult, wiFiWidth)
        val mc80211 = mc80211(scanResult)
        val wiFiStandard = WiFiStandard.findOne(wiFiStandard(scanResult))
        val wiFiSignal = WiFiSignal(
            scanResult.frequency, centerFrequency, wiFiWidth,
            cacheResult.average, mc80211, wiFiStandard, scanResult.timestamp
        )
        val wiFiIdentifier = WiFiIdentifier(
            if (scanResult.SSID == null) String.EMPTY else scanResult.SSID,
            if (scanResult.BSSID == null) String.EMPTY else scanResult.BSSID
        )
        return WiFiDetail(
            wiFiIdentifier,
            if (scanResult.capabilities == null) String.EMPTY else scanResult.capabilities,
            wiFiSignal
        )
    }

}