package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.databinding.ActivityRegisterStep1Binding

class RegisterStep1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext1.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val nic = binding.etNic.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || nic.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass data to next step
            val intent = Intent(this, RegisterStep2Activity::class.java)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("email", email)
            intent.putExtra("phone", phone)
            intent.putExtra("nic", nic)
            startActivity(intent)
        }
    }
}