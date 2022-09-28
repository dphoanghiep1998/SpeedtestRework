package com.example.speedtest_rework.network

sealed class NetworkResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
}
