package com.project4r.data.api

import retrofit2.http.*

// ---- Amadeus token endpoint ----
data class AmadeusTokenRequest(
    val grant_type: String = "client_credentials",
    val client_id: String,
    val client_secret: String
)
data class AmadeusTokenResponse(
    val access_token: String,
    val expires_in: Int
)

// ---- Flight Offers models ----
data class FlightOffersResponse(
    val data: List<FlightOfferRaw>
)
data class FlightOfferRaw(
    val id: String,
    val price: AmadeusPrice,
    val itineraries: List<AmadeusItinerary>,
    val validatingAirlineCodes: List<String>
)
data class AmadeusPrice(val grandTotal: String, val currency: String)
data class AmadeusItinerary(val duration: String, val segments: List<AmadeusSegment>)
data class AmadeusSegment(
    val departure: AmadeusEndpoint,
    val arrival: AmadeusEndpoint,
    val carrierCode: String,
    val number: String
)
data class AmadeusEndpoint(val iataCode: String, val at: String)

interface AmadeusApi {
    @FormUrlEncoded
    @POST("v1/security/oauth2/token")
    suspend fun getToken(
        @Field("grant_type")    grantType: String = "client_credentials",
        @Field("client_id")     clientId: String,
        @Field("client_secret") clientSecret: String
    ): AmadeusTokenResponse

    @GET("v2/shopping/flight-offers")
    suspend fun searchFlights(
        @Header("Authorization") bearer: String,
        @Query("originLocationCode")      origin: String,
        @Query("destinationLocationCode") destination: String,
        @Query("departureDate")           date: String,
        @Query("adults")                  adults: Int = 1,
        @Query("max")                     max: Int = 10,
        @Query("currencyCode")            currency: String = "AED"
    ): FlightOffersResponse
}
