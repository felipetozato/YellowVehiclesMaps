package com.example.felipecantagalli

import android.graphics.Bitmap
import com.example.felipecantagalli.data.ApiResponseData
import com.example.felipecantagalli.model.ApiResponse
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleEmitter

//TODO Use DI to inject this when needed
//TODO Add proper interface for service
class VehiclesServiceApi {

    private val gson = Gson()

    fun getVehicles() : Single<ApiResponse> {
        return Single.create{ emitter : SingleEmitter<ApiResponse> ->
            emitter.onSuccess(gson.fromJson<ApiResponse>(ApiResponseData.data, ApiResponse::class.java))
        }

    }
}