package com.example.radiostations.stations.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.radiostations.core.framework.ConnectivityMonitor
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class StationAvailabilityViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getRadioStation: GetRadioStation
    private lateinit var connectivityMonitor: ConnectivityMonitor
    private lateinit var viewModel: StationAvailabilityViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRadioStation = mockk()
        connectivityMonitor = mockk()
        savedStateHandle = SavedStateHandle().apply {
            set("stationUuid", "stationUuid")
        }
    }

    @Test
    fun initial_state_is_Resource_loading() = runTest {
        coEvery {
            getRadioStation.getStationAvailability("stationUuid")
        } returns flowOf(Resource.Loading)

        viewModel = StationAvailabilityViewModel(getRadioStation, connectivityMonitor, savedStateHandle)
        viewModel.getStationAvailability("stationUuid")

        val initialState = viewModel.availability.value
        assertTrue(
            initialState is Resource.Loading,
            "Expected initial state to be Resource.Loading"
        )
    }

    @Test
    fun getStationsAvailability_return_success() = runTest {
        val mockAvailabilityResponse = listOf(
            StationAvailabilityEntity(
                name = "Station1",
                ok = 1,
                description = "description1"
            ),
            StationAvailabilityEntity(
                name = "Station2",
                ok = 0,
                description = "description2"
            )
        )

        coEvery {
            getRadioStation.getStationAvailability("stationUuid")
        } returns flowOf(Resource.Success(mockAvailabilityResponse))

        savedStateHandle["stationUuid"] = "stationUuid"

        viewModel = StationAvailabilityViewModel(getRadioStation, connectivityMonitor,savedStateHandle)
        viewModel.getStationAvailability("stationUuid")

        val state = viewModel.availability.value
        assertEquals(Resource.Loading, Resource.Loading)
        assertEquals(Resource.Success(mockAvailabilityResponse), state)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}