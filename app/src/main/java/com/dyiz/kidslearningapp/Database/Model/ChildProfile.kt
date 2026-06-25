package com.dyiz.kidslearningapp.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ageRange: String,
    val avatarRes: Int,
    val bgColor: Int,
    // Parent Settings
    val limitHours: Int = 0,
    val limitMinutes: Int = 0,
    // Tracking
    var usedTimeSecondsToday: Long = 0,
    var lastActiveDate: String = "" // Format: "yyyy-MM-dd"

)