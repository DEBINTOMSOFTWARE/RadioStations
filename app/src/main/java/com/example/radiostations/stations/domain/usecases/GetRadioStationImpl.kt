package com.example.radiostations.stations.domain.usecases

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRadioStationImpl @Inject constructor(private val repository: RadioStationRepository) : GetRadioStation{
    override fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>> {
      return repository.getStations(offset, limit)
    }
}