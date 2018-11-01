package com.example.felipecantagalli.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("vehicles") private val _vehicles: Set<Vehicle>,
    @SerializedName("image_urls") private val _imagesInfo: Collection<ImageInfo>
) {
    val vehicles
        get() = _vehicles
    val imagesInfo
        get() = _imagesInfo
}