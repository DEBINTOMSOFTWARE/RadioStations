package com.example.radiostations.stations.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun RadioStationScreen(
    radioStationViewModel: RadioStationViewModel
) {
    val stationState = radioStationViewModel.stations.collectAsState().value
    val availabilityState = radioStationViewModel.availability.collectAsState().value
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                radioStationViewModel.loadMoreStations()
            }
    }
    Scaffold(
        content = { padding ->

            Column {
                StationAvailability1(availabilityState, padding)
                RadioStationList(stationState, padding, listState)
            }
        }
    )
}

@Composable
fun StationAvailability1(
    result: Resource<List<StationAvailabilityEntity>>,
    padding: PaddingValues
) {
    when(result) {
        is Resource.Success -> {
            LazyColumn {
                items(result.data!!) {
                    println("Availability: $it")
                }
            }
        }
        is Resource.Error -> {
            println("Error occurred")
        }
        is Resource.Loading -> {
            println("Loading...")
        }
        is Resource.Initial -> {

        }
    }
}

@Composable
fun RadioStationList(
    result: Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>,
    padding: PaddingValues,
    listState: LazyListState
) {
    Box(modifier = Modifier.padding(padding)) {
        when (result) {
            is Resource.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize(),
                    state = listState) {
                    items(result.data!!) { station ->
                       // println("Result Data:: ${result.data}")
                        RadioStationRow(station)
                    }
                    item { 
                        if (result.data.isNotEmpty()) {
                           CircularProgressIndicator(modifier = Modifier
                               .align(Alignment.Center)
                               .padding(16.dp))
                        }
                    }
                }
            }

            is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is Resource.Error -> Text(
                "Error: ${result.message}",
                modifier = Modifier.align(Alignment.Center)
            )

            is Resource.Initial -> Text(
                "Please wait...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RadioStationRow(stationPair: Pair<RadioStationEntity, List<StationAvailabilityEntity>>) {
    val (station, availability) = stationPair
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Station: ${station.name}", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Country: ${station.country}", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Available in: ${availability.size} regions",
            style = MaterialTheme.typography.bodySmall
        )
        println("Availability : ${availability.size}")
        availability.forEach { availabilityItem ->
            val status = if (availabilityItem.ok == 1) "Online" else "Offline"
            Text(text = "Region: ${availabilityItem.name} - $status")
        }
        Divider()
    }
}
