package com.example.speedtest_rework.viewmodel

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.repositories.AppRepository
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SpeedTestViewModel @Inject constructor(private val appRepository: AppRepository) :
    BaseViewModel() {
    var currentLanguage = ""
    var unitType = MutableLiveData(UnitType.MBPS)
    var isMultiTaskDone: MutableLiveData<Boolean> = MutableLiveData()
    var addressInfoList: MutableList<AddressInfo> = mutableListOf()
    var currentNetworkInfo: CurrentNetworkInfo = CurrentNetworkInfo()
    var userActionRate: Boolean = false
    var wiFiBand = MutableLiveData(WiFiBand.GHZ2)


    private val listDataUsage: MutableLiveData<List<DataUsageModel>> = MutableLiveData()


    private var listSignalLocation = MutableLiveData<MutableList<Pair<String, String>>>(
        mutableListOf()
    )
    val mListSignalLocation: LiveData<MutableList<Pair<String, String>>>
        get() = listSignalLocation

    fun setListSignalLocation(list: MutableList<Pair<String, String>>) {
        listSignalLocation.value = list
    }

    private var isSignalScanning = MutableLiveData(false)
    val signalScanning: LiveData<Boolean>
        get() = isSignalScanning

    fun setSignalScanning(status: Boolean) {
        isSignalScanning.value = status
    }

    private var scanStatus = MutableLiveData<ScanStatus>()
    val mScanStatus: LiveData<ScanStatus>
        get() = scanStatus

    fun setScanStatus(status: ScanStatus) {
        scanStatus.value = status
    }

    fun reOrderListDataUsage(order: Order): List<DataUsageModel>? = when (order) {
        Order.THIRTY_DAYS -> listDataUsage.let { it.value?.take(30) }
        Order.SEVEN_DAYS -> listDataUsage.let { it.value?.take(7) }
        Order.THREE_DAYS -> listDataUsage.let { it.value?.take(3) }
        else -> listDataUsage.let { it.value?.take(1) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getListOfDataUsage(): LiveData<List<DataUsageModel>> {
        viewModelScope.launch {
            listDataUsage.value = appRepository.getDataUsageList()
        }
        return listDataUsage
    }


    private var isPermissionGranted = MutableLiveData<Boolean>()
    val mPermissionGranted: LiveData<Boolean>
        get() = isPermissionGranted

    fun setIsPermissionGranted(status: Boolean) {
        isPermissionGranted.value = status
    }

    // ssot isConnectivityChanged add/remove
    private var isConnectivityChanged: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    val mConnectivityChanged: LiveData<Boolean>
        get() = isConnectivityChanged

    fun addIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.addSource(data, isConnectivityChanged::setValue)
    }

    fun removeIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.removeSource(data)
    }


    private var dataCache: MediatorLiveData<Pair<List<ScanResult>, WifiInfo>> = MediatorLiveData()
    val mDataCache: LiveData<Pair<List<ScanResult>, WifiInfo>>
        get() = dataCache

    fun addDataCacheSource(data: LiveData<Pair<List<ScanResult>, WifiInfo>>) {
        dataCache.addSource(data, dataCache::setValue)
    }

    fun removeDataCacheSource(data: LiveData<Pair<List<ScanResult>, WifiInfo>>) {
        dataCache.removeSource(data)
    }

    private var isWifiEnabled: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>()
    val mWifiEnabled: LiveData<Boolean>
        get() = isWifiEnabled

    fun addIsWifiEnabledSource(data: LiveData<Boolean>) {
        isWifiEnabled.addSource(data, isWifiEnabled::setValue)
    }

    fun removeIsWifiEnabledSource(data: LiveData<Boolean>) {
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


    fun doMultiTask() {
        showLoading(true)
        isError.value = false
        parentJob = viewModelScope.launch(handler) {
            val list = async { appRepository.getAddressInfo() }
            val netInfo = async { appRepository.getCurrentNetworkInfo() }
            val (dList, dNetWorkInfo) = awaitAll(list, netInfo)
            addressInfoList = dList as MutableList<AddressInfo>
            currentNetworkInfo = dNetWorkInfo as CurrentNetworkInfo
            isMultiTaskDone.postValue(true)
        }
        registerJobFinish()
    }

}