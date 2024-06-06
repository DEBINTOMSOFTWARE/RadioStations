package com.example.radiostations.stations.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.radiostations.core.utils.Resource
import com.example.radiostations.stations.domain.model.RadioStationEntity
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import kotlinx.coroutines.flow.map

@Composable
fun RadioStationScreen(
    navController: NavHostController,
    radioStationViewModel: RadioStationViewModel
) {
    val stationState by radioStationViewModel.stations.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(8.dp)) {
        when (val result = stationState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Success -> {
                ShowRadioStations(result = result, navController = navController, viewModel = radioStationViewModel)
            }

            is Resource.Error -> {
                Text(text = "Error: ${(result as Resource.Error).message}")
            }

            else -> {}
        }
    }
}

@Composable
fun ShowRadioStations(
    result: Resource.Success<List<RadioStationEntity>>,
    navController: NavHostController,
    viewModel: RadioStationViewModel
) {
    val stations = result.data ?: emptyList()
    val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            //modifier = Modifier.semantics { contentDescription = "Radio Stations List" },
            contentPadding = PaddingValues(8.dp)
        ) {
            items(stations) { station ->
                RadioStation(station = station, navController = navController)
            }
        }
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo }.map {
                    layoutInfo ->
                layoutInfo.visibleItemsInfo.lastOrNull()?.index
            }.collect { lastVisibleItemIndex ->
                println("Last visible item index: $lastVisibleItemIndex")
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= stations.size - 5) {
                    println("Triggering load more at index $lastVisibleItemIndex")
                    viewModel.getStations()
                }
            }
        }

}

@Composable
fun RadioStation(
    station: RadioStationEntity?, navController: NavHostController
) {
    val stationName = station?.name
    val stationState = station?.state
    val stationCountry = station?.country
    val stationCountryCode = station?.countrycode
    val stationId = station?.stationuuid

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.LightGray)
            .background(Color.White)
            .padding(16.dp)
            .wrapContentHeight()
            .clickable {

            }
            .semantics {
                role = Role.Button
                contentDescription = "Radio Station $stationName"
            }

    ) {
        Text(
            text = stationName ?: "", style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stationState ?: "", style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stationCountry ?: "", style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

    }
}