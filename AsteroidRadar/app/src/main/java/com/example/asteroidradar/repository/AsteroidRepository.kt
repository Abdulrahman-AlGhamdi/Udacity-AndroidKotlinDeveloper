package com.example.asteroidradar.repository

import com.example.asteroidradar.api.Api
import com.example.asteroidradar.common.Constants
import com.example.asteroidradar.database.AsteroidDatabase
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.models.TodayImageResponseModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AsteroidRepository(var database: AsteroidDatabase) {

    private val retrofit: Api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)

    suspend fun getAsteroidsList(): Flow<List<Asteroid>>? {
        val response = retrofit.asteroidsRequest()
        return if (response.isSuccessful) response.body()?.let {
            database.asteroidDao().deleteAllAsteroids()
            it.nearEarthObjects.values.forEach { list ->
                list.forEach { asteroid ->
                    database.asteroidDao().addAsteroid(asteroid)
                }
            }
            database.asteroidDao().getAllAsteroids()
        }
        else database.asteroidDao().getAllAsteroids()
    }

    suspend fun getImageOfTheDay(): Flow<TodayImageResponseModel>? {
        val response = retrofit.planetaryRequest()
        return if (response.isSuccessful) response.body()?.let {
            database.asteroidDao().deleteImage()
            database.asteroidDao().addImage(it)
            database.asteroidDao().getImage()
        } else database.asteroidDao().getImage()
    }
}