package com.project4r.data.repository

import com.project4r.data.api.VercelFlightApi
import com.project4r.data.model.FlightOffer
import com.project4r.nlp.NLPParser
import com.project4r.util.TimezoneHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val vercelApi: VercelFlightApi
) {
    /** Backend note (e.g. "Nepal routes only") — honest messaging, never fake flights. */
    private val _note = MutableStateFlow<String?>(null)
    val note: StateFlow<String?> = _note.asStateFlow()

    /**
     * Live flights only — one-way or round trip.
     * Emits an empty list when nothing real is available.
     */
    fun searchFlights(
        nlpQuery: String,
        defaultOrigin: String = "DXB",
        trip: String = "oneway",
        returnDate: String = ""
    ): Flow<List<FlightOffer>> = flow {
        val intent      = NLPParser.parse(nlpQuery)
        val origin      = intent.origin      ?: defaultOrigin
        val destination = intent.destination ?: "KTM"
        val date        = intent.date        ?: TimezoneHelper.nextFridayGST()

        try {
            val resp = vercelApi.searchFlights(
                origin      = origin,
                destination = destination,
                date        = date,
                trip        = trip,
                returnDate  = returnDate
            )
            _note.value = resp.note

            val offers = resp.flights.map { f ->
                FlightOffer(
                    id             = f.id,
                    airlineName    = f.airlineName,
                    airlineCode    = f.airlineCode,
                    priceAed       = f.priceAed,
                    priceNpr       = f.priceNpr,
                    duration       = f.duration,
                    stops          = f.stops,
                    returnStops    = f.returnStops,
                    returnDuration = f.returnDuration,
                    bookingLinks   = f.bookingLinks,
                    isBestDeal     = f.isBestDeal,
                    departureTime  = formatTime(f.departure.time) + " GST",
                    arrivalTimeNPT = formatTime(f.arrival.time) + " NPT",
                    lastUpdated    = f.source,
                    origin         = resp.origin,
                    destination    = resp.destination,
                    date           = resp.date
                )
            }
            emit(offers)
        } catch (e: Exception) {
            // Real data only: no fake fallback flights
            _note.value = null
            emit(emptyList())
        }
    }

    private fun formatTime(dateTime: String): String =
        dateTime.split(" ").getOrNull(1) ?: dateTime
}
