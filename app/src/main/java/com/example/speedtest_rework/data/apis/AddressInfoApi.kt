package com.example.speedtest_rework.data.apis

import retrofit2.Response
import retrofit2.http.GET
import com.example.speedtest_rework.core.getIP.AddressInfo
import retrofit2.http.Headers

interface AddressInfoApi {
    @GET("/api/js/servers")
    suspend fun getAddressInfoList(): Response<List<AddressInfo>>


}