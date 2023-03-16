package com.neko.hiepdph.calculatorvault.common.daily_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.buildMinVersionO


class NotificationWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("TAG", "doWork: ")
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_notification_small)
        val remoteViewsExpand =
            RemoteViews(context.packageName, R.layout.layout_notification_expand)
        var numOfNotify = AppSharePreference.INSTANCE.getNumNotify(12)
        if (numOfNotify == 9) {
            numOfNotify = 12
            AppSharePreference.INSTANCE.saveNumNotify(numOfNotify)
        }
        if (buildMinVersionO()) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID,
                Constant.CHANNEL_ID_ALARM,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val mIntent = Intent(context.applicationContext, MainActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(
            context, 120, mIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews).setAutoCancel(true).setShowWhen(true)
            .setSmallIcon(R.drawable.ic_logo_notification).setWhen(System.currentTimeMillis())
            .setCustomBigContentView(remoteViewsExpand).setContentIntent(pendingIntent)
            .setOngoing(false).priority = NotificationCompat.PRIORITY_HIGH

        val am = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        am.notify(numOfNotify, builder.build())
        numOfNotify -= 1
        AppSharePreference.INSTANCE.saveNumNotify(numOfNotify)

        return Result.success()
    }

}
