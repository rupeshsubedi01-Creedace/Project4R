package com.project4r.data.model

/**
 * A real observed price point: the cheapest live fare seen for a route
 * at the moment the user searched. Persisted locally, never mocked.
 */
data class PricePoint(
    val route: String,
    val date: String,     // ISO date of the search
    val minPrice: Int,    // cheapest live AED fare seen
    val airline: String
)
