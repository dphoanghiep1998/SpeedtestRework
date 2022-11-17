package com.example.speedtest_rework.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent")

class RecentEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val type: Int,
    val content: String?
) {
}