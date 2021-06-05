package com.example.asteroidradar.api

import com.example.asteroidradar.common.Constants.API_KEY
import com.example.asteroidradar.models.AsteroidResponseModel
import com.example.asteroidradar.models.TodayImageResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("neo/rest/v1/feed")
    suspend fun asteroidsRequest(
        @Query("api_key") apiKey: String = API_KEY
    ): Response<AsteroidResponseModel>

    @GET("planetary/apod")
    suspend fun planetaryRequest(
        @Query("api_key") apiKey: String = API_KEY
    ): Response<TodayImageResponseModel>
}