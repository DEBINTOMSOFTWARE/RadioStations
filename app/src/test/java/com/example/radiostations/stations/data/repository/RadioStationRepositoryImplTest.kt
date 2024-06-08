package com.example.radiostations.stations.data.repository

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.data.model.StationAvailability
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import com.example.radiostations.stations.framework.apiservice.ApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RadioStationRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: RadioStationRepository

    @Before
    fun setUp() {
        apiService = mockk()
        repository = RadioStationRepositoryImpl(apiService)
    }

    @Test
    fun getStations_should_fetch_and_cache_station() = runTest {
        val mockStations = listOf(
            RadioStationItem(
                name = "Station1",
                state = "State1",
                country = "Country1",
                countrycode = "1",
                stationuuid = "id1"
            ),
            RadioStationItem(
                name = "Station2",
                state = "State2",
                country = "Country2",
                countrycode = "2",
                stationuuid = "id2"
            ),
        )
        coEvery {
            apiService.getAllStations(0, 10)
        } returns mockStations

        val flow = repository.getStations(0, 10).toList()
        assertTrue { flow[0] is Resource.Loading }
        assertTrue { flow[1] is Resource.Success }

        val stations = (flow[1] as Resource.Success).data ?: emptyList()
        assertEquals(2, stations.size)
        assertEquals("Station1", stations[0].name)
        assertEquals("Station2", stations[1].name)
        assertEquals("State1", stations[0].state)
        assertEquals("State2", stations[1].state)
        assertEquals("Country1", stations[0].country)
        assertEquals("Country2", stations[1].country)
        assertEquals("1", stations[0].countrycode)
        assertEquals("2", stations[1].countrycode)
        assertEquals("id1", stations[0].stationuuid)
        assertEquals("id2", stations[1].stationuuid)
    }

    @Test
    fun getStations_should_return_paginated_stations() = runTest {
        val mockStations = List(100) { index ->
            RadioStationItem(
                name = "Station$index",
                state = "State$index",
                country = "Country$index",
                countrycode = "CountryCode$index",
                stationuuid = "StationId$index"
            )
        }
        coEvery { apiService.getAllStations(0, 10) } returns mockStations

        val flow = repository.getStations(0, 10).toList()
        assertTrue { flow[0] is Resource.Loading }
        assertTrue { flow[1] is Resource.Success }
        val stations = (flow[1] as Resource.Success).data?.take(10) ?: emptyList()
        assertEquals(10, stations.size)
        assertEquals("Station0", stations[0].name)
    }

    @Test
    fun getStations_should_return_error_when_api_fails() = runTest {
        coEvery { apiService.getAllStations(0, 10) } throws IOException()
        val stations = repository.getStations(0, 10).toList()
        assertTrue { stations[0] is Resource.Loading }
        assertTrue { stations[1] is Resource.Error }
        assertEquals(
            (stations[1] as Resource.Error).message,
            "Couldn't reach server. Check your internet connection"
        )
    }

    @Test
    fun getStationAvailability_fetch_data() = runTest {
        val mockResponse = listOf(
            StationAvailability(
                name = "Station1",
                ok = 1,
                description = "description1"
            ),
            StationAvailability(
                name = "Station2",
                ok = 0,
                description = "description2"
            )
        )

        val stringUuid = "id"

        coEvery {
            apiService.getStationAvailability(stringUuid)
        } returns mockResponse

        val flow = repository.getStationAvailability(stringUuid).toList()
        assertTrue { flow[0] is Resource.Loading }
        assertTrue { flow[1] is Resource.Success }

        val stationAvailability = (flow[1] as Resource.Success).data ?: emptyList()
        assertEquals(2, stationAvailability.size)
        assertEquals("Station1", stationAvailability[0].name)
        assertEquals(1, stationAvailability[0].ok)
    }


    @Test
    fun getStation_Availabity_should_return_error_when_api_fails() = runTest {
        coEvery { apiService.getStationAvailability("id1") } throws IOException()
        val stations = repository.getStationAvailability("id1").toList()
        assertTrue { stations[0] is Resource.Loading }
        assertTrue { stations[1] is Resource.Error }
        assertEquals(
            (stations[1] as Resource.Error).message,
            "Couldn't reach server. Check your internet connection"
        )
    }
}