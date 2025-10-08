package com.example.voltreserve

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityStationDetailsBinding

class StationDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStationDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add your station details logic here
        // You can fetch and display station information
    }
}