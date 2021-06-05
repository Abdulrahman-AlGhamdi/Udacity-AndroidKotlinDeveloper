package com.udacity.project4.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "reminder")
data class ReminderModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable