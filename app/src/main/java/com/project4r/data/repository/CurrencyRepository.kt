package com.project4r.data.repository

import com.project4r.data.api.CurrencyApi
import com.project4r.data.model.CurrencyRate
import com.project4r.data.model.SampleRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val api: CurrencyApi
) {
    // Keyless public endpoint — no API key needed, nothing to leak.

    private val wantedCodes = listOf("NPR", "USD", "INR", "EUR", "GBP", "JPY")
    private val flagMap = mapOf(
        "NPR" to "🇳🇵", "USD" to "🇺🇸", "INR" to "🇮🇳",
        "EUR" to "🇪🇺", "GBP" to "🇬🇧", "JPY" to "🇯🇵"
    )
    private val nameMap = mapOf(
        "NPR" to "Nepali Rupee", "USD" to "US Dollar", "INR" to "Indian Rupee",
        "EUR" to "Euro",         "GBP" to "British Pound", "JPY" to "Japanese Yen"
    )

    fun getLiveRates(): Flow<List<CurrencyRate>> = flow {
        try {
            val resp = api.getRates()
            val rates = resp.rates
            if (resp.result == "success" && rates != null) {
                val list = wantedCodes.mapNotNull { code ->
                    val value = rates[code] ?: return@mapNotNull null
                    val rounded = Math.round(value * 100.0) / 100.0
                    CurrencyRate(
                        code  = code,
                        name  = nameMap[code] ?: code,
                        flag  = flagMap[code] ?: "",
                        value = rounded,
                        trend = "→  Live"
                    )
                }
                emit(list)
            } else {
                emit(SampleRates.list)
            }
        } catch (e: Exception) {
            // Fall back to sample data on any error
            emit(SampleRates.list)
        }
    }
}
