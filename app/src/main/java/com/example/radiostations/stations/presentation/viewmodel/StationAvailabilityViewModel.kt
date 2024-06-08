package com.example.radiostations.stations.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostations.core.framework.ConnectivityMonitor
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationAvailabilityViewModel @Inject constructor(
    private val getRadioStation: GetRadioStation,
    connectivityMonitor: ConnectivityMonitor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _availability =
        MutableStateFlow<Resource<List<StationAvailabilityEntity>>>(Resource.Initial())
    val availability = _availability.asStateFlow()
    private var isLoading = false
    val networkAvailable = connectivityMonitor

    init {
        savedStateHandle.get<String>("stationUuid")?.let { stationUuid ->
            getStationAvailability(stationUuid)
        }
    }

    fun getStationAvailability(stationUuid: String) {
        _availability.value = Resource.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getRadioStation.getStationAvailability(stationUuid).collect { result ->
                _availability.value = result
            }
        }

    }

}