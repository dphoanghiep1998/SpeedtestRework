package com.example.speedtest_rework.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

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

    fun saveString(key: Int, values: String): Unit =
        sharedPreferences().edit { putString(context.getString(key), values) }

    fun getString(key: Int, defaultValues: String): String {
        val keyValue: String = context.getString(key)
        return try {
            sharedPreferences().getString(keyValue, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putString(keyValue, defaultValues) }
            defaultValues
        }
    }

    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context)


}