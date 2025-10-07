package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example welcome message (replace with actual session user if needed)
        binding.tvWelcome.text = "Welcome back, EV Owner!"

        // TODO: Replace with actual counts from API
        binding.tvPendingCount.text = "3"
        binding.tvUpcomingCount.text = "5"

        // Click listeners for tiles
        binding.tileProfile.setOnClickListener {
            startActivity(Intent(this, OwnerProfileActivity::class.java))
        }
//
//        binding.tileCurrentReservations.setOnClickListener {
//            startActivity(Intent(this, CurrentReservationsActivity::class.java))
//        }
//
//        binding.tileReservationHistory.setOnClickListener {
//            startActivity(Intent(this, ReservationHistoryActivity::class.java))
//        }
//
//        binding.tileChargingStations.setOnClickListener {
//            startActivity(Intent(this, ChargingStationsActivity::class.java))
//        }
    }
}
