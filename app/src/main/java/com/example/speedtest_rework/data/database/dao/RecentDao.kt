package com.example.speedtest_rework.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.speedtest_rework.data.database.entities.HistoryEntity
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.model.RecentModel

@Dao
interface RecentDao {
    @Insert
    fun insertRecent(historyEntity: HistoryEntity)

    @Query("delete from history")
    fun deleteAllRecent()

    @Query("select * from history")
    fun getListRecent(): LiveData<List<RecentModel>>
}