package com.example.speedtest_rework.common.daily_notification

import android.content.Context
import androidx.work.*
import com.example.speedtest_rework.common.utils.Constant
import com.neko.hiepdph.calculatorvault.common.daily_notification.NotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

object NotificationMain {
    fun scheduleDailyNotification(context: Context) {
        val calendarCurrent = Calendar.getInstance()
        val calendarPM = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val timeDelay = when {
            calendarCurrent.timeInMillis <= calendarPM.timeInMillis -> calendarPM.timeInMillis - calendarCurrent.timeInMillis
            else -> {
                calendarPM.add(Calendar.DAY_OF_YEAR, 1)
                calendarPM.timeInMillis - calendarCurrent.timeInMillis
            }
        }


        val constraint =
            Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build()

        val notificationWorkRequest2 =
            PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.DAYS)
                .setInitialDelay(
                    timeDelay, TimeUnit.MILLISECONDS
                ).setConstraints(constraint).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(Constant.NOTI_EVERYDAY,ExistingPeriodicWorkPolicy.REPLACE,notificationWorkRequest2)
    }

}