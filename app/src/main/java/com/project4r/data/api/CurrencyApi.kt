package com.project4r.data.api

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * ExchangeRate-API
 * Base URL: https://v6.exchangerate-api.com/v6/{apiKey}/
 */
data class CurrencyApiResponse(
    val result: String,           // "success" or "error"
    val base_code: String,        // "AED"
    val conversion_rates: Map<String, Double>
)

interface CurrencyApi {
    @GET("{apiKey}/latest/{base}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("base")   base: String = "AED"
    ): CurrencyApiResponse
}
