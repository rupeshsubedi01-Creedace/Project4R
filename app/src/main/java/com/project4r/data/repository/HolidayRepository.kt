package com.project4r.data.repository

import com.project4r.data.api.CaldaysHoliday
import com.project4r.data.api.HolidayApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepository @Inject constructor(
    private val api: HolidayApi
) {
    /** Live Nepal public holidays for the current year (caldays.com, keyless). */
    fun nepalHolidays(): Flow<List<CaldaysHoliday>> = flow {
        try {
            val resp = api.getHolidays("np")
            emit((resp.holidays ?: emptyList()).sortedBy { it.date })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
