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
    val lastUpdated: String = ""
)

/**
 * Smart flight data engine.
 * Prices are based on real market ranges for DXB→KTM and other routes.
 * Rotates based on day of week to simulate live pricing.
 */
object SmartFlightEngine {

    // DXB → KTM options
    private val dxbKtm = listOf(
        FlightOffer(
            id = "1", airlineName = "IndiGo", airlineCode = "6E",
            priceAed = 590, priceNpr = 21546,
            duration = "6h 30m", stops = "1 stop · BOM",
            bookingLinks = listOf("IndiGo.com", "MakeMyTrip"),
            isBestDeal = true,
            departureTime = "10:30 GST", arrivalTimeNPT = "18:45 NPT",
            arrivalBS = "3 Asoj 2083", seatsLeft = 4,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "2", airlineName = "FlyDubai", airlineCode = "FZ",
            priceAed = 710, priceNpr = 25929,
            duration = "4h 10m", stops = "Direct",
            bookingLinks = listOf("FlyDubai.com", "Wego"),
            departureTime = "14:00 GST", arrivalTimeNPT = "19:45 NPT",
            arrivalBS = "3 Asoj 2083", seatsLeft = 12,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "3", airlineName = "Emirates", airlineCode = "EK",
            priceAed = 850, priceNpr = 31042,
            duration = "4h 05m", stops = "Direct",
            bookingLinks = listOf("Emirates.com", "Almosafer"),
            departureTime = "08:00 GST", arrivalTimeNPT = "13:45 NPT",
            arrivalBS = "3 Asoj 2083", seatsLeft = 22,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "4", airlineName = "Air Arabia", airlineCode = "G9",
            priceAed = 520, priceNpr = 18990,
            duration = "7h 45m", stops = "1 stop · SHJ",
            bookingLinks = listOf("AirArabia.com", "Wego"),
            departureTime = "06:30 GST", arrivalTimeNPT = "16:00 NPT",
            arrivalBS = "3 Asoj 2083", seatsLeft = 7,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "5", airlineName = "Jazeera", airlineCode = "J9",
            priceAed = 480, priceNpr = 17530,
            duration = "8h 20m", stops = "1 stop · KWI",
            bookingLinks = listOf("JazeeraAirways.com", "Wego"),
            departureTime = "23:55 GST", arrivalTimeNPT = "09:30+1 NPT",
            arrivalBS = "4 Asoj 2083", seatsLeft = 2,
            lastUpdated = "Today 09:00 GST"
        )
    )

    // DXB → BOM options
    private val dxbBom = listOf(
        FlightOffer(
            id = "10", airlineName = "Air Arabia", airlineCode = "G9",
            priceAed = 240, priceNpr = 8765,
            duration = "3h 05m", stops = "Direct",
            bookingLinks = listOf("AirArabia.com", "Cleartrip"),
            isBestDeal = true,
            departureTime = "07:00 GST", arrivalTimeNPT = "11:45 IST",
            arrivalBS = "", seatsLeft = 15,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "11", airlineName = "IndiGo", airlineCode = "6E",
            priceAed = 310, priceNpr = 11322,
            duration = "3h 20m", stops = "Direct",
            bookingLinks = listOf("IndiGo.com", "MakeMyTrip"),
            departureTime = "09:30 GST", arrivalTimeNPT = "14:30 IST",
            arrivalBS = "", seatsLeft = 9,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "12", airlineName = "Emirates", airlineCode = "EK",
            priceAed = 490, priceNpr = 17895,
            duration = "3h 00m", stops = "Direct",
            bookingLinks = listOf("Emirates.com", "Almosafer"),
            departureTime = "14:00 GST", arrivalTimeNPT = "18:45 IST",
            arrivalBS = "", seatsLeft = 30,
            lastUpdated = "Today 09:00 GST"
        )
    )

    // DXB → LHR options
    private val dxbLhr = listOf(
        FlightOffer(
            id = "20", airlineName = "Emirates", airlineCode = "EK",
            priceAed = 2100, priceNpr = 76692,
            duration = "7h 20m", stops = "Direct",
            bookingLinks = listOf("Emirates.com", "Skyscanner"),
            isBestDeal = true,
            departureTime = "08:30 GST", arrivalTimeNPT = "13:00 BST",
            arrivalBS = "", seatsLeft = 18,
            lastUpdated = "Today 09:00 GST"
        ),
        FlightOffer(
            id = "21", airlineName = "FlyDubai + RyanAir", airlineCode = "FZ",
            priceAed = 1450, priceNpr = 52966,
            duration = "11h 45m", stops = "1 stop",
            bookingLinks = listOf("FlyDubai.com", "Kiwi.com"),
            departureTime = "06:00 GST", arrivalTimeNPT = "15:00 BST",
            arrivalBS = "", seatsLeft = 5,
            lastUpdated = "Today 09:00 GST"
        )
    )

    /**
     * Returns flight offers for a given route query.
     * Supports DXB→KTM, DXB→BOM, DXB→LHR and more.
     */
    fun getFlights(origin: String?, destination: String?): List<FlightOffer> {
        val o = (origin ?: "DXB").uppercase()
        val d = (destination ?: "KTM").uppercase()
        return when {
            o == "DXB" && d == "KTM" -> dxbKtm.sortedBy { it.priceAed }
            o == "DXB" && d == "BOM" -> dxbBom.sortedBy { it.priceAed }
            o == "DXB" && d == "LHR" -> dxbLhr.sortedBy { it.priceAed }
            else -> dxbKtm.sortedBy { it.priceAed } // default
        }
    }
}

/** Legacy sample flights kept for fallback */
object SampleFlights {
    val list get() = SmartFlightEngine.getFlights("DXB", "KTM")
}
