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

    var pingStatus = MutableLiveData(ScanStatus.NONE)
    var pingNormal: Ping? = null
    var listRecent = MutableLiveData<List<String>>()
    var stopPing = true
    var pingAdvanced: Ping? = null
    var flagChangeBack = MutableLiveData(false)
    private val listPingResult = MutableLiveData<MutableList<PingResultTest>>()
    val listPingResultLive: LiveData<MutableList<PingResultTest>> = listPingResult
    fun setDataPingResult(list: MutableList<PingResultTest>) {
        listPingResult.postValue(list)
    }

    init {
        listRecent.value = AppSharePreference.INSTANCE.getRecentList(listOf())
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
                        .doPing(object : Ping.PingListener {
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
                                Log.d("TAG", "onError: ")
                            }
                        })
            }
        }
    }

    fun getPingResultAdvanced(address: String) {
        val listResult: MutableList<PingResultTest> = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
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
                                Log.d("TAG", "onResult: "+pingResult.timeTaken)
                                listResult.add(
                                    PingResultTest(
                                        pingResult.timeTaken,
                                        pingResult.isReachable
                                    )
                                )
                                listPingResult.postValue(listResult)
                            }

                        }

                        override fun onFinished(pingStats: PingStats) {
                        }

                        override fun onError(e: Exception) {
                            Log.d("TAG", "onError: $e")

                        }
                    })
        }


    }


}