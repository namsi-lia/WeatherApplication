package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.runtime.*

import com.example.weatherapplication.ui.theme.WeatherApplicationTheme
import com.example.weatherapplication.ui.theme.ui.LocationNotAvailable
import com.example.weatherapplication.ui.theme.ui.WeatherviewModel
import com.example.weatherapplication.ui.theme.ui.screenWeather
import com.example.weatherapplication.ui.theme.ui.utils.MyUpdatedLocationArgs
import com.example.weatherapplication.ui.theme.ui.utils.rememberMyUpdatedLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherviewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherApplicationTheme{
                var shouldRequestLocationPermission by remember {
                    mutableStateOf(false)
                }
                val args = MyUpdatedLocationArgs(
                    onMyUpdatedLocationChanged = viewModel::setLocation,
                    shouldRequestPermission = shouldRequestLocationPermission,
                ) {

                }
                val myUpdatedLocation = rememberMyUpdatedLocation(args = args)
                if (myUpdatedLocation.isLocationPermissionGranted) {
                    screenWeather(viewModel)
                } else {
                    LocationNotAvailable(onContinueClick = {
                        shouldRequestLocationPermission = true
                    })
                }


            }
        }

    }
}
