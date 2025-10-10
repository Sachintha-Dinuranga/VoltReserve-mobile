package com.example.voltreserve.models

data class OwnerDto(
    val id: String,
    val nic: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val isActive: Boolean
)

data class UpdateOwnerRequest(
    val firstName: String,
    val lastName: String,
    val phone: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
