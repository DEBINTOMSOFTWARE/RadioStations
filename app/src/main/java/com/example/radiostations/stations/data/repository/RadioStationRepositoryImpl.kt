package com.example.radiostations.stations.data.repository

import coil.network.HttpException
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import com.example.radiostations.stations.framework.apiservice.RadioStationApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException


class RadioStationRepositoryImpl(private val apiService: RadioStationApiService) : RadioStationRepository {
     private var cachedStations: List<RadioStationEntity>? = null

    override fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>> = flow {
        emit(Resource.Loading)
        try {
            if(cachedStations == null) {
                val response = apiService.getAllStations()
                cachedStations = response.map {
                    station -> station.toRadioStationEntity()
                }
            }
            val paginatedStations = cachedStations!!.drop(offset).take(limit)
            emit(Resource.Success(paginatedStations))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection"))
        }
    }

    private fun RadioStationItem.toRadioStationEntity(): RadioStationEntity {
        return RadioStationEntity(
            name = this.name,
            state = this.state,
            country = this.country,
            countrycode = this.countrycode,
            stationuuid = this.stationuuid
        )
    }
}