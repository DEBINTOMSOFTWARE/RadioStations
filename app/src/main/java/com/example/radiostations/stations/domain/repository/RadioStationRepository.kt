package com.example.radiostations.stations.domain.repository

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import kotlinx.coroutines.flow.Flow

interface RadioStationRepository {
  fun getStations(offset:Int, limit: Int): Flow<Resource<List<RadioStationEntity>>>
}