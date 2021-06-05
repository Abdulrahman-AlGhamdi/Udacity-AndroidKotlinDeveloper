package com.ss.shoestore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoeModel(
    var name: String,
    var size: String,
    var company: String,
    var description: String,
    val images: List<String>? = mutableListOf()
) : Parcelable