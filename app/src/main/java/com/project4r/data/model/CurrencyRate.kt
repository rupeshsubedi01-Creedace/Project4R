package com.project4r.data.model

/**
 * A single currency rate against AED.
 */
data class CurrencyRate(
    val code: String,
    val name: String,
    val flag: String,
    val value: Double,
    val trend: String   // e.g. "up +0.3%" or "down -0.1%"
)

/** Offline fallback rates (Sept-2026 snapshot) used only if the live API fails. */
object SampleRates {
    val list = listOf(
        CurrencyRate("NPR", "Nepali Rupee",   "🇳🇵", 41.68, "fallback"),
        CurrencyRate("USD", "US Dollar",       "🇺🇸",  0.27, "fallback"),
        CurrencyRate("INR", "Indian Rupee",    "🇮🇳", 26.05, "fallback"),
        CurrencyRate("EUR", "Euro",            "🇪🇺",  0.24, "fallback"),
        CurrencyRate("GBP", "British Pound",   "🇬🇧",  0.20, "fallback"),
        CurrencyRate("JPY", "Japanese Yen",    "🇯🇵", 42.85, "fallback"),
    )
}
