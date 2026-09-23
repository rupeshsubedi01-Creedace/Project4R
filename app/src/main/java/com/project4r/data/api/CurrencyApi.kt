package com.project4r.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * ExchangeRate-API — keyless public mirror.
 * Base URL: https://open.er-api.com/v6/
 *
 * No API key required. Same provider and schema family as
 * exchangerate-api.com, served on the free open endpoint.
 */
data class CurrencyApiResponse(
    val result: String,                      // "success" or "error"
    @SerializedName("base_code")
    val baseCode: String?,                   // "AED"
    @SerializedName(value = "rates", alternate = ["conversion_rates"])
    val rates: Map<String, Double>?          // AED-based conversion rates
)

interface CurrencyApi {
    @GET("latest/{base}")
    suspend fun getRates(
        @Path("base") base: String = "AED"
    ): CurrencyApiResponse
}
