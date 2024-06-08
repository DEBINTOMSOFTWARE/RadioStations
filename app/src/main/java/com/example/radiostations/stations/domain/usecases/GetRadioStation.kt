package com.example.radiostations.stations.domain.usecases

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import kotlinx.coroutines.flow.Flow

interface GetRadioStation {
    fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>>
    fun getStationAvailability(stationUuid: String): Flow<Resource<List<StationAvailabilityEntity>>>
    fun getStationWithAvailability(
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>>
}