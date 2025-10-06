package com.example.voltreserve.models

data class OwnerStationSummary(
    val id: String,
    val name: String,
    val location: String,
    val type: String,
    val availableSlots: Int
)

data class CreateReservationRequest(
    val stationId: String,
    val stationName: String,
    val type: String,
    val reservationDate: String,
    val startTime: String,
    val endTime: String
)
