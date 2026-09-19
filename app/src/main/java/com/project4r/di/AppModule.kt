package com.project4r.di

import com.project4r.data.api.CurrencyApi
import com.project4r.data.api.SerpApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    // SerpAPI — Google Flights
    @Provides
    @Singleton
    @Named("serp")
    fun provideSerpRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSerpApi(@Named("serp") retrofit: Retrofit): SerpApi =
        retrofit.create(SerpApi::class.java)

    // ExchangeRate-API — Currency
    @Provides
    @Singleton
    @Named("currency")
    fun provideCurrencyRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCurrencyApi(@Named("currency") retrofit: Retrofit): CurrencyApi =
        retrofit.create(CurrencyApi::class.java)
}
