package com.example.weather

import WeatherViewModelFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.weather.data.remote.RetrofitInstance
import com.example.weather.screens.WeatherScreen
import com.example.weather.ui.theme.WeatherTheme
import com.example.weather.utils.LocationHelper
import com.example.weather.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(RetrofitInstance.api)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            WeatherTheme {
                WeatherScreen(weatherViewModel)
            }
        }

        val locationHelper = LocationHelper(this, fusedLocationClient, weatherViewModel)
        locationHelper.fetchWeatherFromCurrentLocation()
    }
}
















