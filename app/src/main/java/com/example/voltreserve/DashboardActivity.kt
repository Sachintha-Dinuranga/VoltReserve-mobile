package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example welcome message (replace with actual session user if needed)
        binding.tvWelcome.text = "Welcome back, EV Owner!"

        // Click listeners for tiles
        binding.tileProfile.setOnClickListener {
            startActivity(Intent(this, OwnerProfileActivity::class.java))
        }

        binding.tileNewReservation.setOnClickListener {
            startActivity(Intent(this, ReservationActivity::class.java))
        }

        binding.tileMyReservations.setOnClickListener {
            startActivity(Intent(this, ReservationListActivity::class.java))
        }

//        binding.tileChargingStations.setOnClickListener {
//            startActivity(Intent(this, ChargingStationsActivity::class.java))
//        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh stats every time the user comes to this screen
        fetchStats()
    }

    private fun fetchStats() {
        val api = RetrofitClient.getOwnerService(this)

        lifecycleScope.launch {
            try {
                val response = api.getReservationStats() // Call your API
                if (response.isSuccessful) {
                    val stats = response.body()
                    stats?.let {
                        binding.tvPendingCount.text = it.pending.toString()
                        binding.tvUpcomingCount.text = it.approved.toString()
                    }
                } else {
                    Toast.makeText(this@DashboardActivity, "Failed to fetch stats", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DashboardActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
