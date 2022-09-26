package com.example.speedtest_rework.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.speedtest_rework.data.database.AppDatabase
import com.example.speedtest_rework.data.database.entities.HistoryEntity
import com.example.speedtest_rework.data.model.HistoryModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class HistoryRepository constructor(context: Context) {

    private var appDatabase: AppDatabase
    private val executor: Executor

    init {
        appDatabase = AppDatabase.getDatabase(context)
        executor = Executors.newSingleThreadExecutor()
    }

    fun insertHistoryModel(model: HistoryModel) {
        executor.execute {
            appDatabase.historyDao().insertHistory(model.toHistoryEntity())
        }
    }

    fun deleteAllHistory() {
        executor.execute {
            appDatabase.historyDao().deleteAllHistory()
        }
    }

    fun deleteHistory(model: HistoryModel) {
        executor.execute {
            appDatabase.historyDao().deleteHistory(model.toHistoryEntity())
        }
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>>{
        return appDatabase.historyDao().getListHistory()
    }
}