package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import com.github.mikephil.charting.formatter.ValueFormatter


class ValueBarFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
    }
}