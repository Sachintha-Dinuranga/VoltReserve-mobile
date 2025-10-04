package com.example.voltreserve

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voltreserve.client.RetrofitClient
import com.example.voltreserve.databinding.ActivityOwnerProfileBinding
import com.example.voltreserve.models.ChangePasswordRequest
import com.example.voltreserve.models.UpdateOwnerRequest
import kotlinx.coroutines.launch

class OwnerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfile()

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
            deactivateAccount()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getOwnerService(this@OwnerProfileActivity)
                val response = api.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val owner = response.body()!!
                    binding.etFirstName.setText(owner.firstName)
                    binding.etLastName.setText(owner.lastName)
                    binding.etPhone.setText(owner.phone)
                    binding.tvEmail.text = owner.email
                    binding.tvNic.text = owner.nic
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
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getOwnerService(this@OwnerProfileActivity)
                val response = api.updateProfile(req)
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@OwnerProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OwnerProfileActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changePassword(req: ChangePasswordRequest) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getOwnerService(this@OwnerProfileActivity)
                val response = api.changePassword(req)
                if (response.isSuccessful) {
                    Toast.makeText(this@OwnerProfileActivity, "Password changed!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OwnerProfileActivity, "Password change failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deactivateAccount() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getOwnerService(this@OwnerProfileActivity)
                val response = api.deactivate()
                if (response.isSuccessful) {
                    Toast.makeText(this@OwnerProfileActivity, "Account deactivated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@OwnerProfileActivity, "Deactivate failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OwnerProfileActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
