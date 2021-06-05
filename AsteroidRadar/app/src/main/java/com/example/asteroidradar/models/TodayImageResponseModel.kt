package com.example.asteroidradar.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "image")
data class TodayImageResponseModel(
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("title") val title: String,
    @PrimaryKey
    @SerializedName("url") val url: String
)