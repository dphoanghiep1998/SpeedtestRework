package com.example.speedtest_rework.di

import android.content.Context
import androidx.room.Room
import com.example.speedtest_rework.data.database.AppDatabase
import com.example.speedtest_rework.data.database.dao.HistoryDao
import com.example.speedtest_rework.data.database.dao.RecentDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao
    }

    @Provides
    fun provideRecentDao(appDatabase: AppDatabase): RecentDao {
        return appDatabase.recentDao
    }


}