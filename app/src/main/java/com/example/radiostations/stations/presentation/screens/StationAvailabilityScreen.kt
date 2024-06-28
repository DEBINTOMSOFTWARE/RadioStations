package com.example.radiostations.stations.presentation.screens

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.core.presentaion.components.BodyText
import com.example.radiostations.core.presentaion.components.HeaderLargeText
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import com.example.radiostations.stations.presentation.viewmodel.StationAvailabilityViewModel

@Composable
fun StationAvailabilityScreen(
    stationUuid: String,
    radioStationViewModel: RadioStationViewModel,
    availabilityViewModel: StationAvailabilityViewModel,
    navController: NavHostController,
) {
    println("StationUuid:: $stationUuid")
    //val stationAvailability = radioStationViewModel.singleStationAvailability.collectAsState().value

    availabilityViewModel.getStationAvailability(stationUuid)
    val availabilityState = availabilityViewModel.availability.collectAsState().value
    val networkAvailable =
        availabilityViewModel.networkAvailable.observe()
            .collectAsState(ConnectivityObservable.Status.Available)

    if (availabilityState == null) {
        navController.navigate(Destination.StationsScreen.route) {
            popUpTo(Destination.StationsScreen.route)
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(60.dp)
                    .semantics { contentDescription = "Available Stations" },
                title = { HeaderLargeText(text = "Available Stations") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() },
                        modifier = Modifier.semantics {
                            role = Role.Button
                            contentDescription = "Go back"
                        }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            )
        },
    ) { padding ->
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

        StationAvailability(result = availabilityState, paddingValues = padding)
    }
}

@Composable
fun StationAvailability(
    result: Resource<List<StationAvailabilityEntity>>,
    paddingValues: PaddingValues
) {
    when (result) {
        is Resource.Success -> {
            LazyColumn(
                Modifier
                    .padding(paddingValues)
                    .semantics {
                        contentDescription = "Availability List"
                    }) {
                items(result.data!!) { stationAvailability ->
                    StationAvailabilityRow(stationAvailability)
                }
            }
        }

        is Resource.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.semantics { contentDescription = "Loading Progress" }
            )
        }

        is Resource.Error -> {
            BodyText(
                text = "Error: ${(result).message}",
            )
        }

        is Resource.Initial -> {}
    }
}

@Composable
fun StationAvailabilityRow(
    stationAvailability: StationAvailabilityEntity,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight()
            .semantics {
                role = Role.Button
                contentDescription = "Station Available ${stationAvailability.name}"
            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            println("Name:: ${stationAvailability.name!!}")
            BodyText(text = stationAvailability.name!!)
            Box(Modifier.height(8.dp))
            if (stationAvailability.ok == 1) BodyText(
                text = "Online",
                color = Color.Green
            ) else BodyText(
                text = "offline", color = Color.Red
            )
        }
    }
}

