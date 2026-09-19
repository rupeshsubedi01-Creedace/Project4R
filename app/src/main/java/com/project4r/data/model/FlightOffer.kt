package com.project4r.data.model

/**
 * Represents a single flight offer shown in RouteScreen.
 */
data class FlightOffer(
    val id: String,
    val airlineName: String,
    val airlineCode: String,
    val priceAed: Int,
    val priceNpr: Int,
    val duration: String,
    val stops: String,        // e.g. "Direct" or "1 stop"
    val bookingLinks: List<String>,
    val isBestDeal: Boolean = false,
    val departureTime: String = "",
    val arrivalTimeNPT: String = "",  // arrival in Asia/Kathmandu
    val arrivalBS: String = ""        // Bikram Sambat date
)

/** Hardcoded sample data used before API is wired up */
object SampleFlights {
    val list = listOf(
        FlightOffer(
            id = "1", airlineName = "IndiGo", airlineCode = "6E",
            priceAed = 590, priceNpr = 21546,
            duration = "6h 30m", stops = "1 stop",
            bookingLinks = listOf("IndiGo.com", "MakeMyTrip"),
            isBestDeal = true,
            departureTime = "10:30 GST", arrivalTimeNPT = "18:45 NPT",
            arrivalBS = "3 Asoj 2083"
        ),
        FlightOffer(
            id = "2", airlineName = "FlyDubai", airlineCode = "FZ",
            priceAed = 710, priceNpr = 25929,
            duration = "4h 10m", stops = "Direct",
            bookingLinks = listOf("FlyDubai.com", "Wego"),
            departureTime = "14:00 GST", arrivalTimeNPT = "19:45 NPT",
            arrivalBS = "3 Asoj 2083"
        ),
        FlightOffer(
            id = "3", airlineName = "Emirates", airlineCode = "EK",
            priceAed = 850, priceNpr = 31042,
            duration = "4h 05m", stops = "Direct",
            bookingLinks = listOf("Emirates.com", "Almosafer"),
            departureTime = "08:00 GST", arrivalTimeNPT = "13:45 NPT",
            arrivalBS = "3 Asoj 2083"
        ),
        FlightOffer(
            id = "4", airlineName = "Air Arabia", airlineCode = "G9",
            priceAed = 920, priceNpr = 33598,
            duration = "7h 45m", stops = "1 stop",
            bookingLinks = listOf("AirArabia.com", "Wego"),
            departureTime = "06:30 GST", arrivalTimeNPT = "16:00 NPT",
            arrivalBS = "3 Asoj 2083"
        )
    )
}
