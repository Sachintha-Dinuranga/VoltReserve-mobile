package com.example.voltreserve

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityOwnerProfileBinding
import com.example.voltreserve.helpers.SessionDbHelper
import com.example.voltreserve.models.ChangePasswordRequest
import com.example.voltreserve.models.UpdateOwnerRequest
import kotlinx.coroutines.launch

class OwnerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerProfileBinding
    private lateinit var dbHelper: SessionDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SQLite database helper
        dbHelper = SessionDbHelper(this)

        loadProfile()

        // Back button - navigate back to dashboard
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnUpdate.setOnClickListener {
            val req = UpdateOwnerRequest(
                firstName = binding.etFirstName.text.toString(),
                lastName = binding.etLastName.text.toString(),
                phone = binding.etPhone.text.toString()
            )
            updateProfile(req)
        }

        binding.btnChangePassword.setOnClickListener {
            val req = ChangePasswordRequest(
                currentPassword = binding.etCurrentPassword.text.toString(),
                newPassword = binding.etNewPassword.text.toString()
            )
            changePassword(req)
        }

        binding.btnDeactivate.setOnClickListener {
            showDeactivationConfirmation()
        }

        // In your onCreate method, add the logout button click listener:
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.ownerAuthed(this@OwnerProfileActivity)
                val response = api.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val owner = response.body()!!
                    binding.etFirstName.setText(owner.firstName)
                    binding.etLastName.setText(owner.lastName)
                    binding.etPhone.setText(owner.phone)
                    binding.tvEmail.text = owner.email
                    binding.tvNic.text = owner.nic

                    // Combine first and last name for display
                    binding.tvUserName.text = "${owner.firstName} ${owner.lastName}"
                } else {
                    Toast.makeText(this@OwnerProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateProfile(req: UpdateOwnerRequest) {
        if (req.firstName.isEmpty() || req.lastName.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.ownerAuthed(this@OwnerProfileActivity)
                val response = api.updateProfile(req)
                if (response.isSuccessful && response.body() != null) {
                    // Update the displayed username
                    binding.tvUserName.text = "${req.firstName} ${req.lastName}"
                    Toast.makeText(this@OwnerProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Update failed"
                    Toast.makeText(this@OwnerProfileActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changePassword(req: ChangePasswordRequest) {
        if (req.currentPassword.isEmpty() || req.newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill both password fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (req.newPassword.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.ownerAuthed(this@OwnerProfileActivity)
                val response = api.changePassword(req)
                if (response.isSuccessful) {
                    Toast.makeText(this@OwnerProfileActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    binding.etCurrentPassword.text?.clear()
                    binding.etNewPassword.text?.clear()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Password change failed"
                    Toast.makeText(this@OwnerProfileActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeactivationConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Deactivate Account")
            .setMessage("Are you sure you want to deactivate your account?\n\n" +
                    "• You will be logged out immediately\n" +
                    "• You cannot login again until admin reactivates\n" +
                    "• All your data will be preserved\n" +
                    "• Contact admin to reactivate your account")
            .setPositiveButton("Deactivate") { dialog, which ->
                deactivateAccount()
            }
            .setNegativeButton("Cancel", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deactivateAccount() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.ownerAuthed(this@OwnerProfileActivity)
                val response = api.deactivate()
                if (response.isSuccessful) {
                    Toast.makeText(this@OwnerProfileActivity,
                        "Account deactivated! You will be logged out.", Toast.LENGTH_LONG).show()

                    // Logout and go to login screen
                    logoutUser()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Deactivation failed"
                    Toast.makeText(this@OwnerProfileActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Add this method:
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { dialog, which ->
                logoutUser()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logoutUser() {
        // Clear any session/token data from SQLite
        clearSessionData()

        // Navigate to login screen and clear back stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearSessionData() {
        // Clear session from SQLite database
        dbHelper.clearSession()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}