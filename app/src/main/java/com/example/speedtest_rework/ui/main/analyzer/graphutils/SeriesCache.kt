package com.example.speedtest_rework.ui.main.analyzer.graphutils

import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.jjoe64.graphview.series.BaseSeries
import com.jjoe64.graphview.series.Series

@OpenClass
class SeriesCache {
    private val cache: MutableMap<WiFiDetail, BaseSeries<GraphDataPoint>> = mutableMapOf()

    fun difference(series: Set<WiFiDetail>): List<WiFiDetail> = cache.keys.minus(series).toList()

    fun remove(series: List<WiFiDetail>): List<BaseSeries<GraphDataPoint>> =
            series.filter { cache.containsKey(it) }.mapNotNull { cache.remove(it) }

    fun find(series: Series<*>): WiFiDetail = cache.keys.first { series == cache[it] }

    operator fun contains(wiFiDetail: WiFiDetail): Boolean = cache.containsKey(wiFiDetail)

    operator fun get(wiFiDetail: WiFiDetail): BaseSeries<GraphDataPoint> = cache[wiFiDetail]!!

    fun put(wiFiDetail: WiFiDetail, series: BaseSeries<GraphDataPoint>) =
            cache.put(wiFiDetail, series)

}
