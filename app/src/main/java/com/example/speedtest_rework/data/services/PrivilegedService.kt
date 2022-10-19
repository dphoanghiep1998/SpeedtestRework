package com.example.speedtest_rework.data.services

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class PrivilegedService @Inject constructor(@ApplicationContext val context: Context) {

    @RequiresApi(Build.VERSION_CODES.M)
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
            val bucketWifi =
                getNetworkWifiData(
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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkMobileData(
        context: Context,
        startTime: Long,
        endTime: Long
    ): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        return manager.querySummaryForDevice(
            0,
            null,
            startTime,
            endTime
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkWifiData(
        context: Context,
        startTime: Long,
        endTime: Long
    ): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        return manager.querySummaryForDevice(
            1,
            null,
            startTime,
            endTime
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUsageStatsManager(context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

}