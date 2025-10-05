package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityRegisterStep2Binding

class RegisterStep2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from previous step
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val nic = intent.getStringExtra("nic") ?: ""

        binding.btnBack2.setOnClickListener {
            finish()
        }

        binding.btnNext2.setOnClickListener {
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass data to next step
            val intent = Intent(this, RegisterStep3Activity::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("email", email)
            intent.putExtra("phone", phone)
            intent.putExtra("nic", nic)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }
}