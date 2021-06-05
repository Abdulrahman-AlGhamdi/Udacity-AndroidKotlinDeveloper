package com.example.asteroidradar.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.asteroidradar.database.AsteroidDatabase
import com.example.asteroidradar.repository.AsteroidRepository

class AsteroidListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AsteroidRepository(AsteroidDatabase(application))

    suspend fun getAsteroidList() = repository.getAsteroidsList()

    suspend fun getImageOfTheData() = repository.getImageOfTheDay()
}