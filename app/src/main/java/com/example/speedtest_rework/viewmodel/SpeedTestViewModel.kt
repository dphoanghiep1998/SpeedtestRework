package com.example.speedtest_rework.viewmodel

import android.net.wifi.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.repositories.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpeedTestViewModel @Inject constructor(private val appRepository: AppRepository) :
    BaseViewModel() {



    var isMultiTaskDone: MutableLiveData<Boolean> = MutableLiveData()
    var addressInfoList: MutableList<AddressInfo> = mutableListOf()
    var currentNetworkInfo: CurrentNetworkInfo = CurrentNetworkInfo()
    var isError: MutableLiveData<Boolean> = MutableLiveData()


    private var isScanning = MutableLiveData<Boolean>()
    val _isScanning: LiveData<Boolean>
        get() = isScanning

    fun setIsScanning(status: Boolean) {
        isScanning.value = status
    }


    private var isPermissionGranted = MutableLiveData<Boolean>()
    val _isPermissionGranted: LiveData<Boolean>
        get() = isPermissionGranted

    fun setIsPermissionGranted(status: Boolean) {
        isPermissionGranted.value = status
    }

    // ssot isConnectivityChanged add/remove
    private var isConnectivityChanged: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    val _isConnectivityChanged: LiveData<Boolean>
        get() = isConnectivityChanged

    fun addIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.addSource(data, isConnectivityChanged::setValue)
    }

    fun removeIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.removeSource(data)
    }

    // ssot scanResults add/remove
    private var scanResults: MediatorLiveData<List<ScanResult>> =
        MediatorLiveData<List<ScanResult>>()
    val _scanResults: LiveData<List<ScanResult>>
        get() = scanResults

    fun addScanResultsSource(data: LiveData<List<ScanResult>>) {
        scanResults.addSource(data, scanResults::setValue)
    }

    fun removeScanResultsSource(data: LiveData<List<ScanResult>>) {
        scanResults.removeSource(data)
    }

    private var isWifiEnabled: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>()
    val _isWifiEnabled: LiveData<Boolean>
        get() = isWifiEnabled

    fun addIsWifiEnabledSource(data: LiveData<Boolean>) {
        isWifiEnabled.addSource(data, isWifiEnabled::setValue)
    }

    fun removeIsWifiEnabledSource(data: LiveData<List<ScanResult>>) {
        isWifiEnabled.removeSource(data)
    }

    fun getListHistory(): LiveData<List<HistoryModel>> {
        return appRepository.getAllHistory()
    }

    fun insertNewHistoryAction(historyModel: HistoryModel) {
        viewModelScope.launch { appRepository.insertHistoryModel(historyModel) }
    }

    fun deleteHistoryAction(historyModel: HistoryModel) {
        viewModelScope.launch { appRepository.deleteHistory(historyModel) }
    }

    fun deleteAllHistoryAction() {
        viewModelScope.launch { appRepository.deleteAllHistory() }
    }

    suspend fun getServerList() {
        val list = appRepository.getAddressInfo()
        addressInfoList = list.toMutableList()

    }

    suspend fun getCurrentNetworkInfo() {
        val cNetInfo = appRepository.getCurrentNetworkInfo()
        currentNetworkInfo = cNetInfo

    }

    fun doMultiTask() {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            getServerList()
            getCurrentNetworkInfo()
            isMultiTaskDone.postValue(true)
        }
        registerJobFinish()
    }

}