package com.example.speedtest_rework.common.custom_view

import androidx.annotation.StringRes
import com.example.speedtest_rework.R

enum class UnitType(@StringRes val unit: Int) {
    MBPS(R.string.Mbps),
    MBS(R.string.Mbs),
    KBS(R.string.Kbs)
}