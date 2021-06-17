package com.example.mybudget.data.prefs

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class AppPreferenceHelper(application: Application) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val key = "first_init"

    var firstInit: Boolean
        get() = prefs.getBoolean(key, true)
        set(input) {
            val editor = prefs.edit()
            editor.putBoolean(key, input)
            editor.apply()
        }
}