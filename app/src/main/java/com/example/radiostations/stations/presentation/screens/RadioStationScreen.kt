package com.example.radiostations.stations.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.radiostations.Destination
import com.example.radiostations.core.framework.ConnectivityObservable
import com.example.radiostations.core.presentaion.components.BodyLargeText
import com.example.radiostations.core.presentaion.components.BodySmallText
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.core.presentaion.components.BodyText
import com.example.radiostations.core.presentaion.components.HeaderLargeText
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun RadioStationScreen(
    radioStationViewModel: RadioStationViewModel,
    navController: NavHostController
) {

    val stationState = radioStationViewModel.stations.collectAsState().value
    val networkAvailable =
        radioStationViewModel.networkAvailable.observe()
            .collectAsState(ConnectivityObservable.Status.Available)
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
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(60.dp)
                    .semantics { contentDescription = "Radio Stations" },
                title = { HeaderLargeText(text = "Radio Stations") },
                backgroundColor = MaterialTheme.colorScheme.primary,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (networkAvailable.value == ConnectivityObservable.Status.Unavailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BodyLargeText(
                        text = "Network unavailable",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            RadioStationList(stationState, padding, listState, navController, radioStationViewModel)
        }
    }

}

@Composable
fun RadioStationList(
    result: Resource<List<Pair<RadioStationEntity, List<StationAvailabilityEntity>>>>,
    padding: PaddingValues,
    listState: LazyListState,
    navController: NavHostController,
    radioStationViewModel: RadioStationViewModel
) {
    Box(modifier = Modifier.padding(padding)) {
        when (result) {
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = "Radio Stations List" },
                    state = listState
                ) {
                    items(result.data!!) { station ->
                        RadioStationRow(station, navController, radioStationViewModel)
                    }
                    item {
                        if (result.data.isNotEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier.semantics {
                                    contentDescription = "Loading Progress"
                                }
                            )
                        }
                    }
                }
            }

            is Resource.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .semantics { contentDescription = "Loading Progress" })

            is Resource.Error -> BodyText(
                text = "Error: ${(result).message}",
            )

            is Resource.Initial -> {}
        }
    }
}

@Composable
fun RadioStationRow(
    stationPair: Pair<RadioStationEntity, List<StationAvailabilityEntity>>,
    navController: NavHostController,
    radioStationViewModel: RadioStationViewModel
) {
    val (station, availability) = stationPair
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (station.stationuuid != null) {
                    navController.navigate(
                        Destination.StationAvailabilityScreen.createRoute(
                            stationUuid = station.stationuuid
                        )
                    )
                    radioStationViewModel.setSingleStationAvailability(availability)
                }
            }
            .semantics {
                role = Role.Button
                contentDescription = "Radio Station ${station.name},"
            },
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            BodyText(text = "Station: ${station.name?.trim()}")
            BodyText(text = "Country: ${station.country}")
            BodyText(text = "State: ${station.state}")
            BodySmallText(
                text = "Available in: ${availability.size} regions",
                color = Color.Magenta
            )
        }
    }
}
