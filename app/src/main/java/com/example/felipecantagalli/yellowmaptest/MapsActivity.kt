package com.example.felipecantagalli.yellowmaptest

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.felipecantagalli.model.Vehicle
import com.example.felipecantagalli.model.VehiclesInfo

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val viewModel: MapsViewModel = MapsViewModel()

    private lateinit var task: Single<VehiclesInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onStart() {
        super.onStart()
        viewModel.startRetrievingVehicles()
    }

    override fun onStop() {
        viewModel.stopRetrieving()
        super.onStop()
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

        viewModel.reactiveVehiclesInfo
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { info -> updateMap(info.vehicles, info.bikeBitmap, info.scooterBitmap) }
    }

    fun updateMap(vehicles: Collection<Vehicle>, bikeImg: Bitmap, scooterImg: Bitmap) {
        if (!::mMap.isInitialized)
            return

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
            mMap.addMarker(marker)
        }

        val position = LatLng(centerLat / vehicles.size, centerLng / vehicles.size)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13.5f))
    }


}
