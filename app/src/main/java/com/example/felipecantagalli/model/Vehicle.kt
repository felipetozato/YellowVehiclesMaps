package com.example.felipecantagalli.model

import com.google.gson.annotations.SerializedName

data class Vehicle(
    @SerializedName("id") private val _id: String,
    @SerializedName("lat") private val _lat: Double,
    @SerializedName("lng") private val _lng: Double,
    @SerializedName("type") private val _type: Vehicle.Type
) {
    val id
        get() = _id
    val lat
        get() = _lat
    val lng
        get() = _lng
    val type
        get() = _type

    override fun equals(other: Any?): Boolean {
        if (other is Vehicle) {
            return this._id == other._id
        }
        return false
    }

    override fun hashCode(): Int {
        val basePrime = 31
        return basePrime * _id.hashCode()
    }

    enum class Type {
        @SerializedName("Bike") BIKE,
        @SerializedName ("Scooter") SCOOTER
    }
}