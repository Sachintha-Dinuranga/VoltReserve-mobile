package com.example.voltreserve.services

import com.example.voltreserve.models.LoginRequest
import com.example.voltreserve.models.LoginResponse
import com.example.voltreserve.models.RegisterRequest
import com.example.voltreserve.models.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/owners/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    @POST("api/owners/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}