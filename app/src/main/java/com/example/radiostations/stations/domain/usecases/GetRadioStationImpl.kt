package com.example.radiostations.stations.domain.usecases

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRadioStationImpl @Inject constructor(private val repository: RadioStationRepository) : GetRadioStation{
    override fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>> {
      return repository.getStations(offset, limit)
    }

    override fun getStationAvailability(stationUuid: String): Flow<Resource<List<StationAvailabilityEntity>>> {
        return repository.getStationAvailability(stationUuid)
    }

    override fun execute(
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>> {
       return repository.getStationsWithAvailability(offset, limit)
    }
}