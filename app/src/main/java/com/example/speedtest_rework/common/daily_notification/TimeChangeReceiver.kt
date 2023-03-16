package com.example.speedtest_rework.common.daily_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.speedtest_rework.common.daily_notification.NotificationMain


class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationMain.scheduleDailyNotification(context)
    }

}
