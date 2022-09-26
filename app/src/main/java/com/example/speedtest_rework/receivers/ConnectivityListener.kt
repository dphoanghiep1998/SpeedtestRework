package com.example.speedtest_rework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.speedtest_rework.common.NetworkUtils

class ConnectivityListener: BroadcastReceiver() {
    private var lastNameConnection : String =""
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive: " + context?.let { NetworkUtils.isConnected(it) })
        Log.d("TAG", "onReceive: ${intent?.action}")
        Log.d("TAG", "onReceive: " + this.isInitialStickyBroadcast)

    }
}