package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import kotlin.math.abs

class BarChart {
    fun initGraph(graph: GraphView) {
        val series = BarGraphSeries(
            arrayOf<DataPoint>(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 10.0),
                DataPoint(5.0, 168.0),
                DataPoint(6.0, 6.0),
                DataPoint(7.0, 14.0),
                DataPoint(8.0, 22.0),
                DataPoint(9.0, 178.0),
            )
        )
        graph.addSeries(series)

        // styling

        // styling
        val a = intArrayOf(0xFF00FACC.toInt(),0x0000FACC.toInt())
        val position = floatArrayOf(0f,1f)
        series.setValueDependentColor { data ->
//            LinearGradient(0f,0f,0f,data.y.toFloat(),a,position,Shader.TileMode.CLAMP)
        }
    series.customPaint

        series.isAnimated = true

        // draw values on top
        series.isDrawValuesOnTop = true
        series.valuesOnTopColor = Color.RED
        series.valuesOnTopSize = 50f;

        // legend
        //series.setValuesOnTopSize(50);

        // legend
//        series.title = "foo"
        graph.legendRenderer.isVisible = false
        graph.gridLabelRenderer.isVerticalLabelsVisible = false;
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false;

//        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
    }
}