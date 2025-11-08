package com.mentora.mobile.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager class for handling SharedPreferences operations
 */
class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Check if this is the first app launch
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_FIRST_LAUNCH, true)
    }

    /**
     * Set first launch flag
     */
    fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit()
            .putBoolean(Constants.KEY_FIRST_LAUNCH, isFirstLaunch)
            .apply()
    }

    /**
     * Save a string value
     */
    fun saveString(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    /**
     * Get a string value
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Save a boolean value
     */
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    /**
     * Get a boolean value
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Save an integer value
     */
    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit()
            .putInt(key, value)
            .apply()
    }

    /**
     * Get an integer value
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Clear all preferences
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Remove a specific preference
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
