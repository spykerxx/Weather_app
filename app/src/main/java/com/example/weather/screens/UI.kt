package com.example.weather.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.R
import com.example.weather.data.model.WeatherInfo
import com.example.weather.viewmodel.WeatherViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@OptIn(ExperimentalPagerApi::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }

    // State collectors
    val weatherList by weatherViewModel.weatherList.collectAsState()
    val isLoading by weatherViewModel.isLoading.collectAsState()
    val hasRequested by weatherViewModel.hasRequestedInitialWeather.collectAsState()

    val pagerState = rememberPagerState()

    // Determine background based on time (night/day)
    val isNight = weatherList.firstOrNull()?.isNight ?: false
    val backgroundImage = if (isNight)
        painterResource(id = R.drawable.nightime)
    else
        painterResource(id = R.drawable.daytime)

    // Fullscreen container
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Background image
            Image(
                painter = backgroundImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Foreground content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // App title
                Text(
                    "Weather Forecast",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search city") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (searchQuery.isNotBlank()) {
                                weatherViewModel.fetchWeatherForCity(searchQuery.trim())
                                focusManager.clearFocus()
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "Search"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotBlank()) {
                                weatherViewModel.fetchWeatherForCity(searchQuery.trim())
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Main content logic
                when {
                    !hasRequested -> {
                        Spacer(modifier = Modifier.height(100.dp)) // App just started
                    }
                    isLoading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .padding(32.dp)
                                .size(48.dp)
                        )
                    }
                    weatherList.isEmpty() -> {
                        Text(
                            "No weather data found for this city.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    else -> {
                        HorizontalPager(
                            count = weatherList.size,
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { page ->
                            WeatherInfoCard(
                                info = weatherList[page],
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            activeColor = Color.White,
                            inactiveColor = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}



@Composable
fun WeatherInfoCard(info: WeatherInfo, modifier: Modifier = Modifier) {
    val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, MMM d"))

    Card(
        modifier = modifier.padding(horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCCFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = info.city,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = info.iconRes),
                    contentDescription = info.description,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${info.temperature}°C",
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = info.description,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Existing main details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDetailItem(label = "Feels like", value = "${info.feelsLike}°C")
                WeatherDetailItem(label = "Humidity", value = "${info.humidity}%")
                WeatherDetailItem(label = "Wind", value = "${info.windSpeed} km/h")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // New details grid with two columns
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailItem(label = "Pressure", value = "${info.pressure} hPa")
                    WeatherDetailItem(label = "UV Index", value = "${info.uvIndex}")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherDetailItem(label = "Visibility", value = "${info.visibility / 1000} km")
                    WeatherDetailItem(label = "Sunrise", value = info.sunrise)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    WeatherDetailItem(label = "Sunset", value = info.sunset)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
    }
}



