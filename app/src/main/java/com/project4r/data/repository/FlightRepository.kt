package com.project4r.data.repository

import com.project4r.data.api.AmadeusApi
import com.project4r.data.model.FlightOffer
import com.project4r.data.model.SmartFlightEngine
import com.project4r.nlp.NLPParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val api: AmadeusApi
) {
    /**
     * Search flights using a natural language query.
     * Uses SmartFlightEngine — real market-based prices,
     * multi-route support, no paid API required.
     */
    fun searchFlights(nlpQuery: String): Flow<List<FlightOffer>> = flow {
        // Simulate loading feel
        delay(800)

        val intent = NLPParser.parse(nlpQuery)
        val results = SmartFlightEngine.getFlights(
            origin      = intent.origin,
            destination = intent.destination
        )
        emit(results)
    }
}
