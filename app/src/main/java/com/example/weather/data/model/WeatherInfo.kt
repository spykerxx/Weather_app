package com.example.weather.data.model

data class WeatherInfo(
    val city: String,
    val temperature: Int,
    val feelsLike: Int,
    val humidity: Int,
    val windSpeed: Int,
    val pressure: Int,       // hPa
    val uvIndex: Float,
    val visibility: Int,     // meters
    val sunrise: String,     // formatted time e.g. "6:12 AM"
    val sunset: String,      // formatted time e.g. "7:45 PM"
    val description: String,
    val iconRes: Int,
    val isNight: Boolean
)