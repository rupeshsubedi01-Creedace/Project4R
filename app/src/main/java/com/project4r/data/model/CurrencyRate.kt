package com.project4r.data.model

/**
 * A single currency rate against AED.
 */
data class CurrencyRate(
    val code: String,
    val name: String,
    val flag: String,
    val value: Double,
    val trend: String   // e.g. "↑ +0.3%" or "↓ -0.1%"
)

/** Hardcoded sample rates used before ExchangeRate-API is wired up */
object SampleRates {
    val list = listOf(
        CurrencyRate("NPR", "Nepali Rupee",   "🇳🇵", 36.52, "↑ +0.3%"),
        CurrencyRate("USD", "US Dollar",       "🇺🇸",  0.272, "↓ -0.1%"),
        CurrencyRate("INR", "Indian Rupee",    "🇮🇳", 22.68, "↑ +0.2%"),
        CurrencyRate("EUR", "Euro",            "🇪🇺",  0.251, "↓ -0.2%"),
        CurrencyRate("GBP", "British Pound",   "🇬🇧",  0.214, "→  0.0%"),
        CurrencyRate("JPY", "Japanese Yen",    "🇯🇵", 40.21, "↑ +0.4%"),
    )
}
