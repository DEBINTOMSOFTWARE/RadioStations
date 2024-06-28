package com.example.radiostations

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radiostations.stations.presentation.screens.RadioStationScreen
import com.example.radiostations.stations.presentation.screens.StationAvailabilityScreen
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import com.example.radiostations.stations.presentation.viewmodel.StationAvailabilityViewModel
import com.example.radiostations.ui.theme.RadioStationsTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Destination(val route: String) {
    data object StationsScreen : Destination("stationsScreen")

    data object StationAvailabilityScreen : Destination("stationAvailabilityScreen/{stationUuid}") {
        fun createRoute(stationUuid: String?) = "stationAvailabilityScreen/$stationUuid"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stationsViewModel by viewModels<RadioStationViewModel>()
    private val availabilityViewModel by viewModels<StationAvailabilityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadioStationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        radioStationViewModel = stationsViewModel,
                        availabilityViewModel = availabilityViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    radioStationViewModel: RadioStationViewModel,
    availabilityViewModel: StationAvailabilityViewModel,

    ) {
    NavHost(navController = navController, startDestination = Destination.StationsScreen.route) {
        composable(Destination.StationsScreen.route) {
            RadioStationScreen(radioStationViewModel = radioStationViewModel, navController)
        }
        composable(
            route = Destination.StationAvailabilityScreen.route,
            arguments = listOf(navArgument("stationUuid") { type = NavType.StringType })) { navBackStackEntry ->
            val stationUuid = navBackStackEntry.arguments?.getString("stationUuid")
            if (stationUuid == null) {
                Toast.makeText(
                    LocalContext.current,
                    "Station Id required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                StationAvailabilityScreen(
                    stationUuid = stationUuid,
                    radioStationViewModel = radioStationViewModel,
                    availabilityViewModel = availabilityViewModel,
                    navController = navController
                )
            }

        }
    }
}


