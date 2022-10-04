package com.example.speedtest_rework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
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
    var dataCache : MutableLiveData<Pair<List<ScanResult>,WifiInfo>> = MutableLiveData()


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

                    dataCache.postValue(Pair(wifiManager.scanResults,wifiManager.connectionInfo))

                }
            }
        }
    }
}