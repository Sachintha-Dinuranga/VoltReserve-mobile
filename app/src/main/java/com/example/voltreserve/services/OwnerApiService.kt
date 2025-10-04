package com.example.voltreserve.services

import com.example.voltreserve.models.ChangePasswordRequest
import com.example.voltreserve.models.LoginRequest
import com.example.voltreserve.models.LoginResponse
import com.example.voltreserve.models.OwnerDto
import com.example.voltreserve.models.RegisterRequest
import com.example.voltreserve.models.RegisterResponse
import com.example.voltreserve.models.UpdateOwnerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface OwnerApiService {

    @POST("api/owners/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    @POST("api/owners/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/owners/me")
    suspend fun getProfile(): Response<OwnerDto>

    @PUT("api/owners/me")
    suspend fun updateProfile(@Body req: UpdateOwnerRequest): Response<OwnerDto>

    @POST("api/owners/me/change-password")
    suspend fun changePassword(@Body req: ChangePasswordRequest): Response<Unit>

    @POST("api/owners/me/deactivate")
    suspend fun deactivate(): Response<Unit>
}