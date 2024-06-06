package com.example.radiostations.stations.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.usecases.GetRadioStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioStationViewModel @Inject constructor(private val getRadioStation: GetRadioStation) : ViewModel() {

    private val _stations = MutableStateFlow<Resource<List<RadioStationEntity>>>(Resource.Initial())
    val stations = _stations.asStateFlow()

    private val currentStations = mutableListOf<RadioStationEntity>()
    private var currentPage = 0
    private var pageSize = 20
    private var isLoading = false

    init {
        getStations()
    }

    fun getStations() {
        if (isLoading) return
        isLoading = true
        println("Fetching stations for page: $currentPage")
        viewModelScope.launch {
            getRadioStation.getStations(currentPage * pageSize, pageSize).onEach { result ->
                when(result) {
                    is Resource.Success -> {
                        currentStations.addAll(result.data ?: emptyList())
                        _stations.value = Resource.Success(currentStations)
                        currentPage++
                        println("Loaded page $currentPage with ${result.data?.size ?: 0} items")
                    }
                    is Resource.Loading ->  {
                        _stations.value = Resource.Loading
                        println("Loading...")
                    }
                    is Resource.Error -> {
                        _stations.value = result
                        println("Error: ${result.message}")
                    }
                    else -> {}
                }
                isLoading = false
            }.launchIn(this)
        }
    }

}