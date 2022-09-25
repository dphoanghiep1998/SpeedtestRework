package com.example.speedtest_rework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class WifiListener(private val viewModel: SpeedTestViewModel) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN)){
            WifiManager.WIFI_STATE_ENABLED -> viewModel.isWifiEnabled.postValue(true)
            WifiManager.WIFI_STATE_DISABLED -> viewModel.isWifiEnabled.postValue(false)
        }

    }
}