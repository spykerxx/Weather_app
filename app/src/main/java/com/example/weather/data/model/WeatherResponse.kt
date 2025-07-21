package com.example.weather.data.model

data class WeatherResponse(
    val name: String,          // city name
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val sys: Sys,
    val visibility: Int,
    val timezone: Int // offset in seconds from UTC
)