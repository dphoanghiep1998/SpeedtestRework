package com.example.speedtest_rework.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.data.services.HistoryLocalService
import com.example.speedtest_rework.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AppRepository @Inject constructor(
    private val historyLocalService: HistoryLocalService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {


    suspend fun insertHistoryModel(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.insertHistory(model.toHistoryEntity())
    }

    suspend fun deleteAllHistory() =
        withContext(dispatcher) {
            historyLocalService.historyDao.deleteAllHistory()
        }

    suspend fun deleteHistory(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.deleteHistory(model.toHistoryEntity())
    }

    fun getAllHistory(): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.getListHistory()




}