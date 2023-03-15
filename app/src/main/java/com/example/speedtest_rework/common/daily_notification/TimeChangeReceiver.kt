package com.example.speedtest_rework.common.daily_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.speedtest_rework.common.utils.Constant


class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtils.setTimeEveryDay(
            context,
            19,
            0,
            Constant.NOTI_EVERYDAY_EVENING
        )
    }

}
