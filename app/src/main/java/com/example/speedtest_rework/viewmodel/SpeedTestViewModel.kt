package com.example.speedtest_rework.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeedTestViewModel(application: Application) : AndroidViewModel(application) {
    val isScanning = MutableLiveData<Boolean>()
    val isPermissionGrandted = MutableLiveData<Boolean>()
    val isWifiEnabled = MutableLiveData<Boolean>()
}