package com.example.radiostations.stations.domain.usecases

import androidx.compose.runtime.MutableState
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface GetRadioStation {
    fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>>
    fun getStationAvailability(stationUuid: String): Flow<Resource<List<StationAvailabilityEntity>>>
    fun getStationWithAvailability(
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>>

}