package com.example.speedtest_rework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.speedtest_rework.common.Constant


class ConnectivityListener constructor(
    context: Context
) :
    BroadcastReceiver() {
    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    var isWifiEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var isConnectivityChanged: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var scanResults: MutableLiveData<List<ScanResult>> = MutableLiveData<List<ScanResult>>()


    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        if (action != null) {
            when (action) {
                Constant.INTENT_FILER_CONNECTIVITYCHANGE -> {
                    isConnectivityChanged.postValue(true)
                    if (wifiManager.isWifiEnabled) {
                        isWifiEnabled.postValue(true)
                    } else {
                        isWifiEnabled.postValue(false)
                    }
                }
                Constant.INTENT_FILER_SCAN_RESULT -> {
                    Log.d("onReceive", "onReceive: " + wifiManager.scanResults)
                    scanResults.postValue(wifiManager.scanResults)
                }
            }
        }
    }
}