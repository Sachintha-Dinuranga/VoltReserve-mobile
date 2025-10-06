package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityRegisterStep3Binding
import com.example.voltreserve.models.RegisterRequest
import com.example.voltreserve.models.Vehicle
import kotlinx.coroutines.launch

class RegisterStep3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterStep3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from previous steps
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val nic = intent.getStringExtra("nic") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        binding.btnBack3.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val vehicleMake = binding.etVehicleMake.text.toString().trim()
            val vehicleModel = binding.etVehicleModel.text.toString().trim()
            val batteryKWh = binding.etVehicleBattery.text.toString().toDoubleOrNull() ?: 0.0

            // Vehicle is optional, so create only if fields are filled
            val vehicle = if (vehicleMake.isNotEmpty() || vehicleModel.isNotEmpty() || batteryKWh > 0) {
                Vehicle(vehicleMake, vehicleModel, batteryKWh)
            } else {
                null
            }

            val registerRequest = RegisterRequest(nic, firstName, lastName, email, phone, password, vehicle)
            registerUser(registerRequest)
        }
    }

    private fun registerUser(request: RegisterRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.register(request)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    Toast.makeText(
                        this@RegisterStep3Activity,
                        "Registration successful! Welcome, ${user.firstName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Navigate to Login screen
                    startActivity(Intent(this@RegisterStep3Activity, LoginActivity::class.java))
                    finishAffinity() // Close all registration activities
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@RegisterStep3Activity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RegisterStep3Activity, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}