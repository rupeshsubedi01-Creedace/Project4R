package com.project4r.data.api

import retrofit2.http.GET
import retrofit2.http.Query

// ---- SerpAPI Google Flights response models ----

data class SerpFlightsResponse(
    val best_flights: List<SerpFlightGroup>?,
    val other_flights: List<SerpFlightGroup>?,
    val price_insights: SerpPriceInsights?
)

data class SerpFlightGroup(
    val flights: List<SerpFlightSegment>,
    val layovers: List<SerpLayover>?,
    val total_duration: Int,   // minutes
    val price: Int,            // in requested currency (AED)
    val type: String?
)

data class SerpFlightSegment(
    val departure_airport: SerpAirport,
    val arrival_airport: SerpAirport,
    val duration: Int,         // minutes
    val airline: String,
    val flight_number: String,
    val overnight: Boolean?
)

data class SerpAirport(
    val name: String,
    val id: String,    // IATA code
    val time: String   // "2026-09-26 08:00"
)

data class SerpLayover(
    val name: String,
    val id: String,
    val duration: Int
)

data class SerpPriceInsights(
    val lowest_price: Int?,
    val typical_price_range: List<Int>?
)

interface SerpApi {
    @GET("search")
    suspend fun searchFlights(
        @Query("engine")          engine: String = "google_flights",
        @Query("departure_id")    origin: String,
        @Query("arrival_id")      destination: String,
        @Query("outbound_date")   date: String,
        @Query("currency")        currency: String = "AED",
        @Query("hl")              language: String = "en",
        @Query("type")            type: Int = 2,      // 1=round trip, 2=one way
        @Query("adults")          adults: Int = 1,
        @Query("api_key")         apiKey: String
    ): SerpFlightsResponse
}
