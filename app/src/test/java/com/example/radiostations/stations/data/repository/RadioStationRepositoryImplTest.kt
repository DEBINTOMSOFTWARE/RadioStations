package com.example.radiostations.stations.data.repository

import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.domain.repository.RadioStationRepository
import com.example.radiostations.stations.framework.apiservice.RadioStationApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class RadioStationRepositoryImplTest {

    private lateinit var apiService: RadioStationApiService
    private lateinit var repository: RadioStationRepository

    @Before
    fun setUp() {
       apiService = mockk()
       repository = RadioStationRepositoryImpl(apiService)
    }

    @Test
    fun getStations_should_fetch_and_cache_station() = runTest {
       val stations = listOf(
           RadioStationItem(name = "Station1", state = "State1", country = "Country1", countrycode = "1", stationuuid = "id1"),
           RadioStationItem(name = "Station2", state = "State2", country = "Country2", countrycode = "2", stationuuid = "id2"),
       )
        coEvery {
            apiService.getAllStations()
        }returns stations

         val flow = repository.getStations(0, 2).toList()
         assertTrue { flow[0] is Resource.Loading }
         assertTrue { flow[1] is Resource.Success }
    }
}