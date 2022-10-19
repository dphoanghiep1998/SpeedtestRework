package com.example.speedtest_rework.services

import android.annotation.SuppressLint
import android.app.*
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.buildMinVersionO
import java.math.RoundingMode
import java.util.*

enum class ServiceType() {
    NONE,
    SPEED_MONITOR,
    DATA_USAGE,
    BOTH
}

class AppForegroundService : Service() {
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            getString(R.string.action_do_speed_test) -> generateNotification()
        }
        generateNotification()
        return START_NOT_STICKY
    }

    private fun generateNotification() {
        val remoteViews = RemoteViews(packageName, R.layout.layout_notification_speed_test)

        when (serviceType) {
            ServiceType.SPEED_MONITOR -> {
                remoteViews.setOnClickPendingIntent(
                    R.id.btn_speed_test_notification,
                    startPendingIntent(
                        getString(R.string.action_do_speed_test),
                        getString(R.string.key_speed_test)
                    )
                )
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.GONE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.GONE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.VISIBLE)
            }
            ServiceType.DATA_USAGE -> {
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.GONE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.GONE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.GONE)
            }
            else -> {
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.GONE)
            }
        }

        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.ic_logo_menu)
            .setCustomContentView(remoteViews).setAutoCancel(false).setShowWhen(false)
            .setOnlyAlertOnce(true)
        startForeground(SERVICE_ID, builder.build())

        when (serviceType) {
            ServiceType.SPEED_MONITOR -> setDataSpeedMonitor(remoteViews, builder)
            ServiceType.DATA_USAGE -> setDataUsageMonitor(remoteViews, builder)
            ServiceType.BOTH -> {
                setDataSpeedMonitor(remoteViews, builder)
                setDataUsageMonitor(remoteViews, builder)
            }
            else -> Unit
        }


    }

    private fun setDataSpeedMonitor(remoteView: RemoteViews, builder: NotificationCompat.Builder) {
        var tempRx = 0L
        var tempTx = 0L
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 2000L) {
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
                            (convert((rxByte - tempRx) / 1024f, remoteView, 0)).toString()
                        )
                        remoteView.setTextViewText(
                            R.id.tv_upload_value_notification,
                            (convert((txByte - tempTx) / 1024f, remoteView, 1)).toString()
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
        countDownTimer?.start()
    }

    @SuppressLint("NewApi")
    private fun setDataUsageMonitor(remoteView: RemoteViews, builder: NotificationCompat.Builder) {
        val networkStats = getUsageStatsList(applicationContext)
        val bucket = NetworkStats.Bucket()
        if(networkStats.hasNextBucket()){
            networkStats.getNextBucket(bucket)
            Log.d("TAG", "setDataUsageMonitor: ${bucket.rxBytes}")
            Log.d("TAG", "setDataUsageMonitor: ${bucket.txBytes}")
        }


        startForeground(SERVICE_ID, builder.build())
    }

    @SuppressLint("NewApi")
    private fun getUsageStatsList(context: Context): NetworkStats {
        val nsm: NetworkStatsManager = getUsageStatsManager(context)
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startTime = calendar.timeInMillis
        return nsm.queryDetails(0, null, startTime, endTime)
    }

    @SuppressLint("NewApi")
    private fun getUsageStatsManager(context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
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


    private fun convert(value: Float, remoteView: RemoteViews, type: Int): String {
        if (value < 1024) {
            return if (type == 0) {
                "${
                    value.toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Kbs)}"
            } else {
                "${
                    value.toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Mbs)}"
            }
        } else {
            return if (type == 0) {
                "${
                    (value / 1024).toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Kbs)}"
            } else {
                "${
                    (value / 1024).toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Mbs)}"
            }
        }

    }

    companion object {
        var serviceType = ServiceType.NONE
        private const val REQUEST_CODE = 123
        private const val SERVICE_ID = 2022
        fun startService(context: Context, serviceType: ServiceType) {
            val intent = Intent(context, AppForegroundService::class.java)
            if (this.serviceType == ServiceType.NONE) {
                this.serviceType = serviceType
            } else {
                this.serviceType = ServiceType.BOTH
            }
            if (buildMinVersionO()) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context, serviceType: ServiceType) {

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
