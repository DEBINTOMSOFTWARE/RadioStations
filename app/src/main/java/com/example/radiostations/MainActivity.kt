package com.example.radiostations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.radiostations.stations.presentation.screens.RadioStationScreen
import com.example.radiostations.stations.presentation.viewmodel.RadioStationViewModel
import com.example.radiostations.ui.theme.RadioStationsTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Destination(val route: String) {
    data object StationsScreen : Destination("stationsScreen")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stationsViewModel by viewModels<RadioStationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            RadioStationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        radioStationViewModel = stationsViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    radioStationViewModel: RadioStationViewModel
) {
    NavHost(navController = navController, startDestination = Destination.StationsScreen.route) {
        composable(Destination.StationsScreen.route) {
            RadioStationScreen(radioStationViewModel = radioStationViewModel)
        }
    }
}


