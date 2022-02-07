package com.dementiev_a.homecontroller.shared_preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    companion object {
        const val USER_KEY = "key"
    }

    private var sp: SharedPreferences = context.getSharedPreferences(
        "configs", Context.MODE_PRIVATE
    )

    fun hasUserKey() = sp.getString(USER_KEY, null) != null

    fun saveKey(key: String) {
        putString(USER_KEY, key)
    }

    fun getKey(): String {
        return getString(USER_KEY)!!
    }

    private fun putString(key: String, value: String) {
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun getString(key: String): String? {
        return sp.getString(key, null)
    }
}