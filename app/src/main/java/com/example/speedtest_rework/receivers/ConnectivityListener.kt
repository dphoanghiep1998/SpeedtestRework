package com.example.speedtest_rework.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData
import com.example.speedtest_rework.common.Constant
import javax.inject.Inject


class ConnectivityListener constructor(
    private val context: Context
) :
    BroadcastReceiver() {
    private val wifiManager: WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
     var isWifiEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
     var isConnectivityChanged: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
     var scanResults: MutableLiveData<List<ScanResult>> = MutableLiveData<List<ScanResult>>()


    override fun onReceive(context: Context?, intent: Intent?) {
        var action: String? = intent?.action
        if (action != null) {
                when (action) {
                    Constant.INTENT_FILER_CONNECTIVITYCHANGE -> {
                        isConnectivityChanged.postValue(true)
                        if (wifiManager.isWifiEnabled){
                            isWifiEnabled.postValue(true)
                        }
                    }
                    Constant.INTENT_FILER_SCAN_RESULT -> {
                        scanResults.postValue(wifiManager.scanResults)
                    }
                }

        }

    }
}