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
package com.example.speedtest_rework.common

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

val String.Companion.EMPTY: String get() = ""
val String.Companion.SPACE_SEPARATOR: String get() = " "

fun String.specialTrim(): String =
    this.trim { it <= ' ' }.replace(" +".toRegex(), String.SPACE_SEPARATOR)

fun String.toCapitalize(locale: Locale): String =
    this.replaceFirstChar { word -> word.uppercase(locale) }

fun format(d: Double): String {
    val bDec = DecimalFormat("##,###")
    val mDec = DecimalFormat("#,###")
    bDec.roundingMode = RoundingMode.HALF_UP
    mDec.roundingMode = RoundingMode.HALF_UP

    return when (d.toFloat()) {
        in 0f..999f -> String.format("%.2f", d)
        in 1000f..9999f -> mDec.format(d)
        else -> bDec.format(d)
    }
}

fun roundOffDecimal(number: Double): Double {
    val number3digits: Double = (number * 1000.0).roundToInt() / 1000.0
    val number2digits: Double = (number3digits * 100.0).roundToInt() / 100.0
    return (number2digits * 10.0).roundToInt() / 10.0
}