package com.example.radiostations.stations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StationAvailabilityViewModel @Inject constructor(private val getRadioStation: GetRadioStation) : ViewModel() {

    private val _availability = MutableStateFlow<Resource<List<StationAvailabilityEntity>>>(Resource.Initial())
    val availability = _availability.asStateFlow()
    private var isLoading = false

    fun getStationAvailability(stationUuid: String) {
        if (isLoading) return
        isLoading = true
        _availability.value = Resource.Loading
        viewModelScope.launch {
            getRadioStation.getStationAvailability(stationUuid).collect { result ->
                _availability.value = result
            }
        }

    }

}