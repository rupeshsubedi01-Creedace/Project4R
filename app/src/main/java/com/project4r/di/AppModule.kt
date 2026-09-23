package com.project4r.di

import android.content.Context
import com.project4r.data.UserPreferences
import com.project4r.data.api.CurrencyApi
import com.project4r.data.api.HolidayApi
import com.project4r.data.api.VercelFlightApi
import com.project4r.data.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ✅ Live Vercel backend (flight search via SerpAPI)
    private const val VERCEL_BASE_URL = "https://project4-r.vercel.app/"

    // ✅ Keyless ExchangeRate-API public mirror — no API key needed
    private const val CURRENCY_BASE_URL = "https://open.er-api.com/v6/"

    // ✅ Keyless public holidays (caldays.com, CC BY 4.0)
    private const val HOLIDAY_BASE_URL = "https://caldays.com/"

    // ✅ Keyless weather forecast (open-meteo.com)
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    @Provides @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

    @Provides @Singleton @Named("vercel")
    fun provideVercelRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(VERCEL_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideVercelFlightApi(@Named("vercel") retrofit: Retrofit): VercelFlightApi =
        retrofit.create(VercelFlightApi::class.java)

    @Provides @Singleton @Named("currency")
    fun provideCurrencyRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(CURRENCY_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideCurrencyApi(@Named("currency") retrofit: Retrofit): CurrencyApi =
        retrofit.create(CurrencyApi::class.java)

    @Provides @Singleton @Named("holidays")
    fun provideHolidayRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(HOLIDAY_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideHolidayApi(@Named("holidays") retrofit: Retrofit): HolidayApi =
        retrofit.create(HolidayApi::class.java)

    @Provides @Singleton @Named("weather")
    fun provideWeatherRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideWeatherApi(@Named("weather") retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}
