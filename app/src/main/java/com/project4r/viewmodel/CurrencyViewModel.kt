package com.project4r.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project4r.data.model.CurrencyRate
import com.project4r.data.repository.CurrencyRepository
import com.project4r.nlp.NLPParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _nlpQuery = MutableStateFlow("")
    val nlpQuery: StateFlow<String> = _nlpQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _rates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val rates: StateFlow<List<CurrencyRate>> = _rates.asStateFlow()

    private val _convertedResult = MutableStateFlow<String?>(null)
    val convertedResult: StateFlow<String?> = _convertedResult.asStateFlow()

    init {
        loadRates()
    }

    fun updateQuery(q: String) { _nlpQuery.value = q }

    private fun loadRates() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLiveRates()
                .catch { _isLoading.value = false }
                .collect {
                    _rates.value = it
                    _isLoading.value = false
                }
        }
    }

    fun convert(query: String) {
        val intent = NLPParser.parse(query)
        val amount  = intent.amount  ?: return
        val fromCur = intent.fromCurrency ?: "AED"
        val toCur   = intent.toCurrency   ?: "NPR"
        val rate = _rates.value.find { it.code == toCur }?.value ?: run {
            _convertedResult.value = "Rate not available for $toCur"
            return
        }
        val converted = (amount * rate * 100.0).roundToInt() / 100.0
        _convertedResult.value = "$amount $fromCur = $converted $toCur"
    }
}
