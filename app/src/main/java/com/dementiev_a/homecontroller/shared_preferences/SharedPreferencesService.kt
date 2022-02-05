package com.dementiev_a.homecontroller.shared_preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    private var sp: SharedPreferences = context.getSharedPreferences(
        "settings", Context.MODE_PRIVATE
    )

    fun hasUserKey() = sp.getString("key", null) != null
}