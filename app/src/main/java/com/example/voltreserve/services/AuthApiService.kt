package com.example.voltreserve.services

import com.example.voltreserve.models.LoginResponse
import com.example.voltreserve.models.StaffLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    // web users (Backoffice / StationOperator)
    @POST("api/auth/login")
    suspend fun staffLogin(@Body req: StaffLoginRequest): Response<LoginResponse>
}
