package com.example.voltreserve.models

// backend /api/auth/login expects: { email, password }
data class StaffLoginRequest(
    val email: String,
    val password: String
)
