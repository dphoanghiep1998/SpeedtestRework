package com.example.speedtest_rework.common.utils

import android.os.Build

fun buildMinVersionR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun buildMinVersionQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun buildMinVersionP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

fun buildMinVersionS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun buildVersionP(): Boolean = Build.VERSION.SDK_INT == Build.VERSION_CODES.P

fun buildMinVersionN(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun buildMaxVersionN(): Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N

fun buildMinVersionM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun buildMinVersionO(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun buildMinVersionJellyBeanMr1(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1