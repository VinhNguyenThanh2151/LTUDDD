package com.example.run_core.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.run_core.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.example.run_core.data.local.DirectionsResponse
import com.example.run_core.data.remote.DirectionsApiService
import com.example.run_core.databinding.ActivityMapBinding
import com.permissionx.guolindev.PermissionX
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private var firstMarker: MarkerOptions? = null
    private var secondMarker: MarkerOptions? = null
    private var polyline: Polyline? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge mode for the status bar
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        PermissionX.init(this).permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                enableEdgeToEdge()
            } else {
                finish()
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            mMap.isMyLocationEnabled = true
            getDeviceLocation()
        }
        // Set a listener for map clicks
        mMap.setOnMapClickListener { latLng ->
            if (firstMarker == null) {
                // Place the first marker
                firstMarker = MarkerOptions().position(latLng).title("Start")
                mMap.addMarker(firstMarker!!)
            } else if (secondMarker == null) {
                // Place the second marker and draw the route
                secondMarker = MarkerOptions().position(latLng).title("End")
                mMap.addMarker(secondMarker!!)

                // Draw the route between the two points
                drawRoute(firstMarker!!.position, secondMarker!!.position)
            } else {
                // Clear existing markers and route, and place new start marker
                mMap.clear()
                firstMarker = MarkerOptions().position(latLng).title("Start")
                secondMarker = null
                polyline = null
                mMap.addMarker(firstMarker!!)
            }
        }
    }

    private fun drawRoute(start: LatLng, end: LatLng) {
        // Use Google Directions API to get the route
        val directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=${start.latitude},${start.longitude}&destination=${end.latitude},${end.longitude}&key=AIzaSyD5BnWrzmw0Y8Oat_0s1dI-zzzcnsPfMvI"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsApiService::class.java)
        val call = service.getDirections(directionApiPath)

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful) {
                    try {
                        response.body()?.let {
                            val route = it.routes[0]
                            val points = decodePolyline(route.overviewPolyline.points)
                            val polylineOptions = PolylineOptions().addAll(points).color(Color.BLUE).width(5f)
                            polyline = mMap.addPolyline(polylineOptions)
                        }
                    } catch (e :Exception) {
                        Toast.makeText(this@MapActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat / 1E5, lng / 1E5)
            poly.add(p)
        }
        return poly
    }
    private fun getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                        }
                    }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        mMap.isMyLocationEnabled = true
                        getDeviceLocation()
                    }
                } else {
                    // Permission denied, show some message to user
                }
                return
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}