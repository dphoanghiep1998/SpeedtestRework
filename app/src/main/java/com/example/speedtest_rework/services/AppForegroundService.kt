package com.example.speedtest_rework.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.speedtest_rework.R
import com.example.speedtest_rework.activities.MainActivity
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.buildMinVersionM
import com.example.speedtest_rework.common.utils.buildMinVersionO
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.util.*

enum class ServiceType {
    NONE,
    SPEED_MONITOR,
    DATA_USAGE,
    BOTH;

    companion object {
        fun toServiceType(enumString: String): ServiceType {
            return try {
                valueOf(enumString)
            } catch (ex: Exception) {
                NONE
            }
        }
    }

}

class AppForegroundService : Service() {
    private lateinit var job: Job
    private lateinit var job2: Job
    private val scope = CoroutineScope(Dispatchers.IO)
    private var first = true
    private var tempRx = 0L
    private var tempTx = 0L
    private lateinit var mNotificationManager: NotificationManager
    override fun onCreate() {
        super.onCreate()
        remoteViews = RemoteViews(packageName, R.layout.layout_notification_speed_test)
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        generateNotification()
        return START_NOT_STICKY
    }

    private fun generateNotification() {


        when (serviceType) {
            ServiceType.SPEED_MONITOR -> {
                remoteViews.setOnClickPendingIntent(
                    R.id.btn_speed_test_notification,
                    startPendingIntent(
                        getString(R.string.action_do_speed_test),
                        getString(R.string.key_speed_test),
                        REQUEST_CODE
                    )
                )
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.GONE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.GONE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.VISIBLE)
            }
            ServiceType.DATA_USAGE -> {
                remoteViews.setOnClickPendingIntent(
                    R.id.container_data_usage,
                    startPendingIntent(
                        getString(R.string.action_show_data_usage),
                        getString(R.string.key_data_usage),
                        REQUEST_CODE_1
                    )
                )
                remoteViews.setViewVisibility(R.id.tv_upload_value_notification, View.GONE)
                remoteViews.setViewVisibility(R.id.tv_download_value_notification, View.GONE)
                remoteViews.setViewVisibility(R.id.mobile_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.wifi_usage_value, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.GONE)
            }
            ServiceType.BOTH -> {
                remoteViews.setOnClickPendingIntent(
                    R.id.container_speed_monitor,
                    startPendingIntent(
                        getString(R.string.action_do_speed_test),
                        getString(R.string.key_speed_test),
                        REQUEST_CODE
                    )
                )
                remoteViews.setOnClickPendingIntent(
                    R.id.container_data_usage,
                    startPendingIntent(
                        getString(R.string.action_show_data_usage),
                        getString(R.string.key_data_usage),
                        REQUEST_CODE_1
                    )
                )

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
            ServiceType.SPEED_MONITOR -> job = startRepeatingJob(2000L, builder)
            ServiceType.DATA_USAGE -> job2 = startRepeatingJob(builder)
            ServiceType.BOTH -> {
                job = startRepeatingJob(2000L, builder)
                job2 = startRepeatingJob(builder)
            }
            else -> Unit
        }


    }

    private fun startRepeatingJob(timeInterval: Long, builder: NotificationCompat.Builder): Job {
        if(::job.isInitialized){
            job.cancel()
        }
        return scope.launch {
            while (true) {
                ensureActive()
                setDataSpeedMonitor(builder)
                delay(timeInterval)
                first = false
            }
        }
    }

    private fun startRepeatingJob(builder: NotificationCompat.Builder): Job {
        if(::job2.isInitialized){
            job2.cancel()
        }
        return scope.launch {
            while (true) {
                ensureActive()
                setDataUsageMonitor(builder)
                delay(60 * 60 * 1000L)
            }
        }
    }

    private fun setDataSpeedMonitor(builder: NotificationCompat.Builder) {
        if (first) {
            tempRx = TrafficStats.getTotalRxBytes()
            tempTx = TrafficStats.getTotalTxBytes()
        } else {
            val rxByte: Long = TrafficStats.getTotalRxBytes()
            val txByte: Long = TrafficStats.getTotalTxBytes()
            if (tempRx > 0 && tempTx > 0) {
                remoteViews.setTextViewText(
                    R.id.tv_download_value_notification,
                    (convert((rxByte - tempRx) / 1024f, 0))
                )
                remoteViews.setTextViewText(
                    R.id.tv_upload_value_notification,
                    (convert((txByte - tempTx) / 1024f, 1))
                )
            }
            tempRx = rxByte
            tempTx = txByte
        }
        mNotificationManager.notify(SERVICE_ID, builder.build())
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
        mNotificationManager.notify(SERVICE_ID, builder.build())

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
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)



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

