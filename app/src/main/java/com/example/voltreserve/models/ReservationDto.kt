package com.example.voltreserve.models

data class ReservationDto(
    val id: String,
    val nic: String,
    val userId: String,
    val stationId: String,
    val stationName: String,
    val type: String,
    val status: String,
    val reservationDate: String,
    val startTime: String,
    val endTime: String,
    val selectedSlot: Int
)
