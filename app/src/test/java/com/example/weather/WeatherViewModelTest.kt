import com.example.weather.data.model.*
import com.example.weather.data.remote.RetrofitInstance
import com.example.weather.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import org.mockito.kotlin.eq
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel

    @Mock
    private lateinit var mockApi: WeatherApi

    private lateinit var closeable: AutoCloseable
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this)  // fix uninitialized closeable
        viewModel = WeatherViewModel(mockApi)           // pass mockApi to ViewModel
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        closeable.close()
    }

    @Test
    fun `fetchWeatherForCity updates weatherList with correct data`() = runTest {
        val city = "Istanbul"
        val weatherResponse = WeatherResponse(
            name = city,
            main = Main(temp = 25f, feels_like = 27f, pressure = 1012, humidity = 60),
            weather = listOf(Weather(description = "clear sky", icon = "01d")),
            wind = Wind(speed = 5.5f),
            sys = Sys(sunrise = 1627884000L, sunset = 1627934400L),
            visibility = 10000,
            timezone = 10800
        )

        whenever(mockApi.getWeatherByCity(eq(city), eq("9e5865fdad34a67733729d9f1ee89f33"), any())).thenReturn(weatherResponse)


        viewModel.fetchWeatherForCity(city)
        advanceUntilIdle()

        val weatherList = viewModel.weatherList.first()
        assertEquals(1, weatherList.size)

        val info = weatherList[0]
        assertEquals(city, info.city)
        assertEquals(25, info.temperature)
        assertEquals("Clear sky", info.description)
        assertEquals(10000, info.visibility)
        assert(info.iconRes != 0) { "Icon resource should not be zero" }
    }

    @Test
    fun `fetchWeatherForCity completes with isLoading false`() = runTest {
        val city = "Istanbul"
        val weatherResponse = WeatherResponse(
            name = city,
            main = Main(temp = 25f, feels_like = 27f, pressure = 1012, humidity = 60),
            weather = listOf(Weather(description = "clear sky", icon = "01d")),
            wind = Wind(speed = 5.5f),
            sys = Sys(sunrise = 1627884000L, sunset = 1627934400L),
            visibility = 10000,
            timezone = 10800
        )

        whenever(mockApi.getWeatherByCity(eq(city), eq("9e5865fdad34a67733729d9f1ee89f33"), any()))
            .thenReturn(weatherResponse)

        // Start fetching weather
        viewModel.fetchWeatherForCity(city)

        // Advance coroutines until all jobs are finished
        advanceUntilIdle()

        // Now loading should be false after the job finishes
        assertEquals(false, viewModel.isLoading.value)

        // Also optionally check that weatherList was updated
        val weatherList = viewModel.weatherList.first()
        assertEquals(1, weatherList.size)
        assertEquals(city, weatherList[0].city)
    }



}
