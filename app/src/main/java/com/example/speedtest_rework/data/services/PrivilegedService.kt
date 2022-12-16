package com.example.speedtest_rework.data.services

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.example.speedtest_rework.data.model.UsagePackageModel
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject


class PrivilegedService @Inject constructor(@ApplicationContext val context: Context) {

    fun getUsageData(): List<DataUsageModel> {

        val mList = mutableListOf<DataUsageModel>()
        val calendar = Calendar.getInstance()
        for (i in 0..29) {
            val startCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance()

            startCalendar.add(Calendar.DAY_OF_MONTH, -i)
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)

            endCalendar.add(Calendar.DAY_OF_MONTH, -i)
            endCalendar.set(Calendar.HOUR_OF_DAY, 23)
            endCalendar.set(Calendar.MINUTE, 59)
            endCalendar.set(Calendar.SECOND, 59)

            val bucketMobile = getNetworkMobileData(
                context,
                startCalendar.timeInMillis,
                if (i != 0) endCalendar.timeInMillis else calendar.timeInMillis
            )
            val bucketWifi = getNetworkWifiData(
                context,
                startCalendar.timeInMillis,
                if (i != 0) endCalendar.timeInMillis else calendar.timeInMillis
            )
            val dataUsageModel = DataUsageModel(
                startCalendar.time,
                (bucketMobile.txBytes + bucketMobile.rxBytes).toDouble(),
                (bucketWifi.txBytes + bucketWifi.rxBytes).toDouble()
            )
            mList.add(dataUsageModel)
        }
        return mList
    }

    fun getUsageAppList(): List<UsagePackageModel> {
        val usageAppList = mutableListOf<UsagePackageModel>()
        for (info in context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
            if (context.packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                val networkStatsManager = getUsageStatsManager(context)
                val netWorkStatsMobile = networkStatsManager.queryDetailsForUid(
                    0, "", 0L, System.currentTimeMillis(), info.uid
                )
                val netWorkStatsWifi = networkStatsManager.queryDetailsForUid(
                    1, "", 0L, System.currentTimeMillis(), info.uid
                )
                var rxBytesMobile = 0L
                var txBytesMobile = 0L
                var rxBytesWifi = 0L
                var txBytesWifi = 0L
                val bucketMobile = NetworkStats.Bucket()
                val bucketWifi = NetworkStats.Bucket()

                while (netWorkStatsMobile.hasNextBucket()) {
                    netWorkStatsMobile.getNextBucket((bucketMobile))
                    rxBytesMobile += bucketMobile.rxBytes
                    txBytesMobile += bucketMobile.txBytes
                }
                while (netWorkStatsWifi.hasNextBucket()) {
                    netWorkStatsWifi.getNextBucket((bucketWifi))
                    rxBytesWifi += bucketWifi.rxBytes
                    txBytesWifi += bucketWifi.txBytes
                }
                loadIcon(info.packageName)
                usageAppList.add(
                    UsagePackageModel(
                        info.uid,
                        loadIcon(info.packageName),
                        rxBytesMobile + txBytesMobile,
                        rxBytesWifi + txBytesWifi,
                        rxBytesMobile + txBytesMobile + rxBytesWifi + txBytesWifi
                    )
                )
                netWorkStatsMobile.close()
                netWorkStatsWifi.close()
            }
        }
        return usageAppList
    }


    private fun getNetworkMobileData(
        context: Context, startTime: Long, endTime: Long
    ): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        return manager.querySummaryForDevice(
            0, null, startTime, endTime
        )
    }

    private fun getNetworkWifiData(
        context: Context, startTime: Long, endTime: Long
    ): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        return manager.querySummaryForDevice(
            1, null, startTime, endTime
        )
    }


    private fun getUsageStatsManager(context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    private fun loadIcon(packageName: String): Drawable? {
        try {
            return context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return null
    }

}