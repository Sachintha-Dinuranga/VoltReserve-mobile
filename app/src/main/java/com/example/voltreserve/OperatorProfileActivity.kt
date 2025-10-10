package com.example.voltreserve

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityOperatorProfileBinding
import com.example.voltreserve.helpers.SessionDbHelper
import com.example.voltreserve.services.StaffChangePasswordRequest
import kotlinx.coroutines.launch

class OperatorProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperatorProfileBinding
    private lateinit var db: SessionDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = SessionDbHelper(this)

        loadMe()

        // Back button - navigate back to dashboard
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            val current = binding.etCurrentPassword.text?.toString()?.trim().orEmpty()
            val newer = binding.etNewPassword.text?.toString()?.trim().orEmpty()

            if (current.isEmpty() || newer.isEmpty()) {
                Toast.makeText(this, "Both passwords required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newer.length < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            changePassword(current, newer)
        }

        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    private fun loadMe() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.staffAuthed(this@OperatorProfileActivity)
                val res = api.staffMe()
                if (res.isSuccessful && res.body() != null) {
                    val me = res.body()!!
                    binding.tvUserName.text = me.email.substringBefore("@")
                    binding.tvEmail.text = me.email
                    binding.tvRole.text = me.role
                    binding.tvStatus.text = if (me.isActive) "Active" else "Inactive"
                } else {
                    Toast.makeText(this@OperatorProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OperatorProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changePassword(current: String, newer: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.staffAuthed(this@OperatorProfileActivity)
                val res = api.changePassword(StaffChangePasswordRequest(current, newer))
                if (res.isSuccessful) {
                    Toast.makeText(this@OperatorProfileActivity, "Password changed!", Toast.LENGTH_SHORT).show()
                    binding.etCurrentPassword.text?.clear()
                    binding.etNewPassword.text?.clear()
                } else {
                    Toast.makeText(this@OperatorProfileActivity, "Change failed: ${res.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OperatorProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ -> logout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        db.clearSession()
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }
}
