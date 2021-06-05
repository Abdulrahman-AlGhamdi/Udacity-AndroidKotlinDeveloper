package com.example.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.models.TodayImageResponseModel

@Database(entities = [Asteroid::class, TodayImageResponseModel::class], version = 1)
@TypeConverters(AsteroidConverter::class)
abstract class AsteroidDatabase : RoomDatabase() {

    abstract fun asteroidDao(): AsteroidDao

    companion object {

        @Volatile
        private var INSTANCE: AsteroidDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(lock) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroid"
            ).build()
    }
}