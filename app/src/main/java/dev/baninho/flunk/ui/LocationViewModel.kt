package dev.baninho.flunk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(activity: MainActivity) : AndroidViewModel(activity.application) {

    private val locationLiveData = LocationLiveData(activity)
    fun getLocationLiveData() = locationLiveData
}