package com.example.speedtest_rework.common

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

 object NetworkUtils {

         fun isConnected(context: Context): Boolean {
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             val netInfo = cm.activeNetworkInfo
             if (netInfo != null) {
                 val networkInfo = cm.activeNetworkInfo
                 return networkInfo != null && networkInfo.isConnected
             }
             return false
         }
         @JvmStatic
         fun isWifiConnected(context: Context): Boolean {
             return isConnected(context, ConnectivityManager.TYPE_WIFI)
         }

         fun isWifiConnected(context: Context, wifi_name: String): Boolean {
             if (wifi_name.isNotEmpty() && isWifiConnected(context) && getNameWifi(context) === wifi_name) {
                 Log.d("TAG", "isWifiConnected: $wifi_name")
                 return true
             }
             return false
         }

         fun isWifiEnabled(context: Context): Boolean {
             val cm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
             return cm == null && cm.isWifiEnabled
         }

         fun isMobileConnected(context: Context): Boolean {
             return isConnected(context, ConnectivityManager.TYPE_MOBILE)
         }

         fun getInforMobileConnected(context: Context): NetworkInfo? {
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             return if (cm != null) {
                 cm.activeNetworkInfo
             } else null
         }

         private fun isConnected(context: Context, type: Int): Boolean {
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             val netInfo = cm.activeNetworkInfo
             return if (netInfo != null) {
                 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                     val networkInfo = cm.getNetworkInfo(type)
                     networkInfo != null && networkInfo.isConnected
                 } else {
                     isConnected(cm, type)
                 }
             } else false
         }

         @TargetApi(Build.VERSION_CODES.LOLLIPOP)
         private fun isConnected(cm: ConnectivityManager, type: Int): Boolean {
             val networks = cm.allNetworks
             var networkInfo: NetworkInfo?
             for (network in networks) {
                 networkInfo = cm.getNetworkInfo(network)
                 if (networkInfo != null && networkInfo.type == type && networkInfo.isConnected) {
                     return true
                 }
             }
             return false
         }

         fun getDownloadSpeed(context: Context): Int {
             var downloadSpeed = 0
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             if (isWifiConnected(context)) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     val nc = cm.getNetworkCapabilities(cm.activeNetwork)
                     downloadSpeed = nc!!.linkDownstreamBandwidthKbps
                     return downloadSpeed
                 }
             }
             return downloadSpeed
         }

         @TargetApi(Build.VERSION_CODES.M)
         fun getUploadSpeed(context: Context): Int {
             var uploadSpeed = 0
             val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
             if (isWifiConnected(context)) {
                 val nc = cm.getNetworkCapabilities(cm.activeNetwork)
                 uploadSpeed = nc!!.linkUpstreamBandwidthKbps
                 return uploadSpeed
             }
             return uploadSpeed
         }

         fun getWifiInfo(context: Context): WifiInfo? {
             val cm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
             return cm.connectionInfo
         }

         fun getNameWifi(context: Context): String? {
             val manager =
                 context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
             if (manager != null && manager.isWifiEnabled) {
                 val wifiInfo = manager.connectionInfo
                 if (wifiInfo != null) {
                     val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                     if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                         return wifiInfo.ssid.substring(1, wifiInfo.ssid.length - 1)
                     }
                 }
             }
             return null
         }

         fun getNameWifi(manager: WifiManager?): String? {
             if (manager != null && manager.isWifiEnabled) {
                 val wifiInfo = manager.connectionInfo
                 if (wifiInfo != null) {
                     val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                     if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                         return wifiInfo.ssid.substring(1, wifiInfo.ssid.length - 1)
                     }
                 }
             }
             return null
         }

         fun getListWifi(context: Context?, manager: WifiManager?): List<ScanResult?>? {
             return if (manager != null && manager.isWifiEnabled) {
                 manager.scanResults
             } else null
         }

         fun wifiIpAddress(context: Context): String? {
             val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
             var ipAddress = wifiManager.connectionInfo.ipAddress

             // Convert little-endian to big-endianif needed
             if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                 ipAddress = Integer.reverseBytes(ipAddress)
             }
             val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
             val ipAddressString: String?
             ipAddressString = try {
                 InetAddress.getByAddress(ipByteArray).hostAddress
             } catch (ex: UnknownHostException) {
                 null
             }
             return ipAddressString
         }

         fun getRangeWifi(channel: Int, type: Int): IntArray? {
             var r_range = 0
             var l_range = 0
             when (type) {
                 0 -> if (channel <= 2) {
                     r_range = 0
                     l_range = 4
                 } else {
                     r_range = channel - 2
                     l_range = channel + 2
                 }
                 1 -> if (channel - 4 > 0) {
                     r_range = if (channel - 2 > 0) channel - 2 else 0
                     l_range = r_range + 8
                 } else if (channel + 4 > 14) {
                     l_range = if (channel + 2 < 14) channel + 2 else 14
                     r_range = l_range - 8
                 }
             }
             return intArrayOf(r_range, l_range)
         }

         fun convertFreqtoChannel(freq: Int): Int {
             if (freq == 2484) return 14
             return if (freq < 2484) (freq - 2407) / 5 else freq / 5 - 1000
         }

         fun isNetworkConnected(context: Context): Boolean {
             val connectivityManager =
                 context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

             // For 29 api or above
             return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                 val capabilities =
                     connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                         ?: return false
                 if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                     true
                 } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                     true
                 } else capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
             } else {
                 connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
                     .isConnectedOrConnecting
             }
         }

}