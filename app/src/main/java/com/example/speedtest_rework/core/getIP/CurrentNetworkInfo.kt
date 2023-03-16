package com.example.speedtest_rework.core.getIP

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class CurrentNetworkInfo {
    var selfLat = 0.0
    var selfLon = 0.0
    var selfIsp = ""
    var selfIspIp = ""

    val currentNetWorkInfo: CurrentNetworkInfo
        get() {
            try {
                val url = URL("https://speedtest.net/speedtest-config.php")
                val urlConnection = url.openConnection() as HttpsURLConnection
//                urlConnection.sslSocketFactory = SSLCertificateSocketFactory.getInsecure(0, null)
                urlConnection.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                urlConnection.setRequestProperty("Cache-Control", "no-cache")
                urlConnection.setRequestProperty("Expires", "-1")
                urlConnection.setRequestProperty("Pragma", "no-cache")
                urlConnection.setRequestProperty("Connection", "Keep-Alive")
                urlConnection.connectTimeout = 5000
                urlConnection.readTimeout = 5000
                val code = urlConnection.responseCode
                if (code == 200) {
                    val br = BufferedReader(
                        InputStreamReader(
                            urlConnection.inputStream
                        )
                    )

                    br.use { r ->
                        if (r != null) {
                            r.lineSequence().forEach {
                                if (it != null) {
                                    if (!it.contains("isp=")) {
                                        return@forEach
                                    }
                                    selfLat =
                                        it.split("lat=\"".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1].split(" ".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[0].replace("\"", "").toDouble()
                                    selfLon =
                                        it.split("lon=\"".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1].split(" ".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[0].replace("\"", "").toDouble()
                                    selfIsp =
                                        it.split("isp=\"".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1].split(" ".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[0].replace("\"", "")
                                    selfIspIp =
                                        it.split("ip=\"".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1].split(" ".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[0].replace("\"", "")
                                }

                            }
                        }
                    }
                    br.close()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return this
        }

    override fun toString(): String {
        return "CurrentNetworkInfo{" + "selfLat=" + selfLat + ", selfLon=" + selfLon + ", selfIsp='" + selfIsp + '\'' + ", selfIspIp='" + selfIspIp + '\'' + '}'
    }
}