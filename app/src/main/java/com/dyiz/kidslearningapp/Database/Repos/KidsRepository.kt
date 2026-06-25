package com.dyiz.kidslearningapp.Database.Repos

import com.dyiz.kidslearningapp.Database.Dao.ChildDao
import com.dyiz.kidslearningapp.Database.Model.ChildProfile
import jakarta.inject.Inject

class KidsRepository @Inject constructor(private val dao: ChildDao) {
    fun getAllChildren() = dao.getAllProfiles()

    suspend fun getChildById(id: Int) = dao.getChildById(id)

    suspend fun saveNewProfile(profile: ChildProfile) = dao.insertProfile(profile)

    suspend fun updateTimeLimit(id: Int, hours: Int, mins: Int) = dao.updateScreenTime(id, hours, mins)

    suspend fun tickSecond(id: Int) = dao.incrementUsedTime(id)

    suspend fun resetTime(id: Int, todayDate: String) = dao.resetDailyTime(id, todayDate)
    suspend fun resetUsedTime(childId: Int) {
        dao.resetUsedTime(childId)
    }
    suspend fun deleteChildren(ids: List<Int>) {
        dao.deleteProfilesByIds(ids)
    }
    suspend fun updateChild(child: ChildProfile) {
        dao.updateChild(child)
    }


}