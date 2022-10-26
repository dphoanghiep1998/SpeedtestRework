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
package com.example.speedtest_rework.ui.main.analyzer.graphutils

import android.content.Context
import android.graphics.Color
import com.example.speedtest_rework.common.annotation.OpenClass
import com.example.speedtest_rework.ui.main.analyzer.graph.TitleLineGraphSeries
import com.jjoe64.graphview.series.BaseSeries
import com.jjoe64.graphview.series.LineGraphSeries

private fun BaseSeries<GraphDataPoint>.removeSeriesColor(graphColors: GraphColors) = graphColors.addColor(this.color.toLong())

private fun BaseSeries<GraphDataPoint>.highlightSelected(selected: Boolean) {
    val thickness =  THICKNESS_REGULAR
    when (this) {
        is LineGraphSeries<GraphDataPoint> -> this.setThickness(thickness)
        is TitleLineGraphSeries<GraphDataPoint> -> {
            if(selected){
                this.thickness = thickness
                this.setTextBold(true)
                this.color = Color.GREEN
            }else{
                this.setTextBold(false)
                this.thickness = thickness
                this.color = Color.TRANSPARENT
            }

        }
    }
}

private fun BaseSeries<GraphDataPoint>.seriesColor(graphColors: GraphColors) {
    val graphColor = graphColors.graphColor()
    this.color = graphColor.primary.toInt()
    when (this) {
        is LineGraphSeries<GraphDataPoint> -> this.backgroundColor = graphColor.background.toInt()
        is TitleLineGraphSeries<GraphDataPoint> -> this.backgroundColor = graphColor.background.toInt()
    }
}

private fun BaseSeries<GraphDataPoint>.drawBackground(drawBackground: Boolean) {
    when (this) {
        is LineGraphSeries<GraphDataPoint> -> this.isDrawBackground = drawBackground
        is TitleLineGraphSeries<GraphDataPoint> -> this.isDrawBackground = drawBackground
    }
}

@OpenClass
class SeriesOptions(val context: Context,private val graphColors: GraphColors = GraphColors(context)) {

    fun highlightSelected(series: BaseSeries<GraphDataPoint>, selected: Boolean) = series.highlightSelected(selected)

    fun setSeriesColor(series: BaseSeries<GraphDataPoint>) = series.seriesColor(graphColors)

    fun drawBackground(series: BaseSeries<GraphDataPoint>, drawBackground: Boolean) = series.drawBackground(drawBackground)

    fun removeSeriesColor(series: BaseSeries<GraphDataPoint>) = series.removeSeriesColor(graphColors)

}