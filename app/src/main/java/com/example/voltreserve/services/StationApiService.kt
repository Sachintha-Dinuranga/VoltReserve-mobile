package com.example.voltreserve.services
import com.example.voltreserve.models.Station
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface StationApiService {
    @GET("api/stations")
    suspend fun getStations(
        @Header("Authorization") token: String
    ): Response<List<Station>>
}