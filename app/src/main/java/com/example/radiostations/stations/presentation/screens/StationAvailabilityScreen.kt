package com.example.radiostations.stations.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.radiostations.Destination
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.StationAvailabilityEntity
import com.example.radiostations.core.presentaion.components.BodyText
import com.example.radiostations.core.presentaion.components.HeaderLargeText
import com.example.radiostations.stations.presentation.viewmodel.StationAvailabilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationAvailabilityScreen(
    stationUuid: String,
    availabilityViewModel: StationAvailabilityViewModel,
    navController: NavHostController
) {

    availabilityViewModel.getStationAvailability(stationUuid)
    val availabilityState = availabilityViewModel.availability.collectAsState().value

    if(availabilityState == null) {
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
         StationAvailability(result = availabilityState, paddingValues = padding)
    }
}

@Composable
fun StationAvailability(
    result: Resource<List<StationAvailabilityEntity>>,
    paddingValues: PaddingValues
) {
    when(result) {
        is Resource.Success -> {
            LazyColumn(Modifier.padding(paddingValues)) {
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
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            BodyText(text = stationAvailability.name!!)
            Box(Modifier.height(8.dp))
            if (stationAvailability.ok == 1) BodyText(text = "Online", color = Color.Green) else BodyText(
                text = "offline" , color = Color.Red
            )
        }
    }
}

