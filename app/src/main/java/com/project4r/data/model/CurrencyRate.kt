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
