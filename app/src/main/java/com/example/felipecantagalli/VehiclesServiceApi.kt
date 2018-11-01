package com.example.felipecantagalli

import com.example.felipecantagalli.data.ApiResponseData
import com.example.felipecantagalli.model.ApiResponse
import com.google.gson.Gson

//TODO Use DI to inject this when needed
//TODO Add proper interface for service
class VehiclesServiceApi {

    private val gson = Gson()

    fun getVehicles() : ApiResponse {
        return gson.fromJson<ApiResponse>(ApiResponseData.data, ApiResponse::class.java)
    }
}