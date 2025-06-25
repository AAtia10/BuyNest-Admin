package com.example.buynest_admin.data.local.sharedpreference

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var INSTANCE: SharedPreferenceManager? = null

        fun getInstance(context: Context): SharedPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferenceManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    fun <T> saveData(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Double -> putFloat(key, value.toFloat())
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    fun <T> fetchData(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Double -> sharedPreferences.getFloat(key, defaultValue.toFloat()).toDouble() as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun setLoggedIn(value: Boolean) {
        saveData(KEY_IS_LOGGED_IN, value)
    }

    fun isLoggedIn(): Boolean {
        return fetchData(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
