package com.example.weather.viewmodel

import WeatherApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.R
import com.example.weather.data.model.WeatherInfo
import com.example.weather.data.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class WeatherViewModel(
    private val api: WeatherApi // Inject API dependency here
) : ViewModel() {

    private val _weatherList = MutableStateFlow<List<WeatherInfo>>(emptyList())
    val weatherList: StateFlow<List<WeatherInfo>> = _weatherList

    private val apiKey = "9e5865fdad34a67733729d9f1ee89f33"

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _hasRequestedInitialWeather = MutableStateFlow(false)
    val hasRequestedInitialWeather: StateFlow<Boolean> = _hasRequestedInitialWeather

    private fun mapResponseToWeatherInfo(response: WeatherResponse): WeatherInfo {
        val now = Instant.now().epochSecond
        val isNight = now < response.sys.sunrise || now > response.sys.sunset

        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val zoneOffset = ZoneOffset.ofTotalSeconds(response.timezone)
        val sunrise = Instant.ofEpochSecond(response.sys.sunrise).atOffset(zoneOffset).format(formatter)
        val sunset = Instant.ofEpochSecond(response.sys.sunset).atOffset(zoneOffset).format(formatter)

        return WeatherInfo(
            city = response.name,
            temperature = response.main.temp.toInt(),
            feelsLike = response.main.feels_like.toInt(),
            humidity = response.main.humidity,
            windSpeed = response.wind.speed.toInt(),
            pressure = response.main.pressure,
            uvIndex = 0f,
            visibility = response.visibility,
            sunrise = sunrise,
            sunset = sunset,
            description = response.weather[0].description.replaceFirstChar { it.uppercase() },
            iconRes = mapWeatherIcon(response.weather[0].icon),
            isNight = isNight
        )
    }

    private fun mapWeatherIcon(iconCode: String): Int {
        val isNight = iconCode.endsWith("n")

        return when (iconCode) {
            "01d" -> if (isNight) R.drawable.moon else R.drawable.sun
            "01n" -> R.drawable.moon
            "02d", "03d", "04d" -> R.drawable.cloud
            "09d", "10d" -> R.drawable.rain
            "09n", "10n" -> R.drawable.rain
            "11d" -> R.drawable.thunderstorm
            "11n" -> R.drawable.thunderstorm
            "13d" -> R.drawable.snow
            "13n" -> R.drawable.snow
            "50d" -> R.drawable.fog
            "50n" -> R.drawable.fog
            else -> if (isNight) R.drawable.moon else R.drawable.sun
        }
    }

    fun fetchWeatherForCity(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasRequestedInitialWeather.value = true
            try {
                val response = api.getWeatherByCity(city, apiKey)
                val info = mapResponseToWeatherInfo(response)
                _weatherList.value = listOf(info)
            } catch (e: Exception) {
                println("Failed to fetch weather for $city: ${e.message}")
                _weatherList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}
