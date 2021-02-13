package dev.baninho.flunk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court
import kotlin.math.pow

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var padding: Int = 10

    private lateinit var courts: List<Court>
    private lateinit var mMap: GoogleMap
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
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
        mainViewModel = MainViewModel()
        mainViewModel.courts.observe(this, {
            courts -> this.courts = courts
            updateMap()
        })
    }

    private fun updateMap() {
        mMap.clear()
        val markers: ArrayList<MarkerOptions> = ArrayList<MarkerOptions>()
        var minLat = 53.0
        var maxLat = 53.5
        var minLng = 9.0
        var maxLng = 10.0
        courts.forEach { court ->
            if (court.latitude.isNotEmpty() && court.longitude.isNotEmpty()) {
                val marker = MarkerOptions().position(LatLng(court.latitude.toDouble(), court.longitude.toDouble()))
                marker.title(court.toString())
                markers.add(marker)
            }
        }
        markers.forEach {
                marker ->
            if (minLat > marker.position.latitude) { minLat = marker.position.latitude }
            if (maxLat < marker.position.latitude) { maxLat = marker.position.latitude }
            if (minLng > marker.position.longitude) { minLng = marker.position.longitude }
            if (maxLng < marker.position.longitude) { maxLng = marker.position.longitude }
            mMap.addMarker(marker)
        }
        val bounds = LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
        val update: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(update)
        Log.d("LatLng", "$minLat $minLng")
    }
}