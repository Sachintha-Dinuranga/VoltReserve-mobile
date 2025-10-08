// services/AuthApiService.kt
package com.example.voltreserve.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class StaffMeDto(
    val email: String,
    val role: String,
    val isActive: Boolean
)

data class StaffChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

interface AuthApiService {
    // existing
    @POST("api/auth/login")
    suspend fun staffLogin(@Body req: com.example.voltreserve.models.StaffLoginRequest)
            : Response<com.example.voltreserve.models.LoginResponse>

    // new: self-service endpoints
    @GET("api/staff/me")
    suspend fun staffMe(): Response<StaffMeDto>

    @POST("api/staff/me/change-password")
    suspend fun changePassword(@Body req: StaffChangePasswordRequest): Response<Unit>
}
