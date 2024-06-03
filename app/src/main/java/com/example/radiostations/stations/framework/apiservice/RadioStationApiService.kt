package com.example.radiostations.stations.framework.apiservice

import com.example.radiostations.stations.data.model.RadioStationItem
import retrofit2.http.GET

interface RadioStationApiService {
    @GET("https://de1.api.radio-browser.info/json/stations/bylanguage/english")
    suspend fun getAllStations(): List<RadioStationItem>
}