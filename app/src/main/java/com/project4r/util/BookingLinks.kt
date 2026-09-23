package com.project4r.util

/**
 * Turns a booking-chip label ("IndiGo.com", "Wego", "Google Flights"...)
 * into a real, tappable deep link for the given route and date.
 */
object BookingLinks {

    fun urlFor(label: String, origin: String, destination: String, date: String): String {
        val o = origin.uppercase()
        val d = destination.uppercase()
        val lo = o.lowercase()
        val ld = d.lowercase()
        val skyscanner =
            if (date.isBlank()) "https://www.skyscanner.net/transport/flights/$lo/$ld/"
            else "https://www.skyscanner.net/transport/flights/$lo/$ld/$date/"

        return when {
            label.contains("IndiGo",   true) -> "https://www.goindigo.in/"
            label.contains("MakeMyTrip",true)-> "https://www.makemytrip.com/"
            label.contains("FlyDubai", true) -> "https://www.flydubai.com/"
            label.contains("Wego",     true) -> "https://flights.wego.com/"
            label.contains("Google",   true) ->
                "https://www.google.com/travel/flights?q=Flights+from+$o+to+$d+on+$date"
            label.contains("AirIndia", true) -> "https://www.airindia.in/"
            label.contains("Emirates", true) -> "https://www.emirates.com/"
            label.contains("Almosafer",true) -> "https://www.almosafer.com/"
            label.contains("AirArabia",true) -> "https://www.airarabia.com/"
            label.contains("Jazeera",  true) -> "https://www.jazeeraairways.com/"
            label.contains("Nepal",    true) -> "https://nepalairlines.com.np/"
            label.contains("Himalaya", true) -> "https://www.himalaya-airlines.com/"
            label.contains("Kiwi",     true) -> "https://www.kiwi.com/"
            label.contains("Cleartrip",true) -> "https://www.cleartrip.com/"
            label.contains("Skyscanner",true)-> skyscanner
            else -> skyscanner
        }
    }
}
