package com.project4r.di

import android.content.Context
import com.project4r.data.UserPreferences
import com.project4r.data.api.CurrencyApi
import com.project4r.data.api.VercelFlightApi
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

    // ✅ Live Vercel backend
    private const val VERCEL_BASE_URL = "https://project4-r.vercel.app/"

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
            .baseUrl("https://v6.exchangerate-api.com/v6/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideCurrencyApi(@Named("currency") retrofit: Retrofit): CurrencyApi =
        retrofit.create(CurrencyApi::class.java)
}
