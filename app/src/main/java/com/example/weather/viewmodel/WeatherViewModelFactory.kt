import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.viewmodel.WeatherViewModel

class WeatherViewModelFactory(private val api: WeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
