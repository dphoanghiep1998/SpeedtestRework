package com.example.speedtest_rework.viewmodel

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.repositories.AppRepository
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.ItemPingTest
import com.example.speedtest_rework.ui.ping_test.model.PingResultTest
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import com.stealthcopter.networktools.Ping
import com.stealthcopter.networktools.Ping.PingListener
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.ping.PingResult
import com.stealthcopter.networktools.ping.PingStats
import com.stealthcopter.networktools.subnet.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class SpeedTestViewModel @Inject constructor(
    private val appRepository: AppRepository,
    @ApplicationContext val context: Context
) :
    BaseViewModel() {
    var currentLanguage = ""
    var unitType = MutableLiveData(UnitType.MBPS)
    var isMultiTaskDone: MutableLiveData<Boolean> = MutableLiveData()
    var addressInfoList: MutableList<AddressInfo> = mutableListOf()
    var currentNetworkInfo: CurrentNetworkInfo = CurrentNetworkInfo()
    var userActionRate: Boolean = false
    var wiFiBand = MutableLiveData(WiFiBand.GHZ2)
    var isWifiDetectorDone = MutableLiveData(false)
    var pingStatus = MutableLiveData(ScanStatus.DONE)
    var pingAdvanced: Ping? = null
    var pingNormal: Ping? = null
    var wifiDetect: SubnetDevices? = null
    var stopPing = true
    private val listPingResult = MutableLiveData<MutableList<PingResultTest>>()

    var listRecent = MutableLiveData<List<String>>()
    val listPingResultLive: LiveData<MutableList<PingResultTest>> = listPingResult
    fun setDataPingResult(list: MutableList<PingResultTest>) {
        listPingResult.postValue(list)
    }


    var flagChangeBack = MutableLiveData(false)

    private val listDataUsage: MutableLiveData<List<DataUsageModel>> = MutableLiveData()
    var listDevice: MutableLiveData<MutableList<DeviceModel>> = MutableLiveData(mutableListOf())

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

    fun getPingResult(listItem: List<ItemPingTest>) {
        var loop = 0
        viewModelScope.launch(Dispatchers.IO) {
            listItem.filter {
                it.type == 1
            }.filter { itemPingTest ->
                (itemPingTest as ContentPingTest).normal
            }.map {
                loop++
                val item = (it as ContentPingTest)
                val url = URL(item.url)
                pingNormal =
                    Ping.onAddress(url.host).setTimeOutMillis(1000).setTimes(2).setDelayMillis(1000)
                        .let { it1 ->
                            it1.doPing(object : PingListener {
                                override fun onResult(pingResult: PingResult) {
                                }

                                override fun onFinished(pingStats: PingStats) {
                                    if (loop == 11) {
                                        pingStatus.postValue(ScanStatus.DONE)
                                    } else {
                                        pingStatus.postValue(ScanStatus.SCANNING)
                                    }
                                    it.value = pingStats.averageTimeTaken.roundToInt()
                                }

                                override fun onError(e: Exception) {
                                }
                            })

                        }

            }


        }
    }

    fun getPingResultAdvanced(address: String) {
        val listResult: MutableList<PingResultTest> = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL(address)
            pingAdvanced =
                Ping.onAddress(url.host).setTimeOutMillis(0).setTimes(10).setDelayMillis(1000)
                    .doPing(object : PingListener {
                        override fun onResult(pingResult: PingResult) {
                            if (!stopPing) {
                                Log.d("TAG", "onResult: ")
                                listResult.add(
                                    PingResultTest(
                                        pingResult.timeTaken.toInt(),
                                        pingResult.isReachable
                                    )
                                )
                                listPingResult.postValue(listResult)
                            }

                        }

                        override fun onFinished(pingStats: PingStats) {
                            Log.d("TAG", "onFinished: ")
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
        }


    }

    fun getDeviceListWifi() {
        val mList: MutableList<DeviceModel> = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            wifiDetect = SubnetDevices.fromLocalAddress().setTimeOutMillis(400).let {
                it.findDevices(object : SubnetDevices.OnSubnetDeviceFound {
                    override fun onDeviceFound(device: Device?) {
                        device?.let { it1 ->
                            if (it1.ip == NetworkUtils.wifiIpAddress()) {
                                val mDeviceName =
                                    "${Build.MODEL} (${context.getString(R.string.my_device)})"
                                mList.add(0, DeviceModel(mDeviceName, it1.ip))
                            } else {
                                mList.add(
                                    (DeviceModel(
                                        context.getString(R.string.unknown_device),
                                        it1.ip
                                    ))
                                )
                            }
                        }
                    }

                    override fun onFinished(devicesFound: ArrayList<Device>?) {
                        listDevice.postValue(mList)
                        isWifiDetectorDone.postValue(true)
                    }
                })
            }

        }
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