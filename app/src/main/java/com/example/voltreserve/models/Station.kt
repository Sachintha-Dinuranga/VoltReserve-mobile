package com.example.voltreserve.models

data class Schedule(
    val id: String,
    val startTime: String,
    val endTime: String,
    val slotsAvailable: Int
)

data class Station(
    val id: String,
    val name: String,
    val location: String,
    val type: String,
    val availableSlots: Int,
    val isActive: Boolean,
    val schedules: List<Schedule>?
)

