package com.example.radiostations.stations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.cert.CertPathValidatorException.Reason
import javax.inject.Inject

@HiltViewModel
class RadioStationViewModel @Inject constructor(private val getRadioStation: GetRadioStation) : ViewModel() {

    private val _stations = MutableStateFlow<Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>>(Resource.Initial())
    val stations = _stations.asStateFlow()

    private val _availability = MutableStateFlow<Resource<List<StationAvailabilityEntity>>>(Resource.Initial())
    val availability = _availability.asStateFlow()

    private val currentStations = mutableListOf<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>()
    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false

    init {
        getStations()
        //getStationAvailability("86f80f48-0bcd-4569-9f28-f349c53dde68")
    }

    fun getStationAvailability(stationUuid: String) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            getRadioStation.getStationAvailability(stationUuid).collect {
               when(it) {
                   is Resource.Success -> {
                       val availability = it.data ?: emptyList()
                       _availability.value = Resource.Success(availability)
                   }
                   is Resource.Error -> {
                       _availability.value = Resource.Error("Failed to fetch availability")
                   }
                   is Resource.Loading -> {
                       _availability.value = Resource.Loading
                   }
                   is Resource.Initial -> {

                   }
               }
            }
        }

    }

    fun getStations() {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            val offset = currentPage * pageSize
            getRadioStation.execute(offset, pageSize).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val newStations = result.data ?: emptyList()
                        if (newStations.isNotEmpty()) {
                            currentStations.addAll(newStations)
                            _stations.value = Resource.Success(currentStations)
                            currentPage++
                        } else {
                            // Handle empty response
                            _stations.value = Resource.Error("No more stations available")
                        }
                    }
                    is Resource.Loading -> {
                        _stations.value = Resource.Loading
                    }
                    is Resource.Error -> {
                        _stations.value = Resource.Error(result.message)
                    }
                    is Resource.Initial -> {}
                }
                isLoading = false
            }
        }
    }

    fun loadMoreStations() {
        getStations()
    }
}