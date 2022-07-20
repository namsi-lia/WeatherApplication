package com.example.weatherapplication.service

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.weatherapp.service.dto.currentWeather
import com.example.weatherapp.service.dto.FullWeather
import com.example.weatherapplication.BuildConfig
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: OpenWeatherService,
    private val application: Application,
) {
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getCurrentWeather(location: LatLng): Flow<currentWeather?> {
        Log.d("fake tag", "getCurrentWeather: ${BuildConfig.API_KEY}")
        return flow {
            val currentWeather = service.getCurrentWeather(
                location.latitude,
                location.longitude,
                BuildConfig.API_KEY,
            ).body()
            emit(currentWeather)
        }.flowOn(Dispatchers.IO)
    }


    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getcurrentDailyForecast(): Flow<FullWeather> {
        return locationFlow().map {
            service.getFullWeather(it.latitude, it.longitude, BuildConfig.API_KEY).body()
        }.filterNotNull()
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow {
        val client = LocationServices.getFusedLocationProviderClient(application)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation)

            }

        }
        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }

    }


}


