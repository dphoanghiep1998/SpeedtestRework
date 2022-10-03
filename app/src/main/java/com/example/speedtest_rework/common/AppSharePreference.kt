package com.example.speedtest_rework.common

import android.content.Context
import android.content.SharedPreferences

class AppSharePreferenceconstructor(private val context: Context) {
    companion object{
        const val APP_SHARE_KEY = ""
    }

    private fun getSharedPreferences(): SharedPreferences?{
        return context.getSharedPreferences(APP_SHARE_KEY,Context.MODE_PRIVATE)
    }

}