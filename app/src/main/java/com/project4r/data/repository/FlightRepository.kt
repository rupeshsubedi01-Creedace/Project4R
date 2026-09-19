package com.project4r.data.repository

import com.project4r.data.api.SerpApi
import com.project4r.data.model.FlightOffer
import com.project4r.data.model.SmartFlightEngine
import com.project4r.nlp.NLPParser
import com.project4r.util.TimezoneHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val serpApi: SerpApi
) {
    // ❗ Paste your SerpAPI key here after getting it from serpapi.com
    private val serpApiKey = "PASTE_YOUR_SERPAPI_KEY_HERE"

    fun searchFlights(nlpQuery: String): Flow<List<FlightOffer>> = flow {
        val intent = NLPParser.parse(nlpQuery)
        val origin      = intent.origin      ?: "DXB"
        val destination = intent.destination ?: "KTM"
        val date        = intent.date        ?: TimezoneHelper.nextFridayGST()

        if (serpApiKey == "PASTE_YOUR_SERPAPI_KEY_HERE" || serpApiKey.isBlank()) {
            // No key yet — use smart local data
            delay(800)
            emit(SmartFlightEngine.getFlights(origin, destination))
            return@flow
        }

        try {
            val resp = serpApi.searchFlights(
                origin      = origin,
                destination = destination,
                date        = date,
                apiKey      = serpApiKey
            )

            val allGroups = (resp.best_flights ?: emptyList()) +
                            (resp.other_flights ?: emptyList())

            if (allGroups.isEmpty()) {
                emit(SmartFlightEngine.getFlights(origin, destination))
                return@flow
            }

            val offers = allGroups.mapIndexed { idx, group ->
                val firstSeg = group.flights.first()
                val lastSeg  = group.flights.last()
                val stops    = when (group.flights.size) {
                    1    -> "Direct"
                    2    -> "1 stop"
                    else -> "${group.flights.size - 1} stops"
                }
                val durationHrs  = group.total_duration / 60
                val durationMins = group.total_duration % 60
                val priceAed = group.price
                val priceNpr = (priceAed * 36.52).toInt()

                // Booking links based on airline
                val links = bookingLinksFor(firstSeg.airline)

                FlightOffer(
                    id           = "serp_$idx",
                    airlineName  = firstSeg.airline,
                    airlineCode  = firstSeg.flight_number.take(2),
                    priceAed     = priceAed,
                    priceNpr     = priceNpr,
                    duration     = "${durationHrs}h ${durationMins}m",
                    stops        = stops,
                    bookingLinks = links,
                    isBestDeal   = idx == 0,
                    departureTime = formatTime(firstSeg.departure_airport.time) + " GST",
                    arrivalTimeNPT = formatTime(lastSeg.arrival_airport.time) + " NPT",
                    seatsLeft    = 0,
                    lastUpdated  = "Live • Google Flights"
                )
            }.sortedBy { it.priceAed }

            emit(offers)

        } catch (e: Exception) {
            // Fallback to smart local data on any error
            emit(SmartFlightEngine.getFlights(origin, destination))
        }
    }

    private fun formatTime(dateTime: String): String {
        // "2026-09-26 08:00" → "08:00"
        return dateTime.split(" ").getOrNull(1) ?: dateTime
    }

    private fun bookingLinksFor(airline: String): List<String> {
        return when {
            airline.contains("IndiGo",   ignoreCase = true) -> listOf("IndiGo.com", "MakeMyTrip")
            airline.contains("Emirates", ignoreCase = true) -> listOf("Emirates.com", "Almosafer")
            airline.contains("FlyDubai", ignoreCase = true) -> listOf("FlyDubai.com", "Wego")
            airline.contains("Arabia",   ignoreCase = true) -> listOf("AirArabia.com", "Wego")
            airline.contains("Jazeera",  ignoreCase = true) -> listOf("JazeeraAirways.com", "Wego")
            airline.contains("Nepal",    ignoreCase = true) -> listOf("NepalAirlines.com")
            else -> listOf("Google Flights", "Wego")
        }
    }
}
