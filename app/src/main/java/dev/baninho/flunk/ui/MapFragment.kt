package dev.baninho.flunk.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private val padding: Int = 200

    private var courts: ArrayList<Court>? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mMap: GoogleMap
    private var mapReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap -> mMap = googleMap

            mMap.setOnInfoWindowClickListener {
                val joinDialogFragment = JoinCourtDialog(it)
                joinDialogFragment.show(requireActivity().supportFragmentManager, "joinCourt")
            }

            mapReady = true
            updateMap()

        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.courts.observe(viewLifecycleOwner, {
                courts -> this.courts = courts
                updateMap()
        })
    }

    private fun updateMap() {
        if (mapReady && courts != null) {
            mMap.clear()
            var minLat = 90.0
            var maxLat = -90.0
            var minLng = 180.0
            var maxLng = -180.0
            courts!!.forEach {
                if (it.isActive && it.latitude.isNotEmpty() && it.longitude.isNotEmpty()) {
                    val marker = MarkerOptions().position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
                    marker.title(it.toString())
                    marker.snippet("${it.playerCount}/${it.capacity} Spieler. Zum Beitreten klicken")
                    if (minLat > marker.position.latitude) { minLat = marker.position.latitude }
                    if (maxLat < marker.position.latitude) { maxLat = marker.position.latitude }
                    if (minLng > marker.position.longitude) { minLng = marker.position.longitude }
                    if (maxLng < marker.position.longitude) { maxLng = marker.position.longitude }
                    mMap.addMarker(marker).tag = it
                }
            }
            val bounds = LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
            val update: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.moveCamera(update)
            Log.d("LatLng", "$minLat $minLng")
        }
    }
}