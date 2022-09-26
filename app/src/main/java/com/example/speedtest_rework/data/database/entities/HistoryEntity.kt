package com.example.speedtest_rework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    private var internalIP: String = "0.0.0.0",
    private var externalIP: String = "0.0.0.0",
    private var isp: String,
    private var jitter: Double,
    private var loss: Double = 0.0,
    private var name_network: String,
    private var network: String,
    private var ping: Double,
    private var time: Long,
    private var upload: Double,
    private var download: Double
) {

}