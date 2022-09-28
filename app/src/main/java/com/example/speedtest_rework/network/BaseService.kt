package com.fatherofapps.androidbase.base.network

import com.example.speedtest_rework.network.BaseNetworkException
import com.example.speedtest_rework.network.NetworkErrorException


abstract class BaseService {

    protected fun parseError(
        responseMessage: String?,
        responseCode: Int,
        errorBody: String?
    ): BaseNetworkException {

        val baseNetworkException =  BaseNetworkException(responseMessage,responseCode)
        errorBody?.let{
            baseNetworkException.parseFromString(it)
        }

        return baseNetworkException
    }

    protected fun parseNetworkErrorException(throwable: Throwable): NetworkErrorException {
        return NetworkErrorException(throwable.message)
    }




}