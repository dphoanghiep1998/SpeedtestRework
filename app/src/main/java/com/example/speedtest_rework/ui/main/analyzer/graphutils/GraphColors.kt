
package com.example.speedtest_rework.ui.main.analyzer.graphutils

import android.content.Context
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.annotation.OpenClass


private fun String.toColor(): Long = this.substring(1).toLong(16)

data class GraphColor(val primary: Long, val background: Long)

internal val transparent = GraphColor(0x009E9E9E, 0x009E9E9E)

@OpenClass
class GraphColors(val context: Context) {
    private val availableGraphColors: MutableList<GraphColor> = mutableListOf()
    private val currentGraphColors: ArrayDeque<GraphColor> = ArrayDeque()

    private fun availableGraphColors(): List<GraphColor> {
        if (availableGraphColors.isEmpty()) {
            val colors = context.resources.getStringArray(R.array.graph_colors)
                    .filterNotNull()
                    .withIndex()
                    .groupBy { it.index / 2 }
                    .map { GraphColor(it.value[0].value.toColor(), it.value[1].value.toColor()) }
                    .reversed()
            availableGraphColors.addAll(colors)
        }
        return availableGraphColors
    }

    fun graphColor(): GraphColor {
        if (currentGraphColors.isEmpty()) {
            currentGraphColors.addAll(availableGraphColors())
        }
        return currentGraphColors.removeFirst()
    }

    fun addColor(primaryColor: Long) {
        availableGraphColors().firstOrNull { primaryColor == it.primary }?.let {
            if (!currentGraphColors.contains(it)) {
                currentGraphColors.addFirst(it)
            }
        }
    }

}


