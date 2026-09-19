package com.project4r.util

import java.time.LocalDate

/**
 * Converts Gregorian dates to Bikram Sambat (Nepali calendar).
 *
 * The BS calendar is ~56 years 8–9 months ahead of Gregorian.
 * This implementation uses a lookup table for accuracy (2000–2090 AD).
 *
 * Usage:
 *   BiksramSambat.toBS(LocalDate.of(2026, 9, 19)) → "3 Asoj 2083"
 */
object BiksramSambat {

    private val nepaliMonths = listOf(
        "Baisakh", "Jestha", "Ashadh",
        "Shrawan", "Bhadra", "Asoj",
        "Kartik", "Mangsir", "Poush",
        "Magh", "Falgun", "Chaitra"
    )

    /**
     * Days in each BS month per BS year.
     * Index 0 = BS year 2000; each sub-array has 12 month lengths.
     */
    private val bsDays = arrayOf(
        intArrayOf(30,32,31,32,31,30,30,30,29,30,29,31), // 2000
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2001
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2002
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2003
        intArrayOf(30,32,31,32,31,30,30,30,29,30,29,31), // 2004
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2005
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2006
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2007
        intArrayOf(31,31,31,32,31,31,29,30,29,30,29,31), // 2008
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2009
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2010
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2011
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2012
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2013
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2014
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2015
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2016
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2017
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2018
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2019
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2020
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2021
        intArrayOf(31,32,31,32,31,30,30,29,30,29,30,30), // 2022
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,30), // 2023
        intArrayOf(31,31,31,32,31,31,30,29,30,29,30,30), // 2024
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2025
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2026
        intArrayOf(30,32,31,32,31,30,30,30,29,30,29,31), // 2027
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2028
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2029
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2030
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2031
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2032
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2033
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2034
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2035
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2036
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2037
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2038
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2039
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2040
        intArrayOf(31,32,31,32,31,30,30,29,30,29,30,30), // 2041
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2042
        intArrayOf(31,31,31,32,31,31,29,30,30,29,30,30), // 2043
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2044
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2045
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31), // 2046
        intArrayOf(31,31,31,32,31,31,30,29,30,29,30,30), // 2047
        intArrayOf(31,31,32,31,31,31,30,29,30,29,30,30), // 2048
        intArrayOf(31,31,32,32,31,30,30,29,30,29,30,30), // 2049
        intArrayOf(31,32,31,32,31,30,30,30,29,29,30,31)  // 2050
    )

    // Reference point: 2000 BS 1 Baisakh = 1943 April 14 (Gregorian)
    private val refGregorian = LocalDate.of(1943, 4, 14)
    private val refBS = Triple(2000, 1, 1) // year, month (1-based), day

    /**
     * Convert a Gregorian [LocalDate] to a Bikram Sambat string like "3 Asoj 2083".
     */
    fun toBS(date: LocalDate): String {
        var diff = date.toEpochDay() - refGregorian.toEpochDay()
        if (diff < 0) return "Date before BS 2000"

        var bsYear = 2000; var bsMonth = 0; var bsDay = 1

        outer@ for (y in bsDays.indices) {
            for (m in 0..11) {
                val days = bsDays[y][m]
                if (diff < days) {
                    bsYear = 2000 + y
                    bsMonth = m
                    bsDay = diff.toInt() + 1
                    break@outer
                }
                diff -= days
            }
        }
        return "$bsDay ${nepaliMonths[bsMonth]} $bsYear"
    }

    /** Today in BS calendar (Kathmandu timezone) */
    fun todayBS(): String = toBS(LocalDate.now(java.time.ZoneId.of("Asia/Kathmandu")))
}
