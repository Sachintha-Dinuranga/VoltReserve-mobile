package com.example.voltreserve.models

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("availableSlots") val availableSlots: Int?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("schedules") val schedules: List<Schedule>?
)

data class Schedule(
    @SerializedName("id") val id: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("slotsAvailable") val slotsAvailable: Int?
)


