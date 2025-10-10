package com.example.voltreserve

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityQrScannerBinding

class QrScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add your QR scanner logic here
        // You can integrate with camera and QR scanning libraries
    }
}