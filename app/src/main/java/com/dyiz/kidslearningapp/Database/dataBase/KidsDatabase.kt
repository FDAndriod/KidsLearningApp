package com.dyiz.kidslearningapp.Database.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dyiz.kidslearningapp.Database.Dao.ChildDao
import com.dyiz.kidslearningapp.Database.Model.ChildProfile

@Database(
    entities = [
        ChildProfile::class,
    ],
    version = 6,
    exportSchema = false
)
abstract class KidsDatabase: RoomDatabase() {
    abstract fun childDao(): ChildDao

}