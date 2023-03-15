package com.example.speedtest_rework.common.daily_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Thangdd
 */
class AlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        startAlarm(intent, context)
        refreshAlarms(intent, context)
    }

    private fun getAlarmEverydayEvening(): AlarmData? {
        return AppSharePreference.INSTANCE.getObjectFromSharePreference<AlarmData>(
            Constant.NOTI_EVERYDAY_EVENING
        )
    }

    private fun refreshAlarms(intent: Intent?, context: Context) {
        val id = intent?.getIntExtra(Constant.ALARM_ID, -1)
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.Main).launch {
                when (id) {
                    19 -> {
                        getAlarmEverydayEvening()?.let { scheduleAlarmManager(context, it) }
                    }
                }
            }
        }
    }


    private fun startAlarm(intent: Intent?, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            val id = intent?.getIntExtra(Constant.ALARM_ID, -1)
            var alarm: AlarmData? = null
            when (id) {
                19 -> {
                    alarm = getAlarmEverydayEvening()
                }
            }

            alarm?.let {
                NotificationUtils.runNotification(
                    context,
                    alarm.type,
                    it.alarm_id
                )
            }
        }

    }

//    private fun startAlarm(context: Context) {
//        CoroutineScope(Dispatchers.Main).launch {
//            val alarm_morning = getAlarmEverydayMorning()
//            val alarm_evening = getAlarmEverydayEvening()
//            scheduleAlarmManager(context, alarm_morning!!)
//            scheduleAlarmManager(context, alarm_evening!!)
//
//            NotificationUtils.runNotification(
//                context = context,
//                type = alarm_morning.type,
//                alarmID = alarm_morning.alarm_id,
//            )
//            NotificationUtils.runNotification(
//                context = context,
//                type = alarm_evening.type,
//                alarmID = alarm_evening.alarm_id,
//            )
//
//        }
//
//    }


}
