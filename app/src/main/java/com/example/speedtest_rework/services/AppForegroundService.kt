package com.example.speedtest_rework.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity

class AppForegroundService : Service() {
    override fun onCreate() {
        Log.d("TAG", "onCreatdsaasdsaDSADASDASDASDASDAe: " )
        super.onCreate()
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification()
        return START_NOT_STICKY
    }

    private fun sendNotification() {
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intentMainLanding, 0)
        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))

        builder.setContentTitle(
            StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
                .toString()
        )
            .setTicker(
                StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                    .toString()
            )
            .setContentText("Touch to open")
            .setSmallIcon(R.drawable.ic_download)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)


        builder.color = resources.getColor(R.color.purple_200)
        val notification = builder.build()

        startForeground(123, notification)
    }

}