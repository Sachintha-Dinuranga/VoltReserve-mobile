package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityRegisterBinding
import com.example.voltreserve.models.RegisterRequest
import com.example.voltreserve.models.Vehicle
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val nic = binding.etNic.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val vehicleMake = binding.etVehicleMake.text.toString().trim()
            val vehicleModel = binding.etVehicleModel.text.toString().trim()
            val batteryKWh = binding.etVehicleBattery.text.toString().toDoubleOrNull() ?: 0.0

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val vehicle = Vehicle(vehicleMake, vehicleModel, batteryKWh)
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
                        this@RegisterActivity,
                        "Registration successful! Welcome, ${user.firstName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Navigate to Login screen
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RegisterActivity, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

}