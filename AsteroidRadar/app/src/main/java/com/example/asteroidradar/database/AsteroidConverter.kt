package com.example.asteroidradar.database

import androidx.room.TypeConverter
import com.example.asteroidradar.models.CloseApproachData
import com.example.asteroidradar.models.EstimatedDiameter
import com.google.gson.Gson

class AsteroidConverter {

    @TypeConverter
    fun fromEstimatedDiameter(estimatedDiameter: EstimatedDiameter): String {
        return Gson().toJson(estimatedDiameter)
    }

    @TypeConverter
    fun toEstimatedDiameter(json: String) : EstimatedDiameter {
        return Gson().fromJson(json, EstimatedDiameter::class.java)
    }

    @TypeConverter
    fun fromCloseApproachData(closeApproachData: List<CloseApproachData>): String {
        return Gson().toJson(closeApproachData)
    }

    @TypeConverter
    fun toCloseApproachData(json: String) : List<CloseApproachData> {
        return Gson().fromJson(json, Array<CloseApproachData>::class.java).toList()
    }
}