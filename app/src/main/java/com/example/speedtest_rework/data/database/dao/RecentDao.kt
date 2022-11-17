package com.example.speedtest_rework.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.speedtest_rework.data.database.entities.RecentEntity
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.model.RecentModel

@Dao
interface RecentDao {
    @Insert
    fun insertRecent(recentEntity: RecentEntity)

    @Query("delete from recent")
    fun deleteAllRecent()

    @Query("select * from recent")
    fun getListRecent(): LiveData<List<RecentModel>>
}