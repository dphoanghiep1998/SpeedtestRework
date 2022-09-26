package com.example.speedtest_rework.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.speedtest_rework.data.database.entities.HistoryEntity
import com.example.speedtest_rework.data.model.HistoryModel
@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(historyEntity: HistoryEntity)

    @Delete
    fun deleteHistory(historyEntity: HistoryEntity)

    @Query("delete from history")
    fun deleteAllHistory()

    @Query("select * from history")
    fun getListHistory(): LiveData<List<HistoryEntity>>
}