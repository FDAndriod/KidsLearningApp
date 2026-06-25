package com.dyiz.kidslearningapp.Database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dyiz.kidslearningapp.Database.Model.ChildProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertProfile(profile: ChildProfile):Long
    @Query("SELECT * FROM child_profiles")
    fun getAllProfiles(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    suspend fun getChildById(id: Int): ChildProfile?

    @Update
    suspend fun updateChild(child: ChildProfile)

    @Query("UPDATE child_profiles SET usedTimeSecondsToday = usedTimeSecondsToday + 1 WHERE id = :id")
    suspend fun incrementUsedTime(id: Int)

    @Query("UPDATE child_profiles SET limitHours = :h, limitMinutes = :m WHERE id = :id")
    suspend fun updateScreenTime(id: Int, h: Int, m: Int)

    @Query("UPDATE child_profiles SET usedTimeSecondsToday = 0, lastActiveDate = :date WHERE id = :id")
    suspend fun resetDailyTime(id: Int, date: String)

    @Query("UPDATE child_profiles SET usedTimeSecondsToday = 0 WHERE id = :childId")
    suspend fun resetUsedTime(childId: Int)

    @Query("DELETE FROM child_profiles WHERE id IN (:ids)")
    suspend fun deleteProfilesByIds(ids: List<Int>)
}