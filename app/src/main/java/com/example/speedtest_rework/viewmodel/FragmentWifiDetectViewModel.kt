package com.example.speedtest_rework.viewmodel

import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.viewmodel.BaseViewModel
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentWifiDetectViewModel @Inject constructor(@ApplicationContext val context: Context) :
    BaseViewModel() {
    var isWifiDetectorDone = MutableLiveData(false)
    var wifiDetect: SubnetDevices? = null
    var listDevice: MutableLiveData<MutableList<DeviceModel>> = MutableLiveData(mutableListOf())


    fun getDeviceListWifi() {
        val mList: MutableList<DeviceModel> = mutableListOf()
        viewModelScope.launch(Dispatchers.IO) {
            wifiDetect = SubnetDevices.fromLocalAddress().setTimeOutMillis(400).let {
                it.findDevices(object : SubnetDevices.OnSubnetDeviceFound {
                    override fun onDeviceFound(device: Device?) {
                        device?.let { it1 ->
                            if (it1.ip == NetworkUtils.wifiIpAddress()) {
                                val mDeviceName = "${Build.MODEL}"
                                mList.add(0, DeviceModel(mDeviceName, it1.ip))
                            } else {
                                mList.add(
                                    (DeviceModel(
                                       R.string.unknown_device.toString(),
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

}