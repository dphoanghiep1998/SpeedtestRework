package com.example.speedtest_rework.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.buildMinVersionO

class AppForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        generateNotification()
        return START_NOT_STICKY
    }

    private fun generateNotification() {
        val remoteViews = RemoteViews(packageName, R.layout.layout_notification_speed_test)
        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.ic_logo_menu)
            .setCustomContentView(remoteViews).setAutoCancel(false).setShowWhen(false)

        startForeground(123, builder.build())
    }

    companion object {
        fun startMy(context: Context) {
            val intent = Intent(context, AppForegroundService::class.java)
            if (buildMinVersionO()) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

}