package com.example.speedtest_rework.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.buildMinVersionO
import java.math.RoundingMode

class AppForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            getString(R.string.action_do_speed_test) -> generateNotification()
        }
        generateNotification()
        return START_NOT_STICKY
    }

    private fun generateNotification() {

        val remoteViews = RemoteViews(packageName, R.layout.layout_notification_speed_test)
        remoteViews.setOnClickPendingIntent(
            R.id.btn_speed_test_notification,
            startPendingIntent(
                getString(R.string.action_do_speed_test),
                getString(R.string.key_speed_test)
            )
        )
        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.ic_logo_menu)
            .setCustomContentView(remoteViews).setAutoCancel(false).setShowWhen(false)
            .setOnlyAlertOnce(true)
        startForeground(SERVICE_ID, builder.build())
        setDataSpeedMonitor(remoteViews, builder)


    }

    private fun setDataSpeedMonitor(remoteView: RemoteViews, builder: NotificationCompat.Builder) {
        var tempRx = 0L
        var tempTx = 0L
        val countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 2000L) {
            override fun onTick(tick: Long) {
                if (tick < 2000) {
                    tempRx = TrafficStats.getTotalRxBytes()
                    tempTx = TrafficStats.getTotalTxBytes()
                } else {
                    val rxByte: Long = TrafficStats.getTotalRxBytes()
                    val txByte: Long = TrafficStats.getTotalTxBytes()
                    if (tempRx > 0 && tempTx > 0) {
                        remoteView.setTextViewText(
                            R.id.tv_download_value_notification,
                            (convert((rxByte - tempRx) / 1000f, remoteView)).toString()
                        )
                        remoteView.setTextViewText(
                            R.id.tv_upload_value_notification,
                            (convert((txByte - tempTx) / 1000f, remoteView)).toString()
                        )
                    }
                    tempRx = rxByte
                    tempTx = txByte
                }
                startForeground(SERVICE_ID, builder.build())
            }
            override fun onFinish() {
            }

        }
        countDownTimer.start()
    }

    private fun startPendingIntent(key: String, value: String): PendingIntent {
        val speedIntent = Intent(this, MainActivity::class.java)
        speedIntent.putExtra(key, value)
        speedIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return PendingIntent.getActivity(
            this,
            REQUEST_CODE,
            speedIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun convert(value: Float, remoteView: RemoteViews): Double {
        if (value < 1000) {
            remoteView.setTextViewText(
                R.id.tv_download_currency_notification,
                getString(R.string.Kbs)
            )
            remoteView.setTextViewText(
                R.id.tv_upload_currency_notification,
                getString(R.string.Kbs)
            )

            return value.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
        } else {
            remoteView.setTextViewText(
                R.id.tv_download_currency_notification,
                getString(R.string.Mbs)
            )
            remoteView.setTextViewText(
                R.id.tv_upload_currency_notification,
                getString(R.string.Mbs)
            )
            return (value / 1024).toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
        }

    }

    companion object {
        private const val REQUEST_CODE = 123
        private const val SERVICE_ID = 2022
        fun startService(context: Context) {
            val intent = Intent(context, AppForegroundService::class.java)

            if (buildMinVersionO()) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun killService(context: Context) {
            val intent = Intent(context, AppForegroundService::class.java)
            context.stopService(intent)
        }
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

}