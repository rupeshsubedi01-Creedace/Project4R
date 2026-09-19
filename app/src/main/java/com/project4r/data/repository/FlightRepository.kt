package com.project4r.data.repository

import com.project4r.data.api.VercelFlightApi
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
    private val vercelApi: VercelFlightApi
) {
    fun searchFlights(nlpQuery: String): Flow<List<FlightOffer>> = flow {
        val intent      = NLPParser.parse(nlpQuery)
        val origin      = intent.origin      ?: "DXB"
        val destination = intent.destination ?: "KTM"
        val date        = intent.date        ?: TimezoneHelper.nextFridayGST()

        try {
            val resp = vercelApi.searchFlights(
                origin      = origin,
                destination = destination,
                date        = date
            )

            if (resp.flights.isEmpty()) {
                emit(SmartFlightEngine.getFlights(origin, destination))
                return@flow
            }

            val offers = resp.flights.map { f ->
                FlightOffer(
                    id            = f.id,
                    airlineName   = f.airlineName,
                    airlineCode   = f.airlineCode,
                    priceAed      = f.priceAed,
                    priceNpr      = f.priceNpr,
                    duration      = f.duration,
                    stops         = f.stops,
                    bookingLinks  = f.bookingLinks,
                    isBestDeal    = f.isBestDeal,
                    departureTime = formatTime(f.departure.time) + " GST",
                    arrivalTimeNPT = formatTime(f.arrival.time) + " NPT",
                    lastUpdated   = f.source
                )
            }
            emit(offers)

        } catch (e: Exception) {
            // Vercel not deployed yet — use smart local data
            delay(800)
            emit(SmartFlightEngine.getFlights(origin, destination))
        }
    }

    private fun formatTime(dateTime: String): String =
        dateTime.split(" ").getOrNull(1) ?: dateTime
}
