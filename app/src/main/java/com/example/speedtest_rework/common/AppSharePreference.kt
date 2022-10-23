package com.example.speedtest_rework.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.speedtest_rework.services.ServiceType

inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor: SharedPreferences.Editor = edit()
    editor.func()
    editor.apply()
}

class AppSharePreference(private val context: Context) {
    companion object {
        lateinit var INSTANCE: AppSharePreference

        @JvmStatic
        fun getInstance(context: Context): AppSharePreference {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = AppSharePreference(context)
            }
            return INSTANCE
        }
    }

    fun saveLanguage(key: Int, values: String) {
        saveString(key, values)
    }

    fun getSavedLanguage(key: Int, defaultValues: String): String {
        return getString(key, defaultValues)
    }

    fun saveUnitType(key: Int, values: String) {
        saveString(key, values)
    }

    fun getUnitType(key: Int, defaultValues: String): String {
        return getString(key, defaultValues)
    }

    fun saveUnitValue(key: Int, values: String) {
        saveString(key, values)
    }

    fun getUnitValue(key: Int, defaultValues: String): String {
        return getString(key, defaultValues)
    }

    fun saveServiceType(key: Int, values: ServiceType) {
        return saveString(key, values.toString())
    }


    fun getServiceType(key: Int, defaultValues: ServiceType): ServiceType {
        return ServiceType.toServiceType(getString(key, defaultValues.toString()))
    }

    private fun saveString(key: Int, values: String): Unit =
        sharedPreferences().edit { putString(context.getString(key), values) }

    private fun getString(key: Int, defaultValues: String): String {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getString(keyValue, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putString(keyValue, defaultValues) }
            defaultValues
        }
    }

    private fun saveInt(key: Int, values: Int): Unit =
        sharedPreferences().edit { putInt(context.getString(key), values) }

    private fun getInt(key: Int, defaultValues: Int): Int {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getInt(keyValue, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putInt(keyValue, defaultValues) }
            defaultValues
        }
    }

    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context)


}