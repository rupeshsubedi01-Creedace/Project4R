package com.project4r.nlp

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Lightweight on-device NLP parser.
 *
 * Extracts structured intents from natural language queries such as:
 *  - "Cheapest flight to Kathmandu next Friday under AED 700"
 *  - "Convert 590 AED to NPR"
 *  - "Alert me when IndiGo drops below AED 500"
 *  - "Remind me about Dashain 3 weeks before"
 */
object NLPParser {

    // ---- Airport / city mappings ----
    private val cityToIata = mapOf(
        "kathmandu" to "KTM", "ktm" to "KTM",
        "dubai" to "DXB",     "dxb" to "DXB",
        "mumbai" to "BOM",    "bombay" to "BOM",
        "london" to "LHR",
        "delhi" to "DEL",     "new delhi" to "DEL",
        "doha" to "DOH",
        "singapore" to "SIN",
        "bangkok" to "BKK",
    )

    // ---- Currency codes ----
    private val currencyWords = mapOf(
        "aed" to "AED", "dirham" to "AED", "dirhams" to "AED",
        "npr" to "NPR", "rupee" to "NPR",  "rupees" to "NPR",
        "usd" to "USD", "dollar" to "USD", "dollars" to "USD",
        "inr" to "INR",
        "eur" to "EUR", "euro" to "EUR",
        "gbp" to "GBP", "pound" to "GBP", "pounds" to "GBP",
        "jpy" to "JPY", "yen" to "JPY",
    )

    fun parse(raw: String): ParsedIntent {
        val text = raw.lowercase().trim()

        return ParsedIntent(
            origin       = extractOrigin(text),
            destination  = extractDestination(text),
            date         = extractDate(text),
            maxPriceAed  = extractMaxPrice(text),
            amount       = extractAmount(text),
            fromCurrency = extractFromCurrency(text),
            toCurrency   = extractToCurrency(text),
            airline      = extractAirline(text),
            intent       = classifyIntent(text)
        )
    }

    // ---- Intent classification ----
    private fun classifyIntent(text: String): Intent {
        return when {
            text.contains("convert") || text.contains(" to ") && hasCurrency(text) -> Intent.CONVERT
            text.contains("alert") || text.contains("notify") || text.contains("remind") -> Intent.REMIND
            text.contains("trend") || text.contains("history") || text.contains("review") -> Intent.REVIEW
            else -> Intent.SEARCH_FLIGHT
        }
    }

    private fun hasCurrency(text: String) = currencyWords.keys.any { text.contains(it) }

    // ---- Destination extraction ----
    private fun extractDestination(text: String): String? {
        val patterns = listOf("to ", "towards ", "going to ", "fly to ", "flight to ")
        for (p in patterns) {
            val idx = text.indexOf(p)
            if (idx >= 0) {
                val rest = text.substring(idx + p.length).split(" ", ",", ".", "on", "next").first().trim()
                cityToIata[rest]?.let { return it }
            }
        }
        return null
    }

    // ---- Origin extraction ----
    private fun extractOrigin(text: String): String? {
        val patterns = listOf("from ", "depart from ", "leaving ")
        for (p in patterns) {
            val idx = text.indexOf(p)
            if (idx >= 0) {
                val rest = text.substring(idx + p.length).split(" ", ",", ".").first().trim()
                cityToIata[rest]?.let { return it }
            }
        }
        return null
    }

    // ---- Date extraction ----
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun extractDate(text: String): String? {
        val dubai = ZoneId.of("Asia/Dubai")
        val today = LocalDate.now(dubai)
        return when {
            text.contains("tomorrow")    -> today.plusDays(1).format(formatter)
            text.contains("next friday") -> nextWeekday(today, DayOfWeek.FRIDAY).format(formatter)
            text.contains("next monday") -> nextWeekday(today, DayOfWeek.MONDAY).format(formatter)
            text.contains("next week")   -> today.plusWeeks(1).format(formatter)
            text.contains("next month")  -> today.plusMonths(1).format(formatter)
            else -> null
        }
    }

    private fun nextWeekday(from: LocalDate, day: DayOfWeek): LocalDate {
        var d = from.plusDays(1)
        while (d.dayOfWeek != day) d = d.plusDays(1)
        return d
    }

    // ---- Price extraction ----
    private val priceRegex = Regex("""(?:under|below|less than|cheaper than|max|aed)?\s*(\d+)\s*(?:aed|dirhams?)?""")
    private fun extractMaxPrice(text: String): Int? {
        val m = priceRegex.find(text) ?: return null
        return m.groupValues[1].toIntOrNull()
    }

    // ---- Amount extraction ----
    private val amountRegex = Regex("""(\d+(?:\.\d+)?)""")
    private fun extractAmount(text: String): Double? =
        amountRegex.find(text)?.groupValues?.get(1)?.toDoubleOrNull()

    // ---- Currency extraction ----
    private fun extractFromCurrency(text: String): String? {
        val amountIdx = amountRegex.find(text)?.range?.last ?: return null
        val afterAmount = text.substring(amountIdx + 1).trim()
        for ((word, code) in currencyWords) {
            if (afterAmount.startsWith(word)) return code
        }
        return null
    }

    private fun extractToCurrency(text: String): String? {
        val toIdx = text.lastIndexOf(" to ")
        if (toIdx < 0) return null
        val after = text.substring(toIdx + 4).trim()
        for ((word, code) in currencyWords) {
            if (after.startsWith(word)) return code
        }
        return null
    }

    // ---- Airline extraction ----
    private val airlines = mapOf(
        "indigo" to "IndiGo", "6e" to "IndiGo",
        "flydubai" to "FlyDubai", "fz" to "FlyDubai",
        "emirates" to "Emirates", "ek" to "Emirates",
        "air arabia" to "Air Arabia", "g9" to "Air Arabia",
    )
    private fun extractAirline(text: String): String? {
        return airlines.entries.firstOrNull { (k, _) -> text.contains(k) }?.value
    }
}

enum class Intent { SEARCH_FLIGHT, CONVERT, REMIND, REVIEW }

data class ParsedIntent(
    val intent: Intent,
    val origin: String?,
    val destination: String?,
    val date: String?,
    val maxPriceAed: Int?,
    val amount: Double?,
    val fromCurrency: String?,
    val toCurrency: String?,
    val airline: String?
)
