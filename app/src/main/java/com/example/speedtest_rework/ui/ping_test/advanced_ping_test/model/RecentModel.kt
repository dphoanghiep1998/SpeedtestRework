package com.example.speedtest_rework.ui.ping_test.advanced_ping_test.model

import com.example.speedtest_rework.data.database.entities.RecentEntity

class RecentModel(var id: Int = -1, val content: String?) {
    fun toRecentEntity(): RecentEntity {
        val recentId = if (id == -1) 0 else id
        return RecentEntity(
            id = id,
            content = content
        )
    }

    fun RecentEntity.toRecentModel(): RecentModel {
        return RecentModel(
            id = id,
            content = content
        )
    }
}