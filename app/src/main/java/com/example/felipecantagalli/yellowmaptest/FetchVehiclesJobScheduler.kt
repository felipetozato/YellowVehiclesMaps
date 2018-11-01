package com.example.felipecantagalli.yellowmaptest

import android.app.IntentService
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import com.example.felipecantagalli.VehiclesServiceApi
import com.example.felipecantagalli.model.ApiResponse
import com.example.felipecantagalli.model.Vehicle
import com.example.felipecantagalli.model.VehiclesInfo
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.util.concurrent.TimeUnit


//Not working
class FetchVehiclesJobScheduler : IntentService("FetchVehiclesJobScheduler") {

    private var activityMessenger: Messenger? = null

    //TODO injection
    private val vehiclesService: VehiclesServiceApi = VehiclesServiceApi()

    var handler = Handler()
    private val periodicUpdate = object : Runnable {
        override fun run() {
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(5) - SystemClock.elapsedRealtime() % 1000)
            // whatever you want to do below
            getVehicles().subscribeOn(Schedulers.io())
                .subscribe { info ->
                    sendMessage(MSG_FINISHED, info)
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activityMessenger = intent?.getParcelableExtra(MESSENGER_INTENT_KEY)

        return Service.START_NOT_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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

    private fun sendMessage(messageID: Int, params: Any?) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (activityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val message = Message.obtain()
        message.run {
            what = messageID
            obj = params
        }
        try {
            activityMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }
    }

    companion object {
        private val TAG = "MyJobService"
    }


}