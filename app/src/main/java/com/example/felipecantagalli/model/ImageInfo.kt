package com.example.felipecantagalli.model

import com.google.gson.annotations.SerializedName

data class ImageInfo(
    @SerializedName("type") private val _type: Vehicle.Type,
    @SerializedName("url") private val _url: String
) {
    val type
        get() = _type

    val url
        get() = _url
}