package com.example.asteroidradar.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AsteroidResponseModel(
    @SerializedName("near_earth_objects") val nearEarthObjects: Map<String, List<Asteroid>>
)

@Parcelize
@Entity(tableName = "asteroid")
data class Asteroid(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("absolute_magnitude_h") val absoluteMagnitude: Double,
    @SerializedName("estimated_diameter") val estimatedDiameter: EstimatedDiameter,
    @SerializedName("is_potentially_hazardous_asteroid") val isPotentiallyHazardousAsteroid: Boolean,
    @SerializedName("close_approach_data") val closeApproachData: List<CloseApproachData>
) : Parcelable

@Parcelize
data class EstimatedDiameter(
    @SerializedName("kilometers") val kilometers: Kilometers
) : Parcelable

@Parcelize
data class Kilometers(
    @SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double
) : Parcelable

@Parcelize
data class CloseApproachData(
    @SerializedName("relative_velocity") val relativeVelocity: RelativeVelocity,
    @SerializedName("close_approach_date") val closeApproachDate: String,
    @SerializedName("miss_distance") val missDistance: MissDistance
) : Parcelable

@Parcelize
data class RelativeVelocity(
    @SerializedName("kilometers_per_second") val kilometersPerSeconds: String
) : Parcelable

@Parcelize
data class MissDistance(
    @SerializedName("astronomical") val astronomical: String
) : Parcelable