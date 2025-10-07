package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityOperatorDashboardBinding

class OperatorDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperatorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcomeOperator.text = "Welcome Station Operator!"

        // Click listener for QR Code Scanner
        binding.btnStartQrScan.setOnClickListener {
            Toast.makeText(this, "QR scanning coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Click listener for Profile Image with error handling
        binding.imgOperatorProfile.setOnClickListener {
            try {
                val intent = Intent(this, OperatorProfileActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("OperatorDashboard", "Error navigating to profile: ${e.message}")
                Toast.makeText(this, "Profile page not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}