    private fun startPendingIntent(key: String, value: String, requestCode: Int): PendingIntent {
        val speedIntent = Intent(this, MainActivity::class.java)
        speedIntent.putExtra(key, value)
        return if (buildMinVersionM()) {
            PendingIntent.getActivity(
                this,
                requestCode,
                speedIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                requestCode,
                speedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
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
                } ${getString(R.string.Kbs)}"
            }
        } else {
            return if (type == 0) {
                "${
                    (value / 1024).toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Mbs)}"
            } else {
                "${
                    (value / 1024).toBigDecimal().setScale(1, RoundingMode.UP)
                } ${getString(R.string.Mbs)}"
            }
        }

    }

    private fun convertData(value: Float): String {
        return when {
            value <= 0 -> "0.0 MB"
            value < 1024 -> "${round(value)} B"
            value < 1024 * 1024 -> "${round(value / 1024)} KB"
            value < 1024 * 1024 * 1024 -> "${round(value / (1024 * 1024))} MB"
            else -> "${round(value / (1024 * 1024 * 1024))} GB"
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
        remoteViews.setViewVisibility(R.id.btn_speed_test_notification, View.VISIBLE)

    }

    fun startService(context: Context, sType: ServiceType) {
        val intent = Intent(context, AppForegroundService::class.java)
        serviceType = if (serviceType == ServiceType.NONE) {
            AppSharePreference.INSTANCE.saveServiceType(R.string.service_type_key, sType)
            sType
        } else if (serviceType != sType && serviceType != ServiceType.NONE) {
            AppSharePreference.INSTANCE.saveServiceType(R.string.service_type_key, ServiceType.BOTH)
            ServiceType.BOTH
        } else {
            AppSharePreference.INSTANCE.saveServiceType(R.string.service_type_key, sType)
            sType
        }

        if (buildMinVersionO()) {
            context.applicationContext.startForegroundService(intent)
        } else {
            context.applicationContext.startService(intent)
        }
    }

    fun stopService(context: Context, sType: ServiceType) {
        when (serviceType) {
            ServiceType.BOTH -> {
                serviceType = when (sType) {
                    ServiceType.DATA_USAGE -> {
                        hideViewDataUsage()
                        if(::job2.isInitialized) job2.cancel()
                        AppSharePreference.INSTANCE.saveServiceType(
                            R.string.service_type_key,
                            ServiceType.SPEED_MONITOR
                        )
                        ServiceType.SPEED_MONITOR
                    }
                    else -> {
                        if(::job.isInitialized) job.cancel()
                        hideViewSpeedMonitor()
                        AppSharePreference.INSTANCE.saveServiceType(
                            R.string.service_type_key,
                            ServiceType.DATA_USAGE
                        )
                        ServiceType.DATA_USAGE
                    }
                }

            }
            else -> {
                if(::job.isInitialized) job.cancel()
                if(::job2.isInitialized) job2.cancel()
                serviceType = ServiceType.NONE
                AppSharePreference.INSTANCE.saveServiceType(
                    R.string.service_type_key,
                    ServiceType.NONE
                )
                killService(context)
            }
        }
    }


    private fun killService(context: Context) {
        val intent = Intent(context, AppForegroundService::class.java)
        context.stopService(intent)
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isServiceDataUsageRunning(context: Context, serviceClass: Class<*>): Boolean {
        if (isServiceRunning(
                context,
                serviceClass
            ) && (serviceType == ServiceType.DATA_USAGE || serviceType == ServiceType.BOTH)
        ) {
            return true
        }
        return false
    }

    fun isServiceSpeedMonitorRunning(context: Context, serviceClass: Class<*>): Boolean {
        if (isServiceRunning(
                context,
                serviceClass
            ) && (serviceType == ServiceType.SPEED_MONITOR || serviceType == ServiceType.BOTH)
        ) {
            return true
        }
        return false
    }


    companion object {
        private lateinit var INSTANCE: AppForegroundService
        private lateinit var remoteViews: RemoteViews
        private const val REQUEST_CODE = 123
        private const val REQUEST_CODE_1 = 1234
        private const val SERVICE_ID = 2022
        var serviceType =
            AppSharePreference.INSTANCE.getServiceType(R.string.service_type_key, ServiceType.NONE)


        @JvmStatic
        fun getInstance(): AppForegroundService {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = AppForegroundService()
            }
            return INSTANCE
        }


    }

}
