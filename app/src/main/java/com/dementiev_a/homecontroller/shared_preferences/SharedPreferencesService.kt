package com.dementiev_a.homecontroller.shared_preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    companion object {
        const val USER_KEY = "key"
        const val SCALE_COEFFICIENT = "scale_coefficient"
    }

    private var sp: SharedPreferences = context.getSharedPreferences(
        "configs", Context.MODE_PRIVATE
    )

    fun hasUserKey() = sp.getString(USER_KEY, null) != null

    fun saveKey(key: String) {
        putString(USER_KEY, key)
    }

    fun readKey(): String {
        return getString(USER_KEY)!!
    }

    fun saveScaleCoefficient(coefficient: Int) {
        putInt(SCALE_COEFFICIENT, coefficient)
    }

    fun readScaleCoefficient(): Int {
        val result = getInt(SCALE_COEFFICIENT)
        return if (result == -1) {
            3
        } else {
            result
        }
    }

    private fun putString(key: String, value: String) {
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun putInt(key: String, value: Int) {
        val editor = sp.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun getString(key: String): String? {
        return sp.getString(key, null)
    }

    private fun getInt(key: String): Int {
        return sp.getInt(key, -1)
    }
}