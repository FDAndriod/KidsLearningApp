package com.dyiz.kidslearningapp.Database.SensorManager

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context){
    private val PREFS_NAME = "kids_app_prefs"
    private val KEY_MUSIC_ON = "is_music_on"
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isMusicSettingEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ON, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ON, value).apply()
    var currentChildId: Int
        get() = prefs.getInt("active_child_id", -1)
        set(value) = prefs.edit().putInt("active_child_id", value).apply()

    var isFirstTimeParentVisit: Boolean
        get() = prefs.getBoolean("first_parent_visit", true)
        set(value) = prefs.edit().putBoolean("first_parent_visit", value).apply()
}