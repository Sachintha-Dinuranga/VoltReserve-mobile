package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityLoginBinding
import com.example.voltreserve.helpers.SessionDbHelper
import com.example.voltreserve.models.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: SessionDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = SessionDbHelper(this)

        binding.btnLogin.setOnClickListener {
            val identifier = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (identifier.isNotEmpty() && password.isNotEmpty()) {
                loginUser(identifier, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterStep1Activity::class.java))
        }
    }

    private fun loginUser(identifier: String, password: String) {
        lifecycleScope.launch {
            try {
                // 1) Try EV Owner login
                val ownerRes = RetrofitClient.ownerPublic.login(
                    com.example.voltreserve.models.LoginRequest(identifier, password)
                )
                if (ownerRes.isSuccessful && ownerRes.body() != null) {
                    val r = ownerRes.body()!!
                    persistAndRoute(r.token, r.role, r.email, r.expiresAtUtc)
                    return@launch
                }

                // 2) If that failed (401/404), try staff (Backoffice/StationOperator)
                val staffRes = RetrofitClient.staffPublic.staffLogin(
                    com.example.voltreserve.models.StaffLoginRequest(
                        email = identifier, // staff login expects email (not NIC)
                        password = password
                    )
                )
                if (staffRes.isSuccessful && staffRes.body() != null) {
                    val r = staffRes.body()!!
                    persistAndRoute(r.token, r.role, r.email, r.expiresAtUtc)
                    return@launch
                }

                Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun persistAndRoute(token: String, role: String, email: String, expiresAtUtc: String) {
        dbHelper.saveSession(token, role, email, expiresAtUtc)
        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

        when (role.lowercase()) {
            "owner" -> startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            "stationoperator" -> startActivity(Intent(this@LoginActivity, OperatorDashboardActivity::class.java))
            "backoffice" -> {
                // optional: if you later ship a small staff app screen, route there
                startActivity(Intent(this@LoginActivity, OperatorDashboardActivity::class.java))
            }
            else -> Toast.makeText(this@LoginActivity, "Unknown role: $role", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

}
