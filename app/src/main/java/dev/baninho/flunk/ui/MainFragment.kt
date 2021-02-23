package dev.baninho.flunk.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dev.baninho.flunk.R
import dev.baninho.flunk.databinding.MainFragmentBinding
import dev.baninho.flunk.dto.Court

class MainFragment : Fragment() {
    private var mainViewModel: MainViewModel? = null
    private var locationPermissionGranted: Boolean = false

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 2000
    private val AUTH_REQUEST_CODE: Int = 2002

    private lateinit var locationViewModel: LocationViewModel

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureLoginBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        configureLoginBtn()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.btnMap.setOnClickListener {
            (activity as MainActivity).onOpenMap()
        }

        binding.btnEnlist.setOnClickListener {
            // TODO: Move color change to some edit listener of capacity input field
            binding.lblCapacity.setTextColor(Color.BLACK)

            checkLocationPermission()

            val capacity = binding.lblCapacity.text.toString().toIntOrNull()

            when {
                capacity == null -> {
                    binding.lblCapacity.setTextColor(Color.RED)
                }
                mainViewModel!!.user == null -> {
                    Toast.makeText(this.context, resources.getText(R.string.msgNotLoggedIn), Toast.LENGTH_LONG).show()
                }
                else -> {
                    createCourt(capacity)
                    (activity as MainActivity).onOpenMap()
                }
            }
        }

        checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        configureLoginBtn()
    }

    private fun configureLoginBtn() {
        if (_binding == null || mainViewModel == null) return
        if (mainViewModel!!.user == null) {
            binding.btnLogin.setOnClickListener {
                login()
            }
            binding.btnLogin.setText(R.string.btnLoginText)
        } else {
            binding.btnLogin.setOnClickListener {
                logout()
            }
            binding.btnLogin.setText(R.string.btnLogoutText)
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

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this.requireContext())
            .addOnCompleteListener {
                Toast.makeText(this.context, mainViewModel!!.user!!.displayName + resources.getText(R.string.msgStubLogout), Toast.LENGTH_LONG).show()
                mainViewModel!!.user = null
                binding.btnLogin.text = resources.getText(R.string.btnLoginText)
                binding.btnLogin.setOnClickListener {
                    login()
                }
            }
    }

    private fun requestLocationUpdates() {
        locationViewModel = LocationViewModel(this.activity as MainActivity)
        locationViewModel.getLocationLiveData().observe(this.activity as MainActivity, {
            binding.lblLatitudeValue.text = it.latitude
            binding.lblLongitudeValue.text = it.longitude
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
                    Toast.makeText(this.context,
                        resources.getText(R.string.msgNoLocationPermission), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == AUTH_REQUEST_CODE) {
                mainViewModel!!.user = FirebaseAuth.getInstance().currentUser
                binding.btnLogin.text = resources.getText(R.string.btnLogoutText)
                binding.btnLogin.setOnClickListener {
                    logout()
                }
                Toast.makeText(this.context, resources.getText(R.string.msgStubLogin).toString()
                        + mainViewModel!!.user!!.displayName, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this.context, resources.getText(R.string.msgLoginFailed).toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createCourt(capacity: Int): Court? {
        // TODO: Check null asserted calls if they are actually null asserted
        // this currently crashes
        if (mainViewModel == null) return null
        val mVM = mainViewModel as MainViewModel
        val court = Court().apply {
            ownerId = mVM.user!!.uid
            owner = mVM.user!!.displayName ?: ""
            latitude = binding.lblLatitudeValue.text.toString()
            longitude = binding.lblLongitudeValue.text.toString()
            isActive = true
            playerCount = 0
            this.capacity = capacity
        }
        court.join(mVM.user)
        mVM.saveCourt(court)
        return court
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        } else {
            val permissionRequest = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissionRequest, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }


}