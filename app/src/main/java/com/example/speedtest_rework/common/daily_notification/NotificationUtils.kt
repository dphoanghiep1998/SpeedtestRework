package com.example.speedtest_rework.common.daily_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.buildMinVersionO
import java.util.*
import kotlin.random.Random.Default.nextInt


/**
 * Created by Thangdd
 */
object NotificationUtils {

    fun setTimeEveryDay(context: Context, hour: Int, minute: Int, type: String) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        scheduleAlarm(context, calendar, type, 1, hour)
    }


    private fun scheduleAlarm(
        context: Context, calendar: Calendar, type: String, dayRepeat: Int = 1, id: Int
    ) {
        val alarm = AlarmData(
            alarm_id = id, time = calendar.timeInMillis, type = type, repeat = dayRepeat
        )
        AppSharePreference.INSTANCE.saveObjectToSharePreference(type, alarm)
        scheduleAlarmManager(context, alarm)
    }


    private fun isCheckShowEvening(hour: Int, minute: Int): Boolean {
        return minute == 0 && hour == 19
    }

    fun runNotification(context: Context, type: String, alarmID: Int) {
        if (alarmID == 19) {
            if (!isCheckShowEvening(
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE)
                )
            ) return
        }

        val remoteViews = RemoteViews(context.packageName, R.layout.layout_notification_small)
        val remoteViewsExpand = RemoteViews(context.packageName, R.layout.layout_notification_expand)

        if (buildMinVersionO()) {
            val channel = NotificationChannel(
                Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val mIntent = Intent(context.applicationContext, MainActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mIntent.putExtra(Constant.KEY_ACTION_SPEED_TEST, Constant.KEY_ACTION_SPEED_TEST)

        val pendingIntent = PendingIntent.getActivity(
            context,
            1231213,
            mIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, Constant.CHANNEL_ID)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setAutoCancel(true).setShowWhen(true).setSmallIcon(R.drawable.ic_logo_notification)
            .setWhen(System.currentTimeMillis())
            .setCustomBigContentView(remoteViewsExpand)
            .setContentIntent(pendingIntent).setOngoing(false).priority =
            NotificationCompat.PRIORITY_HIGH

        val am = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        am.notify(1231213, builder.build())

    }
}