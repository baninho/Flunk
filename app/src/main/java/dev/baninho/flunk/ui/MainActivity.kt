package dev.baninho.flunk.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dev.baninho.flunk.R
import dev.baninho.flunk.dto.Court

class MainActivity : AppCompatActivity() {

    private val playercount: Int = 0
    private val userId: String = ""

    private var mainViewModel: MainViewModel = MainViewModel()

    private lateinit var mapButton: Button
    private lateinit var enlistButton: Button
    private lateinit var lblLatitudeValue: TextView
    private lateinit var lblLongitudeValue: TextView
    private lateinit var lblCapacity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapButton = findViewById(R.id.mapButton)
        enlistButton = findViewById(R.id.enlistButton)
        lblCapacity = findViewById(R.id.courtCapacity)

        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        enlistButton.setOnClickListener {
            saveCourt()

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveCourt() {
        var court = Court().apply {
            owner = userId
            latitude = lblLatitudeValue.text.toString()
            longitude = lblLongitudeValue.text.toString()
            active = true
            players = playercount
            capacity = lblCapacity.text.toString().toInt()
        }
        mainViewModel.save(court)
    }
}