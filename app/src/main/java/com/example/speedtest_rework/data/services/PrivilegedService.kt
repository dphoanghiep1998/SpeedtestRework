package com.example.speedtest_rework.data.services

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PrivilegedService @Inject constructor() {
    fun getUsageData() {

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkData(context: Context): NetworkStats.Bucket {
        val manager = getUsageStatsManager(context)
        val calendar = Calendar.getInstance()
        return manager.querySummaryForDevice(
            0,
            null,
            calendar.timeInMillis - 24 * 60 * 60 * 1000 * 30L,
            calendar.timeInMillis
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUsageStatsManager(context: Context): NetworkStatsManager {
        return context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

}