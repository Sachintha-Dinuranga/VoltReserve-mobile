package com.example.voltreserve.services
import com.example.voltreserve.models.Station
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StationApiService {
//    @GET("api/stations/public")
//    suspend fun getStations(
//        @Header("Authorization") token: String
//    ): Response<List<Station>>
    @GET("api/stations/public")
    suspend fun getPublicStations(): Response<List<Station>>

    @GET("api/stations/nearby")
    suspend fun getNearbyStations(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 10.0
    ): Response<List<Station>>
}