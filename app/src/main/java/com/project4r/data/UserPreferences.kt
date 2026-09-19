package com.project4r.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("project4r_prefs", Context.MODE_PRIVATE)

    // Whether onboarding has been completed
    var onboardingDone: Boolean
        get()      = prefs.getBoolean("onboarding_done", false)
        set(value) = prefs.edit().putBoolean("onboarding_done", value).apply()

    // Work country (where user lives/earns)
    var workCountryName: String
        get()      = prefs.getString("work_country_name", "United Arab Emirates") ?: "United Arab Emirates"
        set(value) = prefs.edit().putString("work_country_name", value).apply()

    var workCurrencyCode: String
        get()      = prefs.getString("work_currency", "AED") ?: "AED"
        set(value) = prefs.edit().putString("work_currency", value).apply()

    var workCurrencyFlag: String
        get()      = prefs.getString("work_flag", "\uD83C\uDDE6\uD83C\uDDEA") ?: "\uD83C\uDDE6\uD83C\uDDEA"
        set(value) = prefs.edit().putString("work_flag", value).apply()

    // Home country (where family is)
    var homeCountryName: String
        get()      = prefs.getString("home_country_name", "Nepal") ?: "Nepal"
        set(value) = prefs.edit().putString("home_country_name", value).apply()

    var homeCurrencyCode: String
        get()      = prefs.getString("home_currency", "NPR") ?: "NPR"
        set(value) = prefs.edit().putString("home_currency", value).apply()

    var homeCurrencyFlag: String
        get()      = prefs.getString("home_flag", "\uD83C\uDDF3\uD83C\uDDF5") ?: "\uD83C\uDDF3\uD83C\uDDF5"
        set(value) = prefs.edit().putString("home_flag", value).apply()

    var homeAirportCode: String
        get()      = prefs.getString("home_airport", "KTM") ?: "KTM"
        set(value) = prefs.edit().putString("home_airport", value).apply()
}
