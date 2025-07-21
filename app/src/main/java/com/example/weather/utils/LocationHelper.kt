package com.example.weather.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.example.weather.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationHelper(
    private val activity: Activity,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val weatherViewModel: WeatherViewModel
) {

    fun fetchWeatherFromCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location ?: return@addOnSuccessListener

                val geocoder = Geocoder(activity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addresses.isNullOrEmpty()) return@addOnSuccessListener

                val address = addresses[0]
                val rawCity = listOf(
                    address.locality,
                    address.subAdminArea,
                    address.adminArea,
                    address.featureName,
                    address.countryName
                ).firstOrNull { !it.isNullOrBlank() }

                val city = when {
                    rawCity?.contains("Çanakkale", ignoreCase = true) == true -> "Çanakkale"
                    rawCity != null -> rawCity.split(" ").firstOrNull() ?: rawCity
                    else -> null
                }

                if (!city.isNullOrBlank()) {
                    weatherViewModel.setLoading(true)
                    weatherViewModel.fetchWeatherForCity(city)
                }
            }
    }
}