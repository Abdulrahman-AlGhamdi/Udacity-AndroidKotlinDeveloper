package com.example.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.models.TodayImageResponseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAsteroid(asteroid: Asteroid)

    @Query("SELECT * FROM asteroid ORDER BY id DESC")
    fun getAllAsteroids(): Flow<List<Asteroid>>

    @Query("DELETE FROM asteroid")
    suspend fun deleteAllAsteroids()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(todayImageResponseModel: TodayImageResponseModel)

    @Query("SELECT * FROM image")
    fun getImage(): Flow<TodayImageResponseModel>

    @Query("DELETE FROM image")
    suspend fun deleteImage()
}
