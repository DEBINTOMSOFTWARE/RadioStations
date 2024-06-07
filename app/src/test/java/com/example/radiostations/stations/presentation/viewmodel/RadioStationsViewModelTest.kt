package com.example.radiostations.stations.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.data.model.RadioStationItem
import com.example.radiostations.stations.data.model.StationAvailability
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RadioStationsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getRadioStation: GetRadioStation
    private lateinit var viewModel: RadioStationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRadioStation = mockk()
    }

    @Test
    fun initial_state_is_Resource_loading() = runTest {
        coEvery {
            getRadioStation.getStationWithAvailability(0,20)
        } returns flowOf(Resource.Loading)

        viewModel = RadioStationViewModel(getRadioStation)
        viewModel.getStations()

        val initialState = viewModel.stations.value
        assertTrue(initialState is Resource.Loading, "Expected initial state to be Resource.Loading")
    }

    @Test
    fun getStationsAvailability_return_success() = runTest {
        val mockAvailabilityResponse = listOf(
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
        )

        coEvery {
            getRadioStation.getStationAvailability("stationUuid")
        } returns flowOf(Resource.Success(mockAvailabilityResponse))
        viewModel = RadioStationViewModel(getRadioStation)
        viewModel.getStationAvailability("stationUuid")

        val state = viewModel.availability.value
        assertEquals(Resource.Success(mockAvailabilityResponse), state)
    }

    @Test
    fun getStations_return_Success() = runTest {
        val station = RadioStationEntity(name = "Station1", state = "State1", country = "Country1", countrycode = "1", stationuuid = "id1")
        val availability = StationAvailabilityEntity(
            name = "Station1",
            ok =  1,
            description = "description1"
        )

        val stationWithAvailability = listOf(station to listOf(availability) )

        coEvery { getRadioStation.getStationWithAvailability(0,20) } returns flowOf(Resource.Success(stationWithAvailability))
        viewModel = RadioStationViewModel(getRadioStation)

        viewModel.getStations()

        viewModel.stations.test {
         val state = awaitItem()
            assertEquals(Resource.Success(stationWithAvailability), state)
        }


    }



    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}