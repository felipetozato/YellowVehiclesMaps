package com.example.felipecantagalli.yellowmaptest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import com.example.felipecantagalli.VehiclesServiceApi
import com.example.felipecantagalli.model.ApiResponse
import com.example.felipecantagalli.model.Vehicle
import com.example.felipecantagalli.model.VehiclesInfo
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.net.URL
import java.util.concurrent.TimeUnit

class MapsViewModel {

    //TODO injection
    private val vehiclesService: VehiclesServiceApi = VehiclesServiceApi()

    val reactiveVehiclesInfo: BehaviorSubject<VehiclesInfo> = BehaviorSubject.create()

    private val handler = Handler()
    private val runnable = Runnable { recurringRetrival() }
    private fun recurringRetrival() {
        getVehicles().subscribeOn(Schedulers.io())
            .subscribe {info ->
                reactiveVehiclesInfo.onNext(info)
                handler.postDelayed( runnable, TimeUnit.SECONDS.toMillis(30))
            }
    }

    fun startRetrievingVehicles() {
        runnable.run()
    }

    fun stopRetrieving() {
        handler.removeCallbacks(runnable)
        handler.looper.quitSafely()
    }

    private fun getVehicles() : Single<VehiclesInfo> {
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

}