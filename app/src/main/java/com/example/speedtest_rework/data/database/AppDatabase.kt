package com.example.speedtest_rework.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.speedtest_rework.data.database.dao.HistoryDao
import com.example.speedtest_rework.data.database.entities.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val historyDao: HistoryDao
}