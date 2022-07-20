package com.example.weatherapplication.service

import com.example.weatherapp.service.dto.currentWeather
import com.example.weatherapp.service.dto.FullWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("weather?units=metric")
    suspend fun getCurrentWeather(
        @Query("lat")lat:Double,
        @Query("lon")long:Double,
        @Query("appid")appid:String,
    ):Response<currentWeather>

    @GET("onecall?units=metric&exclude=daily,minutely,hourly,alerts")
    suspend fun getFullWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") appid: String,
    ): Response<FullWeather>
}
