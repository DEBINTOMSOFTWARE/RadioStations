package com.example.radiostations.stations.data.repository

import coil.network.HttpException
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.data.model.StationAvailability
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import com.example.radiostations.stations.framework.apiservice.ApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

class RadioStationRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    RadioStationRepository {
    private var cachedStations: MutableList<RadioStationEntity> = mutableListOf()

    override fun getStations(offset: Int, limit: Int): Flow<Resource<List<RadioStationEntity>>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getAllStations(offset, limit)
                val newStations = response.map { station -> station.toRadioStationEntity() }
                cachedStations.addAll(newStations)
                emit(Resource.Success(newStations))
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            }
        }

    override fun getStationAvailability(stationUuid: String): Flow<Resource<List<StationAvailabilityEntity>>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getStationAvailability(stationUuid)
                val availability = response.map { it.toStationAvailabilityEntity() }
                emit(Resource.Success(availability))
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            }
        }

    private suspend fun getAvailabilityForStations(stations: List<RadioStationEntity>): List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>> {
        return coroutineScope {
            stations.map { station ->
                async {
                    val availabilityResource = getStationAvailability(station.stationuuid!!).first()
                    if (availabilityResource is Resource.Success) {
                        station to availabilityResource.data.orEmpty()
                    } else {
                        station to emptyList()
                    }
                }
            }.awaitAll()
        }
    }

    override fun getStationsWithAvailability(
        offset: Int,
        limit: Int
    ): Flow<Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>> = flow {
        emit(Resource.Loading)
        try {
            getStations(offset, limit).collect { stationResource ->
                when (stationResource) {
                    is Resource.Success -> {
                        val stations = stationResource.data
                        if (!stations.isNullOrEmpty()) {
                            val combinedList = getAvailabilityForStations(stations)
                            emit(Resource.Success(combinedList))
                        } else {
                            emit(Resource.Error("No stations found"))
                        }
                    }

                    is Resource.Loading -> emit(Resource.Loading)
                    is Resource.Error -> emit(Resource.Error(stationResource.message))
                    is Resource.Initial -> {}
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load stations with availability: ${e.message}"))
        }
    }

    private fun RadioStationItem.toRadioStationEntity(): RadioStationEntity {
        return RadioStationEntity(
            country = this.country,
            countrycode = this.countrycode,
            name = this.name,
            stationuuid = this.stationuuid,
            state = this.state,
        )
    }

    private fun StationAvailability.toStationAvailabilityEntity(): StationAvailabilityEntity {
        return StationAvailabilityEntity(
            name = this.name,
            ok = this.ok,
            description = this.description,
        )
    }
}


