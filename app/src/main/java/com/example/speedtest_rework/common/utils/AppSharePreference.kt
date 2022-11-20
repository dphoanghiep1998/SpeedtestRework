package com.example.speedtest_rework.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.speedtest_rework.services.ServiceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

    fun saveRecentList(values: List<String>) {
        saveStringList(Constant.KEY_RECENT_LIST, values)
    }

    fun getRecentList(defaultValues: List<String>): List<String> {
        return getStringList(Constant.KEY_RECENT_LIST, defaultValues)
    }

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener): Unit =
        sharedPreferences().registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )

    fun saveIpRouter(values: String) {
        saveString(Constant.KEY_IP_ROUTER, values)
    }

    fun getSavedIpRouter(defaultValues: String): String {
        return getString(Constant.KEY_IP_ROUTER, defaultValues)
    }


    fun saveLanguage(values: String) {
        saveString(Constant.KEY_LANGUAGE, values)
    }

    fun getSavedLanguage(defaultValues: String): String {
        return getString(Constant.KEY_LANGUAGE, defaultValues)
    }

    fun saveUnitType(values: String) {
        saveString(Constant.KEY_UNIT, values)
    }

    fun getUnitType(defaultValues: String): String {
        return getString(Constant.KEY_UNIT, defaultValues)
    }

    fun saveUnitValue(values: String) {
        saveString(Constant.KEY_UNIT_VALUE, values)
    }

    fun getUnitValue(defaultValues: String): String {
        return getString(Constant.KEY_UNIT_VALUE, defaultValues)
    }

    fun saveServiceType(values: ServiceType) {
        saveString(Constant.KEY_SERVICE_TYPE, values.toString())
    }


    fun getServiceType(defaultValues: ServiceType): ServiceType {
        return ServiceType.toServiceType(getString(Constant.KEY_SERVICE_TYPE, defaultValues.toString()))
    }

    fun saveIpList(list: HashSet<String>) {
        saveStringSet(Constant.KEY_IP_LIST, list)
    }

    fun getIpList(defaultValues: HashSet<String>): HashSet<String> {
        return getStringSet(Constant.KEY_IP_LIST, defaultValues)
    }

    private fun saveString(key: String, values: String): Unit =
        sharedPreferences().edit { putString(key, values) }

    private fun getString(key: String, defaultValues: String): String {
        return try {
            sharedPreferences().getString(key, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putString(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveStringList(key: String, values: List<String>): Unit {
        val gson = Gson()
        sharedPreferences().edit { putString(key, gson.toJson(values)) }
    }

    private fun getStringList(key: String, defaultValues: List<String>): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            val returnString = sharedPreferences().getString(key, gson.toJson(defaultValues))
            gson.fromJson(returnString, type)
        } catch (e: Exception) {

            sharedPreferences().edit { putString(key, gson.toJson(defaultValues)) }
            defaultValues
        }
    }



    private fun saveStringSet(key: String, values: HashSet<String>) {
        sharedPreferences().edit { putStringSet(key, values) }
    }

    private fun getStringSet(key: String, defaultValues: HashSet<String>): HashSet<String> {
        return try {
            sharedPreferences().getStringSet(key, defaultValues)!! as HashSet
        } catch (e: Exception) {
            sharedPreferences().edit { putStringSet(key, defaultValues) }
            defaultValues
        }
    }
    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context)


}

