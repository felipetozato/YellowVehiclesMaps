package com.example.felipecantagalli.yellowmaptest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.felipecantagalli.VehiclesServiceApi
import com.example.felipecantagalli.model.ApiResponse
import com.example.felipecantagalli.model.Vehicle
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import java.net.URL

class MapsViewModel {

    //TODO injection
    private val vehiclesService: VehiclesServiceApi = VehiclesServiceApi()

    fun getVehicles() : Single<VehiclesInfo> {
        return vehiclesService.getVehicles()
            .subscribeOn(Schedulers.io())
            .flatMap { response: ApiResponse ->
                // Add a marker in Sydney and move the camera
                val bikeImage = response.imagesInfo.first { imageInfo ->  imageInfo.type == Vehicle.Type.BIKE }
                val scooterImage = response.imagesInfo.first {imageInfo ->  imageInfo.type == Vehicle.Type.SCOOTER }
                getPictures(bikeImage.url, scooterImage.url)
                    .flatMap{  pair -> Single.just(VehiclesInfo(response.vehicles, pair.first, pair.second)) }
            }
    }

    private fun getPictures(bikeUrl: String,  scooterUrl: String) : Single<Pair<Bitmap, Bitmap>> {
        val promise = Single.create{ emitter : SingleEmitter<Pair<Bitmap, Bitmap>> ->
            val bikeBitmap = BitmapFactory.decodeStream(URL(bikeUrl).openConnection().getInputStream())
            val scooterBitmap = BitmapFactory.decodeStream(URL(scooterUrl).openConnection().getInputStream())
            emitter.onSuccess(Pair(bikeBitmap, scooterBitmap))
        }
        return promise.subscribeOn(Schedulers.io())
    }

    data class VehiclesInfo(val vehicles: Collection<Vehicle>,
                            val bikeBitmap: Bitmap,
                            val scooterBitmap: Bitmap) {}

}