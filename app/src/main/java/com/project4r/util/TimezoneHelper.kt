package com.project4r.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Timezone utilities.
 *
 * Dubai (GST)  = UTC+4  → ZoneId "Asia/Dubai"
 * Kathmandu    = UTC+5:45 → ZoneId "Asia/Kathmandu"
 */
object TimezoneHelper {
    private val dubai = ZoneId.of("Asia/Dubai")
    private val ktm   = ZoneId.of("Asia/Kathmandu")
    private val fmt   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** Returns the date string (YYYY-MM-DD) for the next Friday in Dubai time */
    fun nextFridayGST(): String {
        var d = LocalDate.now(dubai).plusDays(1)
        while (d.dayOfWeek != DayOfWeek.FRIDAY) d = d.plusDays(1)
        return d.format(fmt)
    }

    fun todayGST(): String = LocalDate.now(dubai).format(fmt)
    fun todayKTM(): String = LocalDate.now(ktm).format(fmt)
}
