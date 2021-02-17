package dev.baninho.flunk.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court

class MainActivity : AppCompatActivity() {

    private var user: FirebaseUser? = null
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 2000
    private val AUTH_REQUEST_CODE: Int = 2002
    private val playerCount: Int = 0

    private var mainViewModel: MainViewModel = MainViewModel()
    private var locationPermissionGranted: Boolean = false

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var btnMap: Button
    private lateinit var btnEnlist: Button
    private lateinit var btnLogin: Button
    private lateinit var lblLatitudeValue: TextView
    private lateinit var lblLongitudeValue: TextView
    private lateinit var lblCapacity: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMap = findViewById(R.id.mapButton)
        btnEnlist = findViewById(R.id.enlistButton)
        btnLogin = findViewById(R.id.btnLogin)
        lblCapacity = findViewById(R.id.courtCapacity)
        lblLatitudeValue = findViewById(R.id.courtLatitude)
        lblLongitudeValue = findViewById(R.id.courtLongitude)

        btnMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        btnEnlist.setOnClickListener {
            // TODO: Move color change to some edit listener of capacity input field
            lblCapacity.setTextColor(Color.BLACK)

            checkLocationPermission()

            val capacity = lblCapacity.text.toString().toIntOrNull()

            when {
                capacity == null -> {
                    lblCapacity.setTextColor(Color.RED)
                }
                user == null -> {
                    Toast.makeText(this, resources.getText(R.string.msgNotLoggedIn), Toast.LENGTH_LONG).show()
                }
                else -> {
                    createCourt(capacity)
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        btnLogin.setOnClickListener {
            login()
        }

        checkLocationPermission()

        if (user == null) {
            login()
        }
    }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        } else {
            val permissionRequest = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun requestLocationUpdates() {
        locationViewModel = LocationViewModel(this)
        locationViewModel.getLocationLiveData().observe(this, {
            lblLatitudeValue.text = it.latitude
            lblLongitudeValue.text = it.longitude
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()
                    locationPermissionGranted = true
                    Log.d("Location", "Permission granted")
                } else {
                    Log.d("Location", "Permission denied")
                    Toast.makeText(this,
                        resources.getText(R.string.msgNoLocationPermission), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == AUTH_REQUEST_CODE) {
                user = FirebaseAuth.getInstance().currentUser
                btnLogin.text = resources.getText(R.string.btnLogoutText)
                btnLogin.setOnClickListener {
                    logout()
                }
                Toast.makeText(this, resources.getText(R.string.msgStubLogin).toString()
                        + user!!.displayName, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, user!!.displayName + resources.getText(R.string.msgStubLogout), Toast.LENGTH_LONG).show()
                user = null
                btnLogin.text = resources.getText(R.string.btnLoginText)
                btnLogin.setOnClickListener {
                    login()
                }
            }
    }

    private fun createCourt(capacity: Int): Court {
        val court = Court().apply {
            ownerId = user!!.uid
            owner = user!!.displayName ?: ""
            latitude = lblLatitudeValue.text.toString()
            longitude = lblLongitudeValue.text.toString()
            isActive = true
            playerCount = playerCount
            this.capacity = capacity
        }
        mainViewModel.saveCourt(court)
        return court
    }
}