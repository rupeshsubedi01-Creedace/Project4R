package com.project4r.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Project 4R Vercel backend API.
 * Base URL is your Vercel deployment URL, e.g.:
 *   https://project4r-api.vercel.app/
 *
 * After deploying, update BASE_URL in AppModule.kt.
 */

data class VercelFlightResponse(
    val origin: String,
    val destination: String,
    val date: String,
    val count: Int,
    val flights: List<VercelFlight>,
    val priceInsights: VercelPriceInsights?
)

data class VercelFlight(
    val id: String,
    val airlineName: String,
    val airlineCode: String,
    val flightNumber: String?,
    val priceAed: Int,
    val priceNpr: Int,
    val duration: String,
    val stops: String,
    val isBestDeal: Boolean,
    val departure: VercelEndpoint,
    val arrival: VercelEndpoint,
    val bookingLinks: List<String>,
    val source: String
)

data class VercelEndpoint(
    val airport: String,
    val time: String
)

data class VercelPriceInsights(
    val lowest_price: Int?,
    val typical_price_range: List<Int>?
)

interface VercelFlightApi {
    @GET("api/flights")
    suspend fun searchFlights(
        @Query("origin")      origin: String,
        @Query("destination") destination: String,
        @Query("date")        date: String,
        @Query("adults")      adults: Int = 1
    ): VercelFlightResponse
}
