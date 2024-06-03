package com.example.radiostations.stations.data.repository

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import com.example.radiostations.stations.framework.apiservice.RadioStationApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RadioStationRepositoryImpl(private val apiService: RadioStationApiService) : RadioStationRepository {
    override fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>> = flow {
        emit(Resource.Loading)
        emit(Resource.Success(emptyList()))
    }
}