////package com.example.voltreserve
////
////import android.os.Bundle
////import android.widget.Toast
////import androidx.appcompat.app.AppCompatActivity
////import androidx.lifecycle.lifecycleScope
////import com.example.voltreserve.client.RetrofitClient
////import com.example.voltreserve.models.Station
////import com.example.voltreserve.services.StationApiService
////import com.example.voltreserve.helpers.SessionDbHelper
////import com.google.android.gms.maps.CameraUpdateFactory
////import com.google.android.gms.maps.GoogleMap
////import com.google.android.gms.maps.OnMapReadyCallback
////import com.google.android.gms.maps.SupportMapFragment
////import com.google.android.gms.maps.model.BitmapDescriptorFactory
////import com.google.android.gms.maps.model.LatLng
////import com.google.android.gms.maps.model.MarkerOptions
////import kotlinx.coroutines.launch
////import retrofit2.Retrofit
////import retrofit2.converter.gson.GsonConverterFactory
////
////class MapActivity : AppCompatActivity(), OnMapReadyCallback {
////
////    private lateinit var mMap: GoogleMap
////    private lateinit var stationService: StationApiService
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_map)
////
////        // ✅ Initialize Retrofit
////        val retrofit = Retrofit.Builder()
////            .baseUrl("http://192.168.1.2:5029/") // Replace with your backend IP
////            .addConverterFactory(GsonConverterFactory.create())
////            .build()
////
////        stationService = retrofit.create(StationApiService::class.java)
////
////        // ✅ Initialize map
////        val mapFragment = supportFragmentManager
////            .findFragmentById(R.id.mapFragment) as SupportMapFragment
////        mapFragment.getMapAsync(this)
////    }
////
////    override fun onMapReady(googleMap: GoogleMap) {
////        mMap = googleMap
////        mMap.uiSettings.isZoomControlsEnabled = true
////        mMap.uiSettings.isMapToolbarEnabled = true
////
////        // ✅ Load station markers once map is ready
////        loadStations()
////    }
////
////    private fun loadStations() {
////        val dbHelper = SessionDbHelper(this)
////        val token = dbHelper.getSession()
////
////        if (token == null) {
////            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        lifecycleScope.launch {
////            try {
////                val response = stationService.getStations("Bearer $token")
////                if (response.isSuccessful) {
////                    val stations = response.body() ?: emptyList()
////                    displayStationsOnMap(stations)
////                } else {
////                    Toast.makeText(this@MapActivity, "Failed to load stations: ${response.code()}", Toast.LENGTH_SHORT).show()
////                }
////            } catch (e: Exception) {
////                Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
////
////    private fun displayStationsOnMap(stations: List<Station>) {
////        if (stations.isEmpty()) {
////            Toast.makeText(this, "No stations found.", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        // ✅ Center the map on the first station
////        val firstStation = stations.firstOrNull()
////        firstStation?.let {
////            if (it.latitude != null && it.longitude != null) {
////                val firstLocation = LatLng(it.latitude!!, it.longitude!!)
////                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 11f))
////            }
////        }
////
////        // ✅ Add markers for each station
////        for (station in stations) {
////            if (station.latitude != null && station.longitude != null) {
////                val position = LatLng(station.latitude!!, station.longitude!!)
////                val marker = MarkerOptions()
////                    .position(position)
////                    .title(station.name)
////                    .snippet("Type: ${station.type} | Slots: ${station.availableSlots}")
////                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
////
////                mMap.addMarker(marker)
////            }
////        }
////    }
////}
//
//package com.example.voltreserve
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.voltreserve.models.Station
//import com.example.voltreserve.services.StationApiService
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.BitmapDescriptorFactory
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class MapActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//    private lateinit var stationService: StationApiService
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map)
//
//        // ✅ Initialize Retrofit (no auth required)
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://192.168.1.2:5029/") // Replace with your backend IP
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        stationService = retrofit.create(StationApiService::class.java)
//
//        // ✅ Initialize map
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.mapFragment) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        mMap.uiSettings.isZoomControlsEnabled = true
//        mMap.uiSettings.isMapToolbarEnabled = true
//
//        // ✅ Load stations when map is ready
//        loadPublicStations()
//    }
//
//    // ✅ Fetch public stations (no token)
//    private fun loadPublicStations() {
//        lifecycleScope.launch {
//            try {
//                val response = stationService.getPublicStations()
//                if (response.isSuccessful) {
//                    val stations = response.body() ?: emptyList()
//                    if (stations.isEmpty()) {
//                        Toast.makeText(this@MapActivity, "No stations available.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        displayStationsOnMap(stations)
//                    }
//                } else {
//                    Toast.makeText(
//                        this@MapActivity,
//                        "Failed to load stations: ${response.code()}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // ✅ Display stations on map
//    private fun displayStationsOnMap(stations: List<Station>) {
//        val firstStation = stations.firstOrNull { it.latitude != null && it.longitude != null }
//
//        // Move camera to first valid station
//        firstStation?.let {
//            val firstLocation = LatLng(it.latitude!!, it.longitude!!)
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 11f))
//        }
//
//        // Add markers
//        for (station in stations) {
//            if (station.latitude != null && station.longitude != null) {
//                val position = LatLng(station.latitude!!, station.longitude!!)
//                val marker = MarkerOptions()
//                    .position(position)
//                    .title(station.name)
//                    .snippet("Type: ${station.type} | Slots: ${station.availableSlots}")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                mMap.addMarker(marker)
//            }
//        }
//    }
//}
//
//

//changes after nearby stations added
package com.example.voltreserve

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.models.Station
import com.example.voltreserve.services.StationApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var stationService: StationApiService

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // ✅ Initialize Fused Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // ✅ Initialize Retrofit (no auth)
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

        // ✅ Check and request location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // ✅ Enable "My Location" blue dot
        mMap.isMyLocationEnabled = true

        // ✅ Get current location and load nearby stations
        getUserLocationAndLoadStations()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocationAndLoadStations() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLat = location.latitude
                    val userLng = location.longitude
                    val radiusKm = 10.0 // You can adjust radius

                    // Center map on user
                    val userLatLng = LatLng(userLat, userLng)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))

                    // ✅ Fetch nearby stations
                    loadNearbyStations(userLat, userLng, radiusKm)
                } else {
                    Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadNearbyStations(lat: Double, lng: Double, radiusKm: Double) {
        lifecycleScope.launch {
            try {
                val response = stationService.getNearbyStations(lat, lng, radiusKm)
                if (response.isSuccessful) {
                    val stations = response.body() ?: emptyList()
                    if (stations.isEmpty()) {
                        Toast.makeText(this@MapActivity, "No nearby stations found.", Toast.LENGTH_SHORT).show()
                    } else {
                        displayStationsOnMap(stations)
                    }
                } else {
                    Toast.makeText(
                        this@MapActivity,
                        "Failed to load stations: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayStationsOnMap(stations: List<Station>) {
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

    // ✅ Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndLoadStations()
            } else {
                Toast.makeText(this, "Location permission is required to show nearby stations.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

