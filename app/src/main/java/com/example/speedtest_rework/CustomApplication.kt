package com.example.speedtest_rework

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.speedtest_rework.common.AppSharePreference
import com.example.speedtest_rework.common.buildMinVersionM
import com.example.speedtest_rework.common.buildMinVersionO
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
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if (buildMinVersionO()) {
            val channel = NotificationChannel(
                applicationContext.getString(R.string.channel_id),
                applicationContext.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(channel)
            }
        }
    }
}