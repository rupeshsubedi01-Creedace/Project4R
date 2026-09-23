package com.project4r.data.repository

import com.project4r.data.api.OpenMeteoCurrent
import com.project4r.data.api.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    // Airport coordinates for the routes this app cares about.
    private val coords = mapOf(
        "KTM" to Pair(27.6970, 85.3590),   // Kathmandu
        "PKR" to Pair(28.2009, 83.9821),   // Pokhara
        "DXB" to Pair(25.2532, 55.3657),   // Dubai
        "AUH" to Pair(24.4329, 54.6511),   // Abu Dhabi
        "DEL" to Pair(28.5562, 77.1000),   // Delhi
        "BOM" to Pair(19.0896, 72.8686),   // Mumbai
        "DOH" to Pair(25.2731, 51.6084),   // Doha
        "IST" to Pair(41.2753, 28.7519)    // Istanbul
    )

    /** Current weather at destination, or null when unavailable. */
    suspend fun currentFor(iata: String): OpenMeteoCurrent? {
        return try {
            val c = coords[iata.uppercase()] ?: return null
            api.current(c.first, c.second).current
        } catch (e: Exception) {
            null
        }
    }
}
