package com.project4r.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Detects the device's real location and maps it to the nearest major
 * airport (IATA + city). Framework APIs only — no external service, no
 * mock data. Returns null when no fix is available so callers can keep
 * their current default.
 */
object LocationKit {

    data class Airport(val iata: String, val city: String, val lat: Double, val lon: Double)

    private val airports = listOf(
        Airport("DXB", "Dubai",         25.2532,  55.3657),
        Airport("AUH", "Abu Dhabi",     24.4330,  54.6511),
        Airport("SHJ", "Sharjah",       25.3286,  55.5172),
        Airport("DOH", "Doha",          25.2731,  51.6080),
        Airport("RUH", "Riyadh",        24.9576,  46.6988),
        Airport("JED", "Jeddah",        21.6796,  39.1565),
        Airport("BAH", "Manama",        26.2708,  50.6336),
        Airport("KWI", "Kuwait City",   29.2266,  47.9805),
        Airport("MCT", "Muscat",        23.5933,  58.2844),
        Airport("KTM", "Kathmandu",     27.6966,  85.3591),
        Airport("PKR", "Pokhara",       28.2009,  83.9821),
        Airport("DEL", "Delhi",         28.5665,  77.1031),
        Airport("BOM", "Mumbai",        19.0896,  72.8656),
        Airport("BLR", "Bengaluru",     13.7836,  77.7010),
        Airport("HYD", "Hyderabad",     17.2313,  78.4298),
        Airport("AMD", "Ahmedabad",     23.0772,  72.6347),
        Airport("CMB", "Colombo",        7.1808,  79.8841),
        Airport("SIN", "Singapore",      1.3644, 103.9915),
        Airport("KUL", "Kuala Lumpur",   2.7456, 101.7099),
        Airport("BKK", "Bangkok",       13.6900, 100.7501),
        Airport("IST", "Istanbul",      41.2753,  28.7519),
        Airport("LHR", "London",        51.4700,  -0.4543),
        Airport("CDG", "Paris",         49.0097,   2.5479),
        Airport("FRA", "Frankfurt",     50.0379,   8.5622),
        Airport("JFK", "New York",      40.6413, -73.7781),
        Airport("SYD", "Sydney",       -33.9399, 151.1753),
        Airport("NRT", "Tokyo",         35.7653, 140.3856)
    )

    fun nearest(lat: Double, lon: Double): Airport =
        airports.minBy { haversineKm(lat, lon, it.lat, it.lon) }

    @SuppressLint("MissingPermission")
    suspend fun detect(context: Context): Airport? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        // 1) Best last-known fix across enabled providers
        var best: Location? = null
        for (p in lm.getProviders(true)) {
            val l = try { lm.getLastKnownLocation(p) } catch (_: Exception) { null }
            if (l != null && (best == null || l.time > best.time)) best = l
        }

        // 2) No cached fix → ask for one fresh update (5 s budget)
        if (best == null) {
            best = withTimeoutOrNull(5000) {
                suspendCancellableCoroutine<Location?> { cont ->
                    val listener = object : LocationListener {
                        override fun onLocationChanged(l: Location) {
                            if (cont.isActive) cont.resume(l)
                        }
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }
                    var started = false
                    for (p in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
                        if (lm.allProviders.contains(p)) {
                            try {
                                lm.requestSingleUpdate(p, listener, Looper.getMainLooper())
                                started = true
                            } catch (_: Exception) { }
                        }
                    }
                    if (!started && cont.isActive) cont.resume(null)
                    cont.invokeOnCancellation {
                        try { lm.removeUpdates(listener) } catch (_: Exception) { }
                    }
                }
            }
        }

        return best?.let { nearest(it.latitude, it.longitude) }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return 2 * r * atan2(sqrt(a), sqrt(1 - a))
    }
}
