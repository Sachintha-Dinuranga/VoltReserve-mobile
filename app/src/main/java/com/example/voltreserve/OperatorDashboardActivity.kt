package com.example.voltreserve

import android.os.Bundle
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
        binding.btnStartQrScan.setOnClickListener {
            Toast.makeText(this, "QR scanning coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}
