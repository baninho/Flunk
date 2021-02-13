package dev.baninho.flunk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.Observer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

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
        mainViewModel.courts.observe(this, Observer {
            courts -> this.courts = courts
            updateMap()
        })
    }

    private fun updateMap() {
        if (courts != null) {
            mMap.clear()
            courts.forEach { court ->
                if (court.latitude.isNotEmpty() && court.longitude.isNotEmpty()) {
                    val marker = LatLng(court.latitude.toDouble(), court.longitude.toDouble())
                    mMap.addMarker(MarkerOptions().position(marker).title(court.toString()))
                }
            }
        }
    }
}