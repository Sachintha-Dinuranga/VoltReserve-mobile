package com.example.voltreserve.models

data class RegisterResponse(
    val id: String,
    val nic: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val isActive: Boolean,
    val vehicle: Vehicle
)