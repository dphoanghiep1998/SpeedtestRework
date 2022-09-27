package com.example.speedtest_rework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true) var id: Int,
     var internalIP: String = "0.0.0.0",
     var externalIP: String = "0.0.0.0",
     var isp: String,
     var jitter: Double,
     var loss: Double = 0.0,
     var name_network: String,
     var network: String,
     var ping: Double,
     var time: Long,
     var upload: Double,
     var download: Double
) {

}