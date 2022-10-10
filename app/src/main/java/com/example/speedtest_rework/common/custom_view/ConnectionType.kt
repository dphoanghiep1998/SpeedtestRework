package com.example.speedtest_rework.common.custom_view

import androidx.annotation.StringRes
import com.example.speedtest_rework.R

enum class ConnectionType(@StringRes val idRes: Int) {

    WIFI(R.string.Mbps),
    MOBILE(R.string.Mbs),
    UNKNOWN(R.string.no_connection)
}