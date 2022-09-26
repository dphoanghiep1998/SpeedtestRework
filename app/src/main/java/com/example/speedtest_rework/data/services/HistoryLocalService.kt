package com.example.speedtest_rework.data.services

import androidx.lifecycle.LiveData
import com.example.speedtest_rework.data.database.dao.HistoryDao
import com.example.speedtest_rework.data.database.entities.HistoryEntity

class HistoryLocalService constructor(private val historyDao: HistoryDao) {

    suspend fun createHistory(historyEntity: HistoryEntity){
        return historyDao.insertHistory(historyEntity)
    }
    suspend fun getALlHistory(): LiveData<List<HistoryEntity>>{
        return historyDao.getListHistory()
    }
}