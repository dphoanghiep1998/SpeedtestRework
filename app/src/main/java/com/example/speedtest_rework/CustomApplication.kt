package com.example.speedtest_rework

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.speedtest_rework.common.Configuration
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.buildMinVersionO
import com.example.speedtest_rework.services.AppForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication() : Application(),Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
     var currentActivity: Activity? = null

    companion object {
        lateinit var app: CustomApplication
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppSharePreference.getInstance(applicationContext)
        Configuration.getInstance()
        registerActivityLifecycleCallbacks(this)
        AppForegroundService.getInstance()
        NetworkUtils(applicationContext)
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



    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        currentActivity = null
    }
}