package com.example.speedtest_rework.data.services

import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.data.apis.AddressInfoApi
import com.example.speedtest_rework.network.BaseRemoteService
import com.example.speedtest_rework.network.NetworkResult
import javax.inject.Inject

class AddressInfoRemoteService @Inject constructor(private val addressInfoApi: AddressInfoApi) :
    BaseRemoteService() {
    suspend fun getAddressInfoList(): NetworkResult<List<AddressInfo>> {
        return callApi { addressInfoApi.getAddressInfoList() }
    }
}