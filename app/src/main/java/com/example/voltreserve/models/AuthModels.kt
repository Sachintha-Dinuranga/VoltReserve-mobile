package com.example.voltreserve.models

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val role: String,
    val email: String,
    val expiresAtUtc: String
)
