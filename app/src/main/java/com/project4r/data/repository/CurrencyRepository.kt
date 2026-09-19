package com.project4r.data.repository

import com.project4r.BuildConfig
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
        if (BuildConfig.EXCHANGE_API_KEY.isBlank()) {
            emit(SampleRates.list)
            return@flow
        }
        try {
            val resp = api.getRates(apiKey = BuildConfig.EXCHANGE_API_KEY)
            val rates = wantedCodes.mapNotNull { code ->
                val value = resp.conversion_rates[code] ?: return@mapNotNull null
                CurrencyRate(
                    code = code,
                    name = nameMap[code] ?: code,
                    flag = flagMap[code] ?: "",
                    value = Math.round(value * 100.0) / 100.0,
                    trend = "→  0.0%" // trend requires historical data; placeholder
                )
            }
            emit(rates)
        } catch (e: Exception) {
            emit(SampleRates.list)
        }
    }
}
