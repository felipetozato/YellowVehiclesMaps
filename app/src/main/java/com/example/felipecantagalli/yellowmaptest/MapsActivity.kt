package com.example.felipecantagalli.yellowmaptest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.felipecantagalli.VehiclesServiceApi
import com.example.felipecantagalli.model.Vehicle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //TODO injection
    private lateinit var vehiclesService: VehiclesServiceApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        vehiclesService = VehiclesServiceApi() //TODO injection

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val response = vehiclesService.getVehicles()
        val bikeImage = response.imagesInfo.first { imageInfo ->  imageInfo.type == Vehicle.Type.BIKE }
        val scooterImage = response.imagesInfo.first {imageInfo ->  imageInfo.type == Vehicle.Type.SCOOTER }

        getPictures(bikeImage.url, scooterImage.url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pair -> updateMap(response.vehicles, pair.first, pair.second) }
    }

    private fun updateMap(vehicles: Collection<Vehicle>, bikeImg: Bitmap, scooterImg: Bitmap) {
        var centerLat = 0.0
        var centerLng = 0.0

        vehicles.forEach { vehicle ->
            centerLat += vehicle.lat
            centerLng += vehicle.lng
            val position = LatLng(vehicle.lat, vehicle.lng)

            val marker = MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(
                    when(vehicle.type) {
                        Vehicle.Type.BIKE -> bikeImg
                        Vehicle.Type.SCOOTER -> scooterImg
                    })
                )
                .title(vehicle.type.name)
            mMap?.addMarker(marker)
        }

        val position = LatLng(centerLat / vehicles.size, centerLng / vehicles.size)

        mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(13.5f))
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
