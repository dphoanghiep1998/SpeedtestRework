package com.example.speedtest_rework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.ItemPingTest
import com.example.speedtest_rework.ui.ping_test.model.PingResultTest
import com.stealthcopter.networktools.Ping
import com.stealthcopter.networktools.ping.PingResult
import com.stealthcopter.networktools.ping.PingStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class FragmentPingTestViewModel @Inject constructor() : BaseViewModel() {

    var pingListStatus = MutableLiveData(ScanStatus.NONE)
    var pingNormal: Ping? = null
    var listRecent = MutableLiveData<List<String>>()
    var stopPing = true
    var pingAdvanced: Ping? = null
    var isGetHostError = MutableLiveData(false)

    val listPingResultLive: MutableLiveData<MutableList<PingResultTest>> = MutableLiveData()


    init {
        listRecent.value = AppSharePreference.INSTANCE.getRecentList(listOf())
    }


    fun getPingResult(listItem: List<ItemPingTest>) {
        pingListStatus.postValue(ScanStatus.SCANNING)
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
                        .doPing(object : Ping.PingListener {
                            override fun onResult(pingResult: PingResult) {
                            }

                            override fun onFinished(pingStats: PingStats) {
                                if (loop == 11) {
                                    pingListStatus.postValue(ScanStatus.DONE)
                                }
                                it.value = pingStats.averageTimeTaken.roundToInt()
                            }

                            override fun onError(e: Exception) {
                                if (loop == 11) {
                                    pingListStatus.postValue(ScanStatus.DONE)
                                }
                            }
                        })
            }
        }
    }

    fun getPingResultAdvanced(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isGetHostError.postValue(false)
                val url = URL(address)
                val labels = url.host.split("\\.")
                val result = labels.joinToString(separator = ".") { s ->
                    java.net.IDN.toASCII(s)
                }
                pingAdvanced =
                    Ping.onAddress(result).setTimeOutMillis(0).setTimes(10).setDelayMillis(1000)
                        .doPing(object : Ping.PingListener {
                            override fun onResult(pingResult: PingResult) {
                                if (!stopPing) {
                                    listPingResultLive.value?.add(
                                        PingResultTest(
                                            pingResult.timeTaken, pingResult.isReachable
                                        )
                                    )
                                    listPingResultLive.notifyObserver()
                                }

                            }

                            override fun onFinished(pingStats: PingStats) {
                            }

                            override fun onError(e: Exception) {
                                Log.d("TAG", "onError: $e")
                                isGetHostError.postValue(true)
                            }
                        })
            } catch (e: Exception) {
                Log.d("TAG", "getPingResultAdvanced: " + e.message)
                isGetHostError.postValue(true)
            }

        }


    }


}