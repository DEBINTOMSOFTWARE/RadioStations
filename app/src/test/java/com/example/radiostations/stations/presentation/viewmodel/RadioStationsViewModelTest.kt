package com.example.radiostations.stations.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.radiostations.core.framework.ConnectivityMonitor
import com.example.radiostations.core.utils.Resource
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
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class RadioStationsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getRadioStation: GetRadioStation
    private lateinit var connectivityMonitor: ConnectivityMonitor
    private lateinit var viewModel: RadioStationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRadioStation = mockk()
        connectivityMonitor = mockk()
    }

    @Test
    fun initial_state_is_Resource_loading() = runTest {
        coEvery {
            getRadioStation.getStationWithAvailability(0, 20)
        } returns flowOf(Resource.Loading)

        viewModel = RadioStationViewModel(getRadioStation, connectivityMonitor)
        viewModel.getStations()

        val initialState = viewModel.stations.value
        assertTrue(
            initialState is Resource.Loading,
            "Expected initial state to be Resource.Loading"
        )
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}