package com.example.speedtest_rework.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.speedtest_rework.data.converter.Convertes
import com.example.speedtest_rework.data.database.dao.HistoryDao
import com.example.speedtest_rework.data.database.entities.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
@TypeConverters(Convertes::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val historyDao: HistoryDao
}