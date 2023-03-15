package com.example.speedtest_rework.common.daily_notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.speedtest_rework.common.utils.Constant.ALARM_ID
import com.example.speedtest_rework.common.utils.buildMinVersionM
import com.example.speedtest_rework.common.utils.buildMinVersionS
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Thangdd modified by mr H
 */
fun scheduleAlarmManager(context: Context, alarm: AlarmData) {
    var newTime: Long
    if (System.currentTimeMillis() > alarm.time) {
        val oldCalender = Calendar.getInstance()
        oldCalender.time = Date(alarm.time)
        val newCalendar = Calendar.getInstance()
        newCalendar[Calendar.HOUR_OF_DAY] = oldCalender[Calendar.HOUR_OF_DAY]
        newCalendar[Calendar.MINUTE] = oldCalender[Calendar.MINUTE]
        newCalendar[Calendar.SECOND] = 0
        newCalendar[Calendar.MILLISECOND] = 0
        newTime = newCalendar.time.time + TimeUnit.DAYS.toMillis(1)
    } else {
        newTime = alarm.time
    }

    val intent = Intent(context, AlarmsReceiver::class.java)
    intent.putExtra(ALARM_ID, alarm.alarm_id)
    val alarmIntentRTC: PendingIntent = PendingIntent.getBroadcast(
        context, alarm.alarm_id, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (buildMinVersionM()) {
        if(buildMinVersionS()){
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, newTime, alarmIntentRTC
                )
            }
        }else{
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, newTime, alarmIntentRTC
            )
        }

    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, newTime, alarmIntentRTC
        )
    }


}
