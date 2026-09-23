package com.project4r.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open-Meteo — keyless global weather forecast (free for non-commercial use).
 * Base URL: https://api.open-meteo.com/
 */
data class OpenMeteoCurrent(
    @SerializedName("temperature_2m")
    val temperature: Double?,
    @SerializedName("weather_code")
    val weatherCode: Int?
)

data class OpenMeteoResponse(
    val current: OpenMeteoCurrent?
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun current(
        @Query("latitude")  latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current")   current: String = "temperature_2m,weather_code",
        @Query("timezone")  timezone: String = "auto"
    ): OpenMeteoResponse
}
