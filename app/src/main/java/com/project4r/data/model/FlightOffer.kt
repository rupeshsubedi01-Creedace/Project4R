package com.project4r.data.model

data class FlightOffer(
    val id: String,
    val airlineName: String,
    val airlineCode: String,
    val priceAed: Int,
    val priceNpr: Int,
    val duration: String,
    val stops: String,
    val bookingLinks: List<String>,
    val isBestDeal: Boolean = false,
    val departureTime: String = "",
    val arrivalTimeNPT: String = "",
    val arrivalBS: String = "",
    val seatsLeft: Int = 0,
    val lastUpdated: String = "",
    val origin: String = "DXB",
    val destination: String = "KTM",
    val date: String = ""
)
