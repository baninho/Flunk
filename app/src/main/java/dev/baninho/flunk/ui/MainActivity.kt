package dev.baninho.flunk.ui

import android.content.Intent
import android.graphics.Color
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
        lblLatitudeValue = findViewById(R.id.courtLatitude)
        lblLongitudeValue = findViewById(R.id.courtLongitude)

        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        enlistButton.setOnClickListener {
            lblCapacity.setTextColor(Color.BLACK)
            if (saveCourt()) {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveCourt(): Boolean {
        val capacity = lblCapacity.text.toString().toIntOrNull()
        if (capacity == null) {
            lblCapacity.setTextColor(Color.RED)
            return false
        }
        val court = Court().apply {
            ownerId = userId
            latitude = lblLatitudeValue.text.toString()
            longitude = lblLongitudeValue.text.toString()
            isActive = true
            players = playercount
            this.capacity = capacity
        }
        mainViewModel.save(court)
        return true
    }
}