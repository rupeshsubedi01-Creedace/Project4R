package com.project4r.data.api

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * caldays.com — public holidays for 195+ countries.
 * Base URL: https://caldays.com/
 * Keyless, CORS-enabled, data licensed CC BY 4.0.
 *
 * GET /api/holidays/{cc}  e.g. /api/holidays/np  ->  Nepal, current year
 */
data class CaldaysHoliday(
    val date: String,     // "2026-01-14"
    val name: String      // "Maghe Sankranti"
)

data class CaldaysResponse(
    val code: String?,
    val country: String?,
    val year: Int?,
    val count: Int?,
    val holidays: List<CaldaysHoliday>?
)

interface HolidayApi {
    @GET("api/holidays/{cc}")
    suspend fun getHolidays(
        @Path("cc") countryCode: String = "np"
    ): CaldaysResponse
}
