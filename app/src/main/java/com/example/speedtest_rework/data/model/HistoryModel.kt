package com.example.speedtest_rework.data.model

import android.os.Parcelable
import com.example.speedtest_rework.data.database.entities.HistoryEntity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class HistoryModel(
    val id: Int = -1,
    var name_network: String="",
    var internalIP: String = "0.0.0.0",
    var externalIP: String = "0.0.0.0",
    var isp: String = "",
    var jitter: Double = 0.0,
    var loss: Double = 0.0,
    var network: String = "wifi",
    var ping: Double = 0.0,
    var time: Long = 0,
    var upload: Double = 0.0,
    var download: Double = 0.0,

    ) : Parcelable {
    companion object {
        val NETWORK_MOBILE: String = "NETWORK_MOBILE"
        val NETWORK_UNKNOWN: String = "NETWORK_UNKNOWN"
        val NETWORK_WIFI: String = "NETWORK_WIFI "
    }

    fun setNetworkType(i: Int) {
        when (i) {
            0 -> {
                this.network = NETWORK_UNKNOWN
            }
            1 -> {
                this.network = NETWORK_WIFI
            }
            2 -> {
                this.network = NETWORK_MOBILE
            }
        }
    }

    fun toHistoryEntity(): HistoryEntity {
        val historyId = if (id == -1) 0 else id
        return HistoryEntity(
            id = historyId,
            internalIP = internalIP,
            externalIP = externalIP,
            isp = isp,
            jitter = jitter,
            loss = loss,
            name_network = name_network,
            ping = ping,
            time = time,
            upload = upload,
            download = download,
            network = network
        )
    }

    fun HistoryEntity.toHistoryModel(): HistoryModel {
        return HistoryModel(
            id = id,
            internalIP = internalIP,
            externalIP = externalIP,
            isp = isp,
            jitter = jitter,
            loss = loss,
            name_network = name_network,
            ping = ping,
            time = time,
            upload = upload,
            download = download,
            network = network
        )
    }

}