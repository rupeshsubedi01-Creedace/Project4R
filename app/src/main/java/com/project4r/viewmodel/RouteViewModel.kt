package com.project4r.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project4r.data.UserPreferences
import com.project4r.data.model.FlightOffer
import com.project4r.data.model.PricePoint
import com.project4r.data.repository.FlightRepository
import com.project4r.data.repository.WeatherRepository
import com.project4r.nlp.NLPParser
import com.project4r.util.LocationKit
import com.project4r.util.TimezoneHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: FlightRepository,
    private val weatherRepository: WeatherRepository,
    private val prefs: UserPreferences
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

    // Origin airport — auto-detected from the device's real location,
    // Dubai until a fix says otherwise.
    private val _originIata = MutableStateFlow("DXB")
    val originIata: StateFlow<String> = _originIata.asStateFlow()
    private val _originCity = MutableStateFlow("Dubai")
    val originCity: StateFlow<String> = _originCity.asStateFlow()

    // Trip type: "oneway" or "round" (+ return date) — user's choice.
    private val _tripType = MutableStateFlow("oneway")
    val tripType: StateFlow<String> = _tripType.asStateFlow()

    private val _returnDate = MutableStateFlow(defaultReturnDate())
    val returnDate: StateFlow<String> = _returnDate.asStateFlow()

    // Honest backend note (e.g. "Nepal routes only")
    private val _note = MutableStateFlow<String?>(null)
    val note: StateFlow<String?> = _note.asStateFlow()

    init {
        viewModelScope.launch { repository.note.collect { _note.value = it } }
    }

    fun setTripType(type: String) {
        if (_tripType.value == type) return
        _tripType.value = type
        searchFlights(_nlpQuery.value)
    }

    fun setReturnDate(date: String) {
        _returnDate.value = date
        if (_tripType.value == "round") searchFlights(_nlpQuery.value)
    }

    private fun defaultReturnDate(): String {
        val f = java.time.LocalDate.parse(TimezoneHelper.nextFridayGST())
        return f.plusDays(7).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private val cityNames = mapOf(
        "KTM" to "Kathmandu", "PKR" to "Pokhara", "DXB" to "Dubai",
        "AUH" to "Abu Dhabi", "DEL" to "Delhi", "BOM" to "Mumbai",
        "DOH" to "Doha", "IST" to "Istanbul"
    )

    init {
        // Live one-way search on start (origin = detected location or Dubai)
        searchFlights("Cheapest flight to Kathmandu")
    }

    fun updateQuery(query: String) {
        _nlpQuery.value = query
    }

    /** Auto-locate: nearest major airport becomes the origin, then re-search. */
    fun detectLocation(context: Context) {
        viewModelScope.launch {
            val airport = LocationKit.detect(context) ?: return@launch
            if (airport.iata == _originIata.value) return@launch
            _originIata.value = airport.iata
            _originCity.value = airport.city
            val q = _nlpQuery.value
            val explicitOrigin = NLPParser.parse(q).origin
            val newQuery = when {
                q.isBlank() || explicitOrigin == null ->
                    if (airport.iata == "KTM") "Kathmandu to Dubai" else "${airport.city} to Kathmandu"
                else -> q
            }
            _nlpQuery.value = newQuery
            searchFlights(newQuery)
        }
    }

    fun searchFlights(query: String) {
        val destination = NLPParser.parse(query).destination ?: "KTM"
        loadWeather(destination)
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchFlights(
                query,
                _originIata.value,
                _tripType.value,
                _returnDate.value
            )
                .catch { _isLoading.value = false }
                .collect { results ->
                    _flights.value = results
                    _isLoading.value = false
                    persistPricePoint(results)
                }
        }
    }

    /** Remember the cheapest live fare seen — feeds the Review screen. */
    private fun persistPricePoint(results: List<FlightOffer>) {
        if (results.isEmpty()) return
        val first = results.first()
        val best  = results.minBy { it.priceAed }
        try {
            val gson = Gson()
            val list = gson.fromJson(prefs.priceHistoryJson, Array<PricePoint>::class.java)
                ?.toMutableList() ?: mutableListOf()
            list.add(
                PricePoint(
                    route    = "${first.origin} \u2192 ${first.destination}",
                    date     = first.date,
                    minPrice = best.priceAed,
                    airline  = best.airlineName
                )
            )
            prefs.priceHistoryJson = gson.toJson(list.takeLast(90))
        } catch (_: Exception) { /* history is best-effort */ }
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
