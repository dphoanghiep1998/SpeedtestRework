package com.example.speedtest_rework

import android.app.Application
import com.example.speedtest_rework.common.AppSharePreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication() : Application() {
    companion object {
        lateinit var app: CustomApplication

    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(applicationContext)
    }
}