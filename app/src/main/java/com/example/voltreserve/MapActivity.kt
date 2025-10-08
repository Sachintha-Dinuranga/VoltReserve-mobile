package com.example.voltreserve

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.models.Station
import com.example.voltreserve.services.StationApiService
import com.example.voltreserve.helpers.SessionDbHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var stationService: StationApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // ✅ Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.2:5029/") // Replace with your backend IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        stationService = retrofit.create(StationApiService::class.java)

        // ✅ Initialize map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // ✅ Load station markers once map is ready
        loadStations()
    }

    private fun loadStations() {
        val dbHelper = SessionDbHelper(this)
        val token = dbHelper.getSession()

        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = stationService.getStations("Bearer $token")
                if (response.isSuccessful) {
                    val stations = response.body() ?: emptyList()
                    displayStationsOnMap(stations)
                } else {
                    Toast.makeText(this@MapActivity, "Failed to load stations: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStationsOnMap(stations: List<Station>) {
        if (stations.isEmpty()) {
            Toast.makeText(this, "No stations found.", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Center the map on the first station
        val firstStation = stations.firstOrNull()
        firstStation?.let {
            if (it.latitude != null && it.longitude != null) {
                val firstLocation = LatLng(it.latitude!!, it.longitude!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 11f))
            }
        }

        // ✅ Add markers for each station
        for (station in stations) {
            if (station.latitude != null && station.longitude != null) {
                val position = LatLng(station.latitude!!, station.longitude!!)
                val marker = MarkerOptions()
                    .position(position)
                    .title(station.name)
                    .snippet("Type: ${station.type} | Slots: ${station.availableSlots}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                mMap.addMarker(marker)
            }
        }
    }
}
