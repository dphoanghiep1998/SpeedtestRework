package com.example.speedtest_rework.common.utils

import android.util.Log
import com.example.speedtest_rework.BuildConfig

object Logger {

    fun log(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

}