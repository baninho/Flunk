package dev.baninho.flunk.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dev.baninho.flunk.dto.LocationDetails

class LocationLiveData(private val activity: MainActivity) : LiveData<LocationDetails>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity.application)

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = TEN_SECONDS
            fastestInterval = ONE_MINUTE/10
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        private const val TEN_SECONDS: Long = 10000
        private const val ONE_MINUTE: Long = 60000
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActive() {
        super.onActive()
        if (ActivityCompat.checkSelfPermission(
                activity.application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity.application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.checkLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location -> location.also { setLocationData(it) }
        }
        startLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                activity.application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity.application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.checkLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            locationResult ?: return

            for (location in locationResult.locations) setLocationData(location)
        }
    }

    /**
     * if we received a location this function will be called
     */
    private fun setLocationData(location: Location) {
        value = LocationDetails(location.longitude.toString(), location.latitude.toString())
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
