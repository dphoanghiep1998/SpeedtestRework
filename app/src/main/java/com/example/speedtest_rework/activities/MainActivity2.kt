package com.example.speedtest_rework.activities

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.buildMinVersionS
import com.example.speedtest_rework.databinding.ActivityMain2Binding
import kotlinx.coroutines.*

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var job:Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        calculateWifi()
    }

    private fun calculateWifi() {
        if (buildMinVersionS()) {

        } else {
            val wifiManager: WifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

             job = lifecycleScope.launch {
                while (isActive) {
                    val wifiInfo: WifiInfo = wifiManager.connectionInfo
                    Log.d("TAG", "calculateWifi: " + wifiInfo.rssi)
                    binding.tvRssi.text = wifiInfo.rssi.toString()
                    delay(2000L)
                }
            }
            binding.btnStop.setOnClickListener {
                job.cancel()
            }
            binding.btnStart.setOnClickListener {
                calculateWifi()
            }

        }

    }

}