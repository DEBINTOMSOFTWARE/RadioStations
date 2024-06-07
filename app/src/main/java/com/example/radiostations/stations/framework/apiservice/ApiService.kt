package com.example.radiostations.stations.framework.apiservice

import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.data.model.StationAvailability
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("json/stations/bylanguage/english")
    suspend fun getAllStations(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<RadioStationItem>

    @GET("json/checks/{stationuuid}")
    suspend fun getStationAvailability(@Path("stationuuid") stationUuid: String) : List<StationAvailability>
}