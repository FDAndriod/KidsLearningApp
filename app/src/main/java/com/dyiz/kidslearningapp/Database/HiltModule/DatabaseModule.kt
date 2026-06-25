package com.dyiz.kidslearningapp.Database.HiltModule

import android.content.Context
import androidx.room.Room
import com.dyiz.kidslearningapp.badges.BadgeGameProgressStore
import com.dyiz.kidslearningapp.Database.Dao.ChildDao
import com.dyiz.kidslearningapp.Database.dataBase.KidsDatabase
import com.dyiz.kidslearningapp.Database.Repos.KidsRepository
import com.dyiz.kidslearningapp.Database.SensorManager.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KidsDatabase {
        return Room.databaseBuilder(
            context,
            KidsDatabase::class.java,
            "kids_app_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDao(db: KidsDatabase): ChildDao = db.childDao()

    @Provides
    @Singleton
    fun provideRepository(dao: ChildDao) = KidsRepository(dao)

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context) = SessionManager(context)

    @Provides
    @Singleton
    fun provideBadgeGameProgressStore(@ApplicationContext context: Context) =
        BadgeGameProgressStore(context)
}