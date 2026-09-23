package com.project4r.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project4r.data.model.FlightOffer
import com.project4r.data.repository.FlightRepository
import com.project4r.data.repository.WeatherRepository
import com.project4r.nlp.NLPParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: FlightRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _nlpQuery = MutableStateFlow("")
    val nlpQuery: StateFlow<String> = _nlpQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _flights = MutableStateFlow<List<FlightOffer>>(emptyList())
    val flights: StateFlow<List<FlightOffer>> = _flights.asStateFlow()

    // "☀️ 24°C in Kathmandu" — live, keyless (Open-Meteo)
    private val _weather = MutableStateFlow<String?>(null)
    val weather: StateFlow<String?> = _weather.asStateFlow()

    private val cityNames = mapOf(
        "KTM" to "Kathmandu", "PKR" to "Pokhara", "DXB" to "Dubai",
        "AUH" to "Abu Dhabi", "DEL" to "Delhi", "BOM" to "Mumbai",
        "DOH" to "Doha", "IST" to "Istanbul"
    )

    init {
        // Load sample flights on start
        searchFlights("Cheapest flight to Kathmandu")
    }

    fun updateQuery(query: String) {
        _nlpQuery.value = query
    }

    fun searchFlights(query: String) {
        val destination = NLPParser.parse(query).destination ?: "KTM"
        loadWeather(destination)
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchFlights(query)
                .catch { _isLoading.value = false }
                .collect { results ->
                    _flights.value = results
                    _isLoading.value = false
                }
        }
    }

    private fun loadWeather(iata: String) {
        viewModelScope.launch {
            val current = weatherRepository.currentFor(iata) ?: return@launch
            val temp = current.temperature ?: return@launch
            val emoji = weatherEmoji(current.weatherCode ?: 0)
            val city = cityNames[iata.uppercase()] ?: iata
            _weather.value = "$emoji ${temp.toInt()}°C in $city · live"
        }
    }

    private fun weatherEmoji(code: Int): String = when {
        code == 0                      -> "☀️"          // ☀️
        code <= 2                      -> "\uD83C\uDF24\uFE0F" // 🌤️
        code == 3                      -> "⛅"          // ⛅
        code == 45 || code == 48       -> "\uD83C\uDF2B\uFE0F" // 🌫️
        code in 51..67                 -> "\uD83C\uDF27\uFE0F" // 🌧️
        code in 71..77 || code in 85..86 -> "\uD83C\uDF28\uFE0F" // 🌨️
        code in 80..82                 -> "\uD83C\uDF27\uFE0F" // 🌧️
        code >= 95                     -> "⛈️"          // ⛈️
        else                           -> "☁️"          // ☁️
    }
}
