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
    private var serviceType = ServiceType.NONE
    private lateinit var remoteViews: RemoteViews

    override fun onCreate() {
        remoteViews = RemoteViews(packageName, R.layout.layout_notification_speed_test)
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when (intent?.action) {
//            getString(R.string.action_do_speed_test) -> generateNotification()
//        }
        Log.d("TAG", "onStartCommand: ")
        generateNotification()
        return START_NOT_STICKY
    }

    private fun generateNotification() {
        Log.d("TAG", "generateNotification: " + this.serviceType)
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
            ServiceType.BOTH -> {
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.GONE)
            }
            else -> Unit
        }

        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        builder.setAutoCancel(true)
        builder.setSmallIcon(R.drawable.ic_logo_menu)
            .setCustomContentView(remoteViews).setAutoCancel(false).setShowWhen(false)
            .setOnlyAlertOnce(true)
        startForeground(SERVICE_ID, builder.build())

        when (serviceType) {
            ServiceType.SPEED_MONITOR -> setDataSpeedMonitor(builder)
            ServiceType.DATA_USAGE -> setDataUsageMonitor(builder)
            ServiceType.BOTH -> {
                setDataSpeedMonitor(builder)
                setDataUsageMonitor(builder)
            }
            else -> Unit
        }


    }

    private fun setDataSpeedMonitor(builder: NotificationCompat.Builder) {
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
                        remoteViews.setTextViewText(
                            R.id.tv_download_value_notification,
                            (convert((rxByte - tempRx) / 1024f, 0)).toString()
                        )
                        remoteViews.setTextViewText(
                            R.id.tv_upload_value_notification,
                            (convert((txByte - tempTx) / 1024f, 1)).toString()
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
    private fun setDataUsageMonitor(builder: NotificationCompat.Builder) {
        val mobileData = getTodayMobileUsageData(applicationContext)
        val wifiData = getTodayWifiDataUsage(applicationContext)
        val totalMobile = mobileData.rxBytes + mobileData.txBytes
        val totalWifi = wifiData.rxBytes + wifiData.txBytes

        remoteViews.setTextViewText(
            R.id.mobile_usage_value,
            convertData(totalMobile.toFloat())
        )
        remoteViews.setTextViewText(
            R.id.wifi_usage_value, convertData(totalWifi.toFloat())
        )

        startForeground(
            SERVICE_ID, builder.build()
        )
    }

    @SuppressLint("NewApi")
    private fun getTodayMobileUsageData(context: Context): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return manager.querySummaryForDevice(
            0,
            null,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
    }

    @SuppressLint("NewApi")
    private fun getTodayWifiDataUsage(context: Context): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        val calendar = Calendar.getInstance()
        Log.d("TAG", "getTodayWifiDataUsage: " + calendar.timeInMillis)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        Log.d("TAG", "getTodayWifiDataUsage: " + calendar.timeInMillis)
        Log.d("TAG", "System.currentTimeMillis(): " + System.currentTimeMillis())


        return manager.querySummaryForDevice(
            1,
            null,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
    }


    @SuppressLint("NewApi")
    private fun getUsageStatsManager(context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    private fun startPendingIntent(key: String, value: String): PendingIntent {
        val speedIntent = Intent(this, MainActivity::class.java)
        speedIntent.putExtra(key, value)
        speedIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            this,
            REQUEST_CODE,
            speedIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun convert(value: Float, type: Int): String {
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

    private fun convertData(value: Float): String {
        return when {
            value <= 0 -> "0 MB"
            value < 1024 -> "${round(value)} B"
            value < 1024 * 1024 -> "${round(value) / 1024} KB"
            value < 1024 * 1024 * 1024 -> "${round(value) / (1024 * 1024)} MB"
            else -> "${round(value) / (1024 * 1024 * 1024)} GB"
        }
    }


    private fun round(value: Float): Float {
        return value.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
    }

    private fun hideViewSpeedMonitor() {
        remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.GONE)
        remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.GONE)
        remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.GONE)
    }

    private fun hideViewDataUsage() {
        remoteViews.setViewVisibility(R.id.mobile_usage_value, View.GONE)
        remoteViews.setViewVisibility(R.id.wifi_usage_value, View.GONE)
    }

    fun startService(context: Context, sType: ServiceType) {
        val intent = Intent(context, AppForegroundService::class.java)
//        if (this.serviceType == ServiceType.NONE ) {
//            this.serviceType = sType
//        } else if (this.serviceType != sType && this.serviceType != ServiceType.NONE) {
//            this.serviceType = ServiceType.BOTH
//        }else{
//            this.serviceType = sType
//        }
        this.serviceType = sType

        Log.d("TAG", "startService: " + this.serviceType)

        if (buildMinVersionO()) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService(context: Context, sType: ServiceType) {
        when (serviceType) {
            ServiceType.BOTH -> {
                serviceType = when (sType) {
                    ServiceType.DATA_USAGE -> {
                        hideViewDataUsage()
                        ServiceType.SPEED_MONITOR
                    }
                    else -> {
                        hideViewSpeedMonitor()
                        ServiceType.DATA_USAGE
                    }
                }

            }
            else -> {
                serviceType = ServiceType.NONE
                killService(context)
            }
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


    companion object {
        private var INSTANCE: AppForegroundService? = null
        private const val REQUEST_CODE = 123
        private const val SERVICE_ID = 2022

        fun getInstance(): AppForegroundService {
            Log.d("TAG", "getInstance: "+ INSTANCE)
            if (INSTANCE == null) {
                INSTANCE = AppForegroundService()
            }
            return INSTANCE as AppForegroundService
        }


    }

}
