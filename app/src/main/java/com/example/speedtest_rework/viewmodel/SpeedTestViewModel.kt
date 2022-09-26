package com.example.speedtest_rework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.repositories.HistoryRepository
import kotlinx.coroutines.launch


class SpeedTestViewModel constructor(private val historyRepository: HistoryRepository) :
    BaseViewModel() {
    val isScanning = MutableLiveData<Boolean>()
    val isPermissionGrandted = MutableLiveData<Boolean>()
    val isWifiEnabled = MutableLiveData<Boolean>()
    private var _currentTestModel = MutableLiveData<HistoryModel?>()
    fun insertNewHistoryAction(historyModel: HistoryModel) {

    }
}