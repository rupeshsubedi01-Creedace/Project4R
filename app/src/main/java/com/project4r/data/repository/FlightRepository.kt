package com.project4r.data.repository

import com.project4r.BuildConfig
import com.project4r.data.api.AmadeusApi
import com.project4r.data.model.FlightOffer
import com.project4r.data.model.SampleFlights
import com.project4r.nlp.NLPParser
import com.project4r.util.TimezoneHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val api: AmadeusApi
) {
    private var cachedToken: String = ""

    /**
     * Search flights using a natural language query.
     * Parses the query via NLPParser, then calls Amadeus API.
     * Falls back to sample data if keys are missing.
     */
    fun searchFlights(nlpQuery: String): Flow<List<FlightOffer>> = flow {
        if (BuildConfig.AMADEUS_CLIENT_ID.isBlank()) {
            // No API key — emit sample data
            emit(SampleFlights.list)
            return@flow
        }

        try {
            val intent = NLPParser.parse(nlpQuery)
            if (cachedToken.isBlank()) {
                val tokenResp = api.getToken(
                    clientId = BuildConfig.AMADEUS_CLIENT_ID,
                    clientSecret = BuildConfig.AMADEUS_CLIENT_SECRET
                )
                cachedToken = tokenResp.access_token
            }

            val resp = api.searchFlights(
                bearer = "Bearer $cachedToken",
                origin = intent.origin ?: "DXB",
                destination = intent.destination ?: "KTM",
                date = intent.date ?: TimezoneHelper.nextFridayGST()
            )

            val offers = resp.data.mapIndexed { idx, raw ->
                val priceAed = raw.price.grandTotal.toDoubleOrNull()?.toInt() ?: 0
                val itinerary = raw.itineraries.firstOrNull()
                FlightOffer(
                    id = raw.id,
                    airlineName = raw.validatingAirlineCodes.firstOrNull() ?: "-",
                    airlineCode = raw.validatingAirlineCodes.firstOrNull() ?: "-",
                    priceAed = priceAed,
                    priceNpr = (priceAed * 36.52).toInt(),
                    duration = itinerary?.duration ?: "-",
                    stops = if ((itinerary?.segments?.size ?: 1) > 1) "1 stop" else "Direct",
                    bookingLinks = listOf("Amadeus"),
                    isBestDeal = idx == 0
                )
            }
            emit(offers)
        } catch (e: Exception) {
            emit(SampleFlights.list)
        }
    }
}
