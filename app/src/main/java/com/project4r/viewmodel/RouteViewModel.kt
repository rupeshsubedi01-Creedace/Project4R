package com.project4r.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project4r.data.model.FlightOffer
import com.project4r.data.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val repository: FlightRepository
) : ViewModel() {

    private val _nlpQuery = MutableStateFlow("")
    val nlpQuery: StateFlow<String> = _nlpQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _flights = MutableStateFlow<List<FlightOffer>>(emptyList())
    val flights: StateFlow<List<FlightOffer>> = _flights.asStateFlow()

    init {
        // Load sample flights on start
        searchFlights("Cheapest flight to Kathmandu")
    }

    fun updateQuery(query: String) {
        _nlpQuery.value = query
    }

    fun searchFlights(query: String) {
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
}
