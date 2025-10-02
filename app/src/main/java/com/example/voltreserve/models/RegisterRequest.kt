package com.example.voltreserve.models

data class RegisterRequest(
    val nic: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val vehicle: Vehicle
)