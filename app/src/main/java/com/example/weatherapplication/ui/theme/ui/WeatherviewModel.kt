package com.example.weatherapplication.ui.theme.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.service.dto.FullWeather
import com.example.weatherapplication.service.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class WeatherviewModel @Inject constructor(repository: WeatherRepository) : ViewModel() {
    private val location = MutableSharedFlow<LatLng>()

    val current = location.transformLatest { location ->
        val flow = repository.getCurrentWeather(location).onStart {
            Log.i(TAG, "Loading weather updates for $location...")
        }
        emitAll(flow)
    }.catch {
        Log.e(TAG, "Error : ${it.localizedMessage}", it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val forecast: Flow<FullWeather> = repository.getcurrentDailyForecast()

    fun setLocation(location: LatLng) {
        viewModelScope.launch {
            this@WeatherviewModel.location.emit(location)
        }
    }

    private companion object {
        const val TAG = "WeatherviewModel"
    }
}

