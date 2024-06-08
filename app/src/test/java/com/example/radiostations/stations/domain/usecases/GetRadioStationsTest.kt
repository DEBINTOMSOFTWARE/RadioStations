package com.example.radiostations.stations.domain.usecases

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetRadioStationsTest {

    private val repository: RadioStationRepository = mockk()
    private lateinit var getRadioStation: GetRadioStation

    @Before
    fun setUp() {
        getRadioStation = GetRadioStationImpl(repository)
    }

    @Test
    fun getStation_fetches_data_from_repository() = runTest {
        val mockStations: Flow<Resource<List<RadioStationEntity>>> = flow {
            emit(Resource.Success(listOf(
                RadioStationEntity(name = "Station1", state = "State1", country = "Country1", countrycode = "1", stationuuid = "id1"),
                RadioStationEntity(name = "Station2", state = "State2", country = "Country2", countrycode = "2", stationuuid = "id2")
            )))
        }

        coEvery {
            repository.getStations(0,10)
        } returns mockStations

        val flow = getRadioStation.getStations(0,10)

        var resultList: List<RadioStationEntity>? = null
        flow.collect { result ->
            if (result is Resource.Success) {
                resultList = result.data
            }
        }

        assertEquals(2, resultList?.size)
        assertEquals("Station1", resultList?.first()?.name)
        assertEquals("State1", resultList?.first()?.state)
        assertEquals("Country1", resultList?.first()?.country)
        assertEquals("1", resultList?.first()?.countrycode)
        assertEquals("id1", resultList?.first()?.stationuuid)
    }

    @Test
    fun getStations_should_return_error_when_api_fails() = runTest {
        val mockError: Flow<Resource<List<RadioStationEntity>>> = flow {
            emit(Resource.Error(
                "Couldn't reach server. Check your internet connection"
            ))
        }
        coEvery {repository.getStations(0,10) } returns mockError
        val flow = getRadioStation.getStations(0,10)

        flow.collect{result ->
            if(result is Resource.Error) {
                assertEquals((result).message, "Couldn't reach server. Check your internet connection")
            }
        }
    }


    @Test
    fun getStationAvailability_fetches_data_from_repository() = runTest {
        val mockStationsAvailabilities: Flow<Resource<List<StationAvailabilityEntity>>> = flow {
            emit(Resource.Success(listOf(
                StationAvailabilityEntity(
                    name = "Station1",
                    ok =  1,
                    description = "description1"
                ),
                StationAvailabilityEntity(
                    name = "Station2",
                    ok =  0,
                    description = "description2"
                )
            )))
        }

        coEvery {
            repository.getStationAvailability("id1")
        } returns mockStationsAvailabilities

        val flow = getRadioStation.getStationAvailability("id1")

        var resultList: List<StationAvailabilityEntity>? = null
        flow.collect { result ->
            if (result is Resource.Success) {
                resultList = result.data
            }
        }

        assertEquals(2, resultList?.size)
        assertEquals("Station1", resultList?.first()?.name)
        assertEquals(1, resultList?.first()?.ok)
    }


    @Test
    fun getStationsAvailability_should_return_error_when_api_fails() = runTest {
        val mockError: Flow<Resource<List<StationAvailabilityEntity>>> = flow {
            emit(Resource.Error(
                "Couldn't reach server. Check your internet connection"
            ))
        }
        coEvery {repository.getStationAvailability("id1") } returns mockError
        val flow = getRadioStation.getStationAvailability("id1")

        flow.collect{result ->
            if(result is Resource.Error) {
                assertEquals((result).message, "Couldn't reach server. Check your internet connection")
            }
        }
    }


